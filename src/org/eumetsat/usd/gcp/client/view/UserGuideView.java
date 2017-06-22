package org.eumetsat.usd.gcp.client.view;

import org.eumetsat.usd.gcp.client.presenter.UserGuidePresenter;

import com.gwtplatform.mvp.client.PopupViewImpl;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.inject.Inject;

/**
 * User Guide popup's view.
 * 
 * @author USD/C/PBe
 */
public class UserGuideView extends PopupViewImpl implements UserGuidePresenter.MyView
{
    /** Frame. */
    @UiField
    Frame frame;
    /** Close Button. */
    @UiField
    Button closeButton;

    /** Widget. */
    private final Widget widget;

    /** Close Click Handler Registration. */
    private HandlerRegistration clickCloseHandlerReg;

    public interface Binder extends UiBinder<Widget, UserGuideView>
    {
    }

    @Inject
    public UserGuideView(final EventBus eventBus, final Binder binder)
    {
        super(eventBus);
        widget = binder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget()
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

    @Override
    public void setFrameUrl(SafeUri url)
    {
        frame.setUrl(url);
    }
}
