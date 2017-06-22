package org.eumetsat.usd.gcp.client.util;

import java.util.List;

import com.google.gwt.regexp.shared.RegExp;

/**
 * This non-instantiable class contains String utilities.
 * 
 * @author USD/C/PBe
 */
public final class StringUtils
{
    /**
     * Private constructor to make this class non-instantiable.
     * 
     */
    private StringUtils()
    {
    }

    /**
     * Gets the first index in a string list of a string that matches a regular expression.
     * 
     * @param strList
     *            String list.
     * @param regEx
     *            regular expression.
     * @return first index of string that matches regular expression.
     */
    public static int indexOf(final List<String> strList, final String regEx)
    {
        RegExp regExp = RegExp.compile(regEx, "i");

        int i = 0;
        int foundIndex = -1;
        for (String str : strList)
        {
            if (regExp.test(str))
            {
                foundIndex = i;
                break;
            }

            i++;
        }

        return foundIndex;
    }

    /**
     * Check if some string in a string list matches a regular expression.
     * 
     * @param strList
     *            String list.
     * @param regEx
     *            regular expression.
     * @return true if there is a string matching the regular expression.
     */
    public static boolean contains(final List<String> strList, final String regEx)
    {
        RegExp regExp = RegExp.compile(regEx, "i");

        for (String str : strList)
        {
            if (regExp.test(str))
            {
                return true;
            }
        }

        return false;
    }
}
