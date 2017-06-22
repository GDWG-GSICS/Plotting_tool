package org.eumetsat.usd.gcp.shared.exception;

import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Signals that some variable was not found in the NetCDF file.
 * 
 * @author USD/C/PBe
 */
public class FormulaException extends ActionException
{
    /** Automatically generated. */
    private static final long serialVersionUID = -287686249441962520L;

    /**
     * Constructs an {@code VariableNotFoundException} with {@code null} as its error detail
     * message.
     */
    public FormulaException()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            Exception message.
     */
    public FormulaException(final String msg)
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
    public FormulaException(final String msg, final Throwable t)
    {
        super(msg, t);
    }

}
