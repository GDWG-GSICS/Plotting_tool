// @formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2015
 */
// @formatter:on
package org.eumetsat.usd.gcp.server.conf;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author USC/C/PBe
 */
public class VariablesNames implements IsSerializable
{
    private String date;
    private String channelName;
    private String offset;
    private String offsetSe;
    private String slope;
    private String slopeSe;
    private String covariance;
    private String stdSceneTb;
    private String alpha;
    private String beta;
    private String wnc;

    /**
     * Default constructor for serialisation online.
     */
    @SuppressWarnings("unused")
    private VariablesNames()
    {
        // For serialisation only
    }

    public VariablesNames(String date, String channelName, String offset, String offsetSe, String slope,
            String slopeSe, String covariance, String stdSceneTb, String alpha, String beta, String wnc)
    {
        this.date = date;
        this.channelName = channelName;
        this.offset = offset;
        this.offsetSe = offsetSe;
        this.slope = slope;
        this.slopeSe = slopeSe;
        this.covariance = covariance;
        this.stdSceneTb = stdSceneTb;
        this.alpha = alpha;
        this.beta = beta;
        this.wnc = wnc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alpha == null) ? 0 : alpha.hashCode());
        result = prime * result + ((beta == null) ? 0 : beta.hashCode());
        result = prime * result + ((channelName == null) ? 0 : channelName.hashCode());
        result = prime * result + ((covariance == null) ? 0 : covariance.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((offset == null) ? 0 : offset.hashCode());
        result = prime * result + ((offsetSe == null) ? 0 : offsetSe.hashCode());
        result = prime * result + ((slope == null) ? 0 : slope.hashCode());
        result = prime * result + ((slopeSe == null) ? 0 : slopeSe.hashCode());
        result = prime * result + ((stdSceneTb == null) ? 0 : stdSceneTb.hashCode());
        result = prime * result + ((wnc == null) ? 0 : wnc.hashCode());
        return result;
    }

    /**
     * @return the date
     */
    public String getDate()
    {
        return date;
    }

    /**
     * @return the channelName
     */
    public String getChannelName()
    {
        return channelName;
    }

    /**
     * @return the offset
     */
    public String getOffset()
    {
        return offset;
    }

    /**
     * @return the offsetSe
     */
    public String getOffsetSe()
    {
        return offsetSe;
    }

    /**
     * @return the slope
     */
    public String getSlope()
    {
        return slope;
    }

    /**
     * @return the slopeSe
     */
    public String getSlopeSe()
    {
        return slopeSe;
    }

    /**
     * @return the covariance
     */
    public String getCovariance()
    {
        return covariance;
    }

    /**
     * @return the stdSceneTb
     */
    public String getStdSceneTb()
    {
        return stdSceneTb;
    }

    /**
     * @return the alpha
     */
    public String getAlpha()
    {
        return alpha;
    }

    /**
     * @return the beta
     */
    public String getBeta()
    {
        return beta;
    }
    
    /**
     * @return the wnc
     */
    public String getWnc()
    {
        return wnc;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VariablesNames other = (VariablesNames) obj;
        if (alpha == null)
        {
            if (other.alpha != null) return false;
        } else if (!alpha.equals(other.alpha)) return false;
        if (beta == null)
        {
            if (other.beta != null) return false;
        } else if (!beta.equals(other.beta)) return false;
        if (channelName == null)
        {
            if (other.channelName != null) return false;
        } else if (!channelName.equals(other.channelName)) return false;
        if (covariance == null)
        {
            if (other.covariance != null) return false;
        } else if (!covariance.equals(other.covariance)) return false;
        if (date == null)
        {
            if (other.date != null) return false;
        } else if (!date.equals(other.date)) return false;
        if (offset == null)
        {
            if (other.offset != null) return false;
        } else if (!offset.equals(other.offset)) return false;
        if (offsetSe == null)
        {
            if (other.offsetSe != null) return false;
        } else if (!offsetSe.equals(other.offsetSe)) return false;
        if (slope == null)
        {
            if (other.slope != null) return false;
        } else if (!slope.equals(other.slope)) return false;
        if (slopeSe == null)
        {
            if (other.slopeSe != null) return false;
        } else if (!slopeSe.equals(other.slopeSe)) return false;
        if (stdSceneTb == null)
        {
            if (other.stdSceneTb != null) return false;
        } else if (!stdSceneTb.equals(other.stdSceneTb)) return false;
        if (wnc == null)
        {
            if (other.wnc != null) return false;
        } else if (!wnc.equals(other.wnc)) return false;
        return true;
    }

}
