package org.eumetsat.usd.gcp.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.eumetsat.usd.gcp.client.action.CustomAsyncCallback;
import org.eumetsat.usd.gcp.client.dygraphs.ArrayUtils;
import org.eumetsat.usd.gcp.client.dygraphs.DygraphOptions;
import org.eumetsat.usd.gcp.client.dygraphs.CsvDygraph;
import org.eumetsat.usd.gcp.client.dygraphs.Dygraphs;
import org.eumetsat.usd.gcp.client.place.NameTokens;
import org.eumetsat.usd.gcp.client.resources.Resources;
import org.eumetsat.usd.gcp.client.util.ColorUtils;
import org.eumetsat.usd.gcp.shared.action.GenerateClientID;
import org.eumetsat.usd.gcp.shared.action.GenerateClientIDResult;
import org.eumetsat.usd.gcp.shared.action.GetDateWindow;
import org.eumetsat.usd.gcp.shared.action.GetDateWindowResult;
import org.eumetsat.usd.gcp.shared.action.PrepareMultipleData;
import org.eumetsat.usd.gcp.shared.action.PrepareMultipleDataResult;
import org.eumetsat.usd.gcp.shared.conf.PlotConfiguration;
import org.eumetsat.usd.gcp.shared.conf.PlotConfiguration.Names;
import org.eumetsat.usd.gcp.shared.exception.InvalidFilenameException;
import org.eumetsat.usd.gcp.shared.util.Constants;

import com.google.common.collect.Range;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.rpc.shared.DispatchAsync;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;

/**
 * Presenter for a static plot (non-interactive plot).
 * 
 * @author USD/C/PBe
 */
public class StaticPlotPresenter extends Presenter<StaticPlotPresenter.MyView, StaticPlotPresenter.MyProxy>
{
    /** Asynchronous Dispatcher. */
    @Inject
    DispatchAsync dispatcher;

    /** Message Popup. */
    @Inject
    MessagePresenter messagePopup;

    /** Error Popup. */
    @Inject
    ErrorPresenter errorPopup;

    /** Event Bus. */
    private final EventBus eventBus;

    /** Graph. */
    private CsvDygraph graph;

    /**
     * View's interface for a static plot (non-interactive plot).
     * 
     * @author USD/C/PBe
     */
    public interface MyView extends View
    {
        /**
         * Show loading animation.
         */
        void showLoadingAnimation();

        /**
         * Hide loading animation.
         */
        void hideLoadingAnimation();

        /**
         * Get Graph Panel.
         * 
         * @return graph panel.
         */
        AbsolutePanel getGraphPanel();

        /**
         * Get Legend Panel.
         * 
         * @return legend panel.
         */
        HTMLPanel getLegendPanel();
    }

