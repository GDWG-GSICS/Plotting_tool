package org.eumetsat.usd.gcp.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for login authenticated events.
 * 
 * @author USD/C/PBe
 */
public interface LoginAuthenticatedHandler extends EventHandler
{
    /**
     * Method executed when a user login has been authenticated.
     * 
     * @param event
     *            login authenticated event caught.
     */
    void onLoginAuthenticated(LoginAuthenticatedEvent event);
}