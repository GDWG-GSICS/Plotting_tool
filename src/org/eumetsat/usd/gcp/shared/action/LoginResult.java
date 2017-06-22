package org.eumetsat.usd.gcp.shared.action;

import com.gwtplatform.dispatch.rpc.shared.Result;

/**
 * Login Action Result. Generated by GWTP Plugin.
 * 
 * @author USD/C/PBe
 */
public class LoginResult implements Result
{
    /** Auto-generated serial version UID. */
    private static final long serialVersionUID = 8843959461240347459L;
    
    /** Flag if login was successful. */
    private boolean success;

    /**
     * Constructor (for serialization only).
     */
    @SuppressWarnings("unused")
    private LoginResult()
    {
        // For serialization only
    }

    /**
     * Constructor.
     * 
     * @param success
     *            <code>true</code> if login was successful.
     */
    public LoginResult(final boolean success)
    {
        this.success = success;
    }

    /**
     * Check if login was successful.
     * 
     * @return <code>true</code> if login was successful.
     */
    public final boolean success()
    {
        return success;
    }
}