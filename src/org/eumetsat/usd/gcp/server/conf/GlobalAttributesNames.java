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
public class GlobalAttributesNames implements IsSerializable
{
    private String filename;
    private String radToTbConvFormula;
    private String tbToRadConvFormula;
    private String c1;
    private String c2;

    /**
     * Default constructor for serialisation online.
     */
    @SuppressWarnings("unused")
    private GlobalAttributesNames()
    {
        // For serialisation only
    }

    /**
     * Constructor.
     * 
     * @param filename
     * @param radToTbConvFormula
     * @param tbToRadConvFormula
     * @param c1
     * @param c2
     */
    public GlobalAttributesNames(String filename, String radToTbConvFormula, String tbToRadConvFormula, String c1,
            String c2)
    {
        this.filename = filename;
        this.radToTbConvFormula = radToTbConvFormula;
        this.tbToRadConvFormula = tbToRadConvFormula;
        this.c1 = c1;
        this.c2 = c2;
    }

    /**
     * @return the filename
     */
    public String getFilename()
    {
        return filename;
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
    public String getC1()
    {
        return c1;
    }

    /**
     * @return the c2
     */
    public String getC2()
    {
        return c2;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((c1 == null) ? 0 : c1.hashCode());
        result = prime * result + ((c2 == null) ? 0 : c2.hashCode());
        result = prime * result + ((filename == null) ? 0 : filename.hashCode());
        result = prime * result + ((radToTbConvFormula == null) ? 0 : radToTbConvFormula.hashCode());
        result = prime * result + ((tbToRadConvFormula == null) ? 0 : tbToRadConvFormula.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GlobalAttributesNames other = (GlobalAttributesNames) obj;
        if (c1 == null)
        {
            if (other.c1 != null) return false;
        } else if (!c1.equals(other.c1)) return false;
        if (c2 == null)
        {
            if (other.c2 != null) return false;
        } else if (!c2.equals(other.c2)) return false;
        if (filename == null)
        {
            if (other.filename != null) return false;
        } else if (!filename.equals(other.filename)) return false;
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
