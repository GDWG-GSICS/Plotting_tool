package org.eumetsat.usd.gcp.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eumetsat.usd.gcp.client.action.CustomAsyncCallback;
import org.eumetsat.usd.gcp.client.action.GotUserLoggedIn;
import org.eumetsat.usd.gcp.client.dygraphs.ArrayUtils;
import org.eumetsat.usd.gcp.client.dygraphs.CsvDygraph;
import org.eumetsat.usd.gcp.client.dygraphs.DygraphOptions;
import org.eumetsat.usd.gcp.client.dygraphs.Dygraphs;
import org.eumetsat.usd.gcp.client.event.LoginAuthenticatedEvent;
import org.eumetsat.usd.gcp.client.event.LoginAuthenticatedHandler;
import org.eumetsat.usd.gcp.client.place.NameTokens;
import org.eumetsat.usd.gcp.client.resources.Resources;
import org.eumetsat.usd.gcp.client.util.ColorUtils;
import org.eumetsat.usd.gcp.client.util.StringUtils;
import org.eumetsat.usd.gcp.client.view.PlotView;
import org.eumetsat.usd.gcp.client.view.PlotView.MenuOption;
import org.eumetsat.usd.gcp.shared.action.ClearData;
import org.eumetsat.usd.gcp.shared.action.ClearDataResult;
import org.eumetsat.usd.gcp.shared.action.ClearSavedPlotConfigs;
import org.eumetsat.usd.gcp.shared.action.ClearSavedPlotConfigsResult;
import org.eumetsat.usd.gcp.shared.action.GenerateClientID;
import org.eumetsat.usd.gcp.shared.action.GenerateClientIDResult;
import org.eumetsat.usd.gcp.shared.action.GetChannels;
import org.eumetsat.usd.gcp.shared.action.GetChannelsResult;
import org.eumetsat.usd.gcp.shared.action.GetDateTimes;
import org.eumetsat.usd.gcp.shared.action.GetDateTimesResult;
import org.eumetsat.usd.gcp.shared.action.GetHelpItems;
import org.eumetsat.usd.gcp.shared.action.GetHelpItemsResult;
import org.eumetsat.usd.gcp.shared.action.GetImplementedCatalogs;
import org.eumetsat.usd.gcp.shared.action.GetImplementedCatalogsResult;
import org.eumetsat.usd.gcp.shared.action.GetModes;
import org.eumetsat.usd.gcp.shared.action.GetModesResult;
import org.eumetsat.usd.gcp.shared.action.GetRefSatInstrs;
import org.eumetsat.usd.gcp.shared.action.GetRefSatInstrsResult;
import org.eumetsat.usd.gcp.shared.action.GetSatInstrs;
import org.eumetsat.usd.gcp.shared.action.GetSatInstrsResult;
import org.eumetsat.usd.gcp.shared.action.GetSavedPlotConfigs;
import org.eumetsat.usd.gcp.shared.action.GetSavedPlotConfigsResult;
import org.eumetsat.usd.gcp.shared.action.GetGPRCs;
import org.eumetsat.usd.gcp.shared.action.GetGPRCsResult;
import org.eumetsat.usd.gcp.shared.action.GetStdSceneTb;
import org.eumetsat.usd.gcp.shared.action.GetStdSceneTbResult;
import org.eumetsat.usd.gcp.shared.action.GetCorrTypes;
import org.eumetsat.usd.gcp.shared.action.GetCorrTypesResult;
import org.eumetsat.usd.gcp.shared.action.GetUserLoggedIn;
import org.eumetsat.usd.gcp.shared.action.GetVersions;
import org.eumetsat.usd.gcp.shared.action.GetVersionsResult;
import org.eumetsat.usd.gcp.shared.action.GetYears;
import org.eumetsat.usd.gcp.shared.action.GetYearsResult;
import org.eumetsat.usd.gcp.shared.action.Logout;
import org.eumetsat.usd.gcp.shared.action.LogoutResult;
import org.eumetsat.usd.gcp.shared.action.PrepareData;
import org.eumetsat.usd.gcp.shared.action.PrepareDataResult;
import org.eumetsat.usd.gcp.shared.action.PrepareMultipleData;
import org.eumetsat.usd.gcp.shared.action.PrepareMultipleDataResult;
import org.eumetsat.usd.gcp.shared.action.RemoveSavedPlotConfig;
import org.eumetsat.usd.gcp.shared.action.RemoveSavedPlotConfigResult;
import org.eumetsat.usd.gcp.shared.action.SavePlotConfig;
import org.eumetsat.usd.gcp.shared.action.SavePlotConfigResult;
import org.eumetsat.usd.gcp.shared.conf.Defaults;
import org.eumetsat.usd.gcp.shared.conf.HelpItem;
import org.eumetsat.usd.gcp.shared.conf.PlotConfiguration;
import org.eumetsat.usd.gcp.shared.exception.InvalidFilenameException;
import org.eumetsat.usd.gcp.shared.util.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.rpc.shared.DispatchAsync;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;

/**
 * Main presenter of plotter application (interactive plot).
 * 
 * @author USC/C/PBe
 */
public class PlotPresenter extends Presenter<PlotPresenter.MyView, PlotPresenter.MyProxy>
{
    /** Asynchronous dispatcher. */
    @Inject
    DispatchAsync dispatcher;
    /** Message popup. */
    @Inject
    MessagePresenter messagePopup;
    /** Error popup. */
    @Inject
    ErrorPresenter errorPopup;
    /** Exported image popup. */
    @Inject
    ExportedImagePresenter exportedImagePopup;
    /** Confirmation popup. */
    @Inject
    ConfirmationPresenter confirmationPopup;
    /** Login popup. */
    @Inject
    LoginPresenter loginPopup;
    /** Register popup. */
    @Inject
    RegisterPresenter registerPopup;
    /** User Guide popup. */
    @Inject
    UserGuidePresenter userGuidePopup;

    /** Event Bus. */
    private final EventBus eventBus;

    /** Historian. */
    private final Historian historian;

    /** Client ID for the catalog service. */
    private String clientID;

    /** Authenticated user (empty string if none). */
    private String authUser = "";

    /** Graph. */
    private CsvDygraph graph;

    /** Colors for the plots. */
    private Vector<String> colors = new Vector<String>();

    /** Mapping of the saved plot configurations with its IDs. */
    private Map<String, List<PlotConfiguration>> savedPlotConfigsWithID = new LinkedHashMap<String, List<PlotConfiguration>>();

    /** Plotted configurations. */
    private ArrayList<PlotConfiguration> plottedConfigurations = new ArrayList<PlotConfiguration>();

    /** Flag saying if there is something being plotted. */
    private boolean thereIsPlot = false;

    /** Flag to force that plot from URL happens only once. */
    private boolean plottedFromUrl = false;

    /** Visibility check boxes for the plots. */
    private LinkedHashMap<String, CheckBox> visibilityCheckBoxes = new LinkedHashMap<String, CheckBox>();

    /** Default plot configuration 1. */
    private final PlotConfiguration defaultPlotConfig1 = new PlotConfiguration(
            "[EUMETSAT] Demo of MSG1 SEVIRI with MetOpA IASI", Defaults.DEFAULT_SERVER, Defaults.DEFAULT_GPRC,
            Defaults.DEFAULT_CORR_TYPE, "MSG1 SEVIRI", Defaults.DEFAULT_REF_SAT_INSTR, "demo", "2008", null, null,
            "All", -1);

    /** Default plot configuration 2. */
    private final PlotConfiguration defaultPlotConfig2 = new PlotConfiguration(
            "[EUMETSAT] Demo of MSG2 SEVIRI with MetOpA IASI", Defaults.DEFAULT_SERVER, Defaults.DEFAULT_GPRC,
            Defaults.DEFAULT_CORR_TYPE, "MSG2 SEVIRI", Defaults.DEFAULT_REF_SAT_INSTR, "demo", "2008", null, null,
            "All", -1);

    /** Default plot configuration 3. */
    private final PlotConfiguration defaultPlotConfig3 = new PlotConfiguration(
            "[EUMETSAT] Demo of MSG1 SEVIRI IR087 250K with MetOpA IASI", Defaults.DEFAULT_SERVER,
            Defaults.DEFAULT_GPRC, Defaults.DEFAULT_CORR_TYPE, "MSG2 SEVIRI", Defaults.DEFAULT_REF_SAT_INSTR, "demo",
            "2008", null, null, "IR087", 250);

    /**
     * Interface to access the view.
     * 
     * @author USD/C/PBe
     */
    public interface MyView extends com.gwtplatform.mvp.client.View
    {
        // ------------------------------------------------------
        // Configuration parameters change handlers.
        // ------------------------------------------------------

        /**
         * Set handler for server change.
         * 
         * @param changeHandler
         *            handler for server change.
         */
        void setServerChangeHandler(final ChangeHandler changeHandler);

        /**
         * Set handler for GPRC change.
         * 
         * @param changeHandler
         *            handler for source change.
         */
        void setGPRCChangeHandler(final ChangeHandler changeHandler);

        /**
         * Set handler for type change.
         * 
         * @param changeHandler
         *            handler for type change.
         */
        void setCorrTypeChangeHandler(final ChangeHandler changeHandler);

        /**
         * Set handler for sat/instr change.
         * 
         * @param changeHandler
         *            handler for sat/instr change.
         */
        void setSatInstrChangeHandler(final ChangeHandler changeHandler);

        /**
         * Set handler for ref sat/instr handler.
         * 
         * @param changeHandler
         *            handler for ref sat/instr change.
         */
        void setRefSatInstrChangeHandler(final ChangeHandler changeHandler);

        /**
         * Set handler for mode change.
         * 
         * @param changeHandler
         *            handler for mode change.
         */
        void setModeChangeHandler(final ChangeHandler changeHandler);

        /**
         * Set handler for year change.
         * 
         * @param changeHandler
         *            handler for year change.
         */
        void setYearChangeHandler(final ChangeHandler changeHandler);

        /**
         * Set handler for date-time change.
         * 
         * @param changeHandler
         *            handler for date-time change.
         */
        void setDateTimeChangeHandler(final ChangeHandler changeHandler);

        /**
         * Set handler for version change.
         * 
         * @param changeHandler
         *            handler for version change.
         */
        void setVersionChangeHandler(final ChangeHandler changeHandler);

        /**
         * Set handler for channel change.
         * 
         * @param changeHandler
         *            handler for channel change.
         */
        void setChannelChangeHandler(final ChangeHandler changeHandler);

        /** Click handlers. */

        /**
         * Set handler for plot clear.
         * 
         * @param clearPlotHandler
         *            handler for plot clear.
         */
        void setClearPlotHandler(final ClickHandler clearPlotHandler);

        /**
         * Set handler for plot.
         * 
         * @param plotHandler
         *            handler for plot.
         */
        void setPlotHandler(final ClickHandler plotHandler);

        /**
         * Set handler for plot a saved configuration.
         * 
         * @param plotSavedHandler
         *            handler for plot a saved configuration.
         */
        void setPlotSavedHandler(final ClickHandler plotSavedHandler);

        /**
         * Set handler for remove a selected saved configuration.
         * 
         * @param removeSelectedHandler
         *            handler for remove a selected saved configuration.
         */
        void setRemoveSelectedHandler(final ClickHandler removeSelectedHandler);

        /**
         * Set handler for remove all saved configurations.
         * 
         * @param removeAllHandler
         *            handler for remove all saved configurations.
         */
        void setRemoveAllHandler(final ClickHandler removeAllHandler);

        /**
         * Set handler for plot save.
         * 
         * @param savePlotHandler
         *            handler for plot save.
         */
        void setSavePlotHandler(final ClickHandler savePlotHandler);

        /**
         * Set handler for plot export.
         * 
         * @param exportHandler
         *            handler for plot export.
         */
        void setExportHandler(final ClickHandler exportHandler);

