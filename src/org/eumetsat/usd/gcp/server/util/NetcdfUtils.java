// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2012
 */
// @formatter:on
package org.eumetsat.usd.gcp.server.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eumetsat.usd.gcp.shared.exception.ChannelNotFoundException;
import org.eumetsat.usd.gcp.shared.exception.DatasetReadException;
import org.eumetsat.usd.gcp.shared.exception.InvalidFilenameException;
import org.eumetsat.usd.gcp.shared.exception.VariableNotFoundException;
import org.eumetsat.usd.gcp.shared.exception.VariableReadException;

import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * Utility class providing functionality to retrieve data from a NetCDF File.
 * 
 * @author USD/C/PBe
 */
public final class NetcdfUtils
{
    /** File download timeouts. */
    private static final int FILE_CONNECTION_TIMEOUT = 15000; // ms
    private static final int FILE_READ_TIMEOUT = 30000; // ms

    /** Date dimension name. */
    private static final String DATE_DIM_NAME = "date";

    /** Channel dimension name. */
    private static final String CHANNEL_DIM_NAME = "chan";

    /**
     * Private constructor to make this class non-instantiable.
     * 
     */
    private NetcdfUtils()
    {
    }

    /**
     * Download netCDF file to the temp directory and opens it.
     * 
     * @param datasetURL
     *            dataset URL.
     * @returns netCDF file handle.
     * @throws InvalidFilenameException
     *             when dataset has an invalid filename.
     * @throws DatasetReadException
     *             when dataset could not be opened for reading.
     */
    public static NetcdfFile downloadAndOpenFile(final String datasetURL) throws InvalidFilenameException,
            DatasetReadException
    {
        File localFile = new File(System.getProperty("java.io.tmpdir"), FilenameUtils.getName(datasetURL));

        try
        {
            FileUtils.copyURLToFile(new URL(datasetURL), localFile, FILE_CONNECTION_TIMEOUT, FILE_READ_TIMEOUT);

        } catch (MalformedURLException mue)
        {
            if (localFile != null)
            {
                localFile.delete();
            }

            throw new InvalidFilenameException("Dataset URL not valid for downloading.", mue);

        } catch (IOException ioe)
        {
            if (localFile != null)
            {
                localFile.delete();
            }

            throw new DatasetReadException("trying to download <" + datasetURL + ">", ioe);
        }

        // Open for reading local temporary netCDF file.
        NetcdfFile ncfile = null;

        try
        {
            ncfile = NetcdfDataset.openDataset("file:" + FilenameUtils.separatorsToUnix(localFile.getCanonicalPath()));

        } catch (IOException ioe)
        {
            if (localFile != null)
            {
                localFile.delete();
            }

            throw new DatasetReadException("trying to open <" + datasetURL + ">", ioe);
        }

        return ncfile;
    }

    /**
     * Gets a variable from the NetCDF File and returns it as an Array.
     * 
     * @param ncfile
     *            the NetCDF file.
     * @param varName
     *            the variable name.
     * @param channelNum
     *            the channel number.
     * @return Array variable extracted.
     * @throws VariableNotFoundException
     *             when an expected variable is not found.
     * @throws ChannelNotFoundException
     *             when the requested channel is not found.
     * @throws VariableReadException
     *             when an error occurred while reading the variable from the file.
     */
    public static Array readVariable(final NetcdfFile ncfile, final String varName, final long channelNum)
            throws VariableNotFoundException, ChannelNotFoundException, VariableReadException
    {
        Variable v = ncfile.findVariable(varName);

        if (null == v)
        {
            throw new VariableNotFoundException(varName);
        }

        Array data = null;

        try
        {
            data = v.read(":," + channelNum + ":" + channelNum);

        } catch (InvalidRangeException ire)
        {
            throw new ChannelNotFoundException(varName, channelNum, ire);

        } catch (IOException ioe)
        {
            throw new VariableReadException(varName, ioe);
        }

        // Flag with Double.POSITIVE_INFINITY the out of bound values.
        Attribute valueMinAttr = v.findAttributeIgnoreCase("valid_min");
        Attribute valueMaxAttr = v.findAttributeIgnoreCase("valid_max");

        if (valueMinAttr != null && valueMaxAttr != null)
        {
            double valueMin = Double.NEGATIVE_INFINITY;
            double valueMax = Double.POSITIVE_INFINITY;

            if (valueMinAttr.isString())
            {
                valueMin = Double.valueOf(valueMinAttr.getStringValue());

            } else
            {
                valueMin = valueMinAttr.getNumericValue().doubleValue();
            }

            if (valueMaxAttr.isString())
            {
                valueMax = Double.valueOf(valueMaxAttr.getStringValue());

            } else
            {
                valueMax = valueMaxAttr.getNumericValue().doubleValue();
            }

            for (int i = 0; i < data.getSize(); i++)
            {
                if (data.getDouble(i) > valueMax || data.getDouble(i) < valueMin)
                {
                    data.setDouble(i, Double.POSITIVE_INFINITY);
                }
            }
        }

        return data;
    }

