// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2012
 */
// @formatter:on
package org.eumetsat.usd.gcp.client.crypto;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import org.eumetsat.usd.gcp.client.resources.Resources;

/**
 * @author USD/C/PBe
 */
public final class HashUtils
{
    /** flag to check if JS Script has been already installed. */
    private static boolean installed = false;

    /**
     * Prevent construction.
     */
    private HashUtils()
    {
    }

    /**
     * Install the SHA2 JavaScript source into the current document. This method is idempotent.
     */
    public static synchronized void install()
    {
        if (!installed)
        {
            ScriptElement dygraphsScript = Document.get().createScriptElement();
            dygraphsScript.setText(Resources.INSTANCE.sha2().getText());
            Document.get().getBody().appendChild(dygraphsScript);

            installed = true;
        }
    }

    /**
     * Computes the SHA256 hash of a string.
     * 
     * @param toHash
     *            String to be hashed.
     * @return hash of input string.
     */
    public static native String computeSHA256(final String toHash) /*-{
		return $wnd.str_sha256(toHash);
    }-*/;
}
