// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2012
 */
// @formatter:on
package org.eumetsat.usd.gcp.server.data;

import java.util.Date;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * Calibration data time series container.
 * 
 * @author USC/C/PBe
 */
public class CalibrationData
{
    /** Map containing calibration data indexed by Date and Source Name. */
    private TreeMap<Date, CalibrationPacket> data;

    /** Map (keeping insertion order) of added sources with corresponding URLs. */
    private LinkedHashMap<String, String> sources;

    /**
     * Constructor.
     */
    public CalibrationData()
    {
        this.data = new TreeMap<Date, CalibrationPacket>();
        this.sources = new LinkedHashMap<String, String>();
    }

    /**
     * Clears the data container.
     */
    public final void clear()
    {
        data.clear();
        sources.clear();
    }

    /**
     * Add a calibration record for a certain date and source.
     * 
     * @param date
     *            date.
     * @param sourceName
     *            name of the data source.
     * @param sourceURL
     *            URL of the data source.
     * @param calRecord
     *            calibration record to be added.
     */
    public void addRecord(final Date date, final String sourceName, final String sourceURL,
            final CalibrationRecord calRecord)
    {
        CalibrationPacket dataPacket = null;

        // if any other record from another source has been added previously to this date...
        if (data.containsKey(date))
        {
            dataPacket = data.get(date);

        } else
        {
            dataPacket = new CalibrationPacket();
        }

        // add source to list if it has not been added before.
        if (!sources.containsKey(sourceName))
        {
            // change OPENDAP link to HTTPServer, if not already replaced TODO is this the right place for this ?
            sources.put(sourceName, sourceURL.replace("dodsC", "fileServer"));
        }

        // update data packet with new data record.
        dataPacket.addRecord(sourceName, calRecord);

        // add updated/new data packet into the map.
        data.put(date, dataPacket);
    }

    /**
     * Returns the names of the added sources.
     * 
     * @return map with names of added sources and its corresponding URLs.
     */
    public final Map<String, String> addedSources()
    {
        return sources;
    }

    /**
     * Returns the set of dates contained in this data container (keeping the order in which they are stored).
     * 
     * @return dates contained in this data container.
     */
    public final Set<Date> dates()
    {
        return data.keySet();
    }

    /**
     * Returns the first date contained in this data container.
     * 
     * @return first date.
     */
    public final Date firstDate()
    {
        return data.firstKey();
    }

    /**
     * Returns the last date contained in this data container.
     * 
     * @return last date.
     */
    public final Date lastDate()
    {
        return data.lastKey();
    }

    /**
     * Returns the calibration packet for a certain date.
     * 
     * @param date
     *            date.
     * @return calibration packet for this date.
     */
    public final CalibrationPacket getCalibrationPacket(final Date date)
    {
        return data.get(date);
    }

}
