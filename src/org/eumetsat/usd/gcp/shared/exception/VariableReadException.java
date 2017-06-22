package org.eumetsat.usd.gcp.shared.exception;

import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Signals that some variable could not be read from a NetCDF file.
 * 
 * @author USD/C/PBe
 */
public class VariableReadException extends ActionException
{
    /** Automatically generated. */
    private static final long serialVersionUID = 264571884775676054L;

    /** Variable name. */
    private String varName;

    /**
     * Constructs an {@code VariableNotFoundException} with {@code null} as its error detail message.
     * 
     * @param varName
     *            name of the variable not found.
     */
    public VariableReadException(final String varName)
    {
        super();
        this.varName = varName;
    }

    /**
     * Constructs an {@code VariableNotFoundException} with {@code null} as its error detail message.
     * 
     * @param varName
     *            name of the variable not found.
     * @param t
     *            Throwable object.
     */
    public VariableReadException(final String varName, final Throwable t)
    {
        super(t);
        this.varName = varName;
    }

    /**
     * Constructor.
     * 
     * @param varName
     *            name of the variable not found.
     * @param msg
     *            Exception message.
     */
    public VariableReadException(final String varName, final String msg)
    {
        super(msg);
        this.varName = varName;
    }

    /**
     * Constructor.
     * 
     * @param varName
     *            name of the variable not found.
     * @param msg
     *            Exception message.
     * @param t
     *            Throwable object.
     */
    public VariableReadException(final String varName, final String msg, final Throwable t)
    {
        super(msg, t);
        this.varName = varName;
    }

    /**
     * Returns name of the variable not found.
     * 
     * @return variable name.
     */
    public String getVariableName()
    {
        return varName;
    }

}
