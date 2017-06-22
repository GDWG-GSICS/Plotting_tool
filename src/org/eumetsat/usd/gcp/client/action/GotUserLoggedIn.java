//@formatter:off
/*
 * PROJECT: gcp 
 * AUTHOR: USC/C/PBe 
 * COPYRIGHT: EUMETSAT 2014
 */
//@formatter:on
package org.eumetsat.usd.gcp.client.action;

import org.eumetsat.usd.gcp.shared.action.GetUserLoggedInResult;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author USC/C/PBe
 */
public abstract class GotUserLoggedIn extends CustomAsyncCallback<GetUserLoggedInResult>
{
    @Inject
    public GotUserLoggedIn(final EventBus eventBus)
    {
        super(eventBus);
    }
    
    @Override
    public final void onSuccess(final GetUserLoggedInResult result)
    {
        if (result.getUsername() != null)
        {
            onUserLoggedIn(result.getUsername());

        } else
        {
            onNoUserLoggedIn();
        }
    }

    /**
     * Action performed on user logged in.
     * 
     * @param username
     *            username.
     */
    public abstract void onUserLoggedIn(final String username);

    /**
     * Action performed on no user logged in.
     */
    public abstract void onNoUserLoggedIn();
}
