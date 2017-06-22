package org.eumetsat.usd.gcp.server.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eumetsat.usd.gcp.shared.action.Logout;
import org.eumetsat.usd.gcp.shared.action.LogoutResult;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Logout Action Handler.
 * 
 * @author USD/C/PBe
 */
public class LogoutActionHandler implements ActionHandler<Logout, LogoutResult>
{
    /** Request provider. */
    private final Provider<HttpServletRequest> requestProvider;

    /**
     * Constructor.
     * 
     * @param requestProvider
     *            Request provider.
     */
    @Inject
    public LogoutActionHandler(final Provider<HttpServletRequest> requestProvider)
    {
        this.requestProvider = requestProvider;
    }

    @Override
    public final LogoutResult execute(final Logout action, final ExecutionContext context) throws ActionException
    {
        LogoutResult result = null;

        try
        {
            HttpSession session = requestProvider.get().getSession();
            session.setAttribute("login.authenticated", null);
            session.invalidate();

        } catch (Exception e)
        {
            throw new ActionException(e);
        }

        return result;
    }

    @Override
    public final void undo(final Logout action, final LogoutResult result, final ExecutionContext context)
            throws ActionException
    {
    }

    @Override
    public final Class<Logout> getActionType()
    {
        return Logout.class;
    }
}
