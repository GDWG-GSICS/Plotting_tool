//@formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2012
 */
//@formatter:on
package org.eumetsat.usd.gcp.server.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
     * @param timeZone
     *            time zone used.
     * @return Date object.
     * @throws ParseException
     *             when an error during parsing occurred.
     */
    public static Date parse(final String dateStr, final String pattern, final String timeZone) throws ParseException
    {
        DateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));

        return df.parse(dateStr);
    }

    /**
     * Get formatted string from a Date object.
     * 
     * @param date
     *            Date object.
     * @param pattern
     *            pattern to format the string.
     * @param timeZone
     *            time zone used.
     * @return formatted date string.
     */
    public static String format(final Date date, final String pattern, final String timeZone)
    {
        DateFormat df = new SimpleDateFormat(pattern);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));

        return df.format(date);
    }

}