        /**
         * Set handler for plot access url.
         * 
         * @param urlAccessHandler
         *            handler for plot export.
         */
        void setUrlAccessHandler(final ClickHandler urlAccessHandler);

        /**
         * Set handler for static plot access url.
         * 
         * @param urlStaticAccessHandler
         *            handler for plot export.
         */
        void setUrlStaticAccessHandler(final ClickHandler urlStaticAccessHandler);

        /**
         * Set handler for login.
         * 
         * @param loginHandler
         *            handler for login.
         */
        void setLoginHandler(final ClickHandler loginHandler);

        /**
         * Set handler for logout.
         * 
         * @param logoutHandler
         *            handler for logout.
         */
        void setLogoutHandler(final ClickHandler logoutHandler);

        /**
         * Set handler for register.
         * 
         * @param registerHandler
         *            handler for register.
         */
        void setRegisterHandler(final ClickHandler registerHandler);

        /**
         * Set handler for set auto value range.
         * 
         * @param autoValueRangeHandler
         *            handler for auto value range.
         */
        void setAutoValueRangeHandler(final ClickHandler autoValueRangeHandler);

        /**
         * Set handler for change of value range.
         * 
         * @param valueRangeChangeHandler
         *            handler for value range change.
         */
        void setValueRangeChangeHandler(final ChangeHandler valueRangeChangeHandler);

        /**
         * Set handler for a key up on any of value range text boxes.
         * 
         * @param valueRangeKeyUpHandler
         *            handler for a key up on any of value range text boxes.
         */
        void setValueRangeKeyUpHandler(final KeyUpHandler valueRangeKeyUpHandler);

        /**
         * Set handler for help.
         * 
         * @param helpHandler
         *            handler for help.
         */
        void setHelpHandler(final ClickHandler helpHandler);

        // ------------------------------------------------------
        // Clear lists and sets "Loading...".
        // ------------------------------------------------------

        /**
         * Clear server list and set it to "Loading...".
         */
        void setServerListToLoading();

        /**
         * Clear GPRC list and set it to "Loading...".
         */
        void setGPRCListToLoading();

        /**
         * Clear correction type list and set it to "Loading...".
         */
        void setCorrTypeListToLoading();

        /**
         * Clear sat/intsr list and set it to "Loading...".
         */
        void setSatInstrListToLoading();

        /**
         * Clear ref sat/instr list and set it to "Loading...".
         */
        void setRefSatInstrListToLoading();

        /**
         * Clear mode list and set it to "Loading...".
         */
        void setModeListToLoading();

        /**
         * Clear year list and set it to "Loading...".
         */
        void setYearListToLoading();

        /**
         * Clear date-time list and set it to "Loading...".
         */
        void setDateTimeListToLoading();

        /**
         * Clear version list and set it to "Loading...".
         */
        void setVersionListToLoading();

        /**
         * Clear channel list and set it to "Loading...".
         */
        void setChannelListToLoading();

        // ------------------------------------------------------
        // Clear lists and sets "No data".
        // ------------------------------------------------------

        /**
         * Clear server list and set it to "No data".
         */
        void setServerListToNoData();

        /**
         * Clear GPRC list and set it to "No data".
         */
        void setGPRCListToNoData();

        /**
         * Clear correction type list and set it to "No data".
         */
        void setCorrTypeListToNoData();

        /**
         * Clear sat/instr list and set it to "No data".
         */
        void setSatInstrListToNoData();

        /**
         * Clear ref sat/instr list and set it to "No data".
         */
        void setRefSatInstrListToNoData();

        /**
         * Clear mode list and set it to "No data".
         */
        void setModeListToNoData();

        /**
         * Clear year list and set it to "No data".
         */
        void setYearListToNoData();

        /**
         * Clear date-time list and set it to "No data".
         */
        void setDateTimeListToNoData();

        /**
         * Clear version list and set it to "No data".
         */
        void setVersionListToNoData();

        /**
         * Clear channel list and set it to "No data".
         */
        void setChannelListToNoData();

        // ------------------------------------------------------
        // Add option for a configuration parameter.
        // ------------------------------------------------------

        /**
         * Add server to configuration options.
         * 
         * @param server
         *            server to be added.
         */
        void addServer(final String server);

        /**
         * Add GPRC to configuration options.
         * 
         * @param gprc
         *            GPRC to be added.
         */
        void addGPRC(final String gprc);

        /**
         * Add correction type to configuration options.
         * 
         * @param corrType
         *            correction type to be added.
         */
        void addCorrType(final String corrType);

        /**
         * Add sat/instr to configuration options.
         * 
         * @param satInstr
         *            sat/instr to be added.
         */
        void addSatInstr(final String satInstr);

        /**
         * Add ref sat/instr to configuration options.
         * 
         * @param refSatInstr
         *            ref sat/instr to be added.
         */
        void addRefSatInstr(final String refSatInstr);

        /**
         * Add mode to configuration options.
         * 
         * @param mode
         *            mode to be added.
         */
        void addMode(final String mode);

        /**
         * Add year to configuration options.
         * 
         * @param year
         *            year to be added.
         */
        void addYear(final String year);

        /**
         * Add date-time to configuration options.
         * 
         * @param dateTime
         *            date-time to be added.
         */
        void addDateTime(final String dateTime);

        /**
         * Add version to configuration options.
         * 
         * @param version
         *            version to be added.
         */
        void addVersion(final String version);

        /**
         * Add channel to configuration options.
         * 
         * @param channel
         *            channel to be added.
         */
        void addChannel(final String channel);

        /**
         * Add saved plot configuration to configuration options.
         * 
         * @param savedPlot
         *            plot configuration to be added.
         */
        void addSavedPlotConfig(final String savedPlot);

        // ------------------------------------------------------
        // Set Scene Tb from dataset.
        // ------------------------------------------------------

        /**
         * Set selected scene Tb from datasets.
         * 
         * @param sceneTb
         *            scene Tb from datasets.
         */
        void setSelectedSceneTb(final double sceneTb);

        // ------------------------------------------------------
        // Clear configuration lists.
        // ------------------------------------------------------

        /**
         * Clear server list.
         */
        void clearServerList();

        /**
         * Clear GPRC list.
         */
        void clearGPRCList();

        /**
         * Clear correction type list.
         */
        void clearCorrTypeList();

        /**
         * Clear sat/instr list.
         */
        void clearSatInstrList();

        /**
         * Clear ref sat/instr list.
         */
        void clearRefSatInstrList();

        /**
         * Clear mode list.
         */
        void clearModeList();

        /**
         * Clear year list.
         */
        void clearYearList();

        /**
         * Clear date-time list.
         */
        void clearDateTimeList();

        /**
         * Clear version list.
         */
        void clearVersionList();

        /**
         * Clear channel list.
         */
        void clearChannelList();

        /**
         * Remove selected saved plot configuration from the list.
         */
        void removeSelectedSavedPlotConfig();

        /**
         * Clear saved plot configurations list.
         */
        void clearSavedPlotConfigsList();

        /**
         * Clear saved plot name text box.
         */
        void clearSavedPlotNameTextBox();

        /**
         * Clear value range text boxes.
         */
        void clearValueRangeTextBoxes();

        // ------------------------------------------------------
        // Simulate user selection of a configuration parameter.
        // ------------------------------------------------------

        /**
         * Select a server.
         * 
         * @param indexToSelect
         *            index to be selected.
         */
        void selectServer(final int indexToSelect);

        /**
         * Select a GPRC.
         * 
         * @param indexToSelect
         *            index to be selected.
         */
        void selectGPRC(final int indexToSelect);

        /**
         * Select a correction type.
         * 
         * @param indexToSelect
         *            index to be selected.
         */
        void selectCorrType(final int indexToSelect);

        /**
         * Select a sat/instr.
         * 
         * @param indexToSelect
         *            index to be selected.
         */
        void selectSatInstr(final int indexToSelect);

        /**
         * Select a ref sat/instr.
         * 
         * @param indexToSelect
         *            index to be selected.
         */
        void selectRefSatInstr(final int indexToSelect);

        /**
         * Select a mode.
         * 
         * @param indexToSelect
         *            index to be selected.
         */
        void selectMode(final int indexToSelect);

        /**
         * Select a year.
         * 
         * @param indexToSelect
         *            index to be selected.
         */
        void selectYear(final int indexToSelect);

        /**
         * Select a date-time.
         * 
         * @param indexToSelect
         *            index to be selected.
         */
        void selectDateTime(final int indexToSelect);

        /**
         * Select a version.
         * 
         * @param indexToSelect
         *            index to be selected.
         */
        void selectVersion(final int indexToSelect);

        /**
         * Select a channel.
         * 
         * @param indexToSelect
         *            index to be selected.
         */
        void selectChannel(final int indexToSelect);

        /**
         * Select a saved plot.
         */
        void selectSavedPlot();

        // ------------------------------------------------------
        // Get selected configuration parameters.
        // ------------------------------------------------------

        /**
         * Get selected server.
         * 
         * @return selected server.
         */
        String getSelectedServer();

        /**
         * Get selected GPRC.
         * 
         * @return selected GPRC.
         */
        String getSelectedGPRC();

        /**
         * Get selected correction type.
         * 
         * @return selected correction type.
         */
        String getSelectedCorrType();

        /**
         * Get selected sat/instr.
         * 
         * @return selected sat/instr.
         */
        String getSelectedSatInstr();

        /**
         * Get selected ref sat/instr.
         * 
         * @return selected ref sat/instr.
         */
        String getSelectedRefSatInstr();

        /**
         * Get selected mode.
         * 
         * @return selected mode.
         */
        String getSelectedMode();

        /**
         * Get selected year.
         * 
         * @return selected year.
         */
        String getSelectedYear();

        /**
         * Get selected date-time.
         * 
         * @return selected date-time.
         */
        String getSelectedDateTime();

        /**
         * Get selected version.
         * 
         * @return selected version
         */
        String getSelectedVersion();

        /**
         * Get selected channel.
         * 
         * @return selected channel
         */
        String getSelectedChannel();

        /**
         * Get selected scene Tb.
         * 
         * @return selected scene Tb.
         */
        double getSelectedSceneTb();

        /**
         * Get selected saved plot configuration id.
         * 
         * @return selected saved plot configuration id.
         */
        String getSelectedSavedPlotConfig();

        /**
         * Get entered name for the saved plot.
         * 
         * @return name for the saved plot.
         */
        String getEnteredSavedPlotName();

        /**
         * Get entered minimum value.
         * 
         * @return minimum value.
         */
        String getEnteredMinValue();

        /**
         * Get entered maximum value.
         * 
         * @return maximum value.
         */
        String getEnteredMaxValue();

        /**
         * Gets if auto toggle button is pressed.
         * 
         * @return true if auto button is pressed; false otherwise.
         */
        boolean isAutoValueRangePressed();

        // ------------------------------------------------------
        // Search mode with data.
        // ------------------------------------------------------

        /**
         * Search mode with data.
         * 
         * @param targetMode
         *            first mode to try.
         * @return first mode with data in op>preop>demo order.
         */
        int searchAndSelectMode(final String targetMode);

        // ------------------------------------------------------
        // Enable methods.
        // ------------------------------------------------------

        /**
         * Enable save button.
         */
        void enableSaveButton();

        /**
         * Enable remove saved button.
         */
        void enableRemoveSelectedButton();

        /**
         * Enable clear saved button.
         */
        void enableRemoveAllButton();

        /**
         * Enable clear button.
         */
        void enableClearButton();

        /**
         * Enable plot button.
         */
        void enablePlotButton();

        /**
         * Enable export button.
         */
        void enableExportButton();

        /**
         * Enable url access button.
         */
        void enableUrlAccessButton();

        /**
         * Enable url static access button.
         */
        void enableUrlStaticAccessButton();

