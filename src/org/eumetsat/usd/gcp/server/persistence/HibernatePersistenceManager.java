package org.eumetsat.usd.gcp.server.persistence;

import java.util.ArrayList;
import java.util.List;

import org.eumetsat.usd.gcp.server.crypto.BCrypt;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

public class HibernatePersistenceManager implements PersistenceManager
{
    /** Session Factory. */
    private SessionFactory sessionFactory;

    /**
     * Constructor.
     * 
     * @param sessionFactory
     *            Hibernate's session factory.
     */
    @Inject
    public HibernatePersistenceManager(final SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void saveUser(String username, String password)
    {
        // Compute password hash.
        final String passwordHash = BCrypt.hashPw(password, BCrypt.gensalt());

        // Check if user already exists in the database.
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq(User.NAME, username));
        User user = (User) criteria.uniqueResult();

        // if it does not exist.
        if (user == null)
        {
            // ... create it.
            user = new User(username, passwordHash);
            user.setPlotConfigurations(new ArrayList<PlotConfiguration>());
            session.save(user);

        } // if the user exists, does nothing.

        session.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean userPasswordIsValid(String username, String password)
    {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq(User.NAME, username));
        User user = (User) criteria.uniqueResult();

        session.getTransaction().commit();

        if (user != null)
        {
            return BCrypt.checkpw(password, user.getPasswordHash());

        } else
        {
            return false;
        }
    }

    @Override
    public boolean userExists(String username)
    {
        // Check if user already exists in the database.
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq(User.NAME, username));
        User user = (User) criteria.uniqueResult();

        session.getTransaction().commit();

        if (user != null)
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<org.eumetsat.usd.gcp.shared.conf.PlotConfiguration> savedPlotConfigsFor(String username)
    {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq(User.NAME, username));
        User user = (User) criteria.uniqueResult();

        if (user == null)
        {
            // user does not exist yet in the database.
            // TODO: throw exception to let the client know.

            // commit transaction.
            session.getTransaction().commit();

            // return empty ArrayList.
            return new ArrayList<org.eumetsat.usd.gcp.shared.conf.PlotConfiguration>(0);

        } else
        {
            // get plot configurations.
            final List<PlotConfiguration> persistentPlotConfigs = new ArrayList<PlotConfiguration>(
                    user.getPlotConfigurations());

            // commit transaction.
            session.getTransaction().commit();

            // create list of shared PlotConfigurations.
            final List<org.eumetsat.usd.gcp.shared.conf.PlotConfiguration> savedPlotConfigs = new ArrayList<org.eumetsat.usd.gcp.shared.conf.PlotConfiguration>(
                    persistentPlotConfigs.size());

            // Convert to shared PlotConfiguration class.
            for (final PlotConfiguration persistentPlotConfig : persistentPlotConfigs)
            {
                savedPlotConfigs.add(fromPersistent(persistentPlotConfig));
            }

            return savedPlotConfigs;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void savePlotConfigForUser(String username, org.eumetsat.usd.gcp.shared.conf.PlotConfiguration plotConfig)
    {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        // get user from database.
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq(User.NAME, username));
        User user = (User) criteria.uniqueResult();

        if (user != null)
        {
            // save this plot configuration for this user.
            user.getPlotConfigurations().add(asPersistent(plotConfig));
            session.update(user);
        }

        // TODO: throw exception to let the client know when user does not exist yet.

        session.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearPlotConfigsForUser(String username)
    {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        // get user from database.
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq(User.NAME, username));
        User user = (User) criteria.uniqueResult();

        if (user != null)
        {
            // clear saved plot configurations for this user.
            user.getPlotConfigurations().clear();
            session.update(user);
        }

        // TODO: throw exception to let the client know when user does not exist yet.

        session.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePlotConfigForUser(String username, String plotConfigName)
    {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        // get user from database.
        Criteria userCriteria = session.createCriteria(User.class);
        userCriteria.add(Restrictions.eq(User.NAME, username));
        User user = (User) userCriteria.uniqueResult();

        if (user != null)
        {
            // get plot configurations.
            List<PlotConfiguration> savedPlotConfigs = new ArrayList<PlotConfiguration>(user.getPlotConfigurations());

            // find configuration to be removed.
            PlotConfiguration plotConfigToBeRemoved = null;

            for (PlotConfiguration plotConfig : savedPlotConfigs)
            {
                if (plotConfig.getName().equals(plotConfigName))
                {
                    plotConfigToBeRemoved = plotConfig;
                    break;
                }
            }

            // remove this plot configuration for this user.
            user.getPlotConfigurations().remove(plotConfigToBeRemoved);

            // update this user table in database.
            session.update(user);
            session.flush();
        }

        // TODO: throw exception to let the client know when user does not exist yet.

        session.getTransaction().commit();
    }

    /**
     * Factory static method. Returns new PlotConfiguration object from an existing persistent PlotConfiguration.
     * 
     * @param persistentPlotConfig
     *            persistent PlotConfiguration, associated POJO with database.
     * @return PlotConfiguration
     */
    private static org.eumetsat.usd.gcp.shared.conf.PlotConfiguration fromPersistent(
            final org.eumetsat.usd.gcp.server.persistence.PlotConfiguration persistentPlotConfig)
    {
        return new org.eumetsat.usd.gcp.shared.conf.PlotConfiguration(persistentPlotConfig.getName(),
                persistentPlotConfig.getServer(), persistentPlotConfig.getGPRC(), persistentPlotConfig.getCorrType(),
                persistentPlotConfig.getSatInstr(), persistentPlotConfig.getRefSatInstr(),
                persistentPlotConfig.getMode(), persistentPlotConfig.getYear(), persistentPlotConfig.getDateTime(),
                persistentPlotConfig.getVersion(), persistentPlotConfig.getChannel(),
                persistentPlotConfig.getSceneTb().doubleValue());
    }

    /**
     * Factory static method. Returns new persistent PlotConfiguration object from an existing PlotConfiguration.
     * 
     * @param plotConfig
     *            PlotConfiguration object.
     * @return persistent PlotConfiguration, associated POJO with database.
     */
    private static org.eumetsat.usd.gcp.server.persistence.PlotConfiguration asPersistent(
            final org.eumetsat.usd.gcp.shared.conf.PlotConfiguration plotConfig)
    {
        return new org.eumetsat.usd.gcp.server.persistence.PlotConfiguration(plotConfig.getId(),
                plotConfig.getServer(), plotConfig.getGPRC(), plotConfig.getCorrType(), plotConfig.getSatInstr(),
                plotConfig.getRefSatInstr(), plotConfig.getMode(), plotConfig.getYear(), plotConfig.getDateTime(),
                plotConfig.getVersion(), plotConfig.getChannel(), Double.valueOf(plotConfig.getSceneTb()));
    }
}
