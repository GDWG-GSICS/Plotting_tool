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
import java.util.Date;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Range;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * Test NetCDFCalibrationDataManager, mocking also static NetcdfDataset.openDataset() method and a NetcdfDataset
 * instance, in order to speed the process up (Variable.read() takes around 1 minute) and make this independent of
 * Unidata NetCDF API.
 * 
 * @author USC/C/PBe
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(NetcdfDataset.class)
public class NetCDFCalibrationDataManagerTest
{
    private static final double DOUBLE_DELTA = 1e-15;
    private static final String TEST_DATASET_URL = "http://gsics.eumetsat.int/thredds/fileServer/msg1-seviri-metopa-iasi-demo-rac/W_XX-EUMETSAT-"
            + "Darmstadt,SATCAL+RAC+GEOLEOIR,MSG1+SEVIRI-MetOpA+IASI_C_EUMG_20080601000000_demo_03.nc";
    private static final String TEST_LOCAL_DATASET_NAME = "W_XX-EUMETSAT-Darmstadt,SATCAL+RAC+GEOLEOIR,MSG1+SEVIRI-MetOpA+IASI_C_EUMG_20080601000000_demo_03.nc";
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

        // START Mock NetcdfDataset instance.
        NetcdfDataset netcdfDatasetMock = Mockito.mock(NetcdfDataset.class);

        // Global Attributes.
        Attribute c1AttributeMock = Mockito.mock(Attribute.class, Mockito.withSettings().stubOnly());
        Attribute c2AttributeMock = Mockito.mock(Attribute.class, Mockito.withSettings().stubOnly());
        Attribute radToTbAttributeMock = Mockito.mock(Attribute.class, Mockito.withSettings().stubOnly());
        Attribute tbToRadAttributeMock = Mockito.mock(Attribute.class, Mockito.withSettings().stubOnly());
        Number c1NumberMock = Mockito.mock(Number.class, Mockito.withSettings().stubOnly());
        Number c2NumberMock = Mockito.mock(Number.class, Mockito.withSettings().stubOnly());

        Mockito.when(c1AttributeMock.getNumericValue()).thenReturn(c1NumberMock);
        Mockito.when(c2AttributeMock.getNumericValue()).thenReturn(c2NumberMock);
        Mockito.when(c1NumberMock.doubleValue()).thenReturn(1.19104e-5);
        Mockito.when(c2NumberMock.doubleValue()).thenReturn(1.43877);
        Mockito.when(radToTbAttributeMock.getStringValue()).thenReturn(
                "tb=((c2 * wnc)/LN(1.+(c1 * wnc^3)/radiance)-beta)/alpha");
        Mockito.when(tbToRadAttributeMock.getStringValue()).thenReturn(
                "radiance=(c1 * wnc^3) / ((EXP(c2 * wnc / (alpha*tb+beta)))-1)");
        Mockito.when(netcdfDatasetMock.findGlobalAttributeIgnoreCase(globalAttributesNames.getC1())).thenReturn(
                c1AttributeMock);
        Mockito.when(netcdfDatasetMock.findGlobalAttributeIgnoreCase(globalAttributesNames.getC2())).thenReturn(
                c2AttributeMock);
        Mockito.when(netcdfDatasetMock.findGlobalAttributeIgnoreCase(globalAttributesNames.getRadToTbConvFormula())).thenReturn(
                radToTbAttributeMock);
        Mockito.when(netcdfDatasetMock.findGlobalAttributeIgnoreCase(globalAttributesNames.getTbToRadConvFormula())).thenReturn(
                tbToRadAttributeMock);