    /**
     * Gets a variable from the NetCDF File and returns it as an Array.
     * 
     * @param ncfile
     *            the NetCDF file.
     * @param varName
     *            the variable name.
     * 
     * @return Array variable extracted.
     * @throws VariableNotFoundException
     *             when an expected variable is not found.
     * @throws VariableReadException
     *             when an error occurred while reading the variable from the file.
     */
    public static Array readVariable(final NetcdfFile ncfile, final String varName) throws VariableNotFoundException,
            VariableReadException
    {
        Variable v = ncfile.findVariable(varName);

        if (null == v)
        {
            throw new VariableNotFoundException(varName);
        }

        Array data = null;

        try
        {
            data = v.read();

        } catch (IOException ioe)
        {
            throw new VariableReadException(varName, ioe);
        }

        // Flag with Double.POSITIVE_INFINITY the out of bound values.
        Attribute valueMinAttr = v.findAttributeIgnoreCase("valid_min");
        Attribute valueMaxAttr = v.findAttributeIgnoreCase("valid_max");

        if (valueMinAttr != null && valueMaxAttr != null)
        {
            double valueMin = Double.NEGATIVE_INFINITY;
            double valueMax = Double.POSITIVE_INFINITY;

            if (valueMinAttr.isString())
            {
                valueMin = Double.valueOf(valueMinAttr.getStringValue());

            } else
            {
                valueMin = valueMinAttr.getNumericValue().doubleValue();
            }

            if (valueMaxAttr.isString())
            {
                valueMax = Double.valueOf(valueMaxAttr.getStringValue());

            } else
            {
                valueMax = valueMaxAttr.getNumericValue().doubleValue();
            }

            for (int i = 0; i < data.getSize(); i++)
            {
                if (data.getDouble(i) > valueMax || data.getDouble(i) < valueMin)
                {
                    data.setDouble(i, Double.POSITIVE_INFINITY);
                }
            }
        }

        return data;
    }

    /**
     * Gets a variable from the NetCDF File and returns it as an double.
     * 
     * @param ncfile
     *            the NetCDF file.
     * @param varName
     *            the variable name.
     * @param channelNum
     *            the channel number.
     * @return double variable extracted.
     * 
     * @throws VariableNotFoundException
     *             when an expected variable is not found.
     * @throws ChannelNotFoundException
     *             when the requested channel is not found.
     * @throws VariableReadException
     *             when an error occurred while reading the variable from the file.
     */
    public static double readDouble(final NetcdfFile ncfile, final String varName, final int i, final int channelNum)
            throws VariableNotFoundException, ChannelNotFoundException, VariableReadException
    {
        double data = Double.POSITIVE_INFINITY; // default, invalid value.

        Variable v = ncfile.findVariable(varName);

        if (null == v)
        {
            // before throwing exceptions, try first to find it as global attribute...
            data = readGlobalAttrDouble(ncfile, varName);

            return data;
        }

        try
        {
            if (v.isScalar())
            {
                data = v.readScalarDouble();

            } else if (v.getDimensions().size() <= 2)
            {
                int dateDimIdx = v.findDimensionIndex(DATE_DIM_NAME);
                int channelDimIdx = v.findDimensionIndex(CHANNEL_DIM_NAME);

                int[] index = null;
                int[] size = new int[] { 1 };

                if (dateDimIdx != -1 && channelDimIdx != -1)
                {
                    index = new int[] { 0, 0 };
                    size = new int[] { 1, 1 };

                    index[dateDimIdx] = i;
                    index[channelDimIdx] = channelNum;

                } else if (dateDimIdx != -1 && channelDimIdx == -1)
                {
                    index = new int[] { i };
                    size = new int[] { 1 };

                } else if (dateDimIdx == -1 && channelDimIdx != -1)
                {
                    index = new int[] { channelNum };
                    size = new int[] { 1 };

                } else
                // dateDimIdx == -1 && channelDimIdx == -1
                {
                    throw new VariableReadException(varName, "No 'date' or 'chan' dimensions found.");
                }

                data = v.read(index, size).reduce().getDouble(0);

            } else
            {
                throw new VariableReadException(varName, "Too many dimensions. Maximum 2 (date and channel).");
            }

        } catch (InvalidRangeException ire)
        {
            throw new ChannelNotFoundException(varName, channelNum, ire);

        } catch (IOException ioe)
        {
            throw new VariableReadException(varName, ioe);
        }

        // Flag with Double.POSITIVE_INFINITY the out of bound values.
        Attribute valueMinAttr = v.findAttributeIgnoreCase("valid_min");
        Attribute valueMaxAttr = v.findAttributeIgnoreCase("valid_max");

        if (valueMinAttr != null && valueMaxAttr != null)
        {
            double valueMin = Double.NEGATIVE_INFINITY;
            double valueMax = Double.POSITIVE_INFINITY;

            if (valueMinAttr.isString())
            {
                valueMin = Double.valueOf(valueMinAttr.getStringValue());

            } else
            {
                valueMin = valueMinAttr.getNumericValue().doubleValue();
            }

            if (valueMaxAttr.isString())
            {
                valueMax = Double.valueOf(valueMaxAttr.getStringValue());

            } else
            {
                valueMax = valueMaxAttr.getNumericValue().doubleValue();
            }

            if (data > valueMax || data < valueMin)
            {
                data = Double.POSITIVE_INFINITY;
            }

        }

        return data;
    }

