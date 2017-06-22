package org.eumetsat.usd.gcp.client.presenter;

import org.eumetsat.usd.gcp.client.resources.Resources;

import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.PopupView;
import com.google.inject.Inject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Timer;

/**
 * User Guide popup's presenter.
 * 
 * @author USD/C/PBe
 */
public class UserGuidePresenter extends PresenterWidget<UserGuidePresenter.MyView>
{
    /**
     * View's interface for exported image popup.
     * 
     * @author USD/C/PBe
     */
    public interface MyView extends PopupView
    {
        /**
         * Sets the url to be accessed in the frame.
         * 
         * @param url
         *            url.
         */
        void setFrameUrl(final SafeUri url);
        
        /**
         * Sets the close button click handler.
         * 
         * @param clickHandler
         *            close button click handler.
         */
        void setCloseButtonClickHandler(final ClickHandler clickHandler);
    }

    @Inject
    public UserGuidePresenter(final EventBus eventBus, final MyView view)
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
    
    @Override
    protected void onReveal()
    {
        super.onReveal();
        
        getView().setFrameUrl(Resources.INSTANCE.userGuide().getSafeUri());
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
