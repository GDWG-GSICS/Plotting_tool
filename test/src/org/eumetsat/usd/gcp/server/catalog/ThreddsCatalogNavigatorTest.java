//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
//@formatter:on
package org.eumetsat.usd.gcp.server.catalog;

import static org.junit.Assert.*;

import java.util.List;

import javax.servlet.ServletContext;

import org.eumetsat.usd.gcp.server.conf.ConfigManager;
import org.eumetsat.usd.gcp.shared.exception.DatasetNotFoundException;
import org.eumetsat.usd.gcp.shared.exception.InvalidCatalogException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test ThreddsCatalogNavigator using a black box approach, i.e. not mocking the catalog connection. This test will also
 * ensure that EUMETSAT GSICS catalog follows agreed conventions.
 * 
 * @author USC/C/PBe
 */
@RunWith(MockitoJUnitRunner.class)
public class ThreddsCatalogNavigatorTest
{
    /** Thredds Catalog Navigator under test. */
    private ThreddsCatalogNavigator threddsOpeCatalogNavigator;
    private ThreddsCatalogNavigator threddsValCatalogNavigator;

    /** Mock of Servlet Context. */
    @Mock
    private ServletContext servletContextMock;

    /** Mock of NetcdfFile. */
    @Mock
    private ConfigManager configManagerMock;

    /**
     * Setup the dependencies of the unit test.
     * 
     * @throws InvalidCatalogException
     *             error mocking the CatalogNavigator.
     */
    @Before
    public void setUp() throws InvalidCatalogException
    {
        // Init mock of config manager.
        Mockito.when(configManagerMock.getCatalogURL("EUMETSAT")).thenReturn("http://193.17.10.39/thredds/catalog.xml");
        Mockito.when(configManagerMock.getCatalogURL("VAL EUMETSAT")).thenReturn("http://193.17.10.43/thredds/catalog.xml");
        Mockito.when(configManagerMock.getCatalogValidateFlag("EUMETSAT")).thenReturn(false);
        Mockito.when(configManagerMock.getCatalogValidateFlag("VAL EUMETSAT")).thenReturn(false);

        // Create instance of object under test.
        this.threddsOpeCatalogNavigator = new ThreddsCatalogNavigator(configManagerMock, "EUMETSAT");
        this.threddsValCatalogNavigator = new ThreddsCatalogNavigator(configManagerMock, "VAL EUMETSAT");
    }

    /**
     * Tests getting available GPRCs in the catalog.
     * 
     * @throws InvalidCatalogException
     *             if the catalog is invalid or does not follow agreed conventions.
     */
    @Test
    public void testGetGPRCs() throws InvalidCatalogException
    {
        final List<String> gprcs = threddsOpeCatalogNavigator.getGPRCs();

        assertFalse(gprcs.isEmpty());
        assertTrue(gprcs.contains("EUMETSAT"));
    }

