/**
 * $Id: CsvDygraph.java 14 2010-08-31 21:46:16Z steven.jardine $ Copyright (c) 2010 Steven Jardine,
 * MJN Services, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.eumetsat.usd.gcp.client.dygraphs;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;

/**
 * A GWT wrapper for the Dygraph implementation. This wrapper handles only a CSV style Dygraph.
 * 
 * @version $Rev: 14 $
 * @author Steven Jardine
 */
public class CsvDygraph extends AbstractDygraph
{
    /** Id count. */
    private static int idCount = 0;

    /** Flag indicating if auto reload is possible. */
    private Boolean canAutoReload = false;

    /** CSV reload URL. */
    private String csvReloadUrl = null;

    /** CSV URL. */
    private String csvUrl = null;

    /** Dygraph JS object. */
    private JavaScriptObject dygraph = null;

    /** Flag indication if graph has been drawn. */
    private boolean hasDrawn = false;

    /** Reload interval. */
    private Integer reloadInterval = null;

    /** Reload timer. */
    private Timer reloadTimer = null;

    /**
     * Default constructor.
     * 
     * @param csvUrl
     *            the url of the csv data.
     * @param options
     *            the graph options.
     * @author USD/C/PBe
     */
    public CsvDygraph(final String csvUrl, final DygraphOptions options)
    {
        super(getNextId(), options);
        this.csvUrl = csvUrl;
    }

    /**
     * Constructor with custom div id.
     * 
     * @param divId
     *            the id of the div which will contain the graph.
     * @param csvUrl
     *            the url of the csv data.
     * @param options
     *            the graph options.
     * @author USD/C/PBe
     */
    public CsvDygraph(final String divId, final String csvUrl, final DygraphOptions options)
    {
        super(divId, options);
        this.csvUrl = csvUrl;
    }

    /**
     * Draw the dygraph.
     * 
     * @param elementId
     *            the elementId of the div to draw the graph.
     * @param csvUrl
     *            the <b>ENCODED</b> url for the dygraph data.
     * @param options
     *            the dygraph options to set.
     * @return the JavaScript object.
     */
    private static native JavaScriptObject drawDygraph(final String elementId, final String csvUrl,
            final JavaScriptObject options)
    /*-{
		var g = new $wnd.Dygraph($wnd.document.getElementById(elementId),
				csvUrl, options);
		g.id = elementId + "-graph";
		return g;
    }-*/;

    /**
     * @return the next id for the csv dygraph.
     */
    private static String getNextId()
    {
        return "csv-dygraph-" + idCount++;
    }

    /**
     * Resize the graphs div.
     * 
     * @param divId
     *            the div containing the graph.
     * @param height
     *            the height to resize to.
     * @param width
     *            the width to resize to.
     */
    public static native void resizeDiv(final String divId, final int height, final int width)
    /*-{
		var div = $wnd.document.getElementById(divId);
		div.style.height = height + "px";
		div.style.width = width + "px";
    }-*/;

    /**
     * Creates a timer that will reload the graph on a set interval.
     */
    private void createReloadTimer()
    {
        if (!hasDrawn)
        {
            return;
        }
        if (canAutoReload && reloadInterval != null)
        {
            Integer interval = getReloadInterval();
            if (interval != null)
            {
                if (reloadTimer != null)
                {
                    reloadTimer.cancel();
                }
                reloadTimer = new Timer()
                {
                    public void run()
                    {
                        String url = getCsvReloadUrl();
                        if (url == null)
                        {
                            // If csvReloadUrl is not set try and load with the
                            // regular csvUrl.
                            // We require a reloadUrl because of a defect in
                            // dygraphs that loads the csv
                            // header as data on a reload.
                            url = csvUrl;
                        }
                        if (url != null)
                        {
                            reload(url);
                        }
                    }
                };
                reloadTimer.scheduleRepeating(interval);
            }
        } else if (reloadTimer != null)
        {
            reloadTimer.cancel();
            reloadTimer = null;
        }
    }

    /**
     * @return the canAutoReload
     */
    public final Boolean getCanAutoReload()
    {
        return canAutoReload;
    }

    /**
     * @return the csvReloadUrl
     */
    public final String getCsvReloadUrl()
    {
        return csvReloadUrl;
    }

    /**
     * @return the reloadInterval
     */
    public final Integer getReloadInterval()
    {
        return reloadInterval;
    }

    /** {@inheritDoc} */
    @Override
    public final void onLoad()
    {
        if (getAutoDraw())
        {
            draw();
        }
    }

    /**
     * Native reload function.
     * 
     * @param graph
     *            the graph javascript object.
     * @param url
     *            the url of the data csv file.
     */
    private native void reload(final JavaScriptObject graph, final String url)
    /*-{
		graph.updateOptions({
			'file' : url
		});
    }-*/;

    /**
     * Native export as PNG function.
     * 
     * @param graph
     *            the graph javascript object.
     * @param imgElementId
     *            the img element id.
     * @author USD/C/PBe
     */
    private native void exportAsPNG(final JavaScriptObject graph, final String imgElementId)
    /*-{
		$wnd.Dygraph.Export.asPNG(graph, $wnd.document
				.getElementById(imgElementId));
    }-*/;

    /**
     * Export as PNG function.
     * 
     * @param imgElementId
     *            the img element id.
     * @author USD/C/PBe
     */
    public final void exportAsPNG(final String imgElementId)
    {
        exportAsPNG(dygraph, imgElementId);
    }

