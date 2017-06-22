// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2012
 */
// @formatter:on
package org.eumetsat.usd.gcp.server;

import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eumetsat.usd.gcp.server.persistence.HibernateUtils;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Server Initializer.
 * 
 * @author USD/C/PBe
 */
public class ServerInitializer implements ServletContextListener
{
    /**
     * Run when servlet context is initialized.
     * 
     * @param event
     *            Servlet Context Event.
     */
    public final void contextInitialized(final ServletContextEvent event)
    {
        // Install SLF4J for bridging JUL logs.
        // Optionally remove existing handlers attached to j.u.l root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger(); // (since SLF4J 1.6.5)

        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
        // the initialization phase of your application
        SLF4JBridgeHandler.install();

        // Connect to database providing persistency.
        HibernateUtils.getSessionFactory();
    }

    /**
     * Run when servlet context is destroyed.
     * 
     * @param event
     *            Servlet Context Event.
     */
    public final void contextDestroyed(final ServletContextEvent event)
    {
        // Disconnect of database providing persistency.
        HibernateUtils.closeSessionFactory(); // Free all resources, including C3P0 session pool.

        // Perform mysql jdbc clean-up.
        try
        {
            com.mysql.jdbc.AbandonedConnectionCleanupThread.shutdown();
        } catch (Throwable t)
        {
        }

        // This manually deregisters JDBC driver, which prevents Tomcat from complaining about memory leaks
        Enumeration<java.sql.Driver> drivers = java.sql.DriverManager.getDrivers();
        while (drivers.hasMoreElements())
        {
            java.sql.Driver driver = drivers.nextElement();
            try
            {
                java.sql.DriverManager.deregisterDriver(driver);
            } catch (Throwable t)
            {
            }
        }
    }
}