        /**
         * Enable saved plot name text box.
         */
        void enableSavedPlotNameTextBox();

        /**
         * Enable value range text boxes.
         */
        void enableValueRangeTextBoxes();

        /**
         * Enable auto value range button.
         */
        void enableAutoValueRangeButton();

        /**
         * Enable scene Tb selector.
         */
        void enableSceneTbSelector();

        // ------------------------------------------------------
        // Disable methods.
        // ------------------------------------------------------

        /**
         * Disable save button.
         */
        void disableSaveButton();

        /**
         * Disable remove saved button.
         */
        void disableRemoveSelectedButton();

        /**
         * Disable clear saved button.
         */
        void disableRemoveAllButton();

        /**
         * Disable clear button.
         */
        void disableClearButton();

        /**
         * Disable plot button.
         */
        void disablePlotButton();

        /**
         * Disable export button.
         */
        void disableExportButton();

        /**
         * Disable url access button.
         */
        void disableUrlAccessButton();

        /**
         * Disable url static access button.
         */
        void disableUrlStaticAccessButton();

        /**
         * Disable saved plot name text box.
         */
        void disableSavedPlotNameTextBox();

        /**
         * Disable value range text boxes.
         */
        void disableValueRangeTextBoxes();

        /**
         * Disable auto value range button.
         */
        void disableAutoValueRangeButton();

        /**
         * Disable scene Tb selector.
         */
        void disableSceneTbSelector();

        // ------------------------------------------------------
        // Show/hide elements in GUI.
        // ------------------------------------------------------

        /**
         * Show loading animation.
         */
        void showLoadingAnimation();

        /**
         * Hide loading animation.
         */
        void hideLoadingAnimation();

        /**
         * Show graph's legend panel.
         */
        void showLegend();

        /**
         * Hide graph's legend.
         */
        void hideLegend();

        /**
         * Change to logged in GUI status showing user logged in.
         * 
         * @param username
         *            user logged in.
         */
        void changeToLoggedIn(final String username);

        /**
         * Change to logged out GUI status.
         */
        void changeToLoggedOut();

        /**
         * Clear visibility panel.
         */
        void clearVisibilityPanel();

        /**
         * Add a plot to the visibility panel.
         * 
         * @param plotCheckBox
         *            plot to be added to the visibility panel.
         */
        void addPlotToVisibilityPanel(final HorizontalPanel plotCheckBox);

        /**
         * Mark min value text box as invalid.
         */
        void markMinValueInvalid();

        /**
         * Mark min value text box as valid.
         */
        void markMinValueValid();

        /**
         * Mark max value text box as invalid.
         */
        void markMaxValueInvalid();

        /**
         * Mark max value text box as valid.
         */
        void markMaxValueValid();

        /**
         * Add a help item to the help panel.
         * 
         * @param helpItem
         *            help item to be added to the help panel.
         */
        void addItemToHelpPanel(final HelpItem helpItem);

        // ------------------------------------------------------
        // Simulate click plot button.
        // ------------------------------------------------------

        /**
         * Simulate a click to plot button.
         */
        void clickPlotButton();

        // ------------------------------------------------------
        // Change active option in menu stack panel.
        // ------------------------------------------------------

        /**
         * Change active option in menu stack panel.
         * 
         * @param option
         *            option to be selected.
         */
        void setActiveMenuOption(MenuOption option);

        // ------------------------------------------------------
        // Get Graph Panel.
        // ------------------------------------------------------

        /**
         * Get the graph panel.
         * 
         * @return graph panel.
         */
        AbsolutePanel getGraphPanel();
    }

    /** Instance of handler for login authenticated event. */
    private final LoginAuthenticatedHandler loginAuthenticatedHandler = new LoginAuthenticatedHandler()
    {
        @Override
        public void onLoginAuthenticated(final LoginAuthenticatedEvent event)
        {
            // store authenticated user name.
            authUser = event.getUsername();

            // change view to logged out state.
            getView().changeToLoggedIn(authUser);

            // Gets saved plot configurations for this user.
            dispatcher.execute(new GetSavedPlotConfigs(authUser), new CustomAsyncCallback<GetSavedPlotConfigsResult>(
                    eventBus)
            {
                @Override
                public void onSuccess(final GetSavedPlotConfigsResult result)
                {
                    Map<String, List<PlotConfiguration>> userSavedPlotConfigs = result.getSavedPlotConfigs();

                    if (!userSavedPlotConfigs.isEmpty())
                    {
                        // iterate through map.
                        for (Map.Entry<String, List<PlotConfiguration>> entry : userSavedPlotConfigs.entrySet())
                        {
                            // add key to list box.
                            getView().addSavedPlotConfig(entry.getKey());

                            // store the plot configuration (value)
                            // indexed by id (key).
                            idSavedPlotConfigs(entry.getKey(), entry.getValue());
                        }

                        // enable clear button.
                        getView().enableRemoveAllButton();
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
                    errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                            + " GSICS catalog.");
                    addToPopupSlot(errorPopup);
                }

                @Override
                public void onException(Throwable caught)
                {
                    errorPopup.setErrorMessage("An error has occurred while requesting saved plot configurations. "
                            + "Please contact the administrator of this tool.");
                    errorPopup.setErrorDetails(caught.getMessage());
                    addToPopupSlot(errorPopup);
                }
            });
        }
    };