    /**
     * Proxy.
     * 
     * @author USD/C/PBe
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.STATIC_PLOT)
    public interface MyProxy extends ProxyPlace<StaticPlotPresenter>
    {
    }

    /**
     * Constructor.
     * 
     * @param eventBus
     *            event bus.
     * @param view
     *            corresponding view.
     * @param proxy
     *            proxy.
     */
    @Inject
    public StaticPlotPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy)
    {
        super(eventBus, view, proxy);

        this.eventBus = eventBus;
    }

    @Override
    protected final void revealInParent()
    {
        RevealRootLayoutContentEvent.fire(this, this);
    }

    @Override
    protected final void onBind()
    {
        super.onBind();
    }

    @Override
    protected final void onReset()
    {
        super.onReset();

        // Show loading animation.
        getView().showLoadingAnimation();

        dispatcher.execute(new GenerateClientID(), new CustomAsyncCallback<GenerateClientIDResult>(eventBus)
        {
            @Override
            public void onSuccess(final GenerateClientIDResult resultClientID)
            {
                ArrayList<PlotConfiguration> plotConfigsFromUrl = new ArrayList<PlotConfiguration>();

                // Get plot configuration from dataset URL, if defined.
                PlotConfiguration plotConfigFromDatasetUrl = null;
                try
                {
                    plotConfigFromDatasetUrl = PlotConfiguration.fromDatasetURL();

                } catch (InvalidFilenameException ife)
                {
                    errorPopup.setErrorMessage("Entered dataset URL is invalid.");
                    errorPopup.setErrorDetails(ife.getMessage());
                    addToPopupSlot(errorPopup);
                }

                if (plotConfigFromDatasetUrl != null)
                {
                    plotConfigsFromUrl.add(plotConfigFromDatasetUrl);

                } else
                {
                    // Get parameters from URL.
                    plotConfigsFromUrl = PlotConfiguration.fromURL();
                }

                // Plot them, if any (only once).
                if (!plotConfigsFromUrl.isEmpty())
                {
                    // Request server to prepare data to be plotted.
                    dispatcher.execute(new PrepareMultipleData(resultClientID.getUserID(), plotConfigsFromUrl),
                            new CustomAsyncCallback<PrepareMultipleDataResult>(eventBus)
                            {
                                @Override
                                public void onSuccess(final PrepareMultipleDataResult resultPrepareMultipleData)
                                {
                                    dispatcher.execute(new GetDateWindow(resultClientID.getUserID()),
                                            new CustomAsyncCallback<GetDateWindowResult>(eventBus)
                                            {
                                                @Override
                                                public void onSuccess(GetDateWindowResult resultGetDateWindow)
                                                {
                                                    // Hide loading animation.
                                                    getView().hideLoadingAnimation();

                                                    assembleGraphPanel(
                                                            GWT.getHostPageBaseURL() + Constants.CSV_FILE_PATH + "-"
                                                                    + resultPrepareMultipleData.getUserID() + ".csv",
                                                            resultPrepareMultipleData.getDatasetsNames(),
                                                            resultGetDateWindow.getDateWindow());

                                                    Timer timer = new Timer()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            if (graph != null && graph.hasDrawn())
                                                            {
                                                                getView().getGraphPanel().clear();
                                                                getView().getLegendPanel().removeFromParent();
                                                                getView().getGraphPanel().add(
                                                                        new HTMLPanel(
                                                                                "<img id=\""
                                                                                        + Resources.INSTANCE.constants().exportedImageDivId()
                                                                                        + "\" class=\"bg\"/>"));
                                                                graph.exportAsPNG(Resources.INSTANCE.constants().exportedImageDivId());

                                                                this.cancel();
                                                            }
                                                        }
                                                    };

                                                    timer.scheduleRepeating(100);
                                                }

                                                @Override
                                                public void onDisconnected()
                                                {
                                                    // Hide loading animation.
                                                    getView().hideLoadingAnimation();

                                                    errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                                                    addToPopupSlot(errorPopup);
                                                }

                                                @Override
                                                public void onCatalogNotReachable()
                                                {
                                                    // Hide loading animation.
                                                    getView().hideLoadingAnimation();

                                                    errorPopup.setErrorMessage("No connection to the GSICS catalogs.");
                                                    addToPopupSlot(errorPopup);
                                                }

                                                @Override
                                                public void onException(Throwable caught)
                                                {
                                                    // Hide loading animation.
                                                    getView().hideLoadingAnimation();

                                                    errorPopup.setErrorMessage("An error has occurred while getting the date window of the "
                                                            + "datasets to be plotted. "
                                                            + "Please contact the administrator of this tool.");
                                                    errorPopup.setErrorDetails(caught.getMessage());
                                                    addToPopupSlot(errorPopup);
                                                }
                                            });

                                }

                                @Override
                                public void onDisconnected()
                                {
                                    // Hide loading animation.
                                    getView().hideLoadingAnimation();

                                    errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                                    addToPopupSlot(errorPopup);
                                }

                                @Override
                                public void onCatalogNotReachable()
                                {
                                    // Hide loading animation.
                                    getView().hideLoadingAnimation();

                                    errorPopup.setErrorMessage("No connection to the GSICS catalogs.");
                                    addToPopupSlot(errorPopup);
                                }

                                @Override
                                public void onException(Throwable caught)
                                {
                                    // Hide loading animation.
                                    getView().hideLoadingAnimation();

                                    errorPopup.setErrorMessage("An error has occurred while preparing the "
                                            + "datasets to be plotted. "
                                            + "Please contact the administrator of this tool.");
                                    errorPopup.setErrorDetails(caught.getMessage());
                                    addToPopupSlot(errorPopup);
                                }
                            });
                }
            }

            @Override
            public void onDisconnected()
            {
                errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                addToPopupSlot(errorPopup);
            }

            @Override
            public void onCatalogNotReachable()
            {
                // Hide loading animation.
                getView().hideLoadingAnimation();

                errorPopup.setErrorMessage("No connection to the GSICS catalogs.");
                addToPopupSlot(errorPopup);
            }

            @Override
            public void onException(Throwable caught)
            {
                errorPopup.setErrorMessage("An error has occurred while generating the user ID. "
                        + "Please contact the administrator of this tool.");
                errorPopup.setErrorDetails(caught.getMessage());
                addToPopupSlot(errorPopup);
            }
        });

    }

    /**
     * Assemble the graph panel.
     * 
     * @param csvFilename
     *            the filename of the CSV file with the data to be plotted.
     * @param datasetsNames
     *            the names of the datasets to be plotted.
     */
    private void assembleGraphPanel(final String csvFilename, final List<String> datasetsNames,
            final Range<Date> dateWindow)
    {
        // Install the dygraphs code.
        Dygraphs.install();

        // Set up options for the dygraph.
        DygraphOptions options = DygraphOptions.create();
        options.set("labelsSeparateLines", true);
        options.set("labelsDiv", "legend");
        options.set("hideOverlayOnMouseOut", false);
        options.set("xlabel", "Date [UTC]");
        options.set("ylabel", "Brightness Temperature Bias [K]");
        options.set("axisLabelFontSize", 12);
        options.set("errorBars", true);
        options.set("drawPoints", false);
        options.set("includeZero", true);
        options.set("highlightCircleSize", 0);
        options.set("strokeWidth", 0.1);
        options.set("sigma", 1.0);
        options.set("showRangeSelector", true);

        // Set up date window if user provides period.
        String periodYears = com.google.gwt.user.client.Window.Location.getParameter(Names.PERIOD_URL_PARAM);

        if (periodYears != null && dateWindow != null)
        {
            Date startDate = dateWindow.lowerEndpoint();
            Date endDate = dateWindow.upperEndpoint();

            Date startPeriodDate = CalendarUtil.copyDate(endDate);
            CalendarUtil.addMonthsToDate(startPeriodDate, (int) (-12 * Double.valueOf(periodYears)));

            if (startDate.before(startPeriodDate))
            {
                long[] dateWindowInMillis = new long[2];
                dateWindowInMillis[0] = startPeriodDate.getTime();
                dateWindowInMillis[1] = endDate.getTime();

                options.set("dateWindow", ArrayUtils.toJsArray(dateWindowInMillis));
            }
        }

        // Apply thicker lines to "Bias" plots, to highlight them over the min-max plots.
        DygraphOptions suboptions = DygraphOptions.create();
        suboptions.set("strokeWidth", 2.0);
        suboptions.set("highlightCircleSize", 3);

        for (String datasetName : datasetsNames)
        {
            options.set(datasetName, suboptions);
        }

        // generate the needed number of random colors for all the datasets to be plotted.
        Vector<String> colors = new Vector<String>();

        if (datasetsNames.size() * 2 > colors.size()) // "*2" cause every dataset needs 2 plots:
                                                      // bias, and
                                                      // uncertainty.
        {
            colors = ColorUtils.createPlotColors(datasetsNames.size());
        }

        options.set("colors", ArrayUtils.toJsArray(colors.toArray()));

        // Create (or recreate) the graph.
        if (graph != null)
        {
            // Removes the graph.
            getView().getGraphPanel().remove(graph);

            // Destroy the graph javascript object, avoiding memory leaks.
            graph.destroy();
        }

        graph = new CsvDygraph("div_graph", csvFilename, options);

        getView().getGraphPanel().add(graph);
    }
}
