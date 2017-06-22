//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
//@formatter:on
package org.eumetsat.usd.gcp.server.data;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.eumetsat.usd.gcp.server.conf.ConfigManager;
import org.eumetsat.usd.gcp.server.conf.GlobalAttributesDefaults;
import org.eumetsat.usd.gcp.server.conf.GlobalAttributesNames;
import org.eumetsat.usd.gcp.server.conf.VariablesNames;
import org.eumetsat.usd.gcp.server.util.DateUtils;
import org.eumetsat.usd.gcp.shared.conf.NetcdfFilename;
import org.eumetsat.usd.gcp.shared.exception.DatasetReadException;
import org.eumetsat.usd.gcp.shared.exception.FileException;
import org.eumetsat.usd.gcp.shared.exception.FormulaException;
import org.eumetsat.usd.gcp.shared.exception.InvalidFilenameException;
import org.eumetsat.usd.gcp.shared.exception.InvalidFormatException;
import org.eumetsat.usd.gcp.shared.exception.VariableNotFoundException;
import org.eumetsat.usd.gcp.shared.exception.VariableReadException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ucar.ma2.InvalidRangeException;

/**
 * Test NetCDFCalibrationDataManager, mocking also static NetcdfDataset.openDataset() method and a NetcdfDataset
 * instance, in order to speed the process up (Variable.read() takes around 1 minute) and make this independent of
 * Unidata NetCDF API.
 * 
 * @author USC/C/PBe
 */
@RunWith(MockitoJUnitRunner.class)
public class NetCDFCalibrationDataManagerMet7Test
{
    private static final double DOUBLE_DELTA = 1e-15;
    private static final String MET7_DATASET_URL = "http://gsics.eumetsat.int/thredds/fileServer/met7-mviri-metopa-iasi-demo-rac/W_XX-EUMETSAT-"
            + "Darmstadt,SATCAL+RAC+GEOLEOIR,MET7+MVIRI-MetOpA+IASI_C_EUMG_20080601000000_demo_03.nc";
    private static final String TEST_CSV_FILE_PATH = "test/resources/data_to_be_plotted";

    /** NetCDF Calibration Data Manager under test. */
    private NetCDFCalibrationDataManager netCDFCalibrationDataManager;

    /** Mock of Servlet Context. */
    @Mock
    private ServletContext servletContextMock;

    /** Mock of NetcdfFile. */
    @Mock
    private ConfigManager configManagerMock;

    /**
     * Setup the dependencies of the unit test.
     * 
     * @throws IOException
     *             error mocking the NetcdfDataset.
     * @throws InvalidRangeException
     *             error mocking the NetcdfDataset.
     */
    @Before
    public void setUp() throws IOException, InvalidRangeException
    {
        // Init mock of servlet context.
        Mockito.when(servletContextMock.getRealPath(File.separator + TEST_CSV_FILE_PATH)).thenReturn(TEST_CSV_FILE_PATH);

        // Init mock of config manager.
        GlobalAttributesDefaults globalAttributesDefaults = new GlobalAttributesDefaults(
                "((c2*wnc)/log(1+(c1*wnc^3)/radiance)-beta)/alpha", "(c1*wnc^3)/((exp(c2*wnc/(alpha*tb+beta)))-1)",
                1.19104e-5, 1.43877);
        Mockito.when(configManagerMock.getGlobalAttributesDefaults()).thenReturn(globalAttributesDefaults);

        GlobalAttributesNames globalAttributesNames = new GlobalAttributesNames("id",
                "radiance_to_brightness_conversion_formula", "brightness_to_radiance_conversion_formula",
                "planck_function_constant_c1", "planck_function_constant_c2");

        Mockito.when(configManagerMock.getGlobalAttributesNames()).thenReturn(globalAttributesNames);

        VariablesNames variablesNames = new VariablesNames("date", "channel_name", "offset", "offset_se", "slope",
                "slope_se", "covariance", "std_scene_tb", "alpha", "beta", "wnc");

        Mockito.when(configManagerMock.getVariablesNames()).thenReturn(variablesNames);

        // Create instance of object under test.
        this.netCDFCalibrationDataManager = new NetCDFCalibrationDataManager(servletContextMock, TEST_CSV_FILE_PATH,
                configManagerMock);
    }

    /**
     * Test adding data from dataset for specific user.
     * 
     * @throws InvalidFilenameException
     *             when filename is invalid.
     * @throws InvalidFormatException
     *             when format is invalid.
     * @throws DatasetReadException
     *             when error occurred while reading dataset.
     */
    @Test
    public void testAddDataFromDatasetForUser() throws DatasetReadException, InvalidFormatException,
            InvalidFilenameException
    {
        // 8 channels with each std scene tb.
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", MET7_DATASET_URL, "All", -1);
        // 8 channels with user std scene tb.
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", MET7_DATASET_URL, "All", 257);
        // This should not add data, as it was already added at first line.
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", MET7_DATASET_URL, "wv-1", -1);
        // This should add data.
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", MET7_DATASET_URL, "ir-2", 267);

        assertEquals(5, netCDFCalibrationDataManager.addedSourcesForUser("test_user").size());
    }

