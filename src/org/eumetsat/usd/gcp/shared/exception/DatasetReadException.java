package org.eumetsat.usd.gcp.shared.exception;

import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Signals that some dataset could not be read from the catalog.
 * 
 * @author USD/C/PBe
 */
public class DatasetReadException extends ActionException
{
    /** Automatically generated. */
    private static final long serialVersionUID = 8966531977380816976L;

    /**
     * Constructs an {@code DatasetNotFoundException} with {@code null} as its error detail message.
     */
    public DatasetReadException()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            Exception message.
     */
    public DatasetReadException(final String msg)
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
    public DatasetReadException(final String msg, final Throwable t)
    {
        super(msg, t);
    }

}
