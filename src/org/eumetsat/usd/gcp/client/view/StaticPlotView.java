package org.eumetsat.usd.gcp.client.view;

import org.eumetsat.usd.gcp.client.presenter.LoadingPresenter;
import org.eumetsat.usd.gcp.client.presenter.StaticPlotPresenter;
import org.eumetsat.usd.gcp.client.resources.Resources;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * Static plot's view.
 * 
 * @author USD/C/PBe
 */
public class StaticPlotView extends ViewImpl implements StaticPlotPresenter.MyView
{
    /** Graph Panel. */
    @UiField
    AbsolutePanel graphPanel;
    /** Legend Panel. */
    @UiField
    HTMLPanel legendPanel;
    /** Loading Animation. */
    @Inject
    private LoadingPresenter loadingAnimation;
    
    /** Widget. */
    private final Widget widget;

    /**
     * Binder.
     */
    public interface Binder extends UiBinder<Widget, StaticPlotView>
    {
    }

    /**
     * Constructor.
     * 
     * @param binder
     *            binder.
     * @param resources
     *            image, css, ... resources.
     */
    @Inject
    public StaticPlotView(final Binder binder, final Resources resources)
    {
        resources.style().ensureInjected();
        widget = binder.createAndBindUi(this);
    }

    @Override
    public final Widget asWidget()
    {
        return widget;
    }
    
    @Override
    public final void showLoadingAnimation()
    {
        // put up loading animation.
        int left = (Window.getClientWidth() - loadingAnimation.asWidget().getOffsetWidth()) >> 1;
        int top = (Window.getClientHeight() - loadingAnimation.asWidget().getOffsetHeight()) >> 1;

        graphPanel.add(loadingAnimation.asWidget(), Math.max(left, 0), Math.max(top, 0));
    }
    
    @Override
    public final void hideLoadingAnimation()
    {
        graphPanel.remove(loadingAnimation.asWidget());
    }

    @Override
    public final AbsolutePanel getGraphPanel()
    {
        return graphPanel;
    }

    @Override
    public final HTMLPanel getLegendPanel()
    {
        return legendPanel;
    }
}
