//@formatter:off
/*
 * PROJECT: gcp 
 * AUTHOR: USC/C/PBe 
 * COPYRIGHT: EUMETSAT 2015
 */
//@formatter:on
package org.eumetsat.usd.gcp.server.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

/**
 * Hibernate Utilities.
 * 
 * @author USC/C/PBe
 */
public final class HibernateUtils
{
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtils.class);

    /** Session Factory. */
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    /**
     * Private Constructor to make it non-instantiable.
     */
    private HibernateUtils()
    {

    }

    /**
     * Build Session Factory.
     * 
     * @return SessionFactory session factory.
     */
    private static SessionFactory buildSessionFactory()
    {
        try
        {
            LOGGER.info("read hibernate configuration");
            Configuration configuration = new Configuration();
            configuration.configure();
            LOGGER.info("build service registry");
            ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            LOGGER.info("build hibernate session factory");
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (HibernateException he)
        {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + he);
            throw new ExceptionInInitializerError(he);
        }
    }

    /**
     * Get Session Factory.
     * 
     * @return session factory.
     */
    public static SessionFactory getSessionFactory()
    {
        return SESSION_FACTORY;
    }

    /**
     * Close session factory, releasing all related resources.
     */
    public static void closeSessionFactory()
    {
        if (null != SESSION_FACTORY && SESSION_FACTORY instanceof SessionFactoryImpl)
        {
            SessionFactoryImpl sf = (SessionFactoryImpl) SESSION_FACTORY;
            ConnectionProvider conn = sf.getConnectionProvider();
            if (conn instanceof C3P0ConnectionProvider)
            {
                ((C3P0ConnectionProvider) conn).close();
            }
        }
        SESSION_FACTORY.close();
    }
}
