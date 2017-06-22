package org.eumetsat.usd.gcp.client.presenter;

import org.eumetsat.usd.gcp.client.action.RegisteredUser;
import org.eumetsat.usd.gcp.client.crypto.HashUtils;
import org.eumetsat.usd.gcp.client.event.LoginAuthenticatedEvent;
import org.eumetsat.usd.gcp.client.util.FieldVerifier;
import org.eumetsat.usd.gcp.shared.action.Register;

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
 * Registration popup's presenter.
 * 
 * @author USD/C/PBe
 */
public class RegisterPresenter extends PresenterWidget<RegisterPresenter.MyView>
{
    /** Asynchronous Dispatcher. */
    private final DispatchAsync dispatcher;

    /** Event Bus. */
    private final EventBus eventBus;

    /**
     * View's interface for registration popup.
     * 
     * @author USD/C/PBe
     */
    public interface MyView extends PopupView
    {
        // --------------------------
        // get login parameters
        // --------------------------

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

        /**
         * Get confirmation password entered.
         * 
         * @return password entered.
         */
        String getConfirmPassword();

        // --------------------------
        // event handlers
        // --------------------------

        /**
         * Set click handler for register button.
         * 
         * @param clickHandler
         *            click handler for register button.
         */
        void setRegisterButtonClickHandler(final ClickHandler clickHandler);

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

        // --------------------------
        // show/hide methods
        // --------------------------

        /**
         * Reset and focus on username text box.
         */
        void resetAndFocus();

        /**
         * Reset password and confirm password, and focus on password text box.
         */
        void resetAndFocusPassword();

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
     *            corresponding view.
     * @param dispatcher
     *            dispatcher.
     */
    @Inject
    public RegisterPresenter(final EventBus eventBus, final MyView view, final DispatchAsync dispatcher)
    {
        super(eventBus, view);

        this.dispatcher = dispatcher;
        this.eventBus = eventBus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gwtplatform.mvp.client.PresenterWidget#onReveal()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see com.gwtplatform.mvp.client.HandlerContainerImpl#onBind()
     */
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
                    register(getView().getUsername(), getView().getPassword(), getView().getConfirmPassword(), event);
                }
            }
        });

        // Add click handler to login button.
        getView().setRegisterButtonClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                register(getView().getUsername(), getView().getPassword(), getView().getConfirmPassword(), event);
            }
        });
    }

    /**
     * Perform registration of new user.
     * 
     * @param username
     *            name of the user being registered.
     * @param password
     *            password entered.
     * @param confirmPassword
     *            confirm password entered.
     * @param callerEvent
     *            event calling this method.
     */
    private void register(final String username, final String password, final String confirmPassword,
            final DomEvent<?> callerEvent)
    {
        if (FieldVerifier.isValidUsername(username) && FieldVerifier.isValidPassword(password))
        {
            if (password.equals(confirmPassword))
            {
                // Hash password
                HashUtils.install();

                String passwordHash = HashUtils.computeSHA256(password);

                dispatcher.execute(new Register(username, passwordHash), new RegisteredUser(eventBus)
                {
                    @Override
                    public void onRegisteredUser()
                    {
                        LoginAuthenticatedEvent.fire(this, username);

                        getView().resetAndFocus();
                        getView().hide();
                    }

                    @Override
                    public void onUserAlreadyExists()
                    {
                        getView().showErrorMessage("'" + username + "' already exists. Please try another name.");
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
                        getView().showErrorMessage("Creation of new user failed. Unknown error.");
                        getView().resetAndFocus();
                    }
                });

            } else
            {
                callerEvent.stopPropagation();
                getView().showErrorMessage("Password does not match confirm password.");
                getView().resetAndFocusPassword();
            }

        } else if (!FieldVerifier.isValidUsername(username))
        {
            callerEvent.stopPropagation();
            getView().showErrorMessage("Invalid username (it shall be between 8 and 32 characters).");
            getView().resetAndFocus();

        } else if (!FieldVerifier.isValidPassword(password))
        {
            callerEvent.stopPropagation();
            getView().showErrorMessage("Invalid password (it shall be between 8 and 32 characters).");
            getView().resetAndFocus();
        }
    }
}
