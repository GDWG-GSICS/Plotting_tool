/**
 * $Id: YValueFormatter.java 20 2010-09-03 00:04:46Z steven.jardine $ Copyright (c) 2010 MJN Services, Inc., All Rights
 * Reserved.
 */
package org.eumetsat.usd.gcp.client.dygraphs;

/**
 * @version $Rev: 20 $
 * @author Steven Jardine
 */
public interface YValueFormatter
{

    /**
     * Format the y value.
     * 
     * @param yValue
     *            the y value to format.
     * @return a string representation of the yValue.
     */
    String format(double yValue);

}