    /**
     * Gets a global attribute from the NetCDF File and returns it as an double.
     * 
     * @param ncfile
     *            the NetCDF file.
     * @param globalAttrName
     *            global attribute name.
     * @return double from global attribute extracted.
     * @throws VariableNotFoundException
     *             when an expected variable is not found.
     * @throws VariableReadException
     *             when an error occurred while reading the variable from the file.
     */
    public static double readGlobalAttrDouble(final NetcdfFile ncfile, final String globalAttrName)
            throws VariableReadException, VariableNotFoundException
    {
        // Global attributes.
        Attribute attr = ncfile.findGlobalAttributeIgnoreCase(globalAttrName);

        if (attr != null)
        {
            if (attr.isString())
            {
                return Double.valueOf(attr.getStringValue());

            } else
            {
                Number number = attr.getNumericValue();

                if (number != null)
                {
                    return number.doubleValue();

                } else
                {
                    throw new VariableReadException(globalAttrName, "It is not a double.");
                }
            }

        } else
        {
            throw new VariableNotFoundException(globalAttrName);
        }
    }

    /**
     * Returns the index of the string in a 2D NetCDFAPI (by Unidata) Array of chars (ignoring the case).
     * 
     * @param name
     *            to be searched.
     * @param char2DArray
     *            2D NetCDFAPI (by Unidata) Array of chars.
     * @return index of the string in the array; -1 if not found.
     */
    public static int getIndexOf(final String name, final Array char2DArray)
    {
        char[][] charMultiArray = (char[][]) char2DArray.copyToNDJavaArray();
        int numOfRows = char2DArray.getShape()[0];

        for (int i = 0; i < numOfRows; i++)
        {
            // compares only the alphanumeric characters, spaces and hyphens to make it compatible
            // with IE8.
            if (String.valueOf(charMultiArray[i]).trim().replaceAll("[^A-Za-z0-9 -]", "").equalsIgnoreCase(
                    name.trim().replaceAll("[^A-Za-z0-9 -]", "")))
            {
                return i;
            }
        }

        // if not found.
        return -1;
    }

    /**
     * Returns the string for a certain index in a 2D NetCDFAPI (by Unidata) Array of chars.
     * 
     * @param index
     *            to be searched.
     * @param char2DArray
     *            2D NetCDFAPI (by Unidata) Array of chars.
     * @return string in that index.
     */
    public static String getStringFrom(final int index, final Array char2DArray)
    {
        char[][] charMultiArray = (char[][]) char2DArray.copyToNDJavaArray();

        return String.valueOf(charMultiArray[index]).trim();
    }

    /**
     * Returns the number of rows in a 2D NetCDFAPI (by Unidata) Array of chars.
     * 
     * @param char2DArray
     *            2D NetCDFAPI (by Unidata) Array of chars.
     * @return number of rows.
     */
    public static int getNumRowsOf(final Array char2DArray)
    {
        int numOfRows = char2DArray.getShape()[0];

        return numOfRows;
    }
}
