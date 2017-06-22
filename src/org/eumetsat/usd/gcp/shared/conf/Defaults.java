package org.eumetsat.usd.gcp.shared.conf;

/**
 * This non-instantiable class contains the client-side constants definitions for the USD_DT
 * application.
 * 
 * @author USD/C/PBe
 */
public final class Defaults
{
    //----------------------
    // Default selections
    //---------------------- 

    /** Default server. */
    public static final String DEFAULT_SERVER = "EUMETSAT";
    /** Default source. */
    public static final String DEFAULT_GPRC = "EUMETSAT";
    /** Default sat/instr. */
    public static final String DEFAULT_SAT_INSTR = "MSG1 SEVIRI";
    /** Default reference sat/instr. */
    public static final String DEFAULT_REF_SAT_INSTR = "MetOpA IASI";
    /** Default type. */
    public static final String DEFAULT_CORR_TYPE = "RAC";
    /** Default channel. */
    public static final String DEFAULT_CHANNEL = "All";

    /**
     * Private constructor to make this class non-instantiable.
     * 
     */
    private Defaults()
    {
    }
}
