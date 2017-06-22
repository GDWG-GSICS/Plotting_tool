package org.eumetsat.usd.gcp.client.view;

import org.eumetsat.usd.gcp.client.presenter.LoadingPresenter;
import org.eumetsat.usd.gcp.client.presenter.PlotPresenter;
import org.eumetsat.usd.gcp.client.resources.Resources;
import org.eumetsat.usd.gcp.client.slider.SliderBar;
import org.eumetsat.usd.gcp.shared.conf.HelpItem;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * Plot's view (interactive plot).
 * 
 * @author USD/C/PBe
 */
public class PlotView extends ViewImpl implements PlotPresenter.MyView
{
    /** Root Panel. */
    @UiField
    DockLayoutPanel rootPanel;

    /** Stack Main Menu. */
    @UiField
    StackLayoutPanel menuStackPanel;

    // -------------------------------------
    // Buttons
    // -------------------------------------
    /** Login Button. */
    @UiField
    Button loginButton;
    /** Logout Button. */
    @UiField
    Button logoutButton;
    /** Register Button. */
    @UiField
    Button registerButton;
    /** Plot Saved Button. */
    @UiField
    Button plotSavedButton;
    /** Remove Selected Button. */
    @UiField
    Button removeSelectedButton;
    /** Remove All Button. */
    @UiField
    Button removeAllButton;
    /** Save Button. */
    @UiField
    Button saveButton;
    /** Export Button. */
    @UiField
    Button exportButton;
    /** URL Access Button. */
    @UiField
    Button urlAccessButton;
    /** Static URL Access Button. */
    @UiField
    Button urlStaticAccessButton;
    /** Auto Value Range Button. */
    @UiField
    ToggleButton autoValueRangeButton;
    /** Plot Button. */
    @UiField
    Button plotButton;
    /** Clear Button. */
    @UiField
    Button clearButton;
    /** Help Button. */
    @UiField
    Button helpButton;

    // -------------------------------------
    // Configuration Parameters Selectors.
    // -------------------------------------
    /** Saved Plot Config Selector. */
    @UiField
    ListBox savedPlotConfigsListBox;
    /** Server Selector. */
    @UiField
    ListBox serverListBox;
    /** GPRC Selector. */
    @UiField
    ListBox gprcListBox;
    /** Correction Type Selector. */
    @UiField
    ListBox corrTypeListBox;
    /** Sat/Instr Selector. */
    @UiField
    ListBox satInstrListBox;
    /** Ref Sat/Instr Selector. */
    @UiField
    ListBox refSatInstrListBox;
    /** Mode Selector. */
    @UiField
    ListBox modeListBox;
    /** Year Selector. */
    @UiField
    ListBox yearListBox;
    /** DateTime Selector. */
    @UiField
    ListBox dateTimeListBox;
    /** Version Selector. */
    @UiField
    ListBox versionListBox;
    /** Channel Selector. */
    @UiField
    ListBox channelListBox;
    /** SceneTb Slider Bar. */
    @UiField
    SliderBar sceneTbSliderBar;
    /** SceneTb Text Box. */
    @UiField
    TextBox sceneTbMonitor;

    // -------------------------------------
    // Text Boxes.
    // -------------------------------------
    /** Saved Plot Name Text Box. */
    @UiField
    TextBox savedPlotNameTextBox;
    /** Min Value Text Box. */
    @UiField
    TextBox minValueTextBox;
    /** Max Value Text Box. */
    @UiField
    TextBox maxValueTextBox;

    // -------------------------------------
    // Labels.
    // -------------------------------------
    /** Logged-in Label. */
    @UiField
    Label loggedInLabel;

    // -------------------------------------
    // Panels.
    // -------------------------------------
    /** Visibility Panel. */
    @UiField
    VerticalPanel visibilityPanel;
    /** Graph Panel. */
    @UiField
    AbsolutePanel graphPanel;
    /** Legend Panel. */
    @UiField
    HTMLPanel legendPanel;
    /** Help Panel. */
    @UiField
    VerticalPanel helpPanel;

