// @formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
package org.eumetsat.usd.gcp.server.exception;

/**
 * This is the root exception class for all gcp server side exceptions.
 * 
 * @author USC/C/PBe
 */
public class ServerException extends Exception
{

    /** Automatically generated. */
    private static final long serialVersionUID = -5893277424993494149L;

    /**
     * Constructs an {@code GCPException} with {@code null} as its error detail message.
     */
    public ServerException()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            Exception message.
     */
    public ServerException(final String msg)
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
    public ServerException(final String msg, final Throwable t)
    {
        super(msg, t);
    }

    /**
     * This method overrides the Exception getMessage() method and adds information from the Throwable cause to this
     * message.
     * 
     * @return This message plus the messages from the causing Exceptions
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public final String getMessage()
    {
        StringBuilder output = new StringBuilder();
        output.append(super.getMessage());
        if (getCause() != null)
        {
            output.append(":: ");
            output.append(super.getCause().getMessage());
            if (getCause().getCause() != null)
            {
                output.append(":: ");
                output.append(super.getCause().getCause().getMessage());
            }
        }
        return output.toString().replace("org.eumetsat.usd.gcp.", "*.");
    }
}
