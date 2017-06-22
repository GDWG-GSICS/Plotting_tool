package org.eumetsat.usd.gcp.server.exception;


/**
 * Signals a format which is not compliant with WMO conventions.
 * 
 * @author USD/C/PBe
 */
public class InvalidConfigException extends ServerException
{
    /** Automatically generated. */
    private static final long serialVersionUID = 4254521561925283906L;

    /**
     * Constructs an {@code InvalidFormatException} with {@code null} as its error detail message.
     */
    public InvalidConfigException()
    {
        super();
    }
    
    /**
     * Constructor.
     * 
     * @param msg
     *            Exception message.
     */
    public InvalidConfigException(final String msg)
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
    public InvalidConfigException(final String msg, final Throwable t)
    {
        super(msg, t);
    }

}
