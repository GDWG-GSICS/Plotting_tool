package org.eumetsat.usd.gcp.shared.action;

import com.gwtplatform.dispatch.rpc.shared.UnsecuredActionImpl;

/**
 * Get Saved Plot Configs Action Input. Generated GWTP Plugin.
 * 
 * @author USD/C/PBe
 */
public class GetSavedPlotConfigs extends UnsecuredActionImpl<GetSavedPlotConfigsResult>
{
    /** User id. */
    private String userID;

    /**
     * Constructor (for serialisation only).
     */
    @SuppressWarnings("unused")
    private GetSavedPlotConfigs()
    {
        // For serialisation only
    }

    /**
     * Constructor.
     * 
     * @param userID
     *            user id.
     */
    public GetSavedPlotConfigs(final String userID)
    {
        this.userID = userID;
    }

    /**
     * Get user id.
     * 
     * @return user id.
     */
    public final String getUserID()
    {
        return userID;
    }
}
