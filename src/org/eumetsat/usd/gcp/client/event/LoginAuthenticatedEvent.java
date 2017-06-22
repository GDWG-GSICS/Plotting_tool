package org.eumetsat.usd.gcp.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Event fired when a user has been authenticated.
 * 
 * @author USD/C/PBe
 */
public class LoginAuthenticatedEvent extends GwtEvent<LoginAuthenticatedHandler>
{
    /** Associated Type. */
    private static final Type<LoginAuthenticatedHandler> TYPE = new Type<LoginAuthenticatedHandler>();

    /** name of logged in user. */
    private String username;

    /**
     * Constructor.
     * 
     * @param username
     *            name of logged in user.
     */
    public LoginAuthenticatedEvent(final String username)
    {
        this.username = username;
    }

    /**
     * Get name of user logged in.
     * 
     * @return name of user logged in.
     */
    public final String getUsername()
    {
        return username;
    }

    @Override
    protected final void dispatch(final LoginAuthenticatedHandler handler)
    {
        handler.onLoginAuthenticated(this);
    }

    @Override
    public final Type<LoginAuthenticatedHandler> getAssociatedType()
    {
        return TYPE;
    }

    /**
     * Get associated type.
     * 
     * @return associated type.
     */
    public static Type<LoginAuthenticatedHandler> getType()
    {
        return TYPE;
    }

    /**
     * Fire new login authenticated event.
     * 
     * @param source
     *            source.
     * @param username
     *            name of user logged in.
     */
    public static void fire(final HasHandlers source, final String username)
    {
        source.fireEvent(new LoginAuthenticatedEvent(username));
    }
}
