// @formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
// @formatter:on
package org.eumetsat.usd.gcp.server.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.Range;

import org.eumetsat.usd.gcp.server.conf.GlobalAttributesNames;
import org.eumetsat.usd.gcp.server.conf.VariablesNames;
import org.eumetsat.usd.gcp.server.conf.ConfigManager;
import org.eumetsat.usd.gcp.server.exception.BadArgumentException;
import org.eumetsat.usd.gcp.server.util.DateUtils;
import org.eumetsat.usd.gcp.server.util.NetcdfUtils;
import org.eumetsat.usd.gcp.shared.conf.NetcdfFilename;
import org.eumetsat.usd.gcp.shared.exception.ChannelNotFoundException;
import org.eumetsat.usd.gcp.shared.exception.DatasetReadException;
import org.eumetsat.usd.gcp.shared.exception.FileException;
import org.eumetsat.usd.gcp.shared.exception.FormulaException;
import org.eumetsat.usd.gcp.shared.exception.InvalidFilenameException;
import org.eumetsat.usd.gcp.shared.exception.InvalidFormatException;
import org.eumetsat.usd.gcp.shared.exception.VariableNotFoundException;
import org.eumetsat.usd.gcp.shared.exception.VariableReadException;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.Array;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;

import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

/**
 * @author USC/C/PBe
 *
 */
