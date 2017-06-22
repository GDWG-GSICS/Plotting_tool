package org.eumetsat.usd.gcp.shared.exception;

import com.gwtplatform.dispatch.shared.ActionException;

/**
 * Signals that some channel for a variable was not found in the NetCDF file.
 * 
 * @author USD/C/PBe
 */
public class ChannelNotFoundException extends ActionException
{
    /** Automatically generated. */
    private static final long serialVersionUID = 264571884775676054L;

    /** Variable name. */
    private String varName;

    /** Channel number. */
    private long channelNum;

    /**
     * Constructs an {@code VariableNotFoundException} with {@code null} as its error detail message.
     * 
     * @param varName
     *            name of the variable not found.
     * @param channelNum
     *            name of the channel not found.
     */
    public ChannelNotFoundException(final String varName, final long channelNum)
    {
        super();
        this.varName = varName;
        this.channelNum = channelNum;
    }

    /**
     * Constructs an {@code VariableNotFoundException} with {@code null} as its error detail message.
     * 
     * @param varName
     *            name of the variable not found.
     * @param channelNum
     *            name of the channel not found.
     * @param t
     *            Throwable object.
     */
    public ChannelNotFoundException(final String varName, final long channelNum, final Throwable t)
    {
        super(t);
        this.varName = varName;
        this.channelNum = channelNum;
    }

    /**
     * Constructor.
     * 
     * @param varName
     *            name of the variable not found.
     * @param channelNum
     *            name of the channel not found.
     * @param msg
     *            Exception message.
     */
    public ChannelNotFoundException(final String varName, final long channelNum, final String msg)
    {
        super(msg);
        this.varName = varName;
        this.channelNum = channelNum;
    }

    /**
     * Constructor.
     * 
     * @param varName
     *            name of the variable not found.
     * @param channelNum
     *            name of the channel not found.
     * @param msg
     *            Exception message.
     * @param t
     *            Throwable object.
     */
    public ChannelNotFoundException(final String varName, final long channelNum, final String msg, final Throwable t)
    {
        super(msg, t);
        this.varName = varName;
        this.channelNum = channelNum;
    }

    /**
     * Returns name of the variable.
     * 
     * @return variable name.
     */
    public String getVariableName()
    {
        return varName;
    }

    /**
     * Returns number of the channel not found.
     * 
     * @return channel number.
     */
    public long getChannelNum()
    {
        return channelNum;
    }

}
