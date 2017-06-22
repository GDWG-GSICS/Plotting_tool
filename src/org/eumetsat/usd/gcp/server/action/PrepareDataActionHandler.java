// @formatter:off
/*
 * PROJECT: gcp 
 * AUTHOR: USC/C/PBe 
 * COPYRIGHT: EUMETSAT 2015
 */
// @formatter:on
package org.eumetsat.usd.gcp.server.action;

import java.util.ArrayList;

import org.eumetsat.usd.gcp.server.catalog.CatalogNavigator;
import org.eumetsat.usd.gcp.server.data.CalibrationDataManager;
import org.eumetsat.usd.gcp.server.guice.CatalogNavigatorFactory;
import org.eumetsat.usd.gcp.shared.action.PrepareData;
import org.eumetsat.usd.gcp.shared.action.PrepareDataResult;
import org.eumetsat.usd.gcp.shared.conf.PlotConfiguration;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Prepare Data Action Handler. Generated by GWTP Plugin.
 * 
 * @author USD/C/PBe
 */
public class PrepareDataActionHandler implements ActionHandler<PrepareData, PrepareDataResult>
{
    /** Catalog Navigator Factory. */
    private final CatalogNavigatorFactory catalogNavigatorFactory;

    /** Calibration Data Manager. */
    private final CalibrationDataManager calibrationDataManager;

    /**
     * Constructor.
     * 
     * @param catalogNavigatorFactory
     *            factory for catalog navigator.
     * @param calibrationDataManager
     *            calibration data manager.
     */
    @Inject
    public PrepareDataActionHandler(final CatalogNavigatorFactory catalogNavigatorFactory,
            final CalibrationDataManager calibrationDataManager)
    {
        this.catalogNavigatorFactory = catalogNavigatorFactory;
        this.calibrationDataManager = calibrationDataManager;
    }

    @Override
    public final PrepareDataResult execute(final PrepareData action, final ExecutionContext context)
            throws ActionException
    {
        // Get target URL, either using the catalog navigator with an input plot configuration, or
        // using an input dataset URL.
        PlotConfiguration plotConfig = action.getPlotConfiguration();

        String datasetURL = null;
        String channelName = null;
        double sceneTb = -1;

        if (plotConfig != null)
        {
            // Get corresponding catalog navigator.
            CatalogNavigator catalogNavigator = catalogNavigatorFactory.create(plotConfig.getServer());

            // Get url pointing to NetCDF file.
            datasetURL = catalogNavigator.getDatasetURL(plotConfig.getGPRC(), plotConfig.getCorrType(),
                    plotConfig.getSatInstr(), plotConfig.getRefSatInstr(), plotConfig.getMode(), plotConfig.getYear(),
                    plotConfig.getDateTime(), plotConfig.getVersion());

            channelName = plotConfig.getChannel();
            sceneTb = plotConfig.getSceneTb();

        } else
        {
            String userDatasetURL = action.getDatasetUrl();

            if (userDatasetURL != null)
            {
                datasetURL = userDatasetURL;
                channelName = "All";
                sceneTb = -1;

            } else
            {
                throw new ActionException("Invalid inputs in PrepareData request.");
            }
        }

        // Get data from netCDF file and store it to reuse in future connections of this user.
        calibrationDataManager.addDataFromDatasetForUser(action.getUserID(), datasetURL, channelName, sceneTb);

        // Exports current data to a CSV file with the userID in its filename.
        calibrationDataManager.exportToCSVForUser(action.getUserID());

        // Return the datasets' names for dygraph.
        return new PrepareDataResult(new ArrayList<String>(calibrationDataManager.addedSourcesForUser(
                action.getUserID()).keySet()), calibrationDataManager.addedSourcesForUser(action.getUserID()));
    }

    @Override
    public final void undo(final PrepareData action, final PrepareDataResult result, final ExecutionContext context)
            throws ActionException
    {
    }

    @Override
    public final Class<PrepareData> getActionType()
    {
        return PrepareData.class;
    }
}