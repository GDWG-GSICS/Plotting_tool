/**
 * $Id: AssociativeArray.java 9 2010-08-26 16:41:49Z steven.jardine $ Copyright (c) 2010 Steven
 * Jardine, MJN Services, Inc.
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
 * A JavaScriptObject with helper methods to set keys and values.
 * 
 * @version $Rev: 9 $
 * @author Steven Jardine
 */
public final class DygraphOptions extends JavaScriptObject
{

    /**
     * Default constructor.
     */
    protected DygraphOptions()
    {
        super();
    }

    /**
     * @return a new instance of the associative array.
     */
    public static native DygraphOptions create()
    /*-{
		return new Object();
    }-*/;

    /**
     * 
     * @param key
     *            the key to use.
     * @param subkey
     *            the subkey to use.
     * @param value
     *            the value to set.
     * @author USD/C/PBe
     */
    public native void set(final String key, final String subkey, final int value)
    /*-{
		if (typeof (key) != "string") {
			key = key.toString();
		}
		if (typeof (subkey) != "string") {
			subkey = subkey.toString();
		}
		this[key] = {};
		this[key][subkey] = value;
    }-*/;

    /**
     * Sets a string value based on a given key.
     * 
     * @param key
     *            the key to use.
     * @param value
     *            the value to set.
     */
    public native void set(final String key, final String value)
    /*-{
		if (typeof (key) != "string") {
			key = key.toString();
		}

		this[key] = value;
    }-*/;

    /**
     * Sets default interaction model.
     * 
     * @author USD/C/PBe
     */
    public native void setDefaultInteractionModel()
    /*-{
		this["interactionModel"] = $wnd.Dygraph.defaultInteractionModel;
    }-*/;

    /**
     * Sets a int value based on a given key.
     * 
     * @param key
     *            the key to use.
     * @param value
     *            the value to set.
     */
    public native void set(final String key, final int value)
    /*-{
		if (typeof (key) != "string") {
			key = key.toString();
		}
		this[key] = value;
    }-*/;

    /**
     * Sets a string value based on a given key.
     * 
     * @param key
     *            the key to use.
     * @param value
     *            the value to set.
     */
    public native void set(final String key, final boolean value)
    /*-{
		if (typeof (key) != "string") {
			key = key.toString();
		}
		this[key] = value;
    }-*/;

    /**
     * Sets a string value based on a given key.
     * 
     * @param <T>
     *            the type of the value.
     * @param key
     *            the key to use.
     * @param value
     *            the value to set.
     */
    public native <T> void set(final String key, final T value)
    /*-{
		if (typeof (key) != "string") {
			key = key.toString();
		}
		this[key] = value;
    }-*/;

}
