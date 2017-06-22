package org.eumetsat.usd.gcp.shared.exception;

import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Signals that some variable was not found in the NetCDF file.
 * 
 * @author USD/C/PBe
 */
public class InvalidConfigParamException extends ActionException
{
    /** Automatically generated. */
    private static final long serialVersionUID = -8266801995482042498L;

    /**
     * Constructs an {@code VariableNotFoundException} with {@code null} as its error detail
     * message.
     */
    public InvalidConfigParamException()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            Exception message.
     */
    public InvalidConfigParamException(final String msg)
    {
        super(msg);
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            Exception message.
     * @param t
     *            Throwable object.
     */
    public InvalidConfigParamException(final String msg, final Throwable t)
    {
        super(msg, t);
    }

}
