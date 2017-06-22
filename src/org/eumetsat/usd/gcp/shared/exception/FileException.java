package org.eumetsat.usd.gcp.shared.exception;

import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Signals an error when doing some file operation.
 * 
 * @author USD/C/PBe
 */
public class FileException extends ActionException
{
    /** Automatically generated. */
    private static final long serialVersionUID = -4461614571973255161L;

    /**
     * Constructs an {@code FileException} with {@code null} as its error detail message.
     */
    public FileException()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            Exception message.
     */
    public FileException(final String msg)
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
    public FileException(final String msg, final Throwable t)
    {
        super(msg, t);
    }

}