    /**
     * Native update date window function.
     * 
     * @param graph
     *            the graph javascript object.
     * @param window
     *            the date window.
     * @author USD/C/PBe
     */
    private native void updateDateWindow(final JavaScriptObject graph, final JavaScriptObject window)
    /*-{
		graph.updateOptions({
			'dateWindow' : window
		});
    }-*/;

    /**
     * Update date window function.
     * 
     * @param dateWindow
     *            the date window.
     * @author USD/C/PBe
     */
    public final void updateDateWindow(final long[] dateWindow)
    {
        updateDateWindow(dygraph, ArrayUtils.toJsArray(dateWindow));
    }

    /**
     * Native destroy function.
     * 
     * @param graph
     *            the graph javascript object.
     * @author USD/C/PBe
     */
    private native void destroy(final JavaScriptObject graph)
    /*-{
		graph.destroy();
    }-*/;

    /**
     * Destroy function.
     * 
     * @author USD/C/PBe
     */
    public final void destroy()
    {
        destroy(dygraph);
    }

    /**
     * Reload the function.
     * 
     * @param url
     *            the url of the data csv file.
     */
    private void reload(final String url)
    {
        reload(dygraph, url);
    }

    /**
     * Sets the visibility for one of the plots.
     * 
     * @param graph
     *            the graph javascript object.
     * @param index
     *            index of the plot.
     * @param visibility
     *            flag for making the plot visible/invisible.
     * @author USD/C/PBe
     */
    private native void setVisibility(final JavaScriptObject graph, final int index, final boolean visibility)
    /*-{
		graph.setVisibility(index, visibility);
    }-*/;

    /**
     * Sets the visibility for one of the plots.
     * 
     * @param index
     *            index of the plot.
     * @param visibility
     *            flag for making the plot visible/invisible.
     * @author USD/C/PBe
     */
    public final void setVisibility(final int index, final boolean visibility)
    {
        setVisibility(dygraph, index, visibility);
    }

    /**
     * Sets graph title.
     * 
     * @param graph
     *            the graph javascript object.
     * @param title
     *            graph title.
     * @author USD/C/PBe
     */
    private native void setTitle(final JavaScriptObject graph, final String title)
    /*-{
		graph.updateOptions({
			'title' : title
		});
    }-*/;

    /**
     * Sets graph title.
     * 
     * @param title
     *            graph title.
     * @author USD/C/PBe
     */
    public final void setTitle(final String title)
    {
        setTitle(dygraph, title);
    }

    /**
     * Gets graph title.
     * 
     * @param graph
     *            the graph javascript object.
     * @return the graph title.
     * @author USD/C/PBe
     */
    private native String getTitle(final JavaScriptObject graph)
    /*-{
		return graph.getOption('title');
    }-*/;

    /**
     * Gets graph title.
     * 
     * @return the graph title.
     * @author USD/C/PBe
     */
    public final String getTitle()
    {
        return getTitle(dygraph);
    }

    /**
     * Sets the y-axis value range.
     * 
     * @param graph
     *            the graph javascript object.
     * @param minValue
     *            minimum value of the y-axis range.
     * @param maxValue
     *            maximum value of the y-axis range.
     * @author USD/C/PBe
     */
    private native void setYValueRange(final JavaScriptObject graph, final double minValue, final double maxValue)
    /*-{
		graph.updateOptions({
			'valueRange' : [ minValue, maxValue ]
		});
    }-*/;

    /**
     * Sets the y-axis value range.
     * 
     * @param minValue
     *            minimum value of the y-axis range.
     * @param maxValue
     *            maximum value of the y-axis range.
     * @author USD/C/PBe
     */
    public final void setYValueRange(final double minValue, final double maxValue)
    {
        setYValueRange(dygraph, minValue, maxValue);
    }

    /**
     * Sets the y-axis value range to auto.
     * 
     * @param graph
     *            the graph javascript object.
     * @author USD/C/PBe
     */
    private native void setAutoYValueRange(final JavaScriptObject graph)
    /*-{
		graph.updateOptions({
			'valueRange' : null
		});
    }-*/;

    /**
     * Sets the y-axis value range to auto.
     * 
     * @author USD/C/PBe
     */
    public final void setAutoYValueRange()
    {
        setAutoYValueRange(dygraph);
    }

    /**
     * @param canAutoReload
     *            the canAutoReload to set
     */
    public final void setCanAutoReload(final Boolean canAutoReload)
    {
        this.canAutoReload = canAutoReload;
        createReloadTimer();
    }

    /**
     * @param csvReloadUrl
     *            the csvReloadUrl to set
     */
    public final void setCsvReloadUrl(final String csvReloadUrl)
    {
        this.csvReloadUrl = csvReloadUrl;
        createReloadTimer();
    }

    /**
     * @param reloadInterval
     *            the reloadInterval to set
     */
    public final void setReloadInterval(final Integer reloadInterval)
    {
        this.reloadInterval = reloadInterval;
        createReloadTimer();
    }

    /** {@inheritDoc} */
    @Override
    public final void draw()
    {
        dygraph = drawDygraph(getId(), csvUrl, options);
        hasDrawn = true;
        createReloadTimer();
    }

    /**
     * Check if the graph has been drawn.
     * 
     * @return boolean true if it has been drawn.
     */
    public final boolean hasDrawn()
    {
        return hasDrawn;
    }
}
