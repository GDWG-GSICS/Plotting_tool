package org.eumetsat.usd.gcp.client.view;

import org.eumetsat.usd.gcp.client.presenter.LoadingPresenter;

import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Loading Animation's View.
 * 
 * @author USD/C/PBe
 */
public class LoadingView extends ViewImpl implements LoadingPresenter.MyView
{
    /** Widget. */
    private final Widget widget;

    /**
     * Binder.
     */
    public interface Binder extends UiBinder<Widget, LoadingView>
    {
    }

    /**
     * Constructor.
     * 
     * @param binder
     *            UI binder.
     */
    @Inject
    public LoadingView(final Binder binder)
    {
        widget = binder.createAndBindUi(this);
    }

    @Override
    public final Widget asWidget()
    {
        return widget;
    }
}
