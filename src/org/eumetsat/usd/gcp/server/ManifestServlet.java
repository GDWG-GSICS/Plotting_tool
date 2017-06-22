//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2014
 */
//@formatter:on
package org.eumetsat.usd.gcp.server;

import org.realityforge.gwt.appcache.server.AbstractManifestServlet;
import org.realityforge.gwt.appcache.server.propertyprovider.UserAgentPropertyProvider;

import com.google.inject.Singleton;

@Singleton
public class ManifestServlet extends AbstractManifestServlet
{
    /** Auto-generated serial version UID. */
    private static final long serialVersionUID = -85387582433078466L;

    public ManifestServlet()
    {
        addPropertyProvider(new UserAgentPropertyProvider());
        
        addClientSideSelectionProperty("history.pushStateSupported");
    }
}