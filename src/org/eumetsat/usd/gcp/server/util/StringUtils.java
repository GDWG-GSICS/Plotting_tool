package org.eumetsat.usd.gcp.server.util;

import java.util.Collection;
import java.util.regex.Pattern;

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
     * Gets the index of a certain string in a collection of strings.
     * 
     * @param strVector
     *            collection of strings.
     * @param regEx
     *            regular expression used to find a string.
     * @return index of string found.
     */
    public static int indexOf(final Collection<String> strList, final String regEx)
    {
        Pattern regExp = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);

        int i = 0;
        int foundIndex = -1;
        for (String str : strList)
        {
            if (regExp.matcher(str).matches())
            {
                foundIndex = i;
                break;
            }

            i++;
        }

        return foundIndex;
    }

    /**
     * Finds a string in a collection of strings.
     * 
     * @param strVector
     *            collection of strings.
     * @param regEx
     *            regular expression used to find a string.
     * @return string found.
     */
    public static boolean contains(final Collection<String> strList, final String regEx)
    {
        Pattern regExp = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);

        for (String str : strList)
        {
            if (regExp.matcher(str).matches())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Join two strings without their intersection.
     * 
     * @param first
     *            first string.
     * @param second
     *            second string
     * @return joined string.
     * @author josh.trow
     * @see <a
     *      href="http://stackoverflow.com/questions/8808085/concatenate-two-strings-without-intersection">
     *      [stackoverflow] Concatenate two strings without intersection</a>
     */
    public static String joinWithoutIntersection(final String first, final String second)
    {
        char[] f = first.toCharArray();
        char[] s = second.toCharArray();
        if (!first.contains("" + s[0]))
        {
            return first + second;
        }
        int idx = 0;
        while (!matches(f, s, idx))
        {
            idx++;
        }
        return first.substring(0, idx) + second;
    }

    /**
     * Check if the last portion of first char array, starting in idx, match the beginning of the
     * second char array.
     * 
     * @param f
     *            first char array.
     * @param s
     *            second char array.
     * @param idx
     *            start index.
     * @return boolean true if they match, false if they don't.
     */
    private static boolean matches(final char[] f, final char[] s, final int idx)
    {
        for (int i = idx; i < f.length; i++)
        {
            if ((i - idx) > (s.length - 1))
            {
                // avoid out of bounds exception.
                break;
            } else if (f[i] != s[i - idx])
            {
                return false;
            }
        }
        return true;
    }
}
