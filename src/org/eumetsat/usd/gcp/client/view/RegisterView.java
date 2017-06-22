package org.eumetsat.usd.gcp.client.view;

import org.eumetsat.usd.gcp.client.presenter.RegisterPresenter;

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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;

/**
 * Register popup's view.
 * 
 * @author USD/C/PBe
 */
public class RegisterView extends PopupViewImpl implements RegisterPresenter.MyView
{
    /** Login Panel. */
    @UiField
    VerticalPanel loginPanel;
    /** Error Message. */
    @UiField
    Label errorMessage;
    /** Username Text Box. */
    @UiField
    TextBox usernameTextBox;
    /** Password Text Box. */
    @UiField
    PasswordTextBox passwordTextBox;
    /** Confirm Password Text Box. */
    @UiField
    PasswordTextBox confirmPasswordTextBox;
    /** Register Button. */
    @UiField
    Button registerButton;
    /** Cancel Button. */
    @UiField
    Button cancelButton;

    /** Widget. */
    private final Widget widget;

    /** Register Click Handler Registration. */
    private HandlerRegistration clickRegisterHandlerReg;
    /** Cancel Click Handler Registration. */
    private HandlerRegistration clickCancelHandlerReg;
    /** KeyDown Handler Registration. */
    private HandlerRegistration keyDownHandlerReg;

    /**
     * Binder.
     */
    public interface Binder extends UiBinder<Widget, RegisterView>
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
    public RegisterView(final EventBus eventBus, final Binder binder)
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
        confirmPasswordTextBox.setText("");
        usernameTextBox.setFocus(true);
    }

    @Override
    public final void resetAndFocusPassword()
    {
        errorMessage.setVisible(false);
        passwordTextBox.setText("");
        confirmPasswordTextBox.setText("");
        passwordTextBox.setFocus(true);
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
    public final String getConfirmPassword()
    {
        return confirmPasswordTextBox.getText();
    }

    @Override
    public final void setRegisterButtonClickHandler(final ClickHandler clickHandler)
    {
        // remove old handler if exists.
        if (clickRegisterHandlerReg != null)
        {
            clickRegisterHandlerReg.removeHandler();
        }

        // add new handler.
        clickRegisterHandlerReg = registerButton.addClickHandler(clickHandler);
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
        keyDownHandlerReg = confirmPasswordTextBox.addKeyDownHandler(keyDownHandler);
    }
}