    /**
     * Tests getting available correction types for a certain source in the catalog.
     * 
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    @Test
    public void testGetCorrTypes() throws InvalidCatalogException
    {
        final List<String> corrTypes = threddsOpeCatalogNavigator.getCorrTypes("EUMETSAT");

        assertFalse(corrTypes.isEmpty());
        assertTrue(corrTypes.contains("Re-Analysis Corrections (RAC)"));
    }

    /**
     * Tests getting available satellite/instrument pairs for a certain GPRCS, and correction type in the catalog.
     * 
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    @Test
    public void testGetSatInstrs() throws InvalidCatalogException
    {
        final List<String> satInstrs = threddsOpeCatalogNavigator.getSatInstrs("EUMETSAT", "Re-Analysis Corrections (RAC)");

        assertFalse(satInstrs.isEmpty());
        assertTrue(satInstrs.contains("MSG1 SEVIRI"));
    }

    /**
     * Tests getting available reference satellite/instrument pairs for a certain GPRCS, correction type and
     * satellite/instrument in the catalog.
     * 
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    @Test
    public void testGetRefSatInstrs() throws InvalidCatalogException
    {
        final List<String> refSatInstrsOpe = threddsOpeCatalogNavigator.getRefSatInstrs("EUMETSAT",
                "Re-Analysis Corrections (RAC)", "MSG1 SEVIRI");

        assertFalse(refSatInstrsOpe.isEmpty());
        assertTrue(refSatInstrsOpe.contains("METOPA IASI"));
        
        final List<String> refSatInstrsVal = threddsValCatalogNavigator.getRefSatInstrs("EUMETSAT",
                "Re-Analysis Corrections (RAC)", "MSG1 SEVIRI");
        
        assertFalse(refSatInstrsVal.isEmpty());
        assertTrue(refSatInstrsVal.contains("METOPA IASI"));
        assertTrue(refSatInstrsVal.contains("PRIME"));
    }

    /**
     * Tests getting available modes for a certain GPRCS, correction type, satellite/instrument, and reference
     * satellite/instrument in the catalog.
     * 
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    @Test
    public void testGetModes() throws InvalidCatalogException
    {
        final List<String> modes = threddsOpeCatalogNavigator.getModes("EUMETSAT", "Re-Analysis Corrections (RAC)",
                "MSG1 SEVIRI", "MetOpA IASI");

        assertFalse(modes.isEmpty());
        assertTrue(modes.contains("Demonstration"));
    }

    /**
     * Tests getting available years for a certain GPRCS, correction type, satellite/instrument, reference
     * satellite/instrument and mode in the catalog.
     * 
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    @Test
    public void testGetYears() throws InvalidCatalogException
    {
        final List<String> years = threddsOpeCatalogNavigator.getYears("EUMETSAT", "Re-Analysis Corrections (RAC)",
                "MSG1 SEVIRI", "MetOpA IASI", "Demonstration");

        assertFalse(years.isEmpty());
        assertTrue(years.contains("2008"));
    }

    /**
     * Tests getting available date-times for a certain GPRCS, correction type, satellite/instrument, reference
     * satellite/instrument, mode and year in the catalog.
     * 
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    @Test
    public void getDateTimes() throws InvalidCatalogException
    {
        final List<String> dateTimes = threddsOpeCatalogNavigator.getDateTimes("EUMETSAT",
                "Re-Analysis Corrections (RAC)", "MSG1 SEVIRI", "MetOpA IASI", "Demonstration", "2008");

        assertFalse(dateTimes.isEmpty());
        assertTrue(dateTimes.contains("06/01 00:00:00"));
    }

    /**
     * Tests getting available versions for a certain GPRCS, correction type, satellite/instrument, reference
     * satellite/instrument, mode, year and date-time in the catalog.
     * 
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    @Test
    public void testGetVersions() throws InvalidCatalogException
    {
        final List<String> versions = threddsOpeCatalogNavigator.getVersions("EUMETSAT", "Re-Analysis Corrections (RAC)",
                "MSG1 SEVIRI", "MetOpA IASI", "Demonstration", "2008", "06/01 00:00:00");

        assertFalse(versions.isEmpty());
        assertTrue(versions.contains("03"));
    }

    /**
     * Tests getting the dataset URL.
     * 
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     * @throws DatasetNotFoundException
     *             if no dataset for these parameters is found in the catalog.
     */
    @Test
    public void testGetDatasetURL() throws InvalidCatalogException, DatasetNotFoundException
    {
        final String datasetURL = threddsOpeCatalogNavigator.getDatasetURL("EUMETSAT", "Re-Analysis Corrections (RAC)",
                "MSG1 SEVIRI", "MetOpA IASI", "Demonstration", "2008", "06/01 00:00:00", "03");

        assertEquals(
                "http://193.17.10.39/thredds/fileServer/msg1-seviri-metopa-iasi-demo-rac/"
                        + "W_XX-EUMETSAT-Darmstadt,SATCAL+RAC+GEOLEOIR,MSG1+SEVIRI-MetOpA+IASI_C_EUMG_20080601000000_demo_03.nc",
                datasetURL);
    }
}