    // -------------------------------------
    // Handler Registrations
    // -------------------------------------
    /** Server Change Handler Registration. */
    private HandlerRegistration changeServerHandlerReg;
    /** GPRC Change Handler Registration. */
    private HandlerRegistration changeGPRCHandlerReg;
    /** Correction Type Change Handler Registration. */
    private HandlerRegistration changeCorrTypeHandlerReg;
    /** Sat/Instr Change Handler Registration. */
    private HandlerRegistration changeSatInstrHandlerReg;
    /** Ref Sat/Instr Change Handler Registration. */
    private HandlerRegistration changeRefSatInstrHandlerReg;
    /** Mode Change Handler Registration. */
    private HandlerRegistration changeModeHandlerReg;
    /** Year Change Handler Registration. */
    private HandlerRegistration changeYearHandlerReg;
    /** DateTime Change Handler Registration. */
    private HandlerRegistration changeDateTimeHandlerReg;
    /** Version Change Handler Registration. */
    private HandlerRegistration changeVersionHandlerReg;
    /** Channel Change Handler Registration. */
    private HandlerRegistration changeChannelHandlerReg;
    /** Login Click Handler Registration. */
    private HandlerRegistration clickLoginHandlerReg;
    /** Logout Click Handler Registration. */
    private HandlerRegistration clickLogoutHandlerReg;
    /** Register Click Handler Registration. */
    private HandlerRegistration clickRegisterHandlerReg;
    /** Plot Saved Click Handler Registration. */
    private HandlerRegistration clickPlotSavedHandlerReg;
    /** Remove Selected Click Handler Registration. */
    private HandlerRegistration clickRemoveSelectedHandlerReg;
    /** Remove All Click Handler Registration. */
    private HandlerRegistration clickRemoveAllHandlerReg;
    /** Save Click Handler Registration. */
    private HandlerRegistration clickSaveHandlerReg;
    /** Export Click Handler Registration. */
    private HandlerRegistration clickExportHandlerReg;
    /** URL Access Click Handler Registration. */
    private HandlerRegistration clickUrlAccessHandlerReg;
    /** Static URL Access Click Handler Registration. */
    private HandlerRegistration clickUrlStaticAccessHandlerReg;
    /** Auto Value Range Click Handler Registration. */
    private HandlerRegistration clickAutoValueRangeHandlerReg;
    /** Min Value Click Handler Registration. */
    private HandlerRegistration changeMinValueHandlerReg;
    /** Max Value Click Handler Registration. */
    private HandlerRegistration changeMaxValueHandlerReg;
    /** Min Value KeyUp Handler Registration. */
    private HandlerRegistration keyUpMinValueHandlerReg;
    /** Max Value KeyUp Handler Registration. */
    private HandlerRegistration keyUpMaxValueHandlerReg;
    /** Plot Click Handler Registration. */
    private HandlerRegistration clickPlotHandlerReg;
    /** Clear Click Handler Registration. */
    private HandlerRegistration clickClearHandlerReg;
    /** Help Click Handler Registration. */
    private HandlerRegistration clickHelpHandlerReg;

    /** Widget. */
    private final Widget widget;

    /** Loading Animation. */
    @Inject
    private LoadingPresenter loadingAnimation;

    /** Scene Tb selected. */
    private double sceneTb = -1.0;

    /** Menu Stack Panel Options' Ids. */
    public enum MenuOption
    {
        CONFIGANDPLOT(0), SAVEANDLOAD(1), EXPORT(2), CURRENTPLOTS(3), HELP(4);

        private final int id;

        MenuOption(int id)
        {
            this.id = id;
        }

        public int getId()
        {
            return id;
        }
    };

