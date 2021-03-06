package org.eumetsat.usd.gcp.shared.action;

import com.gwtplatform.dispatch.rpc.shared.UnsecuredActionImpl;

/**
 * Get Sat/Instrs. Action Input. Generated by GWTP Plugin.
 * 
 * @author USD/C/PBe
 */
public class GetSatInstrs extends UnsecuredActionImpl<GetSatInstrsResult>
{
    /** Catalog. */
    private String catalog;
    /** GPRC. */
    private String gprc;
    /** Correction Type. */
    private String corrType;

    /**
     * Constructor (for serialisation only).
     */
    @SuppressWarnings("unused")
    private GetSatInstrs()
    {
        // For serialization only
    }

    /**
     * Constructor.
     * 
     * @param catalog
     *            catalog.
     * @param gprc
     *            GPRC.
     * @param corrType
     *            correction type.
     */
    public GetSatInstrs(final String catalog, final String gprc, final String corrType)
    {
        this.catalog = catalog;
        this.gprc = gprc;
        this.corrType = corrType;
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
     * Get GPRC.
     * 
     * @return GPRC.
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
}
