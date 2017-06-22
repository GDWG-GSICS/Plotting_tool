package org.eumetsat.usd.gcp.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

/**
 * Confirmation popup's presenter.
 * 
 * @author USD/C/PBe
 */
public class ConfirmationPresenter extends PresenterWidget<ConfirmationPresenter.MyView>
{
    /**
     * View's interface for confirmation popup.
     * 
     * @author USD/C/PBe
     */
    public interface MyView extends PopupView
    {
        /**
         * Sets a text message.
         * 
         * @param text
         *            text message.
         */
        void setMessage(final String text);

        /**
         * Sets the yes button click handler.
         * 
         * @param clickHandler
         *            yes button click handler.
         */
        void setYesButtonClickHandler(final ClickHandler clickHandler);

        /**
         * Sets the no button click handler.
         * 
         * @param clickHandler
         *            no button click handler.
         */
        void setNoButtonClickHandler(final ClickHandler clickHandler);
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
    public ConfirmationPresenter(final EventBus eventBus, final MyView view)
    {
        super(eventBus, view);
    }

    @Override
    protected final void onBind()
    {
        super.onBind();

        getView().setNoButtonClickHandler(new ClickHandler()
        {
            /**
             * @param event
             */
            public void onClick(final ClickEvent event)
            {
                getView().hide();
            }
        });
    }

    /**
     * Sets the action for the yes button.
     * 
     * @param clickHandler
     *            action for the yes button.
     */
    public final void setYesButtonClickHandler(final ClickHandler clickHandler)
    {
        getView().setYesButtonClickHandler(clickHandler);
    }
    
    /**
     * Sets the text for the confirmation message.
     * 
     * @param text
     *            text for the confirmation message.
     */
    public final void setMessage(final String text)
    {
        getView().setMessage(text);
    }
}
