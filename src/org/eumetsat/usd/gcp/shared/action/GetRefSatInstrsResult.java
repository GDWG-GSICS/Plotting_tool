package org.eumetsat.usd.gcp.shared.action;

import java.util.List;

import com.gwtplatform.dispatch.rpc.shared.Result;

/**
 * Get reference sat/instrs Action Result. Generated by GWTP Plugin.
 * 
 * @author USD/C/PBe
 */
public class GetRefSatInstrsResult implements Result
{
    /** Auto-generated serial version UID. */
    private static final long serialVersionUID = -3500813708639201954L;
    
    /** Reference sat/instrs. */
    private List<String> refSatInstrs;

    /**
     * Constructor (for serialisation only).
     */
    @SuppressWarnings("unused")
    private GetRefSatInstrsResult()
    {
        // For serialisation only
    }

    /**
     * Constructor.
     * @param refSatInstrs reference sat/instrs.
     */
    public GetRefSatInstrsResult(final List<String> refSatInstrs)
    {
        this.refSatInstrs = refSatInstrs;
    }

    /**
     * Get reference sat/instrs.
     * @return reference sat/instrs.
     */
    public final List<String> getRefSatInstrs()
    {
        return refSatInstrs;
    }
}