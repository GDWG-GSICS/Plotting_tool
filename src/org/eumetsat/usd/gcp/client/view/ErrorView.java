package org.eumetsat.usd.gcp.client.view;

import org.eumetsat.usd.gcp.client.presenter.ErrorPresenter;

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
 * Error popup's view.
 * 
 * @author USD/C/PBe
 */
public class ErrorView extends PopupViewImpl implements ErrorPresenter.MyView
{
    /** Error Message Label. */
    @UiField
    Label errorMessageLabel;
    /** Error Details Label. */
    @UiField
    Label errorDetailsLabel;
    /** OK Button. */
    @UiField
    Button okButton;
    
    /** Widget. */
    private final Widget widget;

    /** OK Click Handler Registration. */
    private HandlerRegistration clickOkHandlerReg;

    /**
     * Binder.
     */
    public interface Binder extends UiBinder<Widget, ErrorView>
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
    public ErrorView(final EventBus eventBus, final Binder binder)
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
    public final void setErrorMessage(final String errorMessage)
    {
        errorMessageLabel.setText(errorMessage);
    }

    @Override
    public final void setErrorDetails(final String errorDetails)
    {
        errorDetailsLabel.setText(errorDetails);
    }

    @Override
    public final void setOkButtonClickHandler(final ClickHandler clickHandler)
    {
        // remove old handler if exists.
        if (clickOkHandlerReg != null)
        {
            clickOkHandlerReg.removeHandler();
        }

        // add new handler.
        clickOkHandlerReg = okButton.addClickHandler(clickHandler);
    }
}
