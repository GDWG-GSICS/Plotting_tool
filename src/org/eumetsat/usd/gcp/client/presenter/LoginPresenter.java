package org.eumetsat.usd.gcp.client.presenter;

import org.eumetsat.usd.gcp.client.action.LoggedInUser;
import org.eumetsat.usd.gcp.client.crypto.HashUtils;
import org.eumetsat.usd.gcp.client.event.LoginAuthenticatedEvent;
import org.eumetsat.usd.gcp.shared.action.Login;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.rpc.shared.DispatchAsync;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

/**
 * Login popup's presenter.
 * 
 * @author USD/C/PBe
 */
public class LoginPresenter extends PresenterWidget<LoginPresenter.MyView>
{
    /** Asynchronous dispatcher. */
    private final DispatchAsync dispatcher;

    /** Event Bus. */
    private final EventBus eventBus;

    /**
     * View's interface for login popup.
     * 
     * @author USD/C/PBe
     */
    public interface MyView extends PopupView
    {
        // -------------------------
        // get login parameters
        // -------------------------

        /**
         * Get username entered.
         * 
         * @return username entered.
         */
        String getUsername();

        /**
         * Get password entered.
         * 
         * @return password entered.
         */
        String getPassword();

        // -------------------------
        // event handlers
        // -------------------------

        /**
         * Set click handler for login button.
         * 
         * @param clickHandler
         *            click handler for login button.
         */
        void setLoginButtonClickHandler(final ClickHandler clickHandler);

        /**
         * Set click handler for cancel button.
         * 
         * @param clickHandler
         *            click handler for cancel button.
         */
        void setCancelButtonClickHandler(final ClickHandler clickHandler);

        /**
         * Set key down handler for password text box.
         * 
         * @param keyDownHandler
         *            key down handler for password text box.
         */
        void setPasswordKeyDownHandler(final KeyDownHandler keyDownHandler);

        // -------------------------
        // show/hide methods
        // -------------------------

        /**
         * Reset and focus on username text box.
         */
        void resetAndFocus();

        /**
         * Show error message.
         * 
         * @param text
         *            error message.
         */
        void showErrorMessage(final String text);
    }

    /**
     * Constructor.
     * 
     * @param eventBus
     *            event bus.
     * @param view
     *            view.
     * @param dispatcher
     *            dispatcher.
     */
    @Inject
    public LoginPresenter(final EventBus eventBus, final MyView view, final DispatchAsync dispatcher)
    {
        super(eventBus, view);

        this.dispatcher = dispatcher;
        this.eventBus = eventBus;
    }

    @Override
    protected final void onReveal()
    {
        super.onReveal();

        // Reset and focus.
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
        {
            @Override
            public void execute()
            {
                getView().resetAndFocus();
            }
        });
    }

    @Override
    protected final void onBind()
    {
        super.onBind();

        // Set click handler to cancel button.
        getView().setCancelButtonClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                getView().resetAndFocus();
                getView().hide();
            }
        });

        // Set enter press keyboard handler to the password text box.
        getView().setPasswordKeyDownHandler(new KeyDownHandler()
        {
            @Override
            public void onKeyDown(final KeyDownEvent event)
            {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
                {
                    login(getView().getUsername(), getView().getPassword(), event);
                }
            }
        });

        // Add click handler to login button.
        getView().setLoginButtonClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                login(getView().getUsername(), getView().getPassword(), event);
            }
        });
    }

    /**
     * Perform login.
     * 
     * @param username
     *            username.
     * @param password
     *            password.
     * @param callerEvent
     *            caller event.
     */
    private void login(final String username, final String password, final DomEvent<?> callerEvent)
    {
        // Hash password
        HashUtils.install();

        String passwordHash = HashUtils.computeSHA256(password);

        dispatcher.execute(new Login(username, passwordHash), new LoggedInUser(eventBus)
        {
            @Override
            public void onLoggedInUser()
            {
                LoginAuthenticatedEvent.fire(this, username);

                getView().resetAndFocus();
                getView().hide();
            }

            @Override
            public void onWrongPassword()
            {
                getView().showErrorMessage("Wrong username and/or password.");
                getView().resetAndFocus();
            }

            @Override
            public void onDisconnected()
            {
                getView().showErrorMessage("No connection to the server. Please try again later.");
                getView().resetAndFocus();
            }

            @Override
            public void onCatalogNotReachable()
            {
                // ignore this one since login is not related with the catalog connection.
            }

            @Override
            public void onException(Throwable caught)
            {
                getView().showErrorMessage("Login failed. Unknown error. \n" + caught.getMessage());
                getView().resetAndFocus();
            }
        });
    }
}
