// @formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
package org.eumetsat.usd.gcp.server.exception;

/**
 * Signals a bad argument when calling a function.
 * 
 * @author USC/C/PBe
 */
public class BadArgumentException extends ServerException
{
    /** Automatically generated. */
    private static final long serialVersionUID = 4593844554085506307L;

    /**
     * Constructs an {@code BadArgumentException} with {@code null} as its error detail message.
     */
    public BadArgumentException()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            Exception message.
     */
    public BadArgumentException(final String msg)
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
    public BadArgumentException(final String msg, final Throwable t)
    {
        super(msg, t);
    }

}
