//@formatter:off
/*
 * PROJECT: gcp 
 * AUTHOR: USC/C/PBe 
 * COPYRIGHT: EUMETSAT 2014
 */
//@formatter:on
package org.eumetsat.usd.gcp.client.action;

import org.eumetsat.usd.gcp.shared.action.RegisterResult;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author USC/C/PBe
 */
public abstract class RegisteredUser extends CustomAsyncCallback<RegisterResult>
{
    @Inject
    public RegisteredUser(final EventBus eventBus)
    {
        super(eventBus);
    }
    
    @Override
    public final void onSuccess(final RegisterResult result)
    {
        if(!result.userExistedAlready())
        {
            onRegisteredUser();
        
        } else
        {
            onUserAlreadyExists();
        }
    }

    /**
     * Action performed on user registered.
     */
    public abstract void onRegisteredUser();
    
    /**
     * Action performed on when user already exists.
     */
    public abstract void onUserAlreadyExists();

}
