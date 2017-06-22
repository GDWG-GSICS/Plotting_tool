package org.eumetsat.usd.gcp.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

/**
 * Exported image popup's presenter.
 * 
 * @author USC/C/PBe
 */
public class ExportedImagePresenter extends PresenterWidget<ExportedImagePresenter.MyView>
{
    /**
     * View's interface for exported image popup.
     * 
     * @author USC/C/PBe
     */
    public interface MyView extends PopupView
    {
        /**
         * Sets the close button click handler.
         * 
         * @param clickHandler
         *            close button click handler.
         */
        void setCloseButtonClickHandler(final ClickHandler clickHandler);
    }

    /**
     * Constructor.
     * 
     * @param eventBus
     *            event bus
     * @param view
     *            corresponding view.
     */
    @Inject
    public ExportedImagePresenter(final EventBus eventBus, final MyView view)
    {
        super(eventBus, view);
    }

    @Override
    protected final void onBind()
    {
        super.onBind();

        getView().hide();

        getView().setCloseButtonClickHandler(new ClickHandler()
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
     * Center the image pop-up.
     * 
     * @param delayInMillis
     *            delay in milliseconds.
     */
    public final void centerWithDelay(final int delayInMillis)
    {
        Timer timer = new Timer()
        {
            @Override
            public void run()
            {
                getView().showAndReposition();
            }
        };

        timer.schedule(delayInMillis);
    }
}
