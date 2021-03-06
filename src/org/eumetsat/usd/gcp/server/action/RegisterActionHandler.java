package org.eumetsat.usd.gcp.server.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eumetsat.usd.gcp.server.persistence.PersistenceManager;
import org.eumetsat.usd.gcp.shared.action.Register;
import org.eumetsat.usd.gcp.shared.action.RegisterResult;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Register Action Handler. Generated by GWTP Plugin.
 * 
 * @author USD/C/PBe
 */
public class RegisterActionHandler implements ActionHandler<Register, RegisterResult>
{
    /** Request provider. */
    private final Provider<HttpServletRequest> requestProvider;

    /** Persistence Manager. */
    private PersistenceManager persistenceManager;

    /**
     * Constructor.
     * 
     * @param requestProvider
     *            Request provider.
     */
    @Inject
    public RegisterActionHandler(final Provider<HttpServletRequest> requestProvider,
            final PersistenceManager persistenceManager)
    {
        this.persistenceManager = persistenceManager;
        this.requestProvider = requestProvider;
    }

    @Override
    public final RegisterResult execute(final Register action, final ExecutionContext context) throws ActionException
    {
        boolean userExistedAlready = false;

        if (persistenceManager.userExists(action.getUsername()))
        {
            userExistedAlready = true;

        } else
        {
            // save new user.
            persistenceManager.saveUser(action.getUsername(), action.getPassword());

            // login new user.
            HttpSession session = requestProvider.get().getSession();
            session.setAttribute("login.authenticated", action.getUsername());

            userExistedAlready = false;
        }

        return new RegisterResult(userExistedAlready);
    }

    @Override
    public final void undo(final Register action, final RegisterResult result, final ExecutionContext context)
            throws ActionException
    {
    }

    @Override
    public final Class<Register> getActionType()
    {
        return Register.class;
    }
}
