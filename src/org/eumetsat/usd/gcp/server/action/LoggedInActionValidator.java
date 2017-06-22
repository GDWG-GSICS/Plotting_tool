/*
 * PROJECT: USD_GCP AUTHOR: USD/C/PBe COPYRIGHT: EUMETSAT 2012
 */
package org.eumetsat.usd.gcp.server.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.gwtplatform.dispatch.rpc.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Logged in Action Validator.
 * 
 * @author USD/C/PBe
 */
public class LoggedInActionValidator implements ActionValidator
{
    /** Request Provider. */
    private Provider<HttpServletRequest> requestProvider;

    /**
     * Constructor.
     * 
     * @param requestProvider
     *            request provider.
     */
    @Inject
    LoggedInActionValidator(final Provider<HttpServletRequest> requestProvider)
    {
        this.requestProvider = requestProvider;
    }

    @Override
    @Singleton
    public final boolean isValid(final Action<? extends Result> arg0) throws ActionException
    {
        boolean result = true;

        HttpSession session = requestProvider.get().getSession();

        Object authenticated = session.getAttribute("login.authenticated");

        if (authenticated == null)
        {
            result = false;
        }

        return result;
    }
}
