package org.eumetsat.usd.gcp.client.view;

import org.eumetsat.usd.gcp.client.presenter.ConfirmationPresenter;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;

/**
 * Confirmation popup's view.
 * 
 * @author USD/C/PBe
 */
public class ConfirmationView extends PopupViewImpl implements ConfirmationPresenter.MyView
{
    /** Message. */
    @UiField
    Label message;
    /** Yes Button. */
    @UiField
    Button yesButton;
    /** No Button. */
    @UiField
    Button noButton;

    /** Widget. */
    private final Widget widget;

    /** Yes Click Handler Registration. */
    private HandlerRegistration clickYesHandlerReg;
    /** No Click Handler Registration. */
    private HandlerRegistration clickNoHandlerReg;

    /**
     * Binder.
     */
    public interface Binder extends UiBinder<Widget, ConfirmationView>
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
    public ConfirmationView(final EventBus eventBus, final Binder binder)
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
    public final void setYesButtonClickHandler(final ClickHandler clickHandler)
    {
        // remove old handler if exists.
        if (clickYesHandlerReg != null)
        {
            clickYesHandlerReg.removeHandler();
        }

        clickYesHandlerReg = yesButton.addClickHandler(clickHandler);
    }

    @Override
    public final void setNoButtonClickHandler(final ClickHandler clickHandler)
    {
        // remove old handler if exists.
        if (clickNoHandlerReg != null)
        {
            clickNoHandlerReg.removeHandler();
        }

        // add new handler.
        clickNoHandlerReg = noButton.addClickHandler(clickHandler);
    }

    @Override
    public final void setMessage(final String text)
    {
        message.setText(text);
    }
}
