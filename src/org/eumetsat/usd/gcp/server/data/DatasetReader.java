//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
//@formatter:on
package org.eumetsat.usd.gcp.server.data;

import org.eumetsat.usd.gcp.shared.exception.FileException;
import org.eumetsat.usd.gcp.shared.exception.FormulaException;

/**
 * The Dataset Reader provides the functionality to read from a dataset containing calibration data.
 * 
 * @author USC/C/PBe
 */
public interface DatasetReader
{
    /**
     * Returns the calibration data for a certain user. If there is no data for this user, it creates a new container.
     * 
     * @param userID
     *            id of the user making the request.
     * @return the data requested by this user.
     */
    CalibrationData getCalibrationData(final String userID);

    /**
     * Adds calibration data for a certain user.
     * 
     * @param userID
     *            id of the user making the request.
     * @param calibrationData
     *            calibration data to be stored.
     */
    void addCalibrationData(final String userID, final CalibrationData calibrationData);

    /**
     * Clears the calibration data for a certain user. If there is no data for this user, it does nothing.
     * 
     * @param userID
     *            id of the user making the request.
     */
    void clearCalibrationData(final String userID);

    /**
     * Exports the calibration data to a CSV file for a certain user. This CSV is the file being downloaded from the
     * client to be plotted.
     * 
     * @param userID
     *            id of the user making the request.
     * @throws FileException
     *             when a problem occurred writing to the CSV file.
     * @throws FormulaException
     *             when there is an error with formula parsing.
     */
    void exportToCSV(final String userID) throws FileException, FormulaException;
}
