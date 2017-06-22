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
public class GlobalAttributesDefaults implements IsSerializable
{
    private String radToTbConvFormula;
    private String tbToRadConvFormula;
    private double c1;
    private double c2;

    /**
     * Default constructor for serialisation online.
     */
    @SuppressWarnings("unused")
    private GlobalAttributesDefaults()
    {
        // For serialisation only
    }

    /**
     * Constructor.
     * 
     * @param radToTbConvFormula
     * @param tbToRadConvFormula
     * @param c1
     * @param c2
     */
    public GlobalAttributesDefaults(String radToTbConvFormula, String tbToRadConvFormula, double c1, double c2)
    {
        this.radToTbConvFormula = radToTbConvFormula;
        this.tbToRadConvFormula = tbToRadConvFormula;
        this.c1 = c1;
        this.c2 = c2;
    }

    /**
     * @return the radToTbConvFormula
     */
    public String getRadToTbConvFormula()
    {
        return radToTbConvFormula;
    }

    /**
     * @return the tbToRadConvFormula
     */
    public String getTbToRadConvFormula()
    {
        return tbToRadConvFormula;
    }

    /**
     * @return the c1
     */
    public double getC1()
    {
        return c1;
    }

    /**
     * @return the c2
     */
    public double getC2()
    {
        return c2;
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
        long temp;
        temp = Double.doubleToLongBits(c1);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(c2);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((radToTbConvFormula == null) ? 0 : radToTbConvFormula.hashCode());
        result = prime * result + ((tbToRadConvFormula == null) ? 0 : tbToRadConvFormula.hashCode());
        return result;
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
        GlobalAttributesDefaults other = (GlobalAttributesDefaults) obj;
        if (Double.doubleToLongBits(c1) != Double.doubleToLongBits(other.c1)) return false;
        if (Double.doubleToLongBits(c2) != Double.doubleToLongBits(other.c2)) return false;
        if (radToTbConvFormula == null)
        {
            if (other.radToTbConvFormula != null) return false;
        } else if (!radToTbConvFormula.equals(other.radToTbConvFormula)) return false;
        if (tbToRadConvFormula == null)
        {
            if (other.tbToRadConvFormula != null) return false;
        } else if (!tbToRadConvFormula.equals(other.tbToRadConvFormula)) return false;
        return true;
    }

}
