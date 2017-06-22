// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2012
 */
// @formatter:on
package org.eumetsat.usd.gcp.server.persistence;

import java.util.List;

/**
 * Persistence Manager.
 * 
 * @author USC/C/PBe
 */
public interface PersistenceManager
{
    /**
     * Save a user to the database. If it exists, does nothing.
     * 
     * @param username
     *            user name.
     * @param password
     *            password.
     */
    void saveUser(final String username, final String password);

    /**
     * Check if the user password is valid.
     * 
     * @param username
     *            name of the user.
     * @param password
     *            password.
     * @return <code>true</code> if user login is valid; <code>false</code> otherwise.
     */
    boolean userPasswordIsValid(final String username, final String password);

    /**
     * Check if the user already exists.
     * 
     * @param username
     *            name of the user.
     * @param password
     *            password.
     * @return <code>true</code> if user login is valid; <code>false</code> otherwise.
     */
    boolean userExists(final String username);

    /**
     * Get all saved plot configurations for a certain user.
     * 
     * @param username
     *            user name.
     * @return list of saved plot configurations for the user.
     */
    List<org.eumetsat.usd.gcp.shared.conf.PlotConfiguration> savedPlotConfigsFor(final String username);

    /**
     * Save a plot configuration for a certain user.
     * 
     * @param username
     *            user name.
     * @param plotConfig
     *            plot configuration to be saved.
     */
    void savePlotConfigForUser(final String username,
            final org.eumetsat.usd.gcp.shared.conf.PlotConfiguration plotConfig);

    /**
     * Clears all saved plot configurations for a certain user.
     * 
     * @param username
     *            user name.
     */
    void clearPlotConfigsForUser(final String username);

    /**
     * Clear a specific saved plot configuration for a certain user.
     * 
     * @param username
     *            user name.
     * @param plotConfigName
     *            name of the plot configuration to be removed.
     */
    void removePlotConfigForUser(final String username, final String plotConfigName);
}