public class NetCDFCalibrationDataManager implements CalibrationDataManager
{
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetCDFCalibrationDataManager.class);

    /** Standard name for brightness temperature variable. */
    private static final String TB_VARNAME = "tb";

    /** Standard name for radiance variable. */
    private static final String RAD_VARNAME = "radiance";

    /** Standard name for c1 global attribute, in the formula. TODO: remove workaround when corrected. */
    private static final String C1_VARNAME = "c1";

    /** Standard name for c2 global attribute, in the formula. TODO: remove workaround when corrected. */
    private static final String C2_VARNAME = "c2";

    /**
     * Calibration data indexed by User ID (one for each different browser connecting to the server). No need to be
     * thread-safe, since map entries are identified by unique ID, thus not shared between threads/users.
     */
    private final Map<String, CalibrationData> dataCollection = new HashMap<String, CalibrationData>();

    /** Path to the CSV file with the data to be plotted. */
    private final String csvFilePath;

    /** Configuration manager. */
    private ConfigManager configManager;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @BindingAnnotation
    public @interface CsvFilePath
    {
    }

    /**
     * Constructor.
     * 
     * @param servletContext
     *            servlet context.
     * @param csvFilePath
     *            path to the CSV file.
     * @param configManager
     *            configuration manager.
     */
    @Inject
    public NetCDFCalibrationDataManager(final ServletContext servletContext, @CsvFilePath final String csvFilePath,
            final ConfigManager configManager)
    {
        this.configManager = configManager;

        // set path to CSV file.
        this.csvFilePath = servletContext.getRealPath(File.separator + csvFilePath);
    }

    /**
     * Returns the calibration data for a certain user. If there is no data for this user, it creates a new container.
     * 
     * @param userID
     *            id of the user making the request.
     * @return the data requested by this user.
     */
    private CalibrationData dataForUser(final String userID)
    {
        if (!dataCollection.containsKey(userID))
        {
            // Create new container.
            final CalibrationData calibrationData = new CalibrationData();

            // Add it to collection.
            dataCollection.put(userID, calibrationData);

            return calibrationData;
        } else
        {
            return dataCollection.get(userID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDataFromDatasetForUser(String userID, String datasetURL, String channelName, double userSceneTb)
            throws DatasetReadException, InvalidFormatException, InvalidFilenameException
    {
        // Download file to speed reading up.
        NetcdfFile ncfile = NetcdfUtils.downloadAndOpenFile(datasetURL);

        // Extract the conversion formulas.
        Set<String> convVarsNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER); // init conversion var names
                                                                                        // list, case-insensitive.

        GlobalAttributesNames globalAttrNames = configManager.getGlobalAttributesNames();

        Attribute radToTbFormulaAttr = ncfile.findGlobalAttributeIgnoreCase(globalAttrNames.getRadToTbConvFormula());
        Attribute tbToRadFormulaAttr = ncfile.findGlobalAttributeIgnoreCase(globalAttrNames.getTbToRadConvFormula());

        String radToTbConvFormula = configManager.getGlobalAttributesDefaults().getRadToTbConvFormula(); // default.
        if (radToTbFormulaAttr != null)
        {
            try
            {
                radToTbConvFormula = processRadianceToTbFormula(radToTbFormulaAttr.getStringValue().split("=")[1],
                        convVarsNames);

            } catch (FormulaException fe)
            {
                LOGGER.warn("invalid radiance to tb conversion formula in <" + FilenameUtils.getName(datasetURL)
                        + ">. Using default.", fe);
            }
        } else
        {
            LOGGER.warn("radiance to tb conversion formula not found. Using default.");
        }

        String tbToRadConvFormula = configManager.getGlobalAttributesDefaults().getRadToTbConvFormula(); // default.
        if (tbToRadFormulaAttr != null)
        {
            try
            {
                tbToRadConvFormula = processTbToRadianceFormula(tbToRadFormulaAttr.getStringValue().split("=")[1],
                        convVarsNames);

            } catch (FormulaException fe)
            {
                LOGGER.warn("invalid tb to radiance conversion formula in <" + FilenameUtils.getName(datasetURL)
                        + ">. Using default.", fe);
            }
        } else
        {
            LOGGER.warn("tb to radiance conversion formula not found. Using default.");
        }

        // Extract the variables from the NetCDF file -------------------
        try
        {
            VariablesNames varNames = configManager.getVariablesNames();

            // Get list of channel indexes to retrieve (All channels or single channel)
            Array channelNames = NetcdfUtils.readVariable(ncfile, varNames.getChannelName());

            int firstChannelNum = 0;
            int lastChannelNum = 0;

            if (channelName.equalsIgnoreCase("All"))
            {
                firstChannelNum = 0;
                lastChannelNum = NetcdfUtils.getNumRowsOf(channelNames) - 1;

            } else
            {
                firstChannelNum = NetcdfUtils.getIndexOf(channelName, channelNames);

                if (firstChannelNum == -1)
                {
                    throw new InvalidFormatException("'" + channelName + "' not found in the NetCDF file <"
                            + FilenameUtils.getName(datasetURL) + ">.");
                }

                lastChannelNum = firstChannelNum;
            }

            for (int channelNum = firstChannelNum; channelNum <= lastChannelNum; channelNum++)
            {
                // Get data array from the netCDF file.
                Array dateArray = NetcdfUtils.readVariable(ncfile, varNames.getDate());
                Array offsetArray = NetcdfUtils.readVariable(ncfile, varNames.getOffset(), channelNum);
                Array slopeArray = NetcdfUtils.readVariable(ncfile, varNames.getSlope(), channelNum);
                Array offsetSeArray = NetcdfUtils.readVariable(ncfile, varNames.getOffsetSe(), channelNum);
                Array slopeSeArray = NetcdfUtils.readVariable(ncfile, varNames.getSlopeSe(), channelNum);

                // Flag with Double.POSITIVE_INFINITY if slope is equal to 0.
                for (int i = 0; i < slopeArray.getSize(); i++)
                {
                    if (Double.compare(slopeArray.getDouble(i), 0) == 0)
                    {
                        slopeArray.setDouble(i, Double.POSITIVE_INFINITY);
                    }
                }

                // TODO: check if this workaround to support inconsistent datasets can be removed.
                Array covarianceArray = null;
                try
                {
                    covarianceArray = NetcdfUtils.readVariable(ncfile, varNames.getCovariance(), channelNum);

                } catch (VariableNotFoundException vnfe)
                {
                    try
                    {
                        covarianceArray = NetcdfUtils.readVariable(ncfile, "covar_of_offset_and_slope", channelNum);

                    } catch (VariableNotFoundException vnfe2)
                    {
                        covarianceArray = NetcdfUtils.readVariable(ncfile, "covar", channelNum);
                    }
                }

                // Get stdSceneTb if not defined.
                double sceneTb = 0.0;

                if (Double.compare(userSceneTb, -1.0) == 0)
                {
                    sceneTb = NetcdfUtils.readVariable(ncfile, varNames.getStdSceneTb()).getDouble(channelNum);

                } else
                {
                    sceneTb = userSceneTb;
                }

                NetcdfFilename ncfilename = NetcdfFilename.parse(FilenameUtils.getName(datasetURL));
                String currentChannelName = NetcdfUtils.getStringFrom(channelNum, channelNames);

                // Format timestamp.
                String timestamp = DateUtils.format(
                        DateUtils.parse(ncfilename.getTimestamp(), "yyyyMMddHHmmss", "GMT"), "yyyy/MM/dd HH:mm:ss",
                        "GMT");

                // Construct the dataset name.
                String datasetName = ncfilename.getSatellite() + "/" + ncfilename.getInstrument() + " referenced with "
                        + ncfilename.getRefSatellite() + "/" + ncfilename.getRefInstrument() + " ["
                        + ncfilename.getLocationIndication().split("-")[1] + "][" + ncfilename.getCorrectionType()
                        + "][" + ncfilename.getMode() + "][" + timestamp + "][v" + ncfilename.getVersion() + "]["
                        + currentChannelName + "][" + sceneTb + "K]";

                // Add new records.
                addCalibrationRecords(userID, ncfile, datasetName, datasetURL, dateArray, offsetArray, offsetSeArray,
                        slopeArray, slopeSeArray, covarianceArray, channelNum, sceneTb, radToTbConvFormula,
                        tbToRadConvFormula, convVarsNames);
            }

        } catch (BadArgumentException bae)
        {
            throw new InvalidFormatException("Format of NetCDF file <" + FilenameUtils.getName(datasetURL)
                    + "> is invalid.", bae);

        } catch (ParseException pe)
        {
            throw new InvalidFormatException("Timestamp with invalid format.", pe);

        } catch (VariableNotFoundException vnfe)
        {
            throw new InvalidFormatException("Variable '" + vnfe.getVariableName() + "' not found in NetCDF file <"
                    + FilenameUtils.getName(datasetURL) + ">.", vnfe);

        } catch (VariableReadException vre)
        {
            throw new InvalidFormatException("Variable '" + vre.getVariableName() + "' not found in NetCDF file <"
                    + FilenameUtils.getName(datasetURL) + ">.", vre);

        } catch (ChannelNotFoundException cnfe)
        {
            throw new InvalidFormatException("Channel number '" + cnfe.getChannelNum() + "' not found in variable '"
                    + cnfe.getVariableName() + "' in in NetCDF file <" + FilenameUtils.getName(datasetURL) + ">.", cnfe);

        } finally
        {
            // Clean-up
            if (ncfile != null)
            {
                try
                {
                    ncfile.close();

                } catch (IOException ioe)
                {
                    LOGGER.error("trying to close the NetcdfFile", ioe);
                }
            }
        }

    }

    /**
     * Add several calibration records from a certain source.
     * 
     * @param userID
     *            id of the user making the request.
     * @param ncfile
     *            netCDF file handler to read conversion parameters.
     * @param sourceName
     *            name of the data source.
     * @param sourceURL
     *            URL of the data source.
     * @param dateArray
     *            array of dates.
     * @param offsetArray
     *            array of offsets.
     * @param offsetSeArray
     *            array of standard deviations for offset.
     * @param slopeArray
     *            array of slopes.
     * @param slopeSeArray
     *            array of standard deviations for slope.
     * @param covarianceArray
     *            array of covariances.
     * @param channelNum
     *            channel number.
     * @param sceneTb
     *            scene brightness temperature.
     * @param radToTbConvFormula
     *            rad to tb conversion formula.
     * @param tbToRadConvFormula
     *            tb to rad conversion formula.
     * @param convVarsNames
     *            conversion variables names.
     * @throws DatasetReadException
     *             when dataset could not be opened for reading.
     * @throws InvalidFormatException
     *             when dataset has an invalid format.
     * @throws InvalidFilenameException
     *             when dataset has an invalid filename.
     * @throws VariableReadException
     * @throws ChannelNotFoundException
     * @throws VariableNotFoundException
     */
    private final void addCalibrationRecords(final String userID, final NetcdfFile ncfile, final String sourceName,
            final String sourceURL, final Array dateArray, final Array offsetArray, final Array offsetSeArray,
            final Array slopeArray, final Array slopeSeArray, final Array covarianceArray, final int channelNum,
            final double sceneTb, final String radToTbConvFormula, final String tbToRadConvFormula,
            final Set<String> convVarsNames) throws BadArgumentException, InvalidFilenameException,
            DatasetReadException, VariableNotFoundException, ChannelNotFoundException, VariableReadException
    {
        // Check dimensions consistency.
        if ((dateArray.getShape()[0] != offsetArray.getShape()[0])
                || (dateArray.getShape()[0] != slopeArray.getShape()[0])
                || (dateArray.getShape()[0] != offsetSeArray.getShape()[0])
                || (dateArray.getShape()[0] != slopeSeArray.getShape()[0])
                || (dateArray.getShape()[0] != covarianceArray.getShape()[0]))
        {
            throw new BadArgumentException("array dimensions mismatch.");
        }

        // Sweep arrays and add each record into the map.
        for (int i = 0; i < dateArray.getShape()[0]; i++)
        {
            Double dateDouble = dateArray.getDouble(i) * 1e3; // in [ms]
            Date date = new Date(dateDouble.longValue());

            // Read the conversion variables.
            Map<String, Double> convVars = new HashMap<String, Double>();
            for (String convVarName : convVarsNames)
            {
                // TODO: [Remove workaround when formulas are changed]
                // Restore 'c1' and 'c2', if they are in the formula...
                if (convVarName.equals(configManager.getGlobalAttributesNames().getC1()))
                {
                    convVars.put(C1_VARNAME, NetcdfUtils.readDouble(ncfile, convVarName, i, channelNum));

                } else if (convVarName.equals(configManager.getGlobalAttributesNames().getC2()))
                {
                    convVars.put(C2_VARNAME, NetcdfUtils.readDouble(ncfile, convVarName, i, channelNum));
                } else
                {
                    convVars.put(convVarName, NetcdfUtils.readDouble(ncfile, convVarName, i, channelNum));
                }
            }

            // Create calibration record.
            CalibrationRecord calRecord = new CalibrationRecordImpl(radToTbConvFormula, tbToRadConvFormula, convVars,
                    TB_VARNAME, RAD_VARNAME, offsetArray.getDouble(i), offsetSeArray.getDouble(i),
                    slopeArray.getDouble(i), slopeSeArray.getDouble(i), covarianceArray.getDouble(i), sceneTb);

            // Add calibration record, if valid, to data for this user.
            if (calRecord.isValid())
            {
                dataForUser(userID).addRecord(date, sourceName, sourceURL, calRecord);

                // TODO: to be checked.
                // if single-point, add a second one, with same value, and shifted one second, so that
                // it can be plotted by dygraphs.
                if (dateArray.getShape()[0] == 1)
                {
                    DateTime dt = new DateTime(date);
                    dt = dt.plus(Seconds.ONE);

                    dataForUser(userID).addRecord(dt.toDate(), sourceName, sourceURL, calRecord);
                }
            }
        }
    }

    /**
     * Process the radiance to tb conversion formula (always lower case).
     * 
     * @param radianceToTbFormula
     *            radiance to tb conversion formula.
     * @param convVarsNames
     *            it will be filled with conversion variable names required by the formula.
     * @return processed conversion formula.
     * @throws FormulaException
     *             when there is an error with formula parsing.
     */
    private final String processRadianceToTbFormula(final String radianceToTbFormula, final Set<String> convVarsNames)
            throws FormulaException
    {
        // Support both 'ln' and 'log' for expressing 'logarithmus naturalis (base e)'.
        // Library "JEP" (GPL) support 'ln', and "exp4j" (apache license) support 'log'.
        String radToTbFormulaClean = radianceToTbFormula.toLowerCase().replaceAll("\\s", "").replaceAll("ln", "log");

        // Read the variable names from the formula.
        String[] variableNamesArray = radToTbFormulaClean.split("((\\W)|((?<=\\W)\\d+(?=\\W)?)|"
                + "(abs|acos|asin|atan|cbrt|ceil|cos|cosh|exp|floor|ln|log|log2|log10|sin|sinh|sqrt|tan|tanh))+");

        convVarsNames.addAll(Arrays.asList(variableNamesArray));
        convVarsNames.remove("");

        // Validity check.
        try
        {
            new ExpressionBuilder(radToTbFormulaClean).withVariableNames(
                    convVarsNames.toArray(new String[convVarsNames.size()])).build();

        } catch (UnknownFunctionException ufe)
        {
            throw new FormulaException(ufe.getMessage(), ufe);

        } catch (UnparsableExpressionException upe)
        {
            throw new FormulaException(upe.getMessage(), upe);
        }

        // Remove radiance and tb, since this will not come from any netCDF variable.
        convVarsNames.remove(RAD_VARNAME);
        convVarsNames.remove(TB_VARNAME);

        // TODO: [Remove workaround when formulas are changed]
        // Remove 'c1' and 'c2', if they are in the formula, and substitute them for the configured name...
        if (convVarsNames.contains(C1_VARNAME))
        {
            convVarsNames.remove(C1_VARNAME);
            convVarsNames.add(configManager.getGlobalAttributesNames().getC1());
        }
        if (convVarsNames.contains(C2_VARNAME))
        {
            convVarsNames.remove(C2_VARNAME);
            convVarsNames.add(configManager.getGlobalAttributesNames().getC2());
        }

        return radToTbFormulaClean;
    }

    /**
     * Process tb to radiance conversion formula (always lower case).
     * 
     * @param tbToRadianceFormula
     *            tb to radiance conversion formula.
     * @param convVarsNames
     *            it will be filled with conversion variable names required by the formula.
     * @return processed conversion formula.
     * @throws FormulaException
     *             when there is an error with formula parsing.
     */
    private final String processTbToRadianceFormula(final String tbToRadianceFormula, final Set<String> convVarsNames)
            throws FormulaException
    {
        // Support both 'ln' and 'log' for expressing 'logarithmus naturalis (base e)'.
        // Library "JEP" (GPL) support 'ln', and "exp4j" (apache license) support 'log'.
        String tbToRadFormulaClean = tbToRadianceFormula.toLowerCase().replaceAll("\\s", "").replaceAll("ln", "log");

        // Read the variable names from the formula.
        String[] variableNamesArray = tbToRadFormulaClean.split("((\\W)|((?<=\\W)\\d+(?=\\W)?)|"
                + "(abs|acos|asin|atan|cbrt|ceil|cos|cosh|exp|floor|ln|log|log2|log10|sin|sinh|sqrt|tan|tanh))+");

        convVarsNames.addAll(Arrays.asList(variableNamesArray));
        convVarsNames.remove("");

        // Validity check.
        try
        {
            new ExpressionBuilder(tbToRadFormulaClean).withVariableNames(
                    convVarsNames.toArray(new String[convVarsNames.size()])).build();

        } catch (UnknownFunctionException ufe)
        {
            throw new FormulaException(ufe.getMessage(), ufe);

        } catch (UnparsableExpressionException upe)
        {
            throw new FormulaException(upe.getMessage(), upe);
        }

        // Remove radiance and tb, since this will not come from any netCDF variable.
        convVarsNames.remove(RAD_VARNAME);
        convVarsNames.remove(TB_VARNAME);

        // TODO: [Remove workaround when formulas are changed]
        // Remove 'c1' and 'c2', if they are in the formula, and substitute them for the configured name...
        if (convVarsNames.contains(C1_VARNAME))
        {
            convVarsNames.remove(C1_VARNAME);
            convVarsNames.add(configManager.getGlobalAttributesNames().getC1());
        }
        if (convVarsNames.contains(C2_VARNAME))
        {
            convVarsNames.remove(C2_VARNAME);
            convVarsNames.add(configManager.getGlobalAttributesNames().getC2());
        }

        return tbToRadFormulaClean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearDataForUser(final String userID)
    {
        if (dataCollection.containsKey(userID))
        {
            dataCollection.get(userID).clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Range<Date> dateWindowForUser(final String userID)
    {
        return Range.closed(dataForUser(userID).firstDate(), dataForUser(userID).lastDate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Map<String, String> addedSourcesForUser(final String userID)
    {
        return dataForUser(userID).addedSources();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<String> channelNamesFrom(final String datasetURL) throws DatasetReadException,
            VariableNotFoundException, VariableReadException, InvalidFilenameException
    {
        // Download file to speed reading up.
        NetcdfFile ncfile = NetcdfUtils.downloadAndOpenFile(datasetURL);

        final Array channelArray = NetcdfUtils.readVariable(ncfile, configManager.getVariablesNames().getChannelName());

        char[][] charMultiArray = (char[][]) channelArray.copyToNDJavaArray();
        int numOfRows = channelArray.getShape()[0];

        List<String> channelList = new ArrayList<String>(numOfRows);

        for (int i = 0; i < numOfRows; i++)
        {
            channelList.add(String.valueOf(charMultiArray[i]).trim());
        }

        // Clean-up
        if (ncfile != null)
        {
            try
            {
                ncfile.close();

            } catch (IOException ioe)
            {
                LOGGER.error("trying to close the NetcdfFile", ioe);
            }
        }

        return channelList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double stdSceneTbFrom(final String datasetURL, final String channelName) throws DatasetReadException,
            VariableNotFoundException, VariableReadException, InvalidFormatException, InvalidFilenameException
    {
        double stdSceneTb = -1;

        if (!channelName.equals("All"))
        {
            // Download file to speed reading up.
            NetcdfFile ncfile = NetcdfUtils.downloadAndOpenFile(datasetURL);

            try
            {
                // Extract the std scene tb from the netcdf file.
                VariablesNames varNames = configManager.getVariablesNames();

                int channelNum = NetcdfUtils.getIndexOf(channelName,
                        NetcdfUtils.readVariable(ncfile, varNames.getChannelName()));

                if (channelNum == -1)
                {
                    throw new InvalidFormatException("'" + channelName + "' not found in the NetCDF file <"
                            + FilenameUtils.getName(datasetURL) + ">.");
                }

                stdSceneTb = NetcdfUtils.readVariable(ncfile, varNames.getStdSceneTb()).getDouble(channelNum);

            } catch (VariableNotFoundException vnfe)
            {
                throw new InvalidFormatException("Variable '" + vnfe.getVariableName() + "' not found in NetCDF file <"
                        + FilenameUtils.getName(datasetURL) + ">.", vnfe);

            } catch (VariableReadException vre)
            {
                throw new InvalidFormatException("Variable '" + vre.getVariableName() + "' not found in NetCDF file <"
                        + FilenameUtils.getName(datasetURL) + ">.", vre);

            } finally
            {
                // Clean-up
                if (ncfile != null)
                {
                    try
                    {
                        ncfile.close();

                    } catch (IOException ioe)
                    {
                        LOGGER.error("trying to close the NetcdfFile", ioe);
                    }
                }
            }

        } else
        {
            stdSceneTb = -1;
        }

        return stdSceneTb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void exportToCSVForUser(final String userID) throws FileException, FormulaException
    {
        final CalibrationData data = dataForUser(userID);

        final String userCsvFilePath = csvFilePath + "-" + userID + ".csv";

        // Deletes the file if already exists.
        File csvFile = new File(userCsvFilePath);

        if (csvFile.exists())
        {
            csvFile.delete();
        }

        // Create FileWriter
        FileWriter writer = null;

        try
        {
            writer = new FileWriter(userCsvFilePath);

        } catch (IOException ioe)
        {
            throw new FileException("trying to open '" + userCsvFilePath + "'", ioe);
        }

        // Write to file.
        try
        {
            // Print first line with all sources names.
            writer.append("Date");

            for (String sourceName : dataForUser(userID).addedSources().keySet())
            {
                writer.append("," + sourceName + ",1-sigma +/-");
            }

            writer.append("\n");

            // Sweep map in chronological order (since it is a TreeMap, i.e. sorted map)
            Set<Date> dates = dataForUser(userID).dates();
            Iterator<Date> itDates = dates.iterator();

            while (itDates.hasNext())
            {
                Date date = itDates.next();

                // Write date (local [NOT UTC], because it is what dygraphs expects on the client
                // side).
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String dateStr = df.format(date);

                writer.append(dateStr);

                // Write records.
                CalibrationPacket dataPacket = data.getCalibrationPacket(date);

                // For each added source...
                for (String sourceName : dataForUser(userID).addedSources().keySet())
                {
                    // Get cal record.
                    CalibrationRecord calRecord = dataPacket.getRecord(sourceName);

                    if (calRecord != null)
                    {
                        // Write to file.
                        writer.append("," + calRecord.getTbBias() + "," + calRecord.getTbUncertainty() + ","
                                + calRecord.getTbUncertainty() + ",0.000000000001");

                    } else
                    {
                        // Write to file.
                        writer.append(",,,,");
                    }
                }

                // Write to file.
                writer.append("\n");
            }

        } catch (IOException ioe)
        {
            throw new FileException("trying to append to '" + userCsvFilePath + "'", ioe);

        } finally
        {
            // Close writer.
            IOUtils.closeQuietly(writer);
        }

        LOGGER.info("'" + userCsvFilePath + "' successfully created.");
    }
}
