package org.eumetsat.usd.gcp.shared.exception;

import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Signals that the current catalog is invalid or its content inconsistent with the GSICS
 * conventions.
 * 
 * @author USD/C/PBe
 */
public class InvalidCatalogException extends ActionException
{
    /** Automatically generated. */
    private static final long serialVersionUID = 5290801933694472622L;

    /**
     * Constructs an {@code InvalidCatalogException} with {@code null} as its error detail message.
     */
    public InvalidCatalogException()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            Exception message.
     */
    public InvalidCatalogException(final String msg)
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
    public InvalidCatalogException(final String msg, final Throwable t)
    {
        super(msg, t);
    }

}
