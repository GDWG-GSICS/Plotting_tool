// @formatter:off
/*
 * PROJECT: gcp 
 * AUTHOR: USC/C/PBe 
 * COPYRIGHT: EUMETSAT 2015
 */
// @formatter:on
package org.eumetsat.usd.gcp.server.catalog;

import java.util.List;

import org.eumetsat.usd.gcp.shared.exception.DatasetNotFoundException;
import org.eumetsat.usd.gcp.shared.exception.InvalidCatalogException;

/**
 * Reads and navigates a GSICS THREDDS Catalog, assuming it follows this GSICS agreed folder structure:
 * 
 * GSICS GPRC > Correction Type > Satellite/Instrument calibrated against Reference Satellite/Instrument > Mode
 * 
 * And the files within the last folder must follow WMO file naming conventions, including date-time, and version.
 * 
 * @author USC/C/PBe
 */
public interface CatalogNavigator
{
    /**
     * Gets available GPRCs in the catalog.
     * 
     * @return available GPRCs (no duplicates); may be empty, never <code>null</code>.
     * @throws InvalidCatalogException
     *             if the catalog is invalid or does not follow agreed conventions.
     */
    List<String> getGPRCs() throws InvalidCatalogException;

    /**
     * Gets available correction types for a certain source in the catalog.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @return available correction types (no duplicates); may be empty, never <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    List<String> getCorrTypes(final String gprc) throws InvalidCatalogException;

    /**
     * Gets available satellite/instrument pairs for a certain GPRCS, and correction type in the catalog.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type (RAC, NRTC...).
     * @return available satellite/instrument pairs (no duplicates); may be empty, never <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    List<String> getSatInstrs(final String gprc, final String corrType) throws InvalidCatalogException;

    /**
     * Gets available reference satellite/instrument pairs for a certain GPRCS, correction type and satellite/instrument
     * in the catalog.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type of data (RAC, NRTC...).
     * @param satInstr
     *            satellite/instrument.
     * @return available satellite/instrument pairs (no duplicates); may be empty, never <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    List<String> getRefSatInstrs(final String gprc, final String corrType, final String satInstr)
            throws InvalidCatalogException;

    /**
     * Gets available modes for a certain GPRCS, correction type, satellite/instrument, and reference
     * satellite/instrument in the catalog.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type of data (RAC, NRTC...)
     * @param satInstr
     *            satellite/instrument.
     * @param refSatInstr
     *            reference satellite/instrument.
     * @return available modes (no duplicates); may be empty, never <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    List<String> getModes(final String gprc, final String corrType, final String satInstr, final String refSatInstr)
            throws InvalidCatalogException;

    /**
     * Gets available years for a certain GPRCS, correction type, satellite/instrument, reference satellite/instrument
     * and mode in the catalog.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type of data (RAC, NRTC...)
     * @param satInstr
     *            satellite/instrument.
     * @param refSatInstr
     *            reference satellite/instrument.
     * @param mode
     *            mode of the data (demonstration, pre-operational or operational).
     * @return available years (no duplicates); may be empty, never <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    List<String> getYears(final String gprc, final String corrType, final String satInstr, final String refSatInstr,
            final String mode) throws InvalidCatalogException;

    /**
     * Gets available date-times for a certain GPRCS, correction type, satellite/instrument, reference
     * satellite/instrument, mode and year in the catalog.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type of data (RAC, NRTC...)
     * @param satInstr
     *            satellite/instrument.
     * @param refSatInstr
     *            reference satellite/instrument.
     * @param mode
     *            mode of the data (demonstration, pre-operational or operational).
     * @param year
     *            year of the data.
     * @return available date-times (no duplicates); may be empty, never <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    List<String> getDateTimes(final String gprc, final String corrType, String satInstr, final String refSatInstr,
            final String mode, final String year) throws InvalidCatalogException;

    /**
     * Gets available versions for a certain GPRCS, correction type, satellite/instrument, reference
     * satellite/instrument, mode, year and date-time in the catalog.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type of data (RAC, NRTC...)
     * @param satInstr
     *            satellite/instrument.
     * @param refSatInstr
     *            reference satellite/instrument.
     * @param mode
     *            mode of the data (demonstration, pre-operational or operational).
     * @param year
     *            of the data.
     * @param dateTime
     *            of the data.
     * @return available versions (no duplicates); may be empty, never <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    List<String> getVersions(final String gprc, final String corrType, final String satInstr, final String refSatInstr,
            final String mode, final String year, final String dateTime) throws InvalidCatalogException;

    /**
     * Gets NetCDF dataset URL for a certain GPRCS, correction type, satellite/instrument, reference
     * satellite/instrument, mode, year, date-time and version in the catalog.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type of data (RAC, NRTC...)
     * @param satInstr
     *            satellite/instrument.
     * @param refSatInstr
     *            reference satellite/instrument.
     * @param mode
     *            mode of the data (demonstration, pre-operational or operational); if <code>null</code>, first one with
     *            data, with this preference in mind: operational > pre-operational > demo.
     * @param year
     *            of the data; if <code>null</code>, last year available.
     * @param dateTime
     *            of the data, or "latest"; if <code>null</code>, last date-time available.
     * @param version
     *            version of the data; if <code>null</code>, last version available.
     * @return dataset URL.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     * @throws DatasetNotFoundException
     *             if no dataset for these parameters is found in the catalog.
     */
    String getDatasetURL(final String gprc, final String corrType, final String satInstr, final String refSatInstr,
            String mode, String year, String dateTime, String version) throws InvalidCatalogException,
            DatasetNotFoundException;
}
