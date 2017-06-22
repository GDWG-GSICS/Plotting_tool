package org.eumetsat.usd.gcp.shared.exception;

import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Signals that some dataset could not be found in the catalog.
 * 
 * @author USD/C/PBe
 */
public class DatasetNotFoundException extends ActionException
{
    /** Automatically generated. */
    private static final long serialVersionUID = 8966531977380816976L;

    /**
     * Constructs an {@code DatasetNotFoundException} with {@code null} as its error detail message.
     */
    public DatasetNotFoundException()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            Exception message.
     */
    public DatasetNotFoundException(final String msg)
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
    public DatasetNotFoundException(final String msg, final Throwable t)
    {
        super(msg, t);
    }

}
