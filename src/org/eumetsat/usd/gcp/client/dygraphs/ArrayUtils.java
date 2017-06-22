/**
 * $Id: ArrayUtils.java 9 2010-08-26 16:41:49Z steven.jardine $ Copyright (c) 2010 Steven Jardine,
 * MJN Services, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.eumetsat.usd.gcp.client.dygraphs;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Javascript Array Utilities.
 * 
 * @version $Rev: 9 $
 * @author Steven Jardine
 */
public final class ArrayUtils
{
    /**
     * Default constructor.
     */
    private ArrayUtils()
    {
        super();
    };

    /**
     * @return a new empty javascript array.
     */
    public static native JavaScriptObject create()
    /*-{
		return new Array();
    }-*/;

    /**
     * Push a unnamed value onto a native javascript array.
     * 
     * @param array
     *            the array to modify.
     * @param value
     *            the value to push.
     */
    public static native void push(final JavaScriptObject array, final boolean value)
    /*-{
		array.push(value);
    }-*/;

    /**
     * Push a unnamed value onto a native javascript array.
     * 
     * @param array
     *            the array to modify.
     * @param value
     *            the value to push.
     */
    public static native void push(final JavaScriptObject array, final int value)
    /*-{
		array.push(value);
    }-*/;

    /**
     * Push a unnamed value onto a native javascript array.
     * 
     * @param array
     *            the array to modify.
     * @param value
     *            the value to push.
     */
    public static native void push(final JavaScriptObject array, final String value)
    /*-{
		array.push(value);
    }-*/;

    /**
     * Push a named value onto a native javascript array.
     * 
     * @param array
     *            the array to modify.
     * @param name
     *            the name of the paramter.
     * @param value
     *            the value of the option.
     */
    public static native void push(final JavaScriptObject array, final String name, final boolean value)
    /*-{
		if (typeof (name) != "string") {
			name = name.toString();
		}
		array[name] = value;
    }-*/;

    /**
     * Push a named value onto a native javascript array.
     * 
     * @param array
     *            the array to modify.
     * @param name
     *            the name of the paramter.
     * @param value
     *            the value of the option.
     */
    public static native void push(final JavaScriptObject array, final String name, final int value)
    /*-{
		if (typeof (name) != "string") {
			name = name.toString();
		}
		array[name] = value;
    }-*/;

    /**
     * Push a named value onto a native javascript array.
     * 
     * @param array
     *            the array to modify.
     * @param name
     *            the name of the paramter.
     * @param value
     *            the value of the option.
     */
    public static native void push(final JavaScriptObject array, final String name, final String value)
    /*-{
		if (typeof (name) != "string") {
			name = name.toString();
		}
		array[name] = value;
    }-*/;

    /**
     * Push a named value onto a native javascript array.
     * 
     * @param <T>
     *            the type of the value to push.
     * @param array
     *            the array to modify.
     * @param name
     *            the name of the paramter.
     * @param value
     *            the value of the option.
     */
    public static native <T> void push(final JavaScriptObject array, final String name, final T value)
    /*-{
		if (typeof (name) != "string") {
			name = name.toString();
		}
		array[name] = value;
    }-*/;

    /**
     * Push a unnamed value onto a native javascript array.
     * 
     * @param <T>
     *            the type of the value to push.
     * @param array
     *            the array to modify.
     * @param value
     *            the value to push.
     */
    public static native <T> void push(final JavaScriptObject array, final T value)
    /*-{
		array.push(value);
    }-*/;

    /**
     * Convert to javascript array.
     * 
     * @param array
     *            the array to convert.
     * @return the native javascript array.
     */
    public static JavaScriptObject toJsArray(final boolean[] array)
    {
        if (array != null)
        {
            JavaScriptObject result = create();
            for (boolean value : array)
            {
                push(result, value);
            }
            return result;
        }
        return null;
    }

    /**
     * Convert to javascript array.
     * 
     * @param array
     *            the array to convert.
     * @return the native javascript array.
     */
    public static JavaScriptObject toJsArray(final int[] array)
    {
        if (array != null)
        {
            JavaScriptObject result = create();
            for (int value : array)
            {
                push(result, value);
            }
            return result;
        }
        return null;
    };

    /**
     * Convert to javascript array.
     * 
     * @param array
     *            the array to convert.
     * @return the native javascript array.
     * @author USD/C/PBe
     */
    public static JavaScriptObject toJsArray(final long[] array)
    {
        if (array != null)
        {
            JavaScriptObject result = create();
            for (long value : array)
            {
                push(result, value);
            }
            return result;
        }
        return null;
    };

    /**
     * Convert to javascript array.
     * 
     * @param array
     *            the array to convert.
     * @return the native javascript array.
     */
    public static JavaScriptObject toJsArray(final String[] array)
    {
        if (array != null)
        {
            JavaScriptObject result = create();
            for (String value : array)
            {
                push(result, value);
            }
            return result;
        }
        return null;
    };

    /**
     * Convert to javascript array.
     * 
     * @param <T>
     *            the type of array to convert.
     * @param array
     *            the array to convert.
     * @return the native javascript array.
     */
    public static <T> JavaScriptObject toJsArray(final T[] array)
    {
        if (array != null)
        {
            JavaScriptObject result = create();
            for (T value : array)
            {
                push(result, value);
            }
            return result;
        }
        return null;
    };
}
