//@formatter:off
/*
 * PROJECT: gcp 
 * AUTHOR: USC/C/PBe 
 * COPYRIGHT: EUMETSAT 2014
 */
//@formatter:on
package org.eumetsat.usd.gcp.client.action;

import org.eumetsat.usd.gcp.shared.action.LoginResult;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author USC/C/PBe
 */
public abstract class LoggedInUser extends CustomAsyncCallback<LoginResult>
{
    @Inject
    public LoggedInUser(final EventBus eventBus)
    {
        super(eventBus);
    }

    @Override
    public final void onSuccess(final LoginResult result)
    {
        if(result.success())
        {
            onLoggedInUser();
        
        } else
        {
            onWrongPassword();
        }
    }

    /**
     * Action performed on user registered.
     */
    public abstract void onLoggedInUser();
    
    /**
     * Action performed on wrong password.
     */
    public abstract void onWrongPassword();
}
