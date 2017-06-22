package org.eumetsat.usd.gcp.shared.action;

import com.gwtplatform.dispatch.rpc.shared.UnsecuredActionImpl;

/**
 * Get Ref Sat/Instrs. Action Input. Generated by GWTP Plugin.
 * 
 * @author USD/C/PBe
 */
public class GetRefSatInstrs extends UnsecuredActionImpl<GetRefSatInstrsResult>
{
    /** Catalog. */
    private String catalog;
    /** GPRC. */
    private String gprc;
    /** Correction Type. */
    private String corrType;
    /** Sat/instr. */
    private String satInstr;

    /**
     * Constructor (for serialisation only).
     */
    @SuppressWarnings("unused")
    private GetRefSatInstrs()
    {
        // For serialisation only
    }

    /**
     * Constructor.
     * 
     * @param catalog
     *            catalog.
     * @param GPRC
     *            gprc.
     * @param corrType
     *            correction type.
     * @param satInstr
     *            sat/instr.
     */
    public GetRefSatInstrs(final String catalog, final String gprc, final String corrType, final String satInstr)
    {
        this.catalog = catalog;
        this.gprc = gprc;
        this.corrType = corrType;
        this.satInstr = satInstr;
    }

    /**
     * Get catalog.
     * 
     * @return catalog.
     */
    public final String getCatalog()
    {
        return catalog;
    }

    /**
     * Get source.
     * 
     * @return source.
     */
    public final String getGPRC()
    {
        return gprc;
    }

    /**
     * Get correction type.
     * 
     * @return correction type.
     */
    public final String getCorrType()
    {
        return corrType;
    }

    /**
     * Get sat/instr.
     * 
     * @return sat/instr.
     */
    public final String getSatInstr()
    {
        return satInstr;
    }
}