    /**
     * Test returning the added sources for specific user.
     * 
     * @throws InvalidFilenameException
     *             when filename is invalid.
     * @throws InvalidFormatException
     *             when format is invalid.
     * @throws DatasetReadException
     *             when error occurred while reading dataset.
     * @throws ParseException
     *             when error occurred while parsing timestamp from filename.
     */
    @Test
    public void testAddedSourcesForUser() throws DatasetReadException, InvalidFormatException,
            InvalidFilenameException, ParseException
    {
        assertTrue(netCDFCalibrationDataManager.addedSourcesForUser("test_user").isEmpty());
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", MET7_DATASET_URL, "All", -1);
        assertEquals(2, netCDFCalibrationDataManager.addedSourcesForUser("test_user").size());

        // Check the contents of the added sources map (based on what is mocked in setUp()).
        NetcdfFilename ncfilename = NetcdfFilename.parse(FilenameUtils.getName(MET7_DATASET_URL));
        String timestamp = DateUtils.format(DateUtils.parse(ncfilename.getTimestamp(), "yyyyMMddHHmmss", "GMT"),
                "yyyy/MM/dd HH:mm:ss", "GMT");
        final List<String> channelNameList = Arrays.asList("wv-1", "ir-2");
        double sceneTb = 237;
        for (final String channelName : channelNameList)
        {
            String datasetName = ncfilename.getSatellite() + "/" + ncfilename.getInstrument() + " referenced with "
                    + ncfilename.getRefSatellite() + "/" + ncfilename.getRefInstrument() + " ["
                    + ncfilename.getLocationIndication().split("-")[1] + "][" + ncfilename.getCorrectionType() + "]["
                    + ncfilename.getMode() + "][" + timestamp + "][v" + ncfilename.getVersion() + "][" + channelName
                    + "][" + sceneTb + "K]";

            sceneTb += 48;

            assertTrue(netCDFCalibrationDataManager.addedSourcesForUser("test_user").containsKey(datasetName));
            assertEquals(MET7_DATASET_URL,
                    netCDFCalibrationDataManager.addedSourcesForUser("test_user").get(datasetName));
        }
    }

    /**
     * Test returning the channel names from a dataset.
     * 
     * @throws VariableReadException
     *             when error occurred while reading the channel variable.
     * @throws VariableNotFoundException
     *             when channel variable was not found.
     * @throws DatasetReadException
     *             when error occurred while reading the dataset.
     * @throws InvalidFilenameException
     *             when dataset has an invalid filename.
     */
    @Test
    public void testChannelNamesFrom() throws DatasetReadException, VariableNotFoundException, VariableReadException,
            InvalidFilenameException
    {
        final List<String> channelNameList = Arrays.asList("wv-1", "ir-2");

        assertEquals(channelNameList, netCDFCalibrationDataManager.channelNamesFrom(MET7_DATASET_URL));
    }

    /**
     * Test returning the standard brightness temperature from a dataset.
     * 
     * @throws InvalidFormatException
     *             when dataset format is invalid.
     * @throws VariableReadException
     *             when error occurred while reading a variable.
     * @throws VariableNotFoundException
     *             when a variable was not found.
     * @throws DatasetReadException
     *             when error occurred while reading the dataset.
     * @throws InvalidFilenameException
     *             when dataset has an invalid filename.
     */
    @Test
    public void testStdSceneTbFrom() throws DatasetReadException, VariableNotFoundException, VariableReadException,
            InvalidFormatException, InvalidFilenameException
    {
        final List<String> channelNameList = Arrays.asList("wv-1", "ir-2");
        double sceneTb = 237;
        for (final String channelName : channelNameList)
        {
            assertEquals(sceneTb, netCDFCalibrationDataManager.stdSceneTbFrom(MET7_DATASET_URL, channelName),
                    DOUBLE_DELTA);

            sceneTb += 48;
        }
    }

    /**
     * Test exporting to CSV file for specific user.
     * 
     * @throws InvalidFilenameException
     *             when filename is invalid.
     * @throws InvalidFormatException
     *             when format is invalid.
     * @throws DatasetReadException
     *             when error occurred while reading dataset.
     * @throws FormulaException
     *             when converting between tb and rad.
     * @throws FileException
     *             when error occurred while writing to file.
     */
    @Test
    public void testExportToCSVForUser() throws DatasetReadException, InvalidFormatException, InvalidFilenameException,
            FileException, FormulaException
    {
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", MET7_DATASET_URL, "All", -1);
        netCDFCalibrationDataManager.exportToCSVForUser("test_user");

        assertTrue(new File(TEST_CSV_FILE_PATH + "-test_user.csv").exists());

        new File(TEST_CSV_FILE_PATH + "-test_user.csv").delete();
    }
}
