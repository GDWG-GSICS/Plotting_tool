//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
//@formatter:on
package org.eumetsat.usd.gcp.server.data;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Range;

import org.eumetsat.usd.gcp.shared.exception.DatasetReadException;
import org.eumetsat.usd.gcp.shared.exception.FileException;
import org.eumetsat.usd.gcp.shared.exception.FormulaException;
import org.eumetsat.usd.gcp.shared.exception.InvalidFilenameException;
import org.eumetsat.usd.gcp.shared.exception.InvalidFormatException;
import org.eumetsat.usd.gcp.shared.exception.VariableNotFoundException;
import org.eumetsat.usd.gcp.shared.exception.VariableReadException;

/**
 * The Calibration Data Manager provides the functionality to store and export to a CSV file the calibration data the
 * users requested.
 * 
 * @author USC/C/PBe
 */
public interface CalibrationDataManager
{
    /**
     * Adds calibration data from a dataset for a certain user.
     * 
     * @param userID
     *            id of the user making the request.
     * @param datasetURL
     *            URL of the dataset containing calibration data.
     * @param channelName
     *            name of the channel of the data to be added.
     * @param userSceneTb
     *            scene brightness temperature selected by the user making the request.
     * @throws DatasetReadException
     *             when dataset could not be opened for reading.
     * @throws InvalidFormatException
     *             when dataset has an invalid format.
     * @throws InvalidFilenameException
     *             when dataset has an invalid filename.
     */
    void addDataFromDatasetForUser(final String userID, final String datasetURL, final String channelName,
            final double userSceneTb) throws DatasetReadException, InvalidFormatException, InvalidFilenameException;

    /**
     * Clears the calibration data for a certain user. If there is no data for this user, it does nothing.
     * 
     * @param userID
     *            id of the user making the request.
     */
    void clearDataForUser(final String userID);

    /**
     * Returns the date window of data for a certain user.
     * 
     * @param userID
     *            ID id of the user making the request.
     * @return date window.
     */
    Range<Date> dateWindowForUser(final String userID);

    /**
     * Returns the names and the corresponding URLs of the added sources.
     * 
     * @param userID
     *            ID id of the user making the request.
     * @return map with names of added sources and its corresponding URLs.
     */
    Map<String, String> addedSourcesForUser(final String userID);

    /**
     * Get the list of channel names in a dataset.
     * 
     * @param datasetURL
     *            the dataset URL.
     * @return list of channel names.
     * @throws DatasetReadException
     *             if the dataset has not been found.
     * @throws VariableReadException
     *             when an error occurred while reading the variable from the file.
     * @throws VariableNotFoundException
     *             when an expected variable is not found.
     * @throws InvalidFilenameException
     *             when dataset has an invalid filename.
     */
    List<String> channelNamesFrom(final String datasetURL) throws DatasetReadException, VariableNotFoundException,
            VariableReadException, InvalidFilenameException;

    /**
     * Get the standard brightness temperature of a specific channel from a dataset.
     * 
     * @param datasetURL
     *            the dataset URL.
     * @param channelName
     *            name of the channel.
     * @return standard brightness temperature.
     * @throws DatasetReadException
     *             if the dataset has not been found.
     * @throws VariableReadException
     *             when an error occurred while reading the variable from the file.
     * @throws VariableNotFoundException
     *             when an expected variable is not found.
     * @throws InvalidFormatException
     *             when dataset has an invalid format.
     * @throws InvalidFilenameException
     *             when dataset has an invalid filename.
     */
    double stdSceneTbFrom(final String datasetURL, final String channelName) throws DatasetReadException,
            VariableNotFoundException, VariableReadException, InvalidFormatException, InvalidFilenameException;

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
    void exportToCSVForUser(final String userID) throws FileException, FormulaException;
}
