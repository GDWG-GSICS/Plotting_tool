package org.eumetsat.usd.gcp.client.view;

import org.eumetsat.usd.gcp.client.presenter.MessagePresenter;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;

/**
 * Message popup's view.
 * 
 * @author USD/C/PBe
 */
public class MessageView extends PopupViewImpl implements MessagePresenter.MyView
{
    /** Popup Panel. */
    @UiField
    VerticalPanel popupPanel;
    /** Message Label. */
    @UiField
    Label messageLabel;
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
    public interface Binder extends UiBinder<Widget, MessageView>
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
    public MessageView(final EventBus eventBus, final Binder binder)
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
    public final void setMessageText(final String messageText)
    {
        String messageTextCpy = messageText;
        
        // Add zero-width word joiner after question marks.
        messageTextCpy = messageTextCpy.replace("?", "?\u200D"); // zwj not supported by Chrome
                                                           // v21.0.1180.89 m, but supported by
                                                           // Firefox v15.0.1 and IE8.
        messageTextCpy = messageTextCpy.replace("\u2010", "\u2011");

        // Update message label.
        messageLabel.setText(messageTextCpy);
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