    /**
     * Binder Interface.
     */
    public interface Binder extends UiBinder<Widget, PlotView>
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
    public PlotView(final Binder binder, final Resources resources)
    {
        resources.style().ensureInjected();
        widget = binder.createAndBindUi(this);

        // Add selection handler for main menu.
        menuStackPanel.addSelectionHandler(new SelectionHandler<Integer>()
        {
            @Override
            public void onSelection(final SelectionEvent<Integer> event)
            {
                // @formatter:off
                /*
                 * -- UiBinder layout for main menu stack headers -- 
                 * <g:FlowPanel> 
                 *   <g:HTMLPanel width="100%" height="100%">
                 *     <div id="floater"></div> 
                 *     <g:Image styleName="{resources.style.stackArrow}" resource="{resources.downArrow}" />
                 *     <g:Label styleName="{resources.style.headerLabel}" text="Configuration and Plot" /> 
                 *   </g:HTMLPanel> 
                 * </g:FlowPanel>
                 */
                // @formatter:on

                // down arrow set for header of selected stack.
                // right arrow set for the rest.
                for (int i = 0; i < menuStackPanel.getWidgetCount(); i++)
                {
                    // Navigate down to the arrow image.
                    FlowPanel header = (FlowPanel) menuStackPanel.getHeaderWidget(i);
                    HTMLPanel headerContainer = (HTMLPanel) header.getWidget(0);
                    Image headerArrow = (Image) headerContainer.getWidget(0);

                    // Set it to down or right arrow accordingly.
                    if (i == event.getSelectedItem())
                    {
                        headerArrow.setResource(Resources.INSTANCE.downArrow());

                    } else
                    {
                        headerArrow.setResource(Resources.INSTANCE.rightArrow());
                    }
                }
            }
        });

        // Add change handler for saved plot change.
        savedPlotConfigsListBox.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(final ChangeEvent event)
            {
                // Remove button enabled only when selecting a user saved plot
                if (savedPlotConfigsListBox.getSelectedIndex() < 3) // 3 default plots.
                {
                    removeSelectedButton.setEnabled(false);

                } else
                {
                    removeSelectedButton.setEnabled(true);
                }
            }
        });

        // Configure scene tb slider bar.
        sceneTbSliderBar.setWidth("23em");
        sceneTbSliderBar.setStepSize(1.0);
        sceneTbSliderBar.setNumTicks(10);
        sceneTbSliderBar.setNumLabels(5);
        sceneTbSliderBar.setCurrentValue(200.0);

        // Clear saved plot name text box.
        clearSavedPlotNameTextBox();

        // Add value change handler to scene tb slider bar.
        sceneTbSliderBar.addValueChangeHandler(new ValueChangeHandler<Double>()
        {
            @Override
            public void onValueChange(final ValueChangeEvent<Double> event)
            {
                sceneTbMonitor.setValue(event.getValue().toString() + " K");
                sceneTb = event.getValue();
            }
        });

        // Add click handler to scene tb slider bar.
        sceneTbSliderBar.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                ((SliderBar) event.getSource()).setEnabled(true);
            }
        });

        // Make that clicking deletes current text in saved plot name text box.
        savedPlotNameTextBox.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(final ClickEvent event)
            {
                savedPlotNameTextBox.setText("");
                savedPlotNameTextBox.setFocus(true);
            }
        });

        // Add some keyboard handlers to the saved plot name text box.
        savedPlotNameTextBox.addKeyDownHandler(new KeyDownHandler()
        {
            @Override
            public void onKeyDown(final KeyDownEvent event)
            {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE)
                {
                    clearSavedPlotNameTextBox();

                } else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
                {
                    saveButton.click();
                }
            }
        });

        // disable auto button (doing this in uibinder does not work).
        autoValueRangeButton.setEnabled(false);
    }

    @Override
    public final Widget asWidget()
    {
        return widget;
    }

    // ------------------------------------------------------
    // Configuration parameters change handlers.
    // ------------------------------------------------------

    @Override
    public final void setServerChangeHandler(final ChangeHandler changeHandler)
    {
        // remove old handler if exists.
        if (changeServerHandlerReg != null)
        {
            changeServerHandlerReg.removeHandler();
        }

        // add new handler.
        changeServerHandlerReg = serverListBox.addChangeHandler(changeHandler);
    }

    @Override
    public final void setGPRCChangeHandler(final ChangeHandler changeHandler)
    {
        // remove old handler if exists.
        if (changeGPRCHandlerReg != null)
        {
            changeGPRCHandlerReg.removeHandler();
        }

        // add new handler.
        changeGPRCHandlerReg = gprcListBox.addChangeHandler(changeHandler);
    }

    @Override
    public final void setCorrTypeChangeHandler(final ChangeHandler changeHandler)
    {
        // remove old handler if exists.
        if (changeCorrTypeHandlerReg != null)
        {
            changeCorrTypeHandlerReg.removeHandler();
        }

        // add new handler.
        changeCorrTypeHandlerReg = corrTypeListBox.addChangeHandler(changeHandler);
    }

    @Override
    public final void setSatInstrChangeHandler(final ChangeHandler changeHandler)
    {
        // remove old handler if exists.
        if (changeSatInstrHandlerReg != null)
        {
            changeSatInstrHandlerReg.removeHandler();
        }

        // add new handler.
        changeSatInstrHandlerReg = satInstrListBox.addChangeHandler(changeHandler);
    }

    @Override
    public final void setRefSatInstrChangeHandler(final ChangeHandler changeHandler)
    {
        // remove old handler if exists.
        if (changeRefSatInstrHandlerReg != null)
        {
            changeRefSatInstrHandlerReg.removeHandler();
        }

        // add new handler.
        changeRefSatInstrHandlerReg = refSatInstrListBox.addChangeHandler(changeHandler);
    }

    @Override
    public final void setModeChangeHandler(final ChangeHandler changeHandler)
    {
        // remove old handler if exists.
        if (changeModeHandlerReg != null)
        {
            changeModeHandlerReg.removeHandler();
        }

        // add new handler.
        changeModeHandlerReg = modeListBox.addChangeHandler(changeHandler);
    }

    @Override
    public final void setYearChangeHandler(final ChangeHandler changeHandler)
    {
        // remove old handler if exists.
        if (changeYearHandlerReg != null)
        {
            changeYearHandlerReg.removeHandler();
        }

        // add new handler.
        changeYearHandlerReg = yearListBox.addChangeHandler(changeHandler);
    }

    @Override
    public final void setDateTimeChangeHandler(final ChangeHandler changeHandler)
    {
        // remove old handler if exists.
        if (changeDateTimeHandlerReg != null)
        {
            changeDateTimeHandlerReg.removeHandler();
        }

        // add new handler.
        changeDateTimeHandlerReg = dateTimeListBox.addChangeHandler(changeHandler);
    }

    @Override
    public final void setVersionChangeHandler(final ChangeHandler changeHandler)
    {
        // remove old handler if exists.
        if (changeVersionHandlerReg != null)
        {
            changeVersionHandlerReg.removeHandler();
        }

        // add new handler.
        changeVersionHandlerReg = versionListBox.addChangeHandler(changeHandler);
    }

    @Override
    public final void setChannelChangeHandler(final ChangeHandler changeHandler)
    {
        // remove old handler if exists.
        if (changeChannelHandlerReg != null)
        {
            changeChannelHandlerReg.removeHandler();
        }

        // add new handler.
        changeChannelHandlerReg = channelListBox.addChangeHandler(changeHandler);
    }

    // ------------------------------------------------------
    // Click handlers.
    // ------------------------------------------------------

    @Override
    public final void setLoginHandler(final ClickHandler loginHandler)
    {
        // remove old handler if exists.
        if (clickLoginHandlerReg != null)
        {
            clickLoginHandlerReg.removeHandler();
        }

        // add new handler.
        clickLoginHandlerReg = loginButton.addClickHandler(loginHandler);
    }

    @Override
    public final void setLogoutHandler(final ClickHandler logoutHandler)
    {
        // remove old handler if exists.
        if (clickLogoutHandlerReg != null)
        {
            clickLogoutHandlerReg.removeHandler();
        }

        // add new handler.
        clickLogoutHandlerReg = logoutButton.addClickHandler(logoutHandler);
    }

    @Override
    public final void setRegisterHandler(final ClickHandler registerHandler)
    {
        // remove old handler if exists.
        if (clickRegisterHandlerReg != null)
        {
            clickRegisterHandlerReg.removeHandler();
        }

        // add new handler.
        clickRegisterHandlerReg = registerButton.addClickHandler(registerHandler);
    }

    @Override
    public final void setPlotSavedHandler(final ClickHandler plotSavedHandler)
    {
        // remove old handler if exists.
        if (clickPlotSavedHandlerReg != null)
        {
            clickPlotSavedHandlerReg.removeHandler();
        }

        // add new handler.
        clickPlotSavedHandlerReg = plotSavedButton.addClickHandler(plotSavedHandler);
    }

    @Override
    public final void setRemoveSelectedHandler(final ClickHandler removeSelectedHandler)
    {
        // remove old handler if exists.
        if (clickRemoveSelectedHandlerReg != null)
        {
            clickRemoveSelectedHandlerReg.removeHandler();
        }

        // add new handler.
        clickRemoveSelectedHandlerReg = removeSelectedButton.addClickHandler(removeSelectedHandler);
    }

    @Override
    public final void setRemoveAllHandler(final ClickHandler removeAllHandler)
    {
        // remove old handler if exists.
        if (clickRemoveAllHandlerReg != null)
        {
            clickRemoveAllHandlerReg.removeHandler();
        }

        // add new handler.
        clickRemoveAllHandlerReg = removeAllButton.addClickHandler(removeAllHandler);
    }

    @Override
    public final void setSavePlotHandler(final ClickHandler savePlotHandler)
    {
        // remove old handler if exists.
        if (clickSaveHandlerReg != null)
        {
            clickSaveHandlerReg.removeHandler();
        }

        // add new handler.
        clickSaveHandlerReg = saveButton.addClickHandler(savePlotHandler);
    }

    @Override
    public final void setPlotHandler(final ClickHandler plotHandler)
    {
        // remove old handler if exists.
        if (clickPlotHandlerReg != null)
        {
            clickPlotHandlerReg.removeHandler();
        }

        // add new handler.
        clickPlotHandlerReg = plotButton.addClickHandler(plotHandler);
    }

    @Override
    public final void setClearPlotHandler(final ClickHandler clearPlotHandler)
    {
        // remove old handler if exists.
        if (clickClearHandlerReg != null)
        {
            clickClearHandlerReg.removeHandler();
        }

        // add new handler.
        clickClearHandlerReg = clearButton.addClickHandler(clearPlotHandler);
    }

    @Override
    public final void setExportHandler(final ClickHandler exportHandler)
    {
        // remove old handler if exists.
        if (clickExportHandlerReg != null)
        {
            clickExportHandlerReg.removeHandler();
        }

        // add new handler.
        clickExportHandlerReg = exportButton.addClickHandler(exportHandler);
    }

    @Override
    public final void setUrlAccessHandler(final ClickHandler urlAccessHandler)
    {
        // remove old handler if exists.
        if (clickUrlAccessHandlerReg != null)
        {
            clickUrlAccessHandlerReg.removeHandler();
        }

        // add new handler.
        clickUrlAccessHandlerReg = urlAccessButton.addClickHandler(urlAccessHandler);
    }

    @Override
    public final void setUrlStaticAccessHandler(final ClickHandler urlStaticAccessHandler)
    {
        // remove old handler if exists.
        if (clickUrlStaticAccessHandlerReg != null)
        {
            clickUrlStaticAccessHandlerReg.removeHandler();
        }

        // add new handler.
        clickUrlStaticAccessHandlerReg = urlStaticAccessButton.addClickHandler(urlStaticAccessHandler);
    }

    @Override
    public final void setAutoValueRangeHandler(final ClickHandler autoValueRangeHandler)
    {
        // remove old handler if exists.
        if (clickAutoValueRangeHandlerReg != null)
        {
            clickAutoValueRangeHandlerReg.removeHandler();
        }

        // add new handler.
        clickAutoValueRangeHandlerReg = autoValueRangeButton.addClickHandler(autoValueRangeHandler);
    }

    @Override
    public final void setValueRangeChangeHandler(final ChangeHandler valueRangeChangeHandler)
    {
        // remove old handlers if exists.
        if (changeMinValueHandlerReg != null)
        {
            changeMinValueHandlerReg.removeHandler();
        }

        if (changeMaxValueHandlerReg != null)
        {
            changeMaxValueHandlerReg.removeHandler();
        }

        // add new handlers.
        changeMinValueHandlerReg = minValueTextBox.addChangeHandler(valueRangeChangeHandler);
        changeMaxValueHandlerReg = maxValueTextBox.addChangeHandler(valueRangeChangeHandler);
    }

    @Override
    public final void setValueRangeKeyUpHandler(final KeyUpHandler valueRangeKeyUpHandler)
    {
        // remove old handlers if exists.
        if (keyUpMinValueHandlerReg != null)
        {
            keyUpMinValueHandlerReg.removeHandler();
        }

        if (keyUpMaxValueHandlerReg != null)
        {
            keyUpMaxValueHandlerReg.removeHandler();
        }

        // add new handlers.
        keyUpMinValueHandlerReg = minValueTextBox.addKeyUpHandler(valueRangeKeyUpHandler);
        keyUpMaxValueHandlerReg = maxValueTextBox.addKeyUpHandler(valueRangeKeyUpHandler);
    }

    @Override
    public final void setHelpHandler(final ClickHandler helpHandler)
    {
        // remove old handler if exists.
        if (clickHelpHandlerReg != null)
        {
            clickHelpHandlerReg.removeHandler();
        }

        // add new handler.
        clickHelpHandlerReg = helpButton.addClickHandler(helpHandler);
    }

    // ------------------------------------------------------
    // Clear lists and sets "Loading...".
    // ------------------------------------------------------

    @Override
    public final void setServerListToLoading()
    {
        setListToLoading(serverListBox);
    }

    @Override
    public final void setGPRCListToLoading()
    {
        setListToLoading(gprcListBox);
    }

    @Override
    public final void setCorrTypeListToLoading()
    {
        setListToLoading(corrTypeListBox);
    }

    @Override
    public final void setSatInstrListToLoading()
    {
        setListToLoading(satInstrListBox);
    }

    @Override
    public final void setRefSatInstrListToLoading()
    {
        setListToLoading(refSatInstrListBox);
    }

    @Override
    public final void setModeListToLoading()
    {
        setListToLoading(modeListBox);
    }

    @Override
    public final void setYearListToLoading()
    {
        setListToLoading(yearListBox);
    }

    @Override
    public final void setDateTimeListToLoading()
    {
        setListToLoading(dateTimeListBox);
    }

    @Override
    public final void setVersionListToLoading()
    {
        setListToLoading(versionListBox);
    }

    @Override
    public final void setChannelListToLoading()
    {
        setListToLoading(channelListBox);
    }

    // ------------------------------------------------------
    // Clear lists and sets "No data".
    // ------------------------------------------------------

    @Override
    public final void setServerListToNoData()
    {
        setListToNoData(serverListBox);
    }

    @Override
    public final void setGPRCListToNoData()
    {
        setListToNoData(gprcListBox);
    }

    @Override
    public final void setCorrTypeListToNoData()
    {
        setListToNoData(corrTypeListBox);
    }

    @Override
    public final void setSatInstrListToNoData()
    {
        setListToNoData(satInstrListBox);
    }

    @Override
    public final void setRefSatInstrListToNoData()
    {
        setListToNoData(refSatInstrListBox);
    }

    @Override
    public final void setModeListToNoData()
    {
        setListToNoData(modeListBox);
    }

    @Override
    public final void setYearListToNoData()
    {
        setListToNoData(yearListBox);
    }

    @Override
    public final void setDateTimeListToNoData()
    {
        setListToNoData(dateTimeListBox);
    }

    @Override
    public final void setVersionListToNoData()
    {
        setListToNoData(versionListBox);
    }

    @Override
    public final void setChannelListToNoData()
    {
        setListToNoData(channelListBox);
    }

    // ------------------------------------------------------
    // Add option for a configuration parameter.
    // ------------------------------------------------------

    @Override
    public final void addServer(final String server)
    {
        serverListBox.addItem(server);
    }

    @Override
    public final void addGPRC(final String gprc)
    {
        gprcListBox.addItem(gprc);
    }

    @Override
    public final void addCorrType(final String corrType)
    {
        corrTypeListBox.addItem(corrType);
    }

    @Override
    public final void addSatInstr(final String satInstr)
    {
        satInstrListBox.addItem(satInstr);
    }

    @Override
    public final void addRefSatInstr(final String refSatInstr)
    {
        refSatInstrListBox.addItem(refSatInstr);
    }

    @Override
    public final void addMode(final String mode)
    {
        modeListBox.addItem(mode);
    }

    @Override
    public final void addYear(final String year)
    {
        yearListBox.addItem(year);
    }

    @Override
    public final void addDateTime(final String dateTime)
    {
        dateTimeListBox.addItem(dateTime);
    }

    @Override
    public final void addVersion(final String version)
    {
        versionListBox.addItem(version);
    }

    @Override
    public final void addChannel(final String channel)
    {
        channelListBox.addItem(channel);
    }

    @Override
    public final void addSavedPlotConfig(final String savedPlotConfig)
    {
        savedPlotConfigsListBox.addItem(savedPlotConfig);
    }

    // ------------------------------------------------------
    // Set Scene Tb from dataset.
    // ------------------------------------------------------

    @Override
    public final void setSelectedSceneTb(final double sceneTb)
    {
        sceneTbSliderBar.setCurrentValue(sceneTb);
        sceneTbMonitor.setValue(Math.round(sceneTb) + " K");
        this.sceneTb = sceneTb;
    }

    // ------------------------------------------------------
    // Clear configuration lists.
    // ------------------------------------------------------

    @Override
    public final void clearServerList()
    {
        serverListBox.clear();
    }

    @Override
    public final void clearGPRCList()
    {
        gprcListBox.clear();
    }

    @Override
    public final void clearCorrTypeList()
    {
        corrTypeListBox.clear();
    }

    @Override
    public final void clearSatInstrList()
    {
        satInstrListBox.clear();
    }

    @Override
    public final void clearRefSatInstrList()
    {
        refSatInstrListBox.clear();
    }

    @Override
    public final void clearModeList()
    {
        modeListBox.clear();
    }

    @Override
    public final void clearYearList()
    {
        yearListBox.clear();
    }

    @Override
    public final void clearDateTimeList()
    {
        dateTimeListBox.clear();
    }

    @Override
    public final void clearVersionList()
    {
        versionListBox.clear();
    }

    @Override
    public final void clearChannelList()
    {
        channelListBox.clear();
    }

    @Override
    public final void removeSelectedSavedPlotConfig()
    {
        int selectedIndex = savedPlotConfigsListBox.getSelectedIndex();
        savedPlotConfigsListBox.removeItem(selectedIndex);
        savedPlotConfigsListBox.setSelectedIndex(selectedIndex - 1);

        // force firing change event, so that remove button is disabled accordingly.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), savedPlotConfigsListBox);
    }

    @Override
    public final void clearSavedPlotConfigsList()
    {
        savedPlotConfigsListBox.clear();
    }

    @Override
    public final void clearSavedPlotNameTextBox()
    {
        savedPlotNameTextBox.setText("");
        savedPlotNameTextBox.setFocus(false);
    }

    @Override
    public final void clearValueRangeTextBoxes()
    {
        minValueTextBox.setText("");
        minValueTextBox.setFocus(false);
        maxValueTextBox.setText("");
        maxValueTextBox.setFocus(false);
    }

    // ------------------------------------------------------
    // Simulate user selection of a configuration parameter.
    // ------------------------------------------------------

    @Override
    public final void selectServer(final int indexToSelect)
    {
        // select item (default or, if defined, the one from URL)
        serverListBox.setSelectedIndex(indexToSelect);

        // Fire a ChangeEvent as if the user selected the first element.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), serverListBox);
    }

    @Override
    public final void selectGPRC(final int indexToSelect)
    {
        // select item (default or, if defined, the one from URL)
        gprcListBox.setSelectedIndex(indexToSelect);

        // Fire a ChangeEvent as if the user selected the first element.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), gprcListBox);
    }

    @Override
    public final void selectCorrType(final int indexToSelect)
    {
        // select item (default or, if defined, the one from URL)
        corrTypeListBox.setSelectedIndex(indexToSelect);

        // Fire a ChangeEvent as if the user selected the first element.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), corrTypeListBox);
    }

    @Override
    public final void selectSatInstr(final int indexToSelect)
    {
        // select item (default or, if defined, the one from URL)
        satInstrListBox.setSelectedIndex(indexToSelect);

        // Fire a ChangeEvent as if the user selected the first element.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), satInstrListBox);
    }

    @Override
    public final void selectRefSatInstr(final int indexToSelect)
    {
        // select item (default or, if defined, the one from URL)
        refSatInstrListBox.setSelectedIndex(indexToSelect);

        // Fire a ChangeEvent as if the user selected the first element.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), refSatInstrListBox);
    }

    @Override
    public final void selectMode(final int indexToSelect)
    {
        // select item (default or, if defined, the one from URL)
        modeListBox.setSelectedIndex(indexToSelect);

        // Fire a ChangeEvent as if the user selected the first element.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), modeListBox);
    }

    @Override
    public final void selectYear(final int indexToSelect)
    {
        // select item (default or, if defined, the one from URL)
        yearListBox.setSelectedIndex(indexToSelect);

        // Fire a ChangeEvent as if the user selected the first element.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), yearListBox);
    }

    @Override
    public final void selectDateTime(final int indexToSelect)
    {
        // select item (default or, if defined, the one from URL)
        dateTimeListBox.setSelectedIndex(indexToSelect);

        // Fire a ChangeEvent as if the user selected the first element.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), dateTimeListBox);
    }

    @Override
    public final void selectVersion(final int indexToSelect)
    {
        // select item (default or, if defined, the one from URL)
        versionListBox.setSelectedIndex(indexToSelect);

        // Fire a ChangeEvent as if the user selected the first element.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), versionListBox);
    }

    @Override
    public final void selectChannel(final int indexToSelect)
    {
        // select item (default or, if defined, the one from URL)
        channelListBox.setSelectedIndex(indexToSelect);

        // Fire a ChangeEvent as if the user selected the first element.
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), channelListBox);
    }

    @Override
    public final void selectSavedPlot()
    {
        savedPlotConfigsListBox.setSelectedIndex(savedPlotConfigsListBox.getItemCount() - 1);
    }

    // ------------------------------------------------------
    // Get selected configuration parameters.
    // ------------------------------------------------------

    @Override
    public final String getSelectedServer()
    {
        return serverListBox.getValue(serverListBox.getSelectedIndex());
    }

    @Override
    public final String getSelectedGPRC()
    {
        return gprcListBox.getValue(gprcListBox.getSelectedIndex());
    }

    @Override
    public final String getSelectedCorrType()
    {
        return corrTypeListBox.getValue(corrTypeListBox.getSelectedIndex());
    }

    @Override
    public final String getSelectedSatInstr()
    {
        return satInstrListBox.getValue(satInstrListBox.getSelectedIndex());
    }

    @Override
    public final String getSelectedRefSatInstr()
    {
        return refSatInstrListBox.getValue(refSatInstrListBox.getSelectedIndex());
    }

    @Override
    public final String getSelectedMode()
    {
        return modeListBox.getValue(modeListBox.getSelectedIndex());
    }

    @Override
    public final String getSelectedYear()
    {
        return yearListBox.getValue(yearListBox.getSelectedIndex());
    }

    @Override
    public final String getSelectedDateTime()
    {
        return dateTimeListBox.getValue(dateTimeListBox.getSelectedIndex());
    }

    @Override
    public final String getSelectedVersion()
    {
        return versionListBox.getValue(versionListBox.getSelectedIndex());
    }

    @Override
    public final String getSelectedChannel()
    {
        return channelListBox.getValue(channelListBox.getSelectedIndex());
    }

    @Override
    public final double getSelectedSceneTb()
    {
        return sceneTb;
    }

    @Override
    public final String getSelectedSavedPlotConfig()
    {
        return savedPlotConfigsListBox.getValue(savedPlotConfigsListBox.getSelectedIndex());
    }

    @Override
    public final String getEnteredSavedPlotName()
    {
        return savedPlotNameTextBox.getText();
    }

    @Override
    public final String getEnteredMinValue()
    {
        return minValueTextBox.getText();
    }

    @Override
    public final String getEnteredMaxValue()
    {
        return maxValueTextBox.getText();
    }

    @Override
    public final boolean isAutoValueRangePressed()
    {
        return autoValueRangeButton.isDown();
    }

    // ------------------------------------------------------
    // Search mode with data.
    // ------------------------------------------------------

    @Override
    public final int searchAndSelectMode(final String targetMode)
    {
        // Search for the index of targetMode in the list box.
        RegExp regExp = RegExp.compile("^" + targetMode + ".*");

        int index = -1;
        for (int i = 0; i < modeListBox.getItemCount(); i++)
        {
            if (regExp.test(modeListBox.getValue(i).toLowerCase()))
            {
                index = i;
                modeListBox.setSelectedIndex(index);
            }
        }

        return index;
    }

    // ------------------------------------------------------
    // Enable methods.
    // ------------------------------------------------------

    @Override
    public final void enableSceneTbSelector()
    {
        sceneTbSliderBar.setEnabled(true);
    }

    @Override
    public final void enableRemoveSelectedButton()
    {
        removeSelectedButton.setEnabled(true);
    }

    @Override
    public final void enableRemoveAllButton()
    {
        removeAllButton.setEnabled(true);
    }

    @Override
    public final void enableClearButton()
    {
        clearButton.setEnabled(true);
    }

    @Override
    public final void enablePlotButton()
    {
        plotButton.setEnabled(true);
    }

    @Override
    public final void enableSaveButton()
    {
        saveButton.setEnabled(true);
    }

    @Override
    public final void enableExportButton()
    {
        exportButton.setEnabled(true);
    }

    @Override
    public final void enableUrlAccessButton()
    {
        urlAccessButton.setEnabled(true);
    }

    @Override
    public final void enableUrlStaticAccessButton()
    {
        urlStaticAccessButton.setEnabled(true);
    }

    @Override
    public final void enableSavedPlotNameTextBox()
    {
        savedPlotNameTextBox.setEnabled(true);
    }

    @Override
    public final void enableValueRangeTextBoxes()
    {
        minValueTextBox.setEnabled(true);
        maxValueTextBox.setEnabled(true);
    }

    @Override
    public final void enableAutoValueRangeButton()
    {
        autoValueRangeButton.setEnabled(true);
    }

    // ------------------------------------------------------
    // Disable methods.
    // ------------------------------------------------------

    @Override
    public final void disableSceneTbSelector()
    {
        sceneTbSliderBar.setEnabled(false);
        sceneTbMonitor.setValue("Standard");
        sceneTb = -1.0;
    }

    @Override
    public final void disableRemoveAllButton()
    {
        removeAllButton.setEnabled(false);
    }

    @Override
    public final void disableRemoveSelectedButton()
    {
        removeSelectedButton.setEnabled(false);
    }

    @Override
    public final void disableClearButton()
    {
        clearButton.setEnabled(false);
    }

    @Override
    public final void disablePlotButton()
    {
        plotButton.setEnabled(false);
    }

    @Override
    public final void disableSaveButton()
    {
        saveButton.setEnabled(false);
    }

    @Override
    public final void disableExportButton()
    {
        exportButton.setEnabled(false);
    }

    @Override
    public final void disableUrlAccessButton()
    {
        urlAccessButton.setEnabled(false);
    }

    @Override
    public final void disableUrlStaticAccessButton()
    {
        urlStaticAccessButton.setEnabled(false);
    }

    @Override
    public final void disableSavedPlotNameTextBox()
    {
        savedPlotNameTextBox.setEnabled(false);
    }

    @Override
    public final void disableValueRangeTextBoxes()
    {
        minValueTextBox.setEnabled(false);
        maxValueTextBox.setEnabled(false);
    }

    @Override
    public final void disableAutoValueRangeButton()
    {
        autoValueRangeButton.setEnabled(false);
    }

    // ------------------------------------------------------
    // Show/hide elements in GUI.
    // ------------------------------------------------------

    @Override
    public final void changeToLoggedIn(final String username)
    {
        loginButton.setVisible(false);
        registerButton.setVisible(false);
        logoutButton.setVisible(true);
        loggedInLabel.setText(username + " logged in");
        loggedInLabel.setVisible(true);
    }

    @Override
    public final void changeToLoggedOut()
    {
        loggedInLabel.setVisible(false);
        loggedInLabel.setText("");
        logoutButton.setVisible(false);
        registerButton.setVisible(true);
        loginButton.setVisible(true);
    }

    @Override
    public final void showLoadingAnimation()
    {
        // put up loading animation.
        int left = (graphPanel.getOffsetWidth() - loadingAnimation.asWidget().getOffsetWidth()) >> 1;
        int top = (graphPanel.getOffsetHeight() - loadingAnimation.asWidget().getOffsetHeight()) >> 1;
        
        graphPanel.add(loadingAnimation.asWidget(), Math.max(left, 0), Math.max(top, 0));
        
    }

    @Override
    public final void hideLoadingAnimation()
    {
        graphPanel.remove(loadingAnimation.asWidget());
    }

    @Override
    public final void showLegend()
    {
        legendPanel.setVisible(true);
    }

    @Override
    public final void hideLegend()
    {
        legendPanel.setVisible(false);
    }

    @Override
    public final void clearVisibilityPanel()
    {
        visibilityPanel.clear();
    }

    @Override
    public final void addPlotToVisibilityPanel(final HorizontalPanel plotCheckBox)
    {
        visibilityPanel.add(plotCheckBox);
    }

    @Override
    public final void clickPlotButton()
    {
        plotButton.click();
    }

    @Override
    public final AbsolutePanel getGraphPanel()
    {
        return graphPanel;
    }

    @Override
    public final void markMinValueInvalid()
    {
        minValueTextBox.getElement().getStyle().setBorderColor("red");
        minValueTextBox.getElement().getStyle().setBorderWidth(2, Unit.PX);
    }

    @Override
    public final void markMinValueValid()
    {
        minValueTextBox.getElement().getStyle().clearBorderColor();
        minValueTextBox.getElement().getStyle().clearBorderWidth();
    }

    @Override
    public final void markMaxValueInvalid()
    {
        maxValueTextBox.getElement().getStyle().setBorderColor("red");
        maxValueTextBox.getElement().getStyle().setBorderWidth(2, Unit.PX);
    }

    @Override
    public final void markMaxValueValid()
    {
        maxValueTextBox.getElement().getStyle().clearBorderColor();
        maxValueTextBox.getElement().getStyle().clearBorderWidth();
    }

    @Override
    public void addItemToHelpPanel(final HelpItem helpItem)
    {
        Anchor link = new Anchor(true);

        link.setText(helpItem.getLabel());
        link.setTitle(helpItem.getDescription());
        link.setHref(helpItem.getUrl());
        link.setTarget("_blank");

        helpPanel.add(link);
    }

    @Override
    public final void setActiveMenuOption(MenuOption option)
    {
        menuStackPanel.showWidget(option.getId());
    }

    // ------------------------------------------------------
    // Private methods.
    // ------------------------------------------------------

    /**
     * Clears a list box and adds a single item with text "Loading...".
     * 
     * @param listBox
     *            the list box to be set to "Loading..."
     */
    private void setListToLoading(final ListBox listBox)
    {
        listBox.clear();
        listBox.addItem("Loading...");
    }

    /**
     * Clears a list box and adds a single item with text "No data".
     * 
     * @param listBox
     *            the list box to be cleared.
     */
    private void setListToNoData(final ListBox listBox)
    {
        listBox.clear();

        listBox.addItem("No data");
    }

}
