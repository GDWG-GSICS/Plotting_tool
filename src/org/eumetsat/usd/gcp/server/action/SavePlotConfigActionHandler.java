package org.eumetsat.usd.gcp.server.action;

import org.eumetsat.usd.gcp.server.persistence.PersistenceManager;

import org.eumetsat.usd.gcp.shared.action.SavePlotConfig;
import org.eumetsat.usd.gcp.shared.action.SavePlotConfigResult;
import org.eumetsat.usd.gcp.shared.conf.PlotConfiguration;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Save Plot Config Action Handler. Generated by GWTP Plugin.
 * 
 * @author USD/C/PBe
 */
public class SavePlotConfigActionHandler implements ActionHandler<SavePlotConfig, SavePlotConfigResult>
{
    /** Persistence Manager. */
    private PersistenceManager persistenceManager;

    /**
     * Constructor.
     */
    @Inject
    public SavePlotConfigActionHandler(final PersistenceManager persistenceManager)
    {
        this.persistenceManager = persistenceManager;
    }

    @Override
    public final SavePlotConfigResult execute(final SavePlotConfig action, final ExecutionContext context)
            throws ActionException
    {
        for (final PlotConfiguration plotConfig : action.getPlotConfigs())
        {
            persistenceManager.savePlotConfigForUser(action.getUserID(), plotConfig);
        }

        return new SavePlotConfigResult();
    }

    @Override
    public final void undo(final SavePlotConfig action, final SavePlotConfigResult result,
            final ExecutionContext context) throws ActionException
    {
    }

    @Override
    public final Class<SavePlotConfig> getActionType()
    {
        return SavePlotConfig.class;
    }
}