    /** Proxy. */
    @ProxyCodeSplit
    @NameToken(NameTokens.MAIN)
    public interface MyProxy extends ProxyPlace<PlotPresenter>
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
    public PlotPresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final Historian historian)
    {
        super(eventBus, view, proxy);

        this.eventBus = eventBus;
        this.historian = historian;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gwtplatform.mvp.client.Presenter#revealInParent()
     */
    @Override
    protected final void revealInParent()
    {
        RevealRootLayoutContentEvent.fire(this, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gwtplatform.mvp.client.HandlerContainerImpl#onBind()
     */
    @Override
    protected final void onBind()
    {
        super.onBind();

        registerHandler(eventBus.addHandler(LoginAuthenticatedEvent.getType(), loginAuthenticatedHandler));

        // Add UI handlers.
        bindUiHandlers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.gwtplatform.mvp.client.PresenterWidget#onReset()
     */
    @Override
    protected final void onReset()
    {
        super.onReset();

        // Get user logged in, if any.
        dispatcher.execute(new GetUserLoggedIn(), new GotUserLoggedIn(eventBus)
        {
            @Override
            public void onUserLoggedIn(final String username)
            {
                LoginAuthenticatedEvent.fire(this, username);
            }

            @Override
            public void onNoUserLoggedIn()
            {
                // Do nothing.
            }

            @Override
            public void onDisconnected()
            {
                // Do nothing.
            }

            @Override
            public void onCatalogNotReachable()
            {
                // Do nothing.
            }

            @Override
            public void onException(Throwable caught)
            {
                errorPopup.setErrorMessage("An error has occurred while getting the user logged in. "
                        + "Please contact the administrator of this tool.");
                errorPopup.setErrorDetails(caught.getMessage());
                addToPopupSlot(errorPopup);
            }
        });

        // Set lists to loading.
        getView().setServerListToLoading();
        getView().setGPRCListToLoading();
        getView().setCorrTypeListToLoading();
        getView().setSatInstrListToLoading();
        getView().setRefSatInstrListToLoading();
        getView().setModeListToLoading();
        getView().setYearListToLoading();
        getView().setDateTimeListToLoading();
        getView().setVersionListToLoading();
        getView().setChannelListToLoading();

        // Add default plot configurations in saved plots list box.
        initSavedPlotsList();

        // Add help items as configured in the server.
        fillHelpPanel();

        // Get Client ID (from cookie, and when cookie does not exist, request
        // new to the server).
        clientID = Cookies.getCookie(Constants.CLIENT_ID_COOKIE);

        if (clientID == null)
        {
            dispatcher.execute(new GenerateClientID(), new CustomAsyncCallback<GenerateClientIDResult>(eventBus)
            {
                @Override
                public void onSuccess(final GenerateClientIDResult result)
                {
                    clientID = result.getUserID();

                    // store cookie.
                    Cookies.setCookie(Constants.CLIENT_ID_COOKIE, clientID);
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
                    errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                            + " GSICS catalog.");
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

        } else
        {
            // ensure that data from past executions on the server side is
            // removed for this client.
            dispatcher.execute(new ClearData(clientID), new CustomAsyncCallback<ClearDataResult>(eventBus)
            {
                @Override
                public void onSuccess(final ClearDataResult result)
                {
                    // Do nothing.
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
                    errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                            + " GSICS catalog.");
                    addToPopupSlot(errorPopup);
                }

                @Override
                public void onException(Throwable caught)
                {
                    errorPopup.setErrorMessage("An error has occurred while initializing the server. "
                            + "Please contact the administrator of this tool.");
                    errorPopup.setErrorDetails(caught.getMessage());
                    addToPopupSlot(errorPopup);
                }
            });
        }

        // Get implemented catalogs from the server.
        dispatcher.execute(new GetImplementedCatalogs(),
                new CustomAsyncCallback<GetImplementedCatalogsResult>(eventBus)
                {
                    @Override
                    public void onSuccess(final GetImplementedCatalogsResult result)
                    {
                        updateServerList(result.getCatalogNames());
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
                        errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                + " GSICS catalog.");
                        addToPopupSlot(errorPopup);
                    }

                    @Override
                    public void onException(Throwable caught)
                    {
                        errorPopup.setErrorMessage("An error has occurred while requesting the list of implemented catalogs. "
                                + "Please contact the administrator of this tool.");
                        errorPopup.setErrorDetails(caught.getMessage());
                        addToPopupSlot(errorPopup);
                    }
                });

        // Plot from URL, if defined.
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
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

                if (plotConfigFromDatasetUrl != null && !plottedFromUrl)
                {
                    plot(plotConfigFromDatasetUrl);

                    plottedFromUrl = true;

                } else
                {
                    // Get plot configurations from URL, if defined.
                    ArrayList<PlotConfiguration> plotConfigsFromUrl = PlotConfiguration.fromURL();

                    // Plot them, if any (only once).
                    if (!plotConfigsFromUrl.isEmpty() && !plottedFromUrl)
                    {
                        plot(plotConfigsFromUrl);

                        plottedFromUrl = true;
                    }
                }

                if (plottedFromUrl)
                {
                    // If plotted from URL, show the "Help" option instead of "Configuration and Plot".
                    // Thus, user is not confused with mismatch between configuration default selections and plot.
                    getView().setActiveMenuOption(PlotView.MenuOption.HELP);

                    // And reset URL.
                    historian.newItem("", false);
                }
            }
        });
    }

    /**
     * Initiate saved plot configurations with some defaults.
     */
    private void initSavedPlotsList()
    {
        // register ids (clear current existing).
        savedPlotConfigsWithID.clear();
        ArrayList<PlotConfiguration> defaultPlotConfigs1 = new ArrayList<PlotConfiguration>();
        defaultPlotConfigs1.add(defaultPlotConfig1);
        ArrayList<PlotConfiguration> defaultPlotConfigs2 = new ArrayList<PlotConfiguration>();
        defaultPlotConfigs2.add(defaultPlotConfig2);
        ArrayList<PlotConfiguration> defaultPlotConfigs3 = new ArrayList<PlotConfiguration>();
        defaultPlotConfigs3.add(defaultPlotConfig3);
        savedPlotConfigsWithID.put(defaultPlotConfig1.getId(), defaultPlotConfigs1);
        savedPlotConfigsWithID.put(defaultPlotConfig2.getId(), defaultPlotConfigs2);
        savedPlotConfigsWithID.put(defaultPlotConfig3.getId(), defaultPlotConfigs3);

        // add to list box (clear current existing elements).
        getView().clearSavedPlotConfigsList();
        getView().addSavedPlotConfig(defaultPlotConfig1.getId());
        getView().addSavedPlotConfig(defaultPlotConfig2.getId());
        getView().addSavedPlotConfig(defaultPlotConfig3.getId());
    }

    /**
     * Store a set of plot configurations by its id.
     * 
     * @param id
     *            id of set of plot configurations.
     * @param plotConfigs
     *            set of plot configurations to be indexed.
     */
    private void idSavedPlotConfigs(final String id, final List<PlotConfiguration> plotConfigs)
    {
        savedPlotConfigsWithID.put(id, plotConfigs);
    }

    /**
     * Get the list of PlotConfiguration objects with a certain id.
     * 
     * @param id
     *            id of the PlotConfiguration object wanted.
     * @return ArrayList/<PlotConfiguration/> object wanted, <code>null</code> if not found.
     */
    private List<PlotConfiguration> getSavedPlotConfigsByID(final String id)
    {
        if (plotIdExists(id))
        {
            return savedPlotConfigsWithID.get(id);

        } else
        {
            errorPopup.setErrorMessage("Unexpected error. No saved plot configurations with id '" + id + "'. "
                    + "Please contact the administrator of this tool.");
            addToPopupSlot(errorPopup);

            return null;
        }
    }

    /**
     * Check if a certain PlotConfiguration object exists.
     * 
     * @param id
     *            id of the PlotConfiguration to be checked.
     * @return <code>true</code> if exists, <code>false</code> otherwise.
     */
    private boolean plotIdExists(final String id)
    {
        return savedPlotConfigsWithID.containsKey(id);
    }

    /**
     * Add UI handlers.
     */
    private void bindUiHandlers()
    {
        // Add click handler to login button.
        getView().setLoginHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                addToPopupSlot(loginPopup);
            }
        });

        // Add click handler to logout button.
        getView().setLogoutHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                if (!authUser.isEmpty())
                {
                    dispatcher.execute(new Logout(), new CustomAsyncCallback<LogoutResult>(eventBus)
                    {
                        @Override
                        public void onSuccess(final LogoutResult result)
                        {
                            // inverse of login.
                            authUser = "";

                            // change view to logged out state.
                            getView().changeToLoggedOut();

                            // reset saved plots list.
                            initSavedPlotsList();

                            // disable remove user saved button
                            getView().disableRemoveAllButton();
                            getView().disableRemoveSelectedButton();
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
                            errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                    + " GSICS catalog.");
                            addToPopupSlot(errorPopup);
                        }

                        @Override
                        public void onException(Throwable caught)
                        {
                            errorPopup.setErrorMessage("An error has occurred while logging out. "
                                    + "Please contact the administrator of this tool.");
                            errorPopup.setErrorDetails(caught.getMessage());
                            addToPopupSlot(errorPopup);
                        }
                    });

                }
            }
        });

        // Add click handler to register button.
        getView().setRegisterHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                addToPopupSlot(registerPopup);
            }
        });

        // Add click handler to plot saved button.
        getView().setPlotSavedHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                // get plot configurations and plot them.
                String idSavedPlotSelected = getView().getSelectedSavedPlotConfig();

                final List<PlotConfiguration> plotConfigsToBePlotted = getSavedPlotConfigsByID(idSavedPlotSelected);

                // Plot only if it is the first plot, or its configuration is
                // not plotted yet.
                if (!thereIsPlot || !plottedConfigurations.equals(plotConfigsToBePlotted))
                {
                    // Clear only if there is a plot.
                    if (thereIsPlot)
                    {
                        // configure popup message and yes button click handler.
                        confirmationPopup.setMessage("Current plot will be cleared. Are you sure?");

                        confirmationPopup.setYesButtonClickHandler(new ClickHandler()
                        {
                            @Override
                            public void onClick(final ClickEvent event)
                            {
                                // hide confirmation popup.
                                confirmationPopup.getView().hide();

                                // clear data on the server side...
                                dispatcher.execute(new ClearData(clientID), new CustomAsyncCallback<ClearDataResult>(
                                        eventBus)
                                {
                                    @Override
                                    public void onSuccess(final ClearDataResult result)
                                    {
                                        // ... and then plot new
                                        // configurations.
                                        plot(plotConfigsToBePlotted);
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
                                        errorPopup.setErrorMessage("No connection to the "
                                                + getView().getSelectedServer() + " GSICS catalog.");
                                        addToPopupSlot(errorPopup);
                                    }

                                    @Override
                                    public void onException(Throwable caught)
                                    {
                                        errorPopup.setErrorMessage("An error has occurred while clearing the "
                                                + "data being plotted. Please contact the administrator of this tool.");
                                        errorPopup.setErrorDetails(caught.getMessage());
                                        addToPopupSlot(errorPopup);
                                    }
                                });
                            }
                        });

                        // show popup.
                        addToPopupSlot(confirmationPopup);

                    } else
                    {
                        // plot.
                        plot(plotConfigsToBePlotted);
                    }

                } else
                {

                    if (graph != null)
                    {
                        // if just the title is different change it.
                        if (!graph.getTitle().equals(plotConfigsToBePlotted.get(0).getId()))
                        {
                            graph.setTitle(plotConfigsToBePlotted.get(0).getId());

                        } else
                        {
                            // show message popup.
                            messagePopup.setMessageText("Selection is already plotted.");
                            addToPopupSlot(messagePopup);
                        }
                    }

                }
            }
        });

        // Add click handler to remove selected button.
        getView().setRemoveSelectedHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                confirmationPopup.setMessage("Selected saved plot will be removed. Are you sure?");

                confirmationPopup.setYesButtonClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(final ClickEvent event)
                    {
                        confirmationPopup.getView().hide();

                        dispatcher.execute(new RemoveSavedPlotConfig(authUser, getView().getSelectedSavedPlotConfig()),
                                new CustomAsyncCallback<RemoveSavedPlotConfigResult>(eventBus)
                                {
                                    @Override
                                    public void onSuccess(final RemoveSavedPlotConfigResult result)
                                    {
                                        // remove from map and from UI
                                        // list.
                                        savedPlotConfigsWithID.remove(getView().getSelectedSavedPlotConfig());
                                        getView().removeSelectedSavedPlotConfig();

                                        // if no user saved plots left.
                                        if (savedPlotConfigsWithID.size() == 3) // 3 default plots
                                                                                // on the list.
                                        {
                                            getView().disableRemoveAllButton();
                                            getView().disableRemoveSelectedButton();
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
                                        errorPopup.setErrorMessage("No connection to the "
                                                + getView().getSelectedServer() + " GSICS catalog.");
                                        addToPopupSlot(errorPopup);
                                    }

                                    @Override
                                    public void onException(Throwable caught)
                                    {
                                        errorPopup.setErrorMessage("An error has occurred while removed the selected "
                                                + "saved plot. Please contact the administrator of this tool.");
                                        errorPopup.setErrorDetails(caught.getMessage());
                                        addToPopupSlot(errorPopup);
                                    }
                                });
                    }
                });

                // only if there is an authenticated user.
                if (!authUser.isEmpty())
                {
                    addToPopupSlot(confirmationPopup);
                }
            }
        });

        // Add click handler to remove all button.
        getView().setRemoveAllHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                confirmationPopup.setMessage("All your saved plots will be removed. Are you sure?");

                confirmationPopup.setYesButtonClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(final ClickEvent event)
                    {
                        confirmationPopup.getView().hide();

                        dispatcher.execute(new ClearSavedPlotConfigs(authUser),
                                new CustomAsyncCallback<ClearSavedPlotConfigsResult>(eventBus)
                                {
                                    @Override
                                    public void onSuccess(final ClearSavedPlotConfigsResult result)
                                    {
                                        initSavedPlotsList();
                                        getView().disableRemoveAllButton();
                                        getView().disableRemoveSelectedButton();
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
                                        errorPopup.setErrorMessage("No connection to the "
                                                + getView().getSelectedServer() + " GSICS catalog.");
                                        addToPopupSlot(errorPopup);
                                    }

                                    @Override
                                    public void onException(Throwable caught)
                                    {
                                        errorPopup.setErrorMessage("An error has occurred while clearing the "
                                                + "list of saved plots. "
                                                + "Please contact the administrator of this tool.");
                                        errorPopup.setErrorDetails(caught.getMessage());
                                        addToPopupSlot(errorPopup);
                                    }
                                });
                    }
                });

                // only if there is an authenticated user.
                if (!authUser.isEmpty())
                {
                    addToPopupSlot(confirmationPopup);
                }
            }
        });

        // Add click handler to save plot button.
        getView().setSavePlotHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                if (!getView().getEnteredSavedPlotName().equals(""))
                {
                    savePlot();

                } else
                {
                    messagePopup.setMessageText("Please enter a name for the saved plot.");
                    addToPopupSlot(messagePopup);
                }
            }
        });

        // Add click handler to help button.
        getView().setHelpHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                addToPopupSlot(userGuidePopup);
                userGuidePopup.centerWithDelay(200);
            }
        });

        // Add change value handler to server list box.
        getView().setServerChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Disable plot button. Will be enabled when data is found to be
                // plotted.
                getView().disablePlotButton();

                // set subsequent list boxes to "loading..."
                getView().setGPRCListToLoading();
                getView().setCorrTypeListToLoading();
                getView().setSatInstrListToLoading();
                getView().setRefSatInstrListToLoading();
                getView().setModeListToLoading();
                getView().setYearListToLoading();
                getView().setDateTimeListToLoading();
                getView().setVersionListToLoading();
                getView().setChannelListToLoading();

                // perform corresponding service call.
                dispatcher.execute(new GetGPRCs(getView().getSelectedServer()),
                        new CustomAsyncCallback<GetGPRCsResult>(eventBus)
                        {
                            private void setListsToNoData()
                            {
                                // set subsequent list boxes to "No data"
                                getView().setGPRCListToNoData();
                                getView().setCorrTypeListToNoData();
                                getView().setSatInstrListToNoData();
                                getView().setRefSatInstrListToNoData();
                                getView().setModeListToNoData();
                                getView().setYearListToNoData();
                                getView().setDateTimeListToNoData();
                                getView().setVersionListToNoData();
                                getView().setChannelListToNoData();
                            }

                            @Override
                            public void onSuccess(final GetGPRCsResult result)
                            {
                                updateGPRCList(result.getGPRCs());
                            }

                            @Override
                            public void onDisconnected()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onCatalogNotReachable()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                        + " GSICS catalog.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onException(Throwable caught)
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("An error has occurred while requesting the list of available "
                                        + "sources. Please contact the administrator of this tool.");
                                errorPopup.setErrorDetails(caught.getMessage());
                                addToPopupSlot(errorPopup);
                            }
                        });
            }
        });

        // Add change value handler to source list box.
        getView().setGPRCChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Disable plot button. Will be enabled when data is found to be
                // plotted.
                getView().disablePlotButton();

                // set subsequent list boxes to "loading..."
                getView().setCorrTypeListToLoading();
                getView().setSatInstrListToLoading();
                getView().setRefSatInstrListToLoading();
                getView().setModeListToLoading();
                getView().setYearListToLoading();
                getView().setDateTimeListToLoading();
                getView().setVersionListToLoading();
                getView().setChannelListToLoading();

                // perform corresponding service call.
                dispatcher.execute(new GetCorrTypes(getView().getSelectedServer(), getView().getSelectedGPRC()),
                        new CustomAsyncCallback<GetCorrTypesResult>(eventBus)
                        {
                            private void setListsToNoData()
                            {
                                // set subsequent list boxes to "No data"
                                getView().setCorrTypeListToNoData();
                                getView().setSatInstrListToNoData();
                                getView().setRefSatInstrListToNoData();
                                getView().setModeListToNoData();
                                getView().setYearListToNoData();
                                getView().setDateTimeListToNoData();
                                getView().setVersionListToNoData();
                                getView().setChannelListToNoData();
                            }

                            @Override
                            public void onSuccess(final GetCorrTypesResult result)
                            {
                                updateCorrTypeList(result.getCorrTypes());
                            }

                            @Override
                            public void onDisconnected()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onCatalogNotReachable()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                        + " GSICS catalog.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onException(Throwable caught)
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("An error has occurred while requesting the "
                                        + "list of available types. Please contact the administrator of this tool.");
                                errorPopup.setErrorDetails(caught.getMessage());
                                addToPopupSlot(errorPopup);
                            }
                        });
            }
        });

        // Add change value handler to type list box.
        getView().setCorrTypeChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Disable plot button. Will be enabled when data is found to be plotted.
                getView().disablePlotButton();

                // set subsequent list boxes to "loading..."
                getView().setSatInstrListToLoading();
                getView().setRefSatInstrListToLoading();
                getView().setModeListToLoading();
                getView().setYearListToLoading();
                getView().setDateTimeListToLoading();
                getView().setVersionListToLoading();
                getView().setChannelListToLoading();

                // perform corresponding service call.
                dispatcher.execute(new GetSatInstrs(getView().getSelectedServer(), getView().getSelectedGPRC(),
                        getView().getSelectedCorrType()), new CustomAsyncCallback<GetSatInstrsResult>(eventBus)
                {
                    private void setListsToNoData()
                    {
                        // set subsequent list boxes to "No data"
                        getView().setSatInstrListToNoData();
                        getView().setRefSatInstrListToNoData();
                        getView().setModeListToNoData();
                        getView().setYearListToNoData();
                        getView().setDateTimeListToNoData();
                        getView().setVersionListToNoData();
                        getView().setChannelListToNoData();
                    }

                    @Override
                    public void onSuccess(final GetSatInstrsResult result)
                    {
                        updateSatInstrList(result.getSatInstrs());
                    }

                    @Override
                    public void onDisconnected()
                    {
                        // set subsequent list boxes to "No data"
                        setListsToNoData();

                        errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                        addToPopupSlot(errorPopup);
                    }

                    @Override
                    public void onCatalogNotReachable()
                    {
                        // set subsequent list boxes to "No data"
                        setListsToNoData();

                        errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                + " GSICS catalog.");
                        addToPopupSlot(errorPopup);
                    }

                    @Override
                    public void onException(Throwable caught)
                    {
                        // set subsequent list boxes to "No data"
                        setListsToNoData();

                        errorPopup.setErrorMessage("An error has occurred while requesting the list of available "
                                + "satellite/instruments. Please contact the administrator of this tool.");
                        errorPopup.setErrorDetails(caught.getMessage());
                        addToPopupSlot(errorPopup);
                    }
                });
            }
        });

        // Add change value handler to sat/instr list box.
        getView().setSatInstrChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Disable plot button. Will be enabled when data is found to be
                // plotted.
                getView().disablePlotButton();

                // set subsequent list boxes to "loading..."
                getView().setRefSatInstrListToLoading();
                getView().setModeListToLoading();
                getView().setYearListToLoading();
                getView().setDateTimeListToLoading();
                getView().setVersionListToLoading();
                getView().setChannelListToLoading();

                // perform corresponding service call.
                dispatcher.execute(new GetRefSatInstrs(getView().getSelectedServer(), getView().getSelectedGPRC(),
                        getView().getSelectedCorrType(), getView().getSelectedSatInstr()),
                        new CustomAsyncCallback<GetRefSatInstrsResult>(eventBus)
                        {
                            private void setListsToNoData()
                            {
                                // set subsequent list boxes to "No data"
                                getView().setRefSatInstrListToNoData();
                                getView().setModeListToNoData();
                                getView().setYearListToNoData();
                                getView().setDateTimeListToNoData();
                                getView().setVersionListToNoData();
                                getView().setChannelListToNoData();
                            }

                            @Override
                            public void onSuccess(final GetRefSatInstrsResult result)
                            {
                                updateRefSatInstrList(result.getRefSatInstrs());
                            }

                            @Override
                            public void onDisconnected()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onCatalogNotReachable()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                        + " GSICS catalog.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onException(Throwable caught)
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("An error has occurred while requesting the list of available "
                                        + "reference satellite/instruments. Please contact the administrator of this tool.");
                                errorPopup.setErrorDetails(caught.getMessage());
                                addToPopupSlot(errorPopup);
                            }
                        });
            }
        });

        // Add change value handler to ref sat/instr list box.
        getView().setRefSatInstrChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Disable plot button. Will be enabled when data is found to be
                // plotted.
                getView().disablePlotButton();

                // set subsequent list boxes to "loading..."
                getView().setModeListToLoading();
                getView().setYearListToLoading();
                getView().setDateTimeListToLoading();
                getView().setVersionListToLoading();
                getView().setChannelListToLoading();

                // perform corresponding service call.
                dispatcher.execute(new GetModes(getView().getSelectedServer(), getView().getSelectedGPRC(),
                        getView().getSelectedCorrType(), getView().getSelectedSatInstr(),
                        getView().getSelectedRefSatInstr()), new CustomAsyncCallback<GetModesResult>(eventBus)
                {
                    private void setListsToNoData()
                    {
                        // set subsequent list boxes to "No data"
                        getView().setModeListToNoData();
                        getView().setYearListToNoData();
                        getView().setDateTimeListToNoData();
                        getView().setVersionListToNoData();
                        getView().setChannelListToNoData();
                    }

                    @Override
                    public void onSuccess(final GetModesResult result)
                    {
                        updateModeList(result.getModes());
                    }

                    @Override
                    public void onDisconnected()
                    {
                        // set subsequent list boxes to "No data"
                        setListsToNoData();

                        errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                        addToPopupSlot(errorPopup);
                    }

                    @Override
                    public void onCatalogNotReachable()
                    {
                        // set subsequent list boxes to "No data"
                        setListsToNoData();

                        errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                + " GSICS catalog.");
                        addToPopupSlot(errorPopup);
                    }

                    @Override
                    public void onException(Throwable caught)
                    {
                        // set subsequent list boxes to "No data"
                        setListsToNoData();

                        errorPopup.setErrorMessage("An error has occurred while requesting the list of available "
                                + "modes. Please contact the administrator of this tool.");
                        errorPopup.setErrorDetails(caught.getMessage());
                        addToPopupSlot(errorPopup);
                    }
                });
            }
        });

        // Add change value handler to mode list box.
        getView().setModeChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Disable plot button. Will be enabled when data is found to be
                // plotted.
                getView().disablePlotButton();

                // set subsequent list boxes to "loading..."
                getView().setYearListToLoading();
                getView().setDateTimeListToLoading();
                getView().setVersionListToLoading();
                getView().setChannelListToLoading();

                // perform corresponding service call.
                dispatcher.execute(new GetYears(getView().getSelectedServer(), getView().getSelectedGPRC(),
                        getView().getSelectedCorrType(), getView().getSelectedSatInstr(),
                        getView().getSelectedRefSatInstr(), getView().getSelectedMode()),
                        new CustomAsyncCallback<GetYearsResult>(eventBus)
                        {
                            private void setListsToNoData()
                            {
                                // set subsequent list boxes to "No data"
                                getView().setYearListToNoData();
                                getView().setDateTimeListToNoData();
                                getView().setVersionListToNoData();
                                getView().setChannelListToNoData();
                            }

                            @Override
                            public void onSuccess(final GetYearsResult result)
                            {
                                updateYearList(result.getYears());
                            }

                            @Override
                            public void onDisconnected()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onCatalogNotReachable()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                        + " GSICS catalog.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onException(Throwable caught)
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("An error has occurred while requesting the list "
                                        + "of available years. Please contact the administrator of this tool.");
                                errorPopup.setErrorDetails(caught.getMessage());
                                addToPopupSlot(errorPopup);
                            }
                        });
            }
        });

        // Add change value handler to year list box.
        getView().setYearChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Disable plot button. Will be enabled when data is found to be
                // plotted.
                getView().disablePlotButton();

                // set subsequent list boxes to "loading..."
                getView().setDateTimeListToLoading();
                getView().setVersionListToLoading();
                getView().setChannelListToLoading();

                // perform corresponding service call.
                dispatcher.execute(new GetDateTimes(getView().getSelectedServer(), getView().getSelectedGPRC(),
                        getView().getSelectedCorrType(), getView().getSelectedSatInstr(),
                        getView().getSelectedRefSatInstr(), getView().getSelectedMode(), getView().getSelectedYear()),
                        new CustomAsyncCallback<GetDateTimesResult>(eventBus)
                        {
                            private void setListsToNoData()
                            {
                                // set subsequent list boxes to "No data"
                                getView().setDateTimeListToNoData();
                                getView().setVersionListToNoData();
                                getView().setChannelListToNoData();
                            }

                            @Override
                            public void onSuccess(final GetDateTimesResult result)
                            {
                                updateDateTimeList(result.getDateTimes());
                            }

                            @Override
                            public void onDisconnected()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onCatalogNotReachable()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                        + " GSICS catalog.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onException(Throwable caught)
                            {
                                errorPopup.setErrorMessage("An error has occurred while requesting the list "
                                        + "of available date-times. Please contact the administrator of this tool.");
                                errorPopup.setErrorDetails(caught.getMessage());
                                addToPopupSlot(errorPopup);
                            }
                        });
            }
        });

        // Add change value handler to date-time list box.
        getView().setDateTimeChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Disable plot button. Will be enabled when data is found to be
                // plotted.
                getView().disablePlotButton();

                // set subsequent list boxes to "loading..."
                getView().setVersionListToLoading();
                getView().setChannelListToLoading();

                // perform corresponding service call.
                dispatcher.execute(new GetVersions(getView().getSelectedServer(), getView().getSelectedGPRC(),
                        getView().getSelectedCorrType(), getView().getSelectedSatInstr(),
                        getView().getSelectedRefSatInstr(), getView().getSelectedMode(), getView().getSelectedYear(),
                        getView().getSelectedDateTime()), new CustomAsyncCallback<GetVersionsResult>(eventBus)
                {
                    private void setListsToNoData()
                    {
                        // set subsequent list boxes to "No data"
                        getView().setVersionListToNoData();
                        getView().setChannelListToNoData();
                    }

                    @Override
                    public void onSuccess(final GetVersionsResult result)
                    {
                        updateVersionList(result.getVersions());
                    }

                    @Override
                    public void onDisconnected()
                    {
                        // set subsequent list boxes to "No data"
                        setListsToNoData();

                        errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                        addToPopupSlot(errorPopup);
                    }

                    @Override
                    public void onCatalogNotReachable()
                    {
                        // set subsequent list boxes to "No data"
                        setListsToNoData();

                        errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                + " GSICS catalog.");
                        addToPopupSlot(errorPopup);
                    }

                    @Override
                    public void onException(Throwable caught)
                    {
                        // set subsequent list boxes to "No data"
                        setListsToNoData();

                        errorPopup.setErrorMessage("An error has occurred while requesting the list of available "
                                + "versions. Please contact the administrator of this tool.");
                        errorPopup.setErrorDetails(caught.getMessage());
                        addToPopupSlot(errorPopup);
                    }
                });
            }
        });

        // Add change value handler to version list box.
        getView().setVersionChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Disable plot button. Will be enabled when data is found to be plotted.
                getView().disablePlotButton();

                // set subsequent list boxes to "loading..."
                getView().setChannelListToLoading();

                // perform corresponding service call.
                dispatcher.execute(new GetChannels(getView().getSelectedServer(), getView().getSelectedGPRC(),
                        getView().getSelectedCorrType(), getView().getSelectedSatInstr(),
                        getView().getSelectedRefSatInstr(), getView().getSelectedMode(), getView().getSelectedYear(),
                        getView().getSelectedDateTime(), getView().getSelectedVersion()),
                        new CustomAsyncCallback<GetChannelsResult>(eventBus)
                        {
                            private void setListsToNoData()
                            {
                                // set subsequent list boxes to "No data"
                                getView().setChannelListToNoData();
                            }

                            @Override
                            public void onSuccess(final GetChannelsResult result)
                            {
                                updateChannelList(result.getChannels());
                            }

                            @Override
                            public void onDisconnected()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onCatalogNotReachable()
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                        + " GSICS catalog.");
                                addToPopupSlot(errorPopup);
                            }

                            @Override
                            public void onException(Throwable caught)
                            {
                                // set subsequent list boxes to "No data"
                                setListsToNoData();

                                errorPopup.setErrorMessage("An error has occurred while requesting the list of available "
                                        + "channels. Please contact the administrator of this tool.");
                                errorPopup.setErrorDetails(caught.getMessage());
                                addToPopupSlot(errorPopup);
                            }
                        });
            }
        });

        // Add change value handler to channel list box.
        getView().setChannelChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Disable scene Tb slider bar when first selecting "All"
                // channels option.
                if (getView().getSelectedChannel().equals("All"))
                {
                    getView().disableSceneTbSelector();

                } else
                {
                    getView().enableSceneTbSelector();

                    // perform corresponding service call.
                    dispatcher.execute(new GetStdSceneTb(getView().getSelectedServer(), getView().getSelectedGPRC(),
                            getView().getSelectedCorrType(), getView().getSelectedSatInstr(),
                            getView().getSelectedRefSatInstr(), getView().getSelectedMode(),
                            getView().getSelectedYear(), getView().getSelectedDateTime(),
                            getView().getSelectedVersion(), getView().getSelectedChannel()),
                            new CustomAsyncCallback<GetStdSceneTbResult>(eventBus)
                            {

                                @Override
                                public void onSuccess(final GetStdSceneTbResult result)
                                {
                                    if (Double.compare(result.getStdSceneTb(), -1) != 0)
                                    // if result != -1 (only when not "All" channels is selected)
                                    {
                                        getView().setSelectedSceneTb(result.getStdSceneTb());
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
                                    errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                            + " GSICS catalog.");
                                    addToPopupSlot(errorPopup);
                                }

                                @Override
                                public void onException(Throwable caught)
                                {
                                    errorPopup.setErrorMessage("An error has occurred while requesting the standard scene "
                                            + "brightness temperature. Please contact the administrator of this tool.");
                                    errorPopup.setErrorDetails(caught.getMessage());
                                    addToPopupSlot(errorPopup);
                                }
                            });
                }

            }
        });

        // Add click handler for plot button.
        getView().setPlotHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                double sceneTb = getView().getSelectedSceneTb();

                final PlotConfiguration plotConfigToBePlotted = new PlotConfiguration("",
                        getView().getSelectedServer(), getView().getSelectedGPRC(), getView().getSelectedCorrType(),
                        getView().getSelectedSatInstr(), getView().getSelectedRefSatInstr(),
                        getView().getSelectedMode(), getView().getSelectedYear(), getView().getSelectedDateTime(),
                        getView().getSelectedVersion(), getView().getSelectedChannel(), sceneTb);

                // Plot only if it is the first plot, or its configuration is
                // not plotted yet.
                if (!thereIsPlot || !plottedConfigurations.contains(plotConfigToBePlotted))
                {
                    // Clear only if there is a plot and "All" channels are
                    // selected.
                    if (thereIsPlot && plotConfigToBePlotted.getChannel().equals("All"))
                    {
                        // configure popup message and yes button click handler.
                        confirmationPopup.setMessage("Current plot will be cleared. Are you sure?");

                        confirmationPopup.setYesButtonClickHandler(new ClickHandler()
                        {
                            @Override
                            public void onClick(final ClickEvent event)
                            {
                                // hide confirmation popup.
                                confirmationPopup.getView().hide();

                                // clear data on the server side...
                                dispatcher.execute(new ClearData(clientID), new CustomAsyncCallback<ClearDataResult>(
                                        eventBus)
                                {
                                    @Override
                                    public void onSuccess(final ClearDataResult result)
                                    {
                                        // clear plotted configurations.
                                        plottedConfigurations.clear();

                                        // ... and then
                                        // plot new
                                        // configuration.
                                        plot(plotConfigToBePlotted);
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
                                        errorPopup.setErrorMessage("No connection to the "
                                                + getView().getSelectedServer() + " GSICS catalog.");
                                        addToPopupSlot(errorPopup);
                                    }

                                    @Override
                                    public void onException(Throwable caught)
                                    {
                                        errorPopup.setErrorMessage("An error has occurred while clearing the data "
                                                + "being plotted. Please contact the administrator of this tool.");
                                        errorPopup.setErrorDetails(caught.getMessage());
                                        addToPopupSlot(errorPopup);
                                    }
                                });
                            }
                        });

                        // show popup.
                        addToPopupSlot(confirmationPopup);

                    } else
                    {
                        // plot.
                        plot(plotConfigToBePlotted);
                    }

                } else
                {
                    messagePopup.setMessageText("Selection is already plotted.");
                    addToPopupSlot(messagePopup);
                }
            }
        });

        getView().setClearPlotHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                // clear plots.
                if (thereIsPlot)
                {
                    // configure popup message and yes button click handler.
                    confirmationPopup.setMessage("Current plot will be cleared. Are you sure?");

                    confirmationPopup.setYesButtonClickHandler(new ClickHandler()
                    {
                        @Override
                        public void onClick(final ClickEvent event)
                        {
                            // hide confirmation popup.
                            confirmationPopup.getView().hide();

                            // clear data on the server side.
                            dispatcher.execute(new ClearData(clientID), new CustomAsyncCallback<ClearDataResult>(
                                    eventBus)
                            {
                                @Override
                                public void onSuccess(final ClearDataResult result)
                                {
                                    // clear plots in
                                    // the client side.
                                    clearGraph();

                                    // clear plotted
                                    // configurations.
                                    plottedConfigurations.clear();
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
                                    errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                            + " GSICS catalog.");
                                    addToPopupSlot(errorPopup);
                                }

                                @Override
                                public void onException(Throwable caught)
                                {
                                    errorPopup.setErrorMessage("An error has occurred while clearing the "
                                            + "data being plotted. Please contact the administrator of this tool.");
                                    errorPopup.setErrorDetails(caught.getMessage());
                                    addToPopupSlot(errorPopup);
                                }
                            });

                        }
                    });
                }

                // show popup.
                addToPopupSlot(confirmationPopup);
            }
        });

        // Add Click Handler to export as PNG button.
        getView().setExportHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                if (graph != null)
                {
                    addToPopupSlot(exportedImagePopup);
                    exportedImagePopup.centerWithDelay(200);
                    graph.exportAsPNG(Resources.INSTANCE.constants().exportedImageDivId());
                }
            }
        });

        // Add Click Handler to url plot access button.
        getView().setUrlAccessHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                messagePopup.setMessageText(PlotConfiguration.toURL(plottedConfigurations));
                addToPopupSlot(messagePopup);
            }
        });

        // Add Click Handler to url static plot access button.
        getView().setUrlStaticAccessHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                messagePopup.setMessageText(PlotConfiguration.toURLStatic(plottedConfigurations));
                addToPopupSlot(messagePopup);
            }
        });

        // Add Click Handler to set auto value range button.
        getView().setValueRangeChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // make sure auto toggle button is not pressed.
                if (!getView().isAutoValueRangePressed())
                {
                    applyEnteredValueRange();
                }

            }
        });

        // Add Click Handler to set auto value range button.
        getView().setValueRangeKeyUpHandler(new KeyUpHandler()
        {
            @Override
            public void onKeyUp(final KeyUpEvent event)
            {
                // make sure auto toggle button is not pressed.
                if (!getView().isAutoValueRangePressed())
                {
                    applyEnteredValueRange();
                }

            }
        });

        // Add Click Handler to set auto value range button.
        getView().setAutoValueRangeHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                if (getView().isAutoValueRangePressed())
                {
                    // set graph y-axis to auto.
                    if (graph != null)
                    {
                        graph.setAutoYValueRange();
                    }

                    // disable value range text boxes, keeping entered values if any.
                    getView().disableValueRangeTextBoxes();

                } else
                // manual value range.
                {
                    applyEnteredValueRange();

                    // enable value range text boxes, keeping entered values if any.
                    getView().enableValueRangeTextBoxes();
                }
            }
        });
    }

    /**
     * Updates the list of available servers.
     * 
     * @param implCatalogs
     *            implemented catalogs in the server side.
     */
    private void updateServerList(final List<String> implCatalogs)
    {
        if (!implCatalogs.isEmpty())
        {
            getView().clearServerList();

            // update list box.
            for (String catalog : implCatalogs)
            {
                getView().addServer(catalog);
            }

            // find index of default value.
            int indexToSelect = StringUtils.indexOf(implCatalogs, ".*" + Defaults.DEFAULT_SERVER + ".*");

            if (indexToSelect == -1)
            {
                indexToSelect = 0;
            }

            // select item.
            getView().selectServer(indexToSelect);

        } else
        {
            getView().setServerListToNoData();

            // Clear subsequent lists.
            getView().setGPRCListToNoData();
            getView().setCorrTypeListToNoData();
            getView().setSatInstrListToNoData();
            getView().setRefSatInstrListToNoData();
            getView().setModeListToNoData();
            getView().setYearListToNoData();
            getView().setDateTimeListToNoData();
            getView().setVersionListToNoData();
            getView().setChannelListToNoData();
        }
    }

    /**
     * Updates the list of available GPRCs.
     * 
     * @param gprcs
     *            available GPRCs in the server side.
     */
    private void updateGPRCList(final List<String> gprcs)
    {
        if (!gprcs.isEmpty())
        {
            getView().clearGPRCList();

            for (String gprc : gprcs)
            {
                getView().addGPRC(gprc);
            }

            // find index of default value.
            int indexToSelect = StringUtils.indexOf(gprcs, ".*" + Defaults.DEFAULT_GPRC + ".*");

            if (indexToSelect == -1)
            {
                indexToSelect = 0;
            }

            // select item.
            getView().selectGPRC(indexToSelect);

        } else
        {
            getView().setGPRCListToNoData();

            // Clear subsequent lists.
            getView().setCorrTypeListToNoData();
            getView().setSatInstrListToNoData();
            getView().setRefSatInstrListToNoData();
            getView().setModeListToNoData();
            getView().setYearListToNoData();
            getView().setDateTimeListToNoData();
            getView().setVersionListToNoData();
            getView().setChannelListToNoData();
        }
    }

    /**
     * Updates the list of available correction types.
     * 
     * @param corrTypes
     *            available correction types in the server side.
     */
    private void updateCorrTypeList(final List<String> corrTypes)
    {
        if (!corrTypes.isEmpty())
        {
            getView().clearCorrTypeList();

            // update list box.
            for (String corrType : corrTypes)
            {
                getView().addCorrType(corrType);
            }

            // find index of default value.
            int indexToSelect = StringUtils.indexOf(corrTypes, ".*" + Defaults.DEFAULT_CORR_TYPE + ".*");

            if (indexToSelect == -1)
            {
                indexToSelect = 0;
            }

            // select item
            getView().selectCorrType(indexToSelect);

        } else
        {
            getView().setCorrTypeListToNoData();

            // Clear subsequent lists.
            getView().setSatInstrListToNoData();
            getView().setRefSatInstrListToNoData();
            getView().setModeListToNoData();
            getView().setYearListToNoData();
            getView().setDateTimeListToNoData();
            getView().setVersionListToNoData();
            getView().setChannelListToNoData();
        }
    }

    /**
     * Updates the list of available sat/instruments.
     * 
     * @param satInstrs
     *            available sat/instruments in the server side.
     */
    private void updateSatInstrList(final List<String> satInstrs)
    {
        if (!satInstrs.isEmpty())
        {
            getView().clearSatInstrList();

            // update list box.
            for (String satInstr : satInstrs)
            {
                getView().addSatInstr(satInstr);
            }

            // find index of default value.
            int indexToSelect = StringUtils.indexOf(satInstrs, Defaults.DEFAULT_SAT_INSTR);

            if (indexToSelect == -1)
            {
                indexToSelect = 0;
            }

            // select item.
            getView().selectSatInstr(indexToSelect);

        } else
        {
            getView().setSatInstrListToNoData();

            // Clear subsequent lists.
            getView().setRefSatInstrListToNoData();
            getView().setModeListToNoData();
            getView().setYearListToNoData();
            getView().setDateTimeListToNoData();
            getView().setVersionListToNoData();
            getView().setChannelListToNoData();
        }
    }

    /**
     * Updates the list of available reference sat/instruments.
     * 
     * @param refSatInstrs
     *            reference available sat/instruments in the server side.
     */
    private void updateRefSatInstrList(final List<String> refSatInstrs)
    {
        if (!refSatInstrs.isEmpty())
        {
            getView().clearRefSatInstrList();

            // update list box.
            for (String refSatInstr : refSatInstrs)
            {
                getView().addRefSatInstr(refSatInstr);
            }

            // find index of default value.
            int indexToSelect = StringUtils.indexOf(refSatInstrs, Defaults.DEFAULT_REF_SAT_INSTR);

            if (indexToSelect == -1)
            {
                indexToSelect = 0;
            }

            // select item.
            getView().selectRefSatInstr(indexToSelect);

        } else
        {
            getView().setRefSatInstrListToNoData();

            // Clear subsequent lists.
            getView().setModeListToNoData();
            getView().setYearListToNoData();
            getView().setDateTimeListToNoData();
            getView().setVersionListToNoData();
            getView().setChannelListToNoData();
        }
    }

    /**
     * Selects the default mode as the first one with data, with this preference in mind: operational > pre-operational
     * > demo.
     * 
     * @param targetMode
     *            mode which will be selected as default if there is data available under its folder in the server.
     */
    private void selectDefaultMode(final String targetMode)
    {
        // Search for the index of targetMode in the list box.
        int index = getView().searchAndSelectMode(targetMode);

        if (index != -1)
        {
            // perform corresponding service call.
            dispatcher.execute(new GetYears(getView().getSelectedServer(), getView().getSelectedGPRC(),
                    getView().getSelectedCorrType(), getView().getSelectedSatInstr(),
                    getView().getSelectedRefSatInstr(), getView().getSelectedMode()),
                    new CustomAsyncCallback<GetYearsResult>(eventBus)
                    {
                        private void setListsToNoData()
                        {
                            // set subsequent list boxes to "No data"
                            getView().setYearListToNoData();
                            getView().setDateTimeListToNoData();
                            getView().setVersionListToNoData();
                            getView().setChannelListToNoData();
                        }

                        @Override
                        public void onSuccess(final GetYearsResult result)
                        {
                            if (!result.getYears().isEmpty())
                            {
                                updateYearList(result.getYears());

                            } else
                            {
                                if (targetMode.equals("op"))
                                {
                                    selectDefaultMode("pre-op");

                                } else if (targetMode.equals("pre-op"))
                                {
                                    selectDefaultMode("demo");

                                } else
                                {
                                    // No data in any mode.
                                    // Clear subsequent lists.
                                    setListsToNoData();
                                }
                            }
                        }

                        @Override
                        public void onDisconnected()
                        {
                            // set subsequent list boxes to "No data"
                            setListsToNoData();

                            errorPopup.setErrorMessage("No connection to the server. Please try again later.");
                            addToPopupSlot(errorPopup);
                        }

                        @Override
                        public void onCatalogNotReachable()
                        {
                            // set subsequent list boxes to "No data"
                            setListsToNoData();

                            errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                    + " GSICS catalog.");
                            addToPopupSlot(errorPopup);
                        }

                        @Override
                        public void onException(Throwable caught)
                        {
                            // set subsequent list boxes to "No data"
                            setListsToNoData();

                            errorPopup.setErrorMessage("An error has occurred while requesting the list of available years. "
                                    + "Please contact the administrator of this tool.");
                            errorPopup.setErrorDetails(caught.getMessage());
                            addToPopupSlot(errorPopup);
                        }
                    });

        } else
        // targetMode not found in list box.
        {
            if (targetMode.equals("op"))
            {
                selectDefaultMode("pre-op");

            } else if (targetMode.equals("pre-op"))
            {
                selectDefaultMode("demo");

            } else
            // No mode available.
            {
                getView().setModeListToNoData();

                // Clear subsequent lists.
                getView().setYearListToNoData();
                getView().setDateTimeListToNoData();
                getView().setVersionListToNoData();
                getView().setChannelListToNoData();
            }
        }
    }

    /**
     * Updates the list of available modes.
     * 
     * @param modes
     *            available modes in the server side.
     */
    private void updateModeList(final List<String> modes)
    {
        if (!modes.isEmpty())
        {
            getView().clearModeList();

            // update list box.
            for (String mode : modes)
            {
                getView().addMode(mode);
            }

            // select default.
            selectDefaultMode("op");

        } else
        {
            getView().setModeListToNoData();

            // Clear subsequent lists.
            getView().setYearListToNoData();
            getView().setDateTimeListToNoData();
            getView().setVersionListToNoData();
            getView().setChannelListToNoData();
        }
    }

    /**
     * Updates the list of available years.
     * 
     * @param years
     *            available years in the server side.
     */
    private void updateYearList(final List<String> years)
    {
        if (!years.isEmpty())
        {
            // Sort the years list in natural order.
            Collections.sort(years);

            getView().clearYearList();

            // update list box.
            for (String year : years)
            {
                getView().addYear(year);
            }

            // set index of latest available.
            int indexToSelect = years.size() - 1;

            // select item.
            getView().selectYear(indexToSelect);

        } else
        {
            getView().setYearListToNoData();

            // Clear subsequent lists.
            getView().setDateTimeListToNoData();
            getView().setVersionListToNoData();
            getView().setChannelListToNoData();
        }
    }

    /**
     * Updates the list of available date-times.
     * 
     * @param date
     *            -times available dates in the server side.
     */
    private void updateDateTimeList(final List<String> dateTimes)
    {
        if (!dateTimes.isEmpty())
        {
            // Sort the date-times list in natural order.
            Collections.sort(dateTimes);

            getView().clearDateTimeList();

            // update list box.
            for (String dateTime : dateTimes)
            {
                getView().addDateTime(dateTime);
            }

            // set index of latest available.
            int indexToSelect = dateTimes.size() - 1;

            // select item.
            getView().selectDateTime(indexToSelect);

        } else
        {
            getView().setDateTimeListToNoData();

            // Clear subsequent lists.
            getView().setVersionListToNoData();
            getView().setChannelListToNoData();
        }
    }

    /**
     * Updates the list of available versions.
     * 
     * @param versions
     *            available versions in the server side.
     */
    private void updateVersionList(final List<String> versions)
    {
        if (!versions.isEmpty())
        {
            // Sort the date-times list in natural order.
            Collections.sort(versions);

            getView().clearVersionList();

            // update list box.
            for (String version : versions)
            {
                getView().addVersion(version);
            }

            // set index of latest available.
            int indexToSelect = versions.size() - 1;

            // select item.
            getView().selectVersion(indexToSelect);

        } else
        {
            getView().setVersionListToNoData();

            // Clear subsequent lists.
            getView().setChannelListToNoData();
        }
    }

    /**
     * Updates the list of available channels.
     * 
     * @param channels
     *            available channels in the server side.
     */
    private void updateChannelList(final List<String> channels)
    {
        if (!channels.isEmpty())
        {
            getView().clearChannelList();

            getView().addChannel("All");

            // update list box.
            for (String channel : channels)
            {
                getView().addChannel(channel);
            }

            // default is first item "All".
            getView().selectChannel(0);

            // Enable plot button, since there is data to be plotted.
            getView().enablePlotButton();

        } else
        {
            getView().setChannelListToNoData();
        }
    }

    /**
     * Update visibility check boxes with the current datasets being plotted.
     * 
     * @param datasetsNames
     *            names of the datasets being plotted.
     * @param downloadLinks
     *            download links.
     */
    private void updateVisibilityCheckBoxes(final List<String> datasetsNames, final Map<String, String> downloadLinks)
    {
        // clears the panel.
        getView().clearVisibilityPanel();

        // updates it.
        for (String datasetName : datasetsNames)
        {
            CheckBox checkBox = null;

            if (!visibilityCheckBoxes.containsKey(datasetName))
            {
                checkBox = new CheckBox(datasetName);
                checkBox.setValue(true);
                checkBox.addClickHandler(new ClickHandler()
                {
                    @Override
                    public void onClick(final ClickEvent event)
                    {
                        CheckBox sourceCheckBox = (CheckBox) event.getSource();

                        // find index of this dataset.
                        int index = 0;
                        for (Map.Entry<String, CheckBox> entry : visibilityCheckBoxes.entrySet())
                        {
                            if (entry.getValue().equals(sourceCheckBox))
                            {
                                break;
                            }

                            index++;
                        }

                        // updates visibility in the dygraphs.
                        if (graph != null)
                        {
                            int biasIndex = index * 2;
                            int uncertaintyIndex = index * 2 + 1;
                            graph.setVisibility(biasIndex, sourceCheckBox.getValue());
                            graph.setVisibility(uncertaintyIndex, sourceCheckBox.getValue());
                        }
                    }
                });

                this.visibilityCheckBoxes.put(datasetName, checkBox);

            } else
            {
                checkBox = this.visibilityCheckBoxes.get(datasetName);
            }

            // Add to the panel.
            HorizontalPanel checkBoxPanel = new HorizontalPanel();
            checkBoxPanel.add(checkBox);
            checkBoxPanel.add(new Label(" "));
            checkBoxPanel.add(new Anchor("download", downloadLinks.get(datasetName)));
            getView().addPlotToVisibilityPanel(checkBoxPanel);
        }
    }

    /**
     * Clear visibility check boxes.
     */
    private void clearVisibilityCheckBoxes()
    {
        // clears the panel.
        getView().clearVisibilityPanel();

        // clears the visibility check boxes.
        visibilityCheckBoxes.clear();
    }

    /**
     * Fill help panel with help items in the configuration file.
     * 
     */
    private void fillHelpPanel()
    {
        dispatcher.execute(new GetHelpItems(), new CustomAsyncCallback<GetHelpItemsResult>(eventBus)
        {
            @Override
            public void onSuccess(GetHelpItemsResult result)
            {
                for (HelpItem helpItem : result.getHelpItems())
                {
                    getView().addItemToHelpPanel(helpItem);
                }
            }

            @Override
            public void onDisconnected()
            {
                // do nothing.
            }

            @Override
            public void onCatalogNotReachable()
            {
                // do nothing.
            }

            @Override
            public void onException(Throwable caught)
            {
                // do nothing.

            }
        });
    }

    /**
     * Assemble the graph panel.
     * 
     * @param title
     *            title of the graph.
     * @param csvFilename
     *            the filename of the CSV file with the data to be plotted.
     * @param datasetsNames
     *            the names of the datasets to be plotted.
     */
    private void assembleGraphPanel(final String title, final String csvFilename, final List<String> datasetsNames)
    {
        // Install the dygraphs code.
        Dygraphs.install();

        // Set up options for the dygraph.
        DygraphOptions options = DygraphOptions.create();
        options.set("title", title);
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
        options.set("connectSeparatedPoints", true);
        options.setDefaultInteractionModel();

        // Apply thicker lines to "Bias" plots, to highlight them over the
        // min-max plots.
        DygraphOptions suboptions = DygraphOptions.create();
        suboptions.set("strokeWidth", 2.0);
        suboptions.set("highlightCircleSize", 3);

        for (String datasetName : datasetsNames)
        {
            options.set(datasetName, suboptions);
        }

        // generate the needed number of random colors for all the datasets to be plotted.
        if (datasetsNames.size() * 2 > colors.size()) // "*2" cause every
                                                      // dataset needs 2
                                                      // plots: bias, and
                                                      // uncertainty.
        {
            colors = ColorUtils.createPlotColors(datasetsNames.size());
        }

        options.set("colors", ArrayUtils.toJsArray(colors.toArray()));

        // set the visibility as currently configured by user.
        int index = 0;
        boolean[] visibility = new boolean[visibilityCheckBoxes.size() * 2];
        for (Map.Entry<String, CheckBox> entry : visibilityCheckBoxes.entrySet())
        {
            visibility[index * 2] = entry.getValue().getValue();
            visibility[index * 2 + 1] = entry.getValue().getValue();
            index++;
        }
        options.set("visibility", ArrayUtils.toJsArray(visibility));

        // Create (or recreate) the graph.
        if (graph != null)
        {
            // Removes the graph.
            getView().getGraphPanel().remove(graph);

            // Destroy the graph javascript object, avoiding memory leaks.
            graph.destroy();
        }

        graph = new CsvDygraph("div_graph", csvFilename, options);

        // Add graph to the UI.
        getView().getGraphPanel().add(graph);

        // make legend panel visible.
        getView().showLegend();
    }

    /**
     * Clear graph panel.
     */
    private void clearGraphPanel()
    {
        if (graph != null)
        {
            // Removes the graph and makes the legend invisible.
            getView().getGraphPanel().remove(graph);
            getView().hideLegend();
        }
    }

    /**
     * Plot selections.
     * 
     * @param plotConfiguration
     *            configuration of the plot to be requested to the server.
     */
    private void plot(final PlotConfiguration plotConfiguration)
    {
        // Clear graph.
        clearGraph();

        // Show loading animation.
        getView().showLoadingAnimation();

        // Request server to prepare data to be plotted.
        dispatcher.execute(new PrepareData(clientID, plotConfiguration), new CustomAsyncCallback<PrepareDataResult>(
                eventBus)
        {
            @Override
            public void onSuccess(final PrepareDataResult result)
            {
                // Hide loading animation.
                getView().hideLoadingAnimation();

                // Save current plot configuration.
                plottedConfigurations.add(plotConfiguration);

                // Update visibility panel and create graph.
                updateVisibilityCheckBoxes(result.getDatasetsNames(), result.getDownloadLinks());

                assembleGraphPanel(plotConfiguration.getId(), GWT.getHostPageBaseURL() + Constants.CSV_FILE_PATH + "-"
                        + clientID + ".csv", result.getDatasetsNames());

                // Enable clear button.
                getView().enableClearButton();

                // Enable save button.
                getView().enableSavedPlotNameTextBox();
                getView().enableSaveButton();

                // Enable export button.
                getView().enableExportButton();

                // Enable url plot access button.
                getView().enableUrlAccessButton();
                getView().enableUrlStaticAccessButton();

                // Enable value range panel.
                if (!getView().isAutoValueRangePressed())
                {
                    getView().enableValueRangeTextBoxes();

                    // we apply entered value range after some delay, to give time for the
                    // graph javascript to be constructed.
                    Timer timer = new Timer()
                    {
                        @Override
                        public void run()
                        {
                            applyEnteredValueRange();
                        }
                    };

                    timer.schedule(100);
                }
                getView().enableAutoValueRangeButton();

                // Set the there is plot flag.
                thereIsPlot = true;
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

                errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer() + " GSICS catalog.");
                addToPopupSlot(errorPopup);
            }

            @Override
            public void onException(Throwable caught)
            {
                // Hide loading animation.
                getView().hideLoadingAnimation();

                errorPopup.setErrorMessage("An error has occurred while preparing the " + "datasets to be plotted. "
                        + "Please contact the administrator of this tool.");
                errorPopup.setErrorDetails(caught.getMessage());
                addToPopupSlot(errorPopup);
            }
        });
    }

    /**
     * Plot selections.
     * 
     * @param plotConfigurations
     *            set of configurations of the plots to be requested to the server.
     */
    private void plot(final List<PlotConfiguration> plotConfigurations)
    {
        // Clear graph.
        clearGraph();

        // Show loading animation.
        getView().showLoadingAnimation();

        // Request server to prepare data to be plotted.
        dispatcher.execute(new PrepareMultipleData(clientID, plotConfigurations),
                new CustomAsyncCallback<PrepareMultipleDataResult>(eventBus)
                {
                    @Override
                    public void onSuccess(final PrepareMultipleDataResult result)
                    {
                        // Hide loading animation.
                        getView().hideLoadingAnimation();

                        // Save current plot configuration (as a copy, otherwise
                        // it would be a reference and
                        // modifying it would modify the one in the
                        // savedPlotConfigsWithID).
                        plottedConfigurations = new ArrayList<PlotConfiguration>(plotConfigurations);

                        // Update visibility panel and create graph.
                        updateVisibilityCheckBoxes(result.getDatasetsNames(), result.getDownloadLinks());

                        assembleGraphPanel(plotConfigurations.get(0).getId(), GWT.getHostPageBaseURL()
                                + Constants.CSV_FILE_PATH + "-" + clientID + ".csv", result.getDatasetsNames());

                        // Enable clear button.
                        getView().enableClearButton();

                        // Enable save button.
                        getView().enableSavedPlotNameTextBox();
                        getView().enableSaveButton();

                        // Enable export button.
                        getView().enableExportButton();

                        // Enable url plot access button.
                        getView().enableUrlAccessButton();
                        getView().enableUrlStaticAccessButton();

                        // Enable value range panel.
                        if (!getView().isAutoValueRangePressed())
                        {
                            getView().enableValueRangeTextBoxes();

                            // we apply entered value range after some delay, to give time for the
                            // graph javascript to be constructed.
                            Timer timer = new Timer()
                            {
                                @Override
                                public void run()
                                {
                                    applyEnteredValueRange();
                                }
                            };

                            timer.schedule(100);
                        }

                        getView().enableAutoValueRangeButton();

                        // Set the there is plot flag.
                        thereIsPlot = true;
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

                        errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                + " GSICS catalog.");
                        addToPopupSlot(errorPopup);
                    }

                    @Override
                    public void onException(Throwable caught)
                    {
                        // Hide loading animation.
                        getView().hideLoadingAnimation();

                        errorPopup.setErrorMessage("An error has occurred while preparing the "
                                + "datasets to be plotted. " + "Please contact the administrator of this tool.");
                        errorPopup.setErrorDetails(caught.getMessage());
                        addToPopupSlot(errorPopup);
                    }
                });
    }

    /**
     * Save current plot.
     */
    private void savePlot()
    {
        if (!plottedConfigurations.isEmpty())
        {
            if (!plotIdExists(getView().getEnteredSavedPlotName()))
            {
                // save it in the server if authenticated user.
                if (!authUser.isEmpty())
                {
                    // make a deep copy of plottedConfigurations, otherwise
                    // references to the plot configuration are stored and when
                    // later saved again, old saves are also modified.
                    final ArrayList<PlotConfiguration> plottedConfigurationsCpy = new ArrayList<PlotConfiguration>(
                            plottedConfigurations.size());

                    for (final PlotConfiguration plotConfiguration : plottedConfigurations)
                    {
                        PlotConfiguration plotConfigurationCpy = new PlotConfiguration(plotConfiguration);

                        // change id of the plotted configurations to be saved.
                        plotConfigurationCpy.setId(getView().getEnteredSavedPlotName());

                        // add it to array copy.
                        plottedConfigurationsCpy.add(plotConfigurationCpy);
                    }

                    // request to the server saving these plot configurations.
                    dispatcher.execute(new SavePlotConfig(authUser, plottedConfigurationsCpy),
                            new CustomAsyncCallback<SavePlotConfigResult>(eventBus)
                            {
                                @Override
                                public void onSuccess(final SavePlotConfigResult result)
                                {
                                    // change graph title.
                                    if (graph != null)
                                    {
                                        graph.setTitle(getView().getEnteredSavedPlotName());
                                    }

                                    // store this plot configuration indexed by
                                    // id.
                                    idSavedPlotConfigs(getView().getEnteredSavedPlotName(), plottedConfigurationsCpy);

                                    // add it in the list box.
                                    getView().addSavedPlotConfig(getView().getEnteredSavedPlotName());

                                    // select it.
                                    getView().selectSavedPlot();

                                    // enable remove all button.
                                    getView().enableRemoveAllButton();

                                    // enable remove selected button.
                                    getView().enableRemoveSelectedButton();

                                    // init text box.
                                    getView().clearSavedPlotNameTextBox();
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
                                    errorPopup.setErrorMessage("No connection to the " + getView().getSelectedServer()
                                            + " GSICS catalog.");
                                    addToPopupSlot(errorPopup);
                                }

                                @Override
                                public void onException(Throwable caught)
                                {
                                    errorPopup.setErrorMessage("An error has occurred while saving the plot. "
                                            + "Please contact the administrator of this tool.");
                                    errorPopup.setErrorDetails(caught.getMessage());
                                    addToPopupSlot(errorPopup);
                                }
                            });
                } else
                {
                    messagePopup.setMessageText("In order to save plots you need to login.");
                    addToPopupSlot(messagePopup);
                }

            } else
            {
                messagePopup.setMessageText("'" + getView().getEnteredSavedPlotName()
                        + "' already exists in the list of saved plots. Please choose a different one.");
                addToPopupSlot(messagePopup);
            }

        } else
        {
            errorPopup.setErrorMessage("It seems there is nothing to save. If that is not the case, "
                    + "please contact the administrator of this tool.");
            addToPopupSlot(errorPopup);
        }
    }

    /**
     * Clear current graph.
     */
    private void clearGraph()
    {
        // Update visibility panel and remove graph.
        clearVisibilityCheckBoxes();
        clearGraphPanel();

        // Disable the clear button.
        getView().disableClearButton();

        // Disable the save button.
        getView().disableSavedPlotNameTextBox();
        getView().disableSaveButton();

        // Disable the export button.
        getView().disableExportButton();

        // Disable the get plot url button.
        getView().disableUrlAccessButton();
        getView().disableUrlStaticAccessButton();

        // Disable the value range panel.
        getView().disableValueRangeTextBoxes();
        getView().disableAutoValueRangeButton();

        // Set the there is plot flag.
        thereIsPlot = false;

        // set graph object to null.
        graph = null;
    }

    /**
     * Apply entered the graph y-axis value range.
     */
    private void applyEnteredValueRange()
    {
        Double minValue = null;
        Double maxValue = null;

        try
        {
            minValue = Double.valueOf(getView().getEnteredMinValue());

            // if no exception is thrown, mark it as valid.
            getView().markMinValueValid();

        } catch (NumberFormatException nfe)
        {
            if (getView().getEnteredMinValue().isEmpty())
            {
                getView().markMinValueValid();

            } else
            {
                getView().markMinValueInvalid();
            }
        }

        try
        {
            maxValue = Double.valueOf(getView().getEnteredMaxValue());

            // if no exception is thrown, mark it as valid.
            getView().markMaxValueValid();

        } catch (NumberFormatException nfe)
        {
            if (getView().getEnteredMaxValue().isEmpty())
            {
                getView().markMaxValueValid();

            } else
            {
                getView().markMaxValueInvalid();
            }
        }

        // if entered strings are valid, and min is smaller than max value, update
        // value range.
        if (minValue != null && maxValue != null)
        {
            if (Double.compare(minValue, maxValue) < 0)
            {
                if (graph != null)
                {
                    graph.setYValueRange(minValue, maxValue);
                }

            } else
            {
                getView().markMinValueInvalid();
                getView().markMaxValueInvalid();
            }
        }
    }
}
