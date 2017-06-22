// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2013
 */
// @formatter:on
package org.eumetsat.usd.gcp.shared.conf;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author USD/C/PBe
 * 
 */
public class HelpItem implements IsSerializable
{
    /** Label. */
    String label;
    /** Description. */
    String description;
    /** URL. */
    String url;

    /**
     * Default constructor for serialisation online.
     */
    @SuppressWarnings("unused")
    private HelpItem()
    {
        // For serialisation only
    }

    /**
     * Constructor.
     * 
     * @param label
     *            label.
     * @param description
     *            description.
     * @param url
     *            url.
     */
    public HelpItem(final String label, final String description, final String url)
    {
        this.label = label;
        this.description = description;
        this.url = url;
    }

    /**
     * Get label.
     * 
     * @return the label
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Get description.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Get url.
     * 
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        HelpItem other = (HelpItem) obj;
        if (description == null)
        {
            if (other.description != null) return false;
        } else if (!description.equals(other.description)) return false;
        if (label == null)
        {
            if (other.label != null) return false;
        } else if (!label.equals(other.label)) return false;
        if (url == null)
        {
            if (other.url != null) return false;
        } else if (!url.equals(other.url)) return false;
        return true;
    }

}
