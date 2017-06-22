package org.eumetsat.usd.gcp.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

/**
 * Error popup's presenter.
 * 
 * @author USD/C/PBe
 */
public class ErrorPresenter extends PresenterWidget<ErrorPresenter.MyView>
{
    /**
     * View's interface for error popup.
     * 
     * @author USD/C/PBe
     */
    public interface MyView extends PopupView
    {
        /**
         * Set the message of the error.
         * 
         * @param errorMessage
         *            text to be shown in the message.
         */
        void setErrorMessage(final String errorMessage);

        /**
         * Set the details of the error.
         * 
         * @param errorDetails
         *            text to be shown in the message.
         */
        void setErrorDetails(final String errorDetails);

        /**
         * Sets the ok button click handler.
         * 
         * @param clickHandler
         *            the click handler for ok button.
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
    public ErrorPresenter(final EventBus eventBus, final MyView view)
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
     * Sets message of the error.
     * 
     * @param errorMessage
     *            message of the error.
     */
    public final void setErrorMessage(final String errorMessage)
    {
        getView().setErrorMessage(errorMessage);
    }

    /**
     * Sets details of the error.
     * 
     * @param errorDetails
     *            details of the error.
     */
    public final void setErrorDetails(final String errorDetails)
    {
        getView().setErrorDetails(errorDetails);
    }
}
