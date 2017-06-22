package org.eumetsat.usd.gcp.client.presenter;

import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Loading Animation's Presenter.
 * 
 * @author USD/C/PBe
 */
public class LoadingPresenter extends PresenterWidget<LoadingPresenter.MyView>
{
    /**
     * View's interface.
     * 
     * @author USD/C/PBe
     */
    public interface MyView extends View
    {
    }

    /**
     * Constructor.
     * 
     * @param eventBus
     *            event bus.
     * @param view
     *            view.
     */
    @Inject
    public LoadingPresenter(final EventBus eventBus, final MyView view)
    {
        super(eventBus, view);
    }

    @Override
    protected final void onBind()
    {
        super.onBind();
    }
}
