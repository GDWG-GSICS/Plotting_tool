package org.eumetsat.usd.gcp.client.view;

import org.eumetsat.usd.gcp.client.presenter.LoginPresenter;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;

/**
 * Login popup's view.
 * 
 * @author USD/C/PBe
 */
public class LoginView extends PopupViewImpl implements LoginPresenter.MyView
{
    /** Error Message. */
    @UiField
    Label errorMessage;
    /** Username Text Box. */
    @UiField
    TextBox usernameTextBox;
    /** Password Text Box. */
    @UiField
    PasswordTextBox passwordTextBox;
    /** Login Button. */
    @UiField
    Button loginButton;
    /** Cancel Button. */
    @UiField
    Button cancelButton;
    
    /** Widget. */
    private final Widget widget;

    /** Login Click Handler Registration. */
    private HandlerRegistration clickLoginHandlerReg;
    /** Cancel Click Handler Registration. */
    private HandlerRegistration clickCancelHandlerReg;
    /** KeyDown Handler Registration. */
    private HandlerRegistration keyDownHandlerReg;

    /**
     * Binder.
     */
    public interface Binder extends UiBinder<Widget, LoginView>
    {
    }

    /**
     * Constructor.
     * 
     * @param eventBus
     *            event bus.
     * @param binder
     *            binder.
     */
    @Inject
    public LoginView(final EventBus eventBus, final Binder binder)
    {
        super(eventBus);
        widget = binder.createAndBindUi(this);
    }

    @Override
    public final Widget asWidget()
    {
        return widget;
    }

    @Override
    public final void resetAndFocus()
    {
        errorMessage.setVisible(false);
        usernameTextBox.setText("");
        passwordTextBox.setText("");
        usernameTextBox.setFocus(true);
    }

    @Override
    public final void showErrorMessage(final String text)
    {
        errorMessage.setText(text);
        Scheduler sched = Scheduler.get();
        sched.scheduleDeferred(new Scheduler.ScheduledCommand()
        {
            @Override
            public void execute()
            {
                errorMessage.setVisible(true);
            }
        });
    }

    @Override
    public final String getUsername()
    {
        // since database check is case-insensitive, username is always transformed to lower case
        // letters.
        return usernameTextBox.getText().toLowerCase();
    }

    @Override
    public final String getPassword()
    {
        return passwordTextBox.getText();
    }

    @Override
    public final void setLoginButtonClickHandler(final ClickHandler clickHandler)
    {
        // remove old handler if exists.
        if (clickLoginHandlerReg != null)
        {
            clickLoginHandlerReg.removeHandler();
        }

        // add new handler.
        clickLoginHandlerReg = loginButton.addClickHandler(clickHandler);
    }

    @Override
    public final void setCancelButtonClickHandler(final ClickHandler clickHandler)
    {
        // remove old handler if exists.
        if (clickCancelHandlerReg != null)
        {
            clickCancelHandlerReg.removeHandler();
        }

        // add new handler.
        clickCancelHandlerReg = cancelButton.addClickHandler(clickHandler);
    }

    @Override
    public final void setPasswordKeyDownHandler(final KeyDownHandler keyDownHandler)
    {
        // remove old handler if exists.
        if (keyDownHandlerReg != null)
        {
            keyDownHandlerReg.removeHandler();
        }

        // add new handler.
        keyDownHandlerReg = passwordTextBox.addKeyDownHandler(keyDownHandler);
    }
}
