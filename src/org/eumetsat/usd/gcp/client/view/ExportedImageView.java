package org.eumetsat.usd.gcp.client.view;

import org.eumetsat.usd.gcp.client.presenter.ExportedImagePresenter;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;

/**
 * Exported image popup's view.
 * 
 * @author USD/C/PBe
 */
public class ExportedImageView extends PopupViewImpl implements ExportedImagePresenter.MyView
{
    /** Close Button. */
    @UiField
    Button closeButton;

    /** Widget. */
    private final Widget widget;
    
    /** Close Click Handler Registration. */
    private HandlerRegistration clickCloseHandlerReg;

    /**
     * Binder.
     */
    public interface Binder extends UiBinder<Widget, ExportedImageView>
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
    public ExportedImageView(final EventBus eventBus, final Binder binder)
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
    public final void setCloseButtonClickHandler(final ClickHandler clickHandler)
    {
        // remove old handler if exists.
        if (clickCloseHandlerReg != null)
        {
            clickCloseHandlerReg.removeHandler();
        }

        // add new handler.
        clickCloseHandlerReg = closeButton.addClickHandler(clickHandler);
    }
}
