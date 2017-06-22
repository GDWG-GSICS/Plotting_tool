package org.eumetsat.usd.gcp.server.action;

import org.eumetsat.usd.gcp.server.catalog.CatalogNavigator;
import org.eumetsat.usd.gcp.server.guice.CatalogNavigatorFactory;
import org.eumetsat.usd.gcp.shared.action.GetCorrTypes;
import org.eumetsat.usd.gcp.shared.action.GetCorrTypesResult;

import com.google.inject.Inject;
import com.gwtplatform.dispatch.rpc.server.ExecutionContext;
import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Get Types Action Handler. Generated by GWTP Plugin.
 * 
 * @author USD/C/PBe
 */
public class GetCorrTypesActionHandler implements ActionHandler<GetCorrTypes, GetCorrTypesResult>
{
    /** Catalog Navigator Factory. */
    private CatalogNavigatorFactory catalogNavigatorFactory;
    
    /**
     * Constructor.
     */
    @Inject
    public GetCorrTypesActionHandler(final CatalogNavigatorFactory catalogNavigatorFactory)
    {
        this.catalogNavigatorFactory = catalogNavigatorFactory;
    }

    @Override
    public final GetCorrTypesResult execute(final GetCorrTypes action, final ExecutionContext context) throws ActionException
    {
        CatalogNavigator catalogNavigator = catalogNavigatorFactory.create(action.getCatalog());

        GetCorrTypesResult result = new GetCorrTypesResult(catalogNavigator.getCorrTypes(action.getGPRC()));

        return result;
    }

    @Override
    public final void undo(final GetCorrTypes action, final GetCorrTypesResult result, final ExecutionContext context)
            throws ActionException
    {
    }

    @Override
    public final Class<GetCorrTypes> getActionType()
    {
        return GetCorrTypes.class;
    }
}