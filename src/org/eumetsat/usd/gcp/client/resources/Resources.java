// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2012
 */
// @formatter:on
package org.eumetsat.usd.gcp.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * @author USD/C/PBe
 */
public interface Resources extends ClientBundle
{
    /** Unique Instance. */
    Resources INSTANCE = GWT.create(Resources.class);

    /**
     * CSS Style.
     * 
     * @return style.
     */
    @Source("css/PlotterStyle.css")
    PlotterStyle style();

    /**
     * CSS Constants.
     * 
     * @return CSS constants.
     */
    @Source("css/Constants.css")
    Constants constants();

    /**
     * EUMETSAT Logo.
     * 
     * @return EUMETSAT Logo.
     */
    @Source("images/EUMETSAT_logo.jpg")
    ImageResource eumetsatLogo();

    /**
     * GSICS Logo.
     * 
     * @return GSICS Logo.
     */
    @Source("images/GSICS_logo.jpg")
    ImageResource gsicsLogo();

    /**
     * Loading animation GIF.
     * 
     * @return loading animation.
     */
    @Source("images/loading.gif")
    ImageResource loadingAnimation();

    /**
     * hborder PNG.
     * 
     * @return hborder.
     */
    @Source("images/hborder.png")
    DataResource hborder();
    
    /**
     * Right arrow PNG.
     * 
     * @return right arrow.
     */
    @Source("images/right_arrow.png")
    ImageResource rightArrow();

    /**
     * Down arrow PNG.
     * 
     * @return down arrow.
     */
    @Source("images/down_arrow.png")
    ImageResource downArrow();

    /**
     * Javascript implementing SHA2 hashing algorithm.
     * 
     * @return sha2 JS.
     * */
    @Source("js/sha2.js")
    TextResource sha2();

    /**
     * Javascript implementing interactive graph.
     * 
     * @return dygraph-combined JS.
     */
    @Source("js/dygraph-combined.js")
    TextResource dygraphs();

    /**
     * Javascript providing extra functionality to dygraphs.
     * 
     * @return dygraph-extra JS.
     */
    @Source("js/dygraph-extra.js")
    TextResource dygraphsExtra();
    
    /**
     * User Guide.
     * 
     * @return userGuide.
     */
    @Source("pdf/GSICS_Plotting_Tool_UserGuide.pdf")
    DataResource userGuide();

    /**
     * Plotter Style interface.
     * 
     * @author USD/C/PBe
     */
    public interface PlotterStyle extends CssResource
    {
        /**
         * Logo.
         * 
         * @return logo style.
         */
        String logo();

        /**
         * Stack Arrow.
         * 
         * @return stack arrow style.
         */
        String stackArrow();

        /**
         * Header Label.
         * 
         * @return header label style.
         */
        String headerLabel();

        /**
         * Title Label.
         * 
         * @return title label style.
         */
        String titleLabel();

        /**
         * Login.
         * 
         * @return login style.
         */
        String loginPanel();

        /**
         * Config Label.
         * 
         * @return config label style.
         */
        String configLabel();

        /**
         * Value Range.
         * 
         * @return value range style.
         */
        String valueRange();

        /**
         * Config Panel.
         * 
         * @return config panel style.
         */
        String configPanel();

        /**
         * Plot Button.
         * 
         * @return plot button style.
         */
        String plotButtonPanel();

        /**
         * Scene Tb Text Box.
         * 
         * @return scene tb text box style.
         */
        String sceneTbTextBox();

        /**
         * Legend.
         * 
         * @return legend style.
         */
        String legend();
        
        /**
         * text.
         * 
         * @return text style.
         */
        String text();

        /**
         * Popup.
         * 
         * @return popup style.
         */
        String popup();
        
        /**
         * Popup Label.
         * 
         * @return popup label style.
         */
        String popupLabel();

        /**
         * Login Popup.
         * 
         * @return login popup style.
         */
        String loginPopup();

        /**
         * Error Message.
         * 
         * @return error message style.
         */
        String errorMessage();

        /**
         * Error Details.
         * 
         * @return error details style.
         */
        String errorDetailsLabel();

        /**
         * Label.
         * 
         * @return label style.
         */
        String label();

        /**
         * Button.
         * 
         * @return button style.
         */
        String button();
    }

    /**
     * CSS Constants interface.
     * 
     * @author USD/C/PBe
     */
    public interface Constants extends CssResource
    {
        /**
         * Exported Image DIV id.
         * 
         * @return exported image div id.
         */
        String exportedImageDivId();

        /**
         * Legend DIV id.
         * 
         * @return legend div id.
         */
        String legendDivId();
    }
}
