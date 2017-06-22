// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2012
 */
// @formatter:on
package org.eumetsat.usd.gcp.server.data;

import java.util.LinkedHashMap;

/**
 * @author USD/C/PBe
 */
public class CalibrationPacket
{
    /** Packet containing calibration records from several data sources. */
    private LinkedHashMap<String, CalibrationRecord> packet;

    /**
     * Default constructor.
     */
    public CalibrationPacket()
    {
        this.packet = new LinkedHashMap<String, CalibrationRecord>();
    }

    /**
     * Get the calibration record for a certain source in this data packet.
     * 
     * @param sourceName
     *            name of the data source.
     * @return the calibration record for this source, null if it does not exist.
     */
    public final CalibrationRecord getRecord(final String sourceName)
    {
        return this.packet.get(sourceName);
    }

    /**
     * Add a calibration record from a certain data source.
     * 
     * @param sourceName
     *            name of the data source.
     * @param calRecord
     *            calibration record to be added.
     */
    public final void addRecord(final String sourceName, final CalibrationRecord calRecord)
    {
        this.packet.put(sourceName, calRecord);
    }

    /**
     * Remove a calibration record from a certain data source.
     * 
     * @param sourceName
     *            name of the data source.
     */
    public final void removeRecord(final String sourceName)
    {
        this.packet.remove(sourceName);
    }

    /**
     * Returns {@code true} if the data packet contains a record from this source.
     * 
     * @param sourceName
     *            source whose presence in this data packet is to be tested.
     * 
     * @return {@code true} if this data packet contains a record for the specified data source.
     */
    public final boolean contains(final String sourceName)
    {
        return this.packet.containsKey(sourceName);
    }
}
