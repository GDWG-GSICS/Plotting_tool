package org.eumetsat.usd.gcp.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

/**
 * Message popup's presenter.
 * 
 * @author USD/C/PBe
 */
public class MessagePresenter extends PresenterWidget<MessagePresenter.MyView>
{
    /**
     * View's interface for message popup.
     * 
     * @author USD/C/PBe
     */
    public interface MyView extends PopupView
    {
        /**
         * Set the text of the message.
         * 
         * @param messageText
         *            text to be shown in the message.
         */
        void setMessageText(final String messageText);

        /**
         * Sets the ok button click handler.
         * 
         * @param clickHandler
         *            click handler for ok button.
         */
        void setOkButtonClickHandler(final ClickHandler clickHandler);
    }

    /**
     * Constructor.
     * 
     * @param eventBus
     *            event bus.
     * @param view
     *            corresponding view.
     */
    @Inject
    public MessagePresenter(final EventBus eventBus, final MyView view)
    {
        super(eventBus, view);
    }

    @Override
    protected final void onBind()
    {
        super.onBind();

        getView().setOkButtonClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                getView().hide();
            }
        });
    }

    /**
     * Sets text of the message.
     * 
     * @param messageText
     *            text of the message.
     */
    public final void setMessageText(final String messageText)
    {
        getView().setMessageText(messageText);
    }
}
