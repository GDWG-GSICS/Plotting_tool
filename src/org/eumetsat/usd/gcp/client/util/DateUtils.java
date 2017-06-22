// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2012
 */
// @formatter:on
package org.eumetsat.usd.gcp.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * @author USD/C/PBe
 */
public final class DateUtils
{
    /**
     * Private constructor to make it non-instantiable.
     */
    private DateUtils()
    {

    }

    /**
     * Parse a date string with a certain pattern and in a certain time zone.
     * 
     * @param dateStr
     *            string to parse.
     * @param pattern
     *            pattern which the date is formatted to string with.
     * @return Date object.
     */
    public static Date parse(final String dateStr, final String pattern)
    {
        DateTimeFormat df = DateTimeFormat.getFormat(pattern);

        return df.parse(dateStr);
    }

    /**
     * Get formatted string from a Date object.
     * 
     * @param date
     *            Date object.
     * @param pattern
     *            pattern to format the string.
     * @return formatted date string.
     */
    public static String format(final Date date, final String pattern)
    {
        DateTimeFormat df = DateTimeFormat.getFormat(pattern);

        return df.format(date);
    }

}
