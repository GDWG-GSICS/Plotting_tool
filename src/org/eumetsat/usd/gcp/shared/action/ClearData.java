package org.eumetsat.usd.gcp.shared.action;

import com.gwtplatform.dispatch.rpc.shared.UnsecuredActionImpl;

/**
 * Clear Data Action Input. Generated by GWTP Plugin.
 * 
 * @author USD/C/PBe
 */
public class ClearData extends UnsecuredActionImpl<ClearDataResult>
{
    /** User id. */
    private String userID;

    /**
     * Constructor (for serialisation only).
     */
    @SuppressWarnings("unused")
    private ClearData()
    {
        // For serialisation only
    }

    /**
     * Constructor.
     * 
     * @param userID
     *            user id.
     */
    public ClearData(final String userID)
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