        // Variables.
        Variable channelVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());
        Variable dateVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());
        Variable offsetVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());
        Variable offsetSeVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());
        Variable slopeVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());
        Variable slopeSeVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());
        Variable covarianceVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());
        Variable stdSceneTbVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());
        Variable alphaVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());
        Variable betaVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());
        Variable wncVariableMock = Mockito.mock(Variable.class, Mockito.withSettings().stubOnly());

        char[] channel1DCharArray = { 'I', 'R', '0', '3', '9', 'I', 'R', '0', '6', '2', 'I', 'R', '0', '7', '3', 'I',
                'R', '0', '8', '7', 'I', 'R', '0', '9', '7', 'I', 'R', '1', '0', '8', 'I', 'R', '1', '2', '0', 'I',
                'R', '1', '3', '4' };

        Array channelArray = Array.factory(DataType.CHAR, new int[] { 8, 5 }, channel1DCharArray);
        Mockito.when(channelVariableMock.read()).thenReturn(channelArray);

        Array dateArray = Array.makeArray(DataType.DOUBLE, 2312, 1.2122784e9, 1e5);
        Mockito.when(dateVariableMock.read()).thenReturn(dateArray);

        Array offsetArray = Array.makeArray(DataType.DOUBLE, 2312, 1e-2, DOUBLE_DELTA);
        Array offsetSeArray = Array.makeArray(DataType.DOUBLE, 2312, 1e-4, DOUBLE_DELTA);
        Array slopeArray = Array.makeArray(DataType.DOUBLE, 2312, 1, DOUBLE_DELTA);
        Array slopeSeArray = Array.makeArray(DataType.DOUBLE, 2312, 1e-2, DOUBLE_DELTA);
        Array covarianceArray = Array.makeArray(DataType.DOUBLE, 2312, -1e-7, DOUBLE_DELTA);
        for (int channelNum = 0; channelNum <= 7; channelNum++)
        {
            Mockito.when(offsetVariableMock.read(":," + channelNum + ":" + channelNum)).thenReturn(offsetArray);
            Mockito.when(offsetSeVariableMock.read(":," + channelNum + ":" + channelNum)).thenReturn(offsetSeArray);
            Mockito.when(slopeVariableMock.read(":," + channelNum + ":" + channelNum)).thenReturn(slopeArray);
            Mockito.when(slopeSeVariableMock.read(":," + channelNum + ":" + channelNum)).thenReturn(slopeSeArray);
            Mockito.when(covarianceVariableMock.read(":," + channelNum + ":" + channelNum)).thenReturn(covarianceArray);
        }

        Array stdSceneTbArray = Array.makeArray(DataType.DOUBLE, 8, 230, 5);
        Mockito.when(stdSceneTbVariableMock.read()).thenReturn(stdSceneTbArray);

        Array alphaArray = Array.makeArray(DataType.DOUBLE, 1, 0.99, DOUBLE_DELTA);
        Array betaArray = Array.makeArray(DataType.DOUBLE, 1, 1, DOUBLE_DELTA);
        Array wncArray = Array.makeArray(DataType.DOUBLE, 1, 1000, DOUBLE_DELTA);

        Mockito.when(stdSceneTbVariableMock.read(new int[] { Mockito.anyInt(), Mockito.anyInt() }, new int[] { 1, 1 })).thenReturn(
                stdSceneTbArray);
        Mockito.when(alphaVariableMock.read(new int[] { Mockito.anyInt(), Mockito.anyInt() }, new int[] { 1, 1 })).thenReturn(
                alphaArray);
        Mockito.when(betaVariableMock.read(new int[] { Mockito.anyInt(), Mockito.anyInt() }, new int[] { 1, 1 })).thenReturn(
                betaArray);
        Mockito.when(wncVariableMock.read(new int[] { Mockito.anyInt(), Mockito.anyInt() }, new int[] { 1, 1 })).thenReturn(
                wncArray);

        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getChannelName())).thenReturn(channelVariableMock);
        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getDate())).thenReturn(dateVariableMock);
        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getOffset())).thenReturn(offsetVariableMock);
        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getOffsetSe())).thenReturn(offsetSeVariableMock);
        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getSlope())).thenReturn(slopeVariableMock);
        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getSlopeSe())).thenReturn(slopeSeVariableMock);
        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getCovariance())).thenReturn(covarianceVariableMock);
        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getStdSceneTb())).thenReturn(stdSceneTbVariableMock);
        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getAlpha())).thenReturn(alphaVariableMock);
        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getBeta())).thenReturn(betaVariableMock);
        Mockito.when(netcdfDatasetMock.findVariable(variablesNames.getWnc())).thenReturn(wncVariableMock);

        // END Mock NetcdfDataset instance.

        // Mock static methods of ucar's NetcdfDataset
        PowerMockito.mockStatic(NetcdfDataset.class);
        String testLocalDatasetURL = "file:"
                + FilenameUtils.separatorsToUnix(new File(System.getProperty("java.io.tmpdir")
                        + TEST_LOCAL_DATASET_NAME).getCanonicalPath());

        Mockito.when(NetcdfDataset.openDataset(TEST_DATASET_URL)).thenReturn(netcdfDatasetMock);
        Mockito.when(NetcdfDataset.openDataset(testLocalDatasetURL)).thenReturn(netcdfDatasetMock);
        Mockito.when(NetcdfDataset.openFile(TEST_DATASET_URL, null)).thenReturn(netcdfDatasetMock);
        Mockito.when(NetcdfDataset.openFile(testLocalDatasetURL, null)).thenReturn(netcdfDatasetMock);

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
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", TEST_DATASET_URL, "All", -1);
        // 8 channels with user std scene tb.
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", TEST_DATASET_URL, "All", 257);
        // This should not add data, as it was already added at first line.
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", TEST_DATASET_URL, "IR087", -1);
        // This should add data.
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", TEST_DATASET_URL, "IR087", 267);

        assertEquals(17, netCDFCalibrationDataManager.addedSourcesForUser("test_user").size());
    }

    /**
     * Test clearing data for specific user.
     * 
     * @throws InvalidFilenameException
     *             when filename is invalid.
     * @throws InvalidFormatException
     *             when format is invalid.
     * @throws DatasetReadException
     *             when error occurred while reading dataset.
     */
    @Test
    public void testClearDataForUser() throws DatasetReadException, InvalidFormatException, InvalidFilenameException
    {
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user1", TEST_DATASET_URL, "All", -1);
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user1", TEST_DATASET_URL, "All", 257);
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user2", TEST_DATASET_URL, "IR087", -1);
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user2", TEST_DATASET_URL, "IR087", 257);

        assertEquals(16, netCDFCalibrationDataManager.addedSourcesForUser("test_user1").size());
        assertEquals(2, netCDFCalibrationDataManager.addedSourcesForUser("test_user2").size());

        netCDFCalibrationDataManager.clearDataForUser("test_user1");

        assertTrue(netCDFCalibrationDataManager.addedSourcesForUser("test_user1").isEmpty());
        assertFalse(netCDFCalibrationDataManager.addedSourcesForUser("test_user2").isEmpty());
    }

    /**
     * Test returning the date window of data for specific user.
     * 
     * @throws InvalidFilenameException
     *             when filename is invalid.
     * @throws InvalidFormatException
     *             when format is invalid.
     * @throws DatasetReadException
     *             when error occurred while reading dataset.
     */
    @Test
    public void testDateWindowForUser() throws DatasetReadException, InvalidFormatException, InvalidFilenameException
    {
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user1", TEST_DATASET_URL, "All", -1);

        final Range<Date> dateWindow = netCDFCalibrationDataManager.dateWindowForUser("test_user1");

        assertEquals(new Date(1212278400000L), dateWindow.lowerEndpoint());
        assertEquals(new Date(1443378400000L), dateWindow.upperEndpoint());
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
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", TEST_DATASET_URL, "All", -1);
        assertEquals(8, netCDFCalibrationDataManager.addedSourcesForUser("test_user").size());

        // Check the contents of the added sources map (based on what is mocked in setUp()).
        NetcdfFilename ncfilename = NetcdfFilename.parse(FilenameUtils.getName(TEST_DATASET_URL));
        String timestamp = DateUtils.format(DateUtils.parse(ncfilename.getTimestamp(), "yyyyMMddHHmmss", "GMT"),
                "yyyy/MM/dd HH:mm:ss", "GMT");
        final List<String> channelNameList = Arrays.asList("IR039", "IR062", "IR073", "IR087", "IR097", "IR108",
                "IR120", "IR134");
        double sceneTb = 230;
        for (final String channelName : channelNameList)
        {
            String datasetName = ncfilename.getSatellite() + "/" + ncfilename.getInstrument() + " referenced with "
                    + ncfilename.getRefSatellite() + "/" + ncfilename.getRefInstrument() + " ["
                    + ncfilename.getLocationIndication().split("-")[1] + "][" + ncfilename.getCorrectionType() + "]["
                    + ncfilename.getMode() + "][" + timestamp + "][v" + ncfilename.getVersion() + "][" + channelName
                    + "][" + sceneTb + "K]";

            sceneTb += 5;

            assertTrue(netCDFCalibrationDataManager.addedSourcesForUser("test_user").containsKey(datasetName));
            assertEquals(TEST_DATASET_URL,
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
        final List<String> channelNameList = Arrays.asList("IR039", "IR062", "IR073", "IR087", "IR097", "IR108",
                "IR120", "IR134");

        assertEquals(channelNameList, netCDFCalibrationDataManager.channelNamesFrom(TEST_DATASET_URL));
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
        final List<String> channelNameList = Arrays.asList("IR039", "IR062", "IR073", "IR087", "IR097", "IR108",
                "IR120", "IR134");
        double sceneTb = 230;
        for (final String channelName : channelNameList)
        {
            assertEquals(sceneTb, netCDFCalibrationDataManager.stdSceneTbFrom(TEST_DATASET_URL, channelName),
                    DOUBLE_DELTA);

            sceneTb += 5;
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
        netCDFCalibrationDataManager.addDataFromDatasetForUser("test_user", TEST_DATASET_URL, "All", -1);
        netCDFCalibrationDataManager.exportToCSVForUser("test_user");

        assertTrue(new File(TEST_CSV_FILE_PATH + "-test_user.csv").exists());

        new File(TEST_CSV_FILE_PATH + "-test_user.csv").delete();
    }
}
