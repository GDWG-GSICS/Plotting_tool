//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
//@formatter:on
package org.eumetsat.usd.gcp.server.conf;

import static org.junit.Assert.*;

import java.io.File;

import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.eumetsat.usd.gcp.server.exception.InvalidConfigException;
import org.eumetsat.usd.gcp.server.guice.UnmarshallerFactory;
import org.eumetsat.usd.gcp.shared.conf.HelpItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test XmlConfigManager.
 * 
 * @author USC/C/PBe
 */
@RunWith(MockitoJUnitRunner.class)
public class XmlConfigManagerTest
{
    private static final double DOUBLE_DELTA = 1e-15;

    /** XML Configuration Manager under test. */
    private XmlConfigManager xmlConfigManager;

    /** Mock of Servlet Context. */
    @Mock
    private ServletContext servletContextMock;

    /**
     * Setup the dependencies of the unit test.
     * 
     * @throws InvalidConfigException
     *             when a problem occurred while reading the configuration file.
     */
    @Before
    public void setUp() throws InvalidConfigException
    {
        // Init required factories.
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        UnmarshallerFactory uf = new UnmarshallerFactory();

        // Init mock of servlet context.
        Mockito.when(servletContextMock.getRealPath(File.separator + "test/resources/conf/conf.xml")).thenReturn(
                "test/resources/conf/conf.xml");
        Mockito.when(servletContextMock.getRealPath(File.separator + "test/resources/conf/conf.xsd")).thenReturn(
                "test/resources/conf/conf.xsd");

        // Create instance of object under test.
        this.xmlConfigManager = new XmlConfigManager(servletContextMock, "test/resources/conf/conf.xml",
                "test/resources/conf/conf.xsd", sf, uf);
    }

    /**
     * Test getting the configured catalogs, and compare to hard-coded values.
     */
    @Test
    public void testGetConfiguredCatalogs()
    {
        // Check that there are 2 products.
        assertEquals(2, xmlConfigManager.getConfiguredCatalogs().size());

        // Check that EUMETSAT is there.
        assertTrue(xmlConfigManager.getConfiguredCatalogs().contains("EUMETSAT"));

        // Check that NOAA is there.
        assertTrue(xmlConfigManager.getConfiguredCatalogs().contains("NOAA"));
    }

    /**
     * Test getting the catalog URL, and compare to hard-coded values.
     */
    @Test
    public void testGetCatalogURL()
    {
        assertEquals("http://193.17.10.39/thredds/catalog.xml", xmlConfigManager.getCatalogURL("EUMETSAT"));
        assertEquals("http://gsics.nesdis.noaa.gov/thredds/catalog.xml", xmlConfigManager.getCatalogURL("NOAA"));
    }

    /**
     * Test getting the catalog validate flag, and compare to hard-coded values.
     */
    @Test
    public void testGetCatalogValidateFlag()
    {
        assertEquals(false, xmlConfigManager.getCatalogValidateFlag("EUMETSAT"));
        assertEquals(false, xmlConfigManager.getCatalogValidateFlag("NOAA"));
    }

    /**
     * Test getting the GlobalAttributesDefaults, and compare to hard-coded values.
     */
    @Test
    public void testGetGlobalAttributesDefaults()
    {
        final GlobalAttributesDefaults globalAttrDefaults = xmlConfigManager.getGlobalAttributesDefaults();

        assertEquals("((c2*wnc)/log(1+(c1*wnc^3)/radiance)-beta)/alpha", globalAttrDefaults.getRadToTbConvFormula());
        assertEquals("(c1*wnc^3)/((exp(c2*wnc/(alpha*tb+beta)))-1)", globalAttrDefaults.getTbToRadConvFormula());
        assertEquals(1.19104e-5, globalAttrDefaults.getC1(), DOUBLE_DELTA);
        assertEquals(1.43877, globalAttrDefaults.getC2(), DOUBLE_DELTA);
    }

    /**
     * Test getting the GlobalAttributesNames, and compare to hard-coded values.
     */
    @Test
    public void testGetGlobalAttributesNames()
    {
        final GlobalAttributesNames globalAttrNames = xmlConfigManager.getGlobalAttributesNames();

        assertEquals("id", globalAttrNames.getFilename());
        assertEquals("radiance_to_brightness_conversion_formula", globalAttrNames.getRadToTbConvFormula());
        assertEquals("brightness_to_radiance_conversion_formula", globalAttrNames.getTbToRadConvFormula());
        assertEquals("planck_function_constant_c1", globalAttrNames.getC1());
        assertEquals("planck_function_constant_c2", globalAttrNames.getC2());
    }

    /**
     * Test getting the VariablesNames, and compare to hard-coded values.
     */
    @Test
    public void testGetVariablesNames()
    {
        final VariablesNames variableNames = xmlConfigManager.getVariablesNames();

        assertEquals("date", variableNames.getDate());
        assertEquals("channel_name", variableNames.getChannelName());
        assertEquals("offset", variableNames.getOffset());
        assertEquals("offset_se", variableNames.getOffsetSe());
        assertEquals("slope", variableNames.getSlope());
        assertEquals("slope_se", variableNames.getSlopeSe());
        assertEquals("covariance", variableNames.getCovariance());
        assertEquals("std_scene_tb", variableNames.getStdSceneTb());
        assertEquals("alpha", variableNames.getAlpha());
        assertEquals("beta", variableNames.getBeta());
        assertEquals("wnc", variableNames.getWnc());
    }

    /**
     * Test getting the help items, and compare to hard-coded values.
     */
    @Test
    public void testGetHelpItems()
    {
        // Check that there are 4 items.
        assertEquals(4, xmlConfigManager.getHelpItems().size());

        int count = 0;
        for (final HelpItem helpItem : xmlConfigManager.getHelpItems())
        {
            if (helpItem.getLabel().equals("EUMETSAT GSICS Webpage"))
            {
                count++;
                assertEquals("GSICS information specific to the EUMETSAT GSICS products.", helpItem.getDescription());
                assertEquals(
                        "http://www.eumetsat.int/Home/Main/AboutEUMETSAT/InternationalRelations/CGMS/SP_1226312587804?l=en",
                        helpItem.getUrl());
            }
            if (helpItem.getLabel().equals("WMO GSICS Portal"))
            {
                count++;
                assertEquals(
                        "World Meteorological Organisation GSICS Portal, provide general information for the GSICS project.",
                        helpItem.getDescription());
                assertEquals("http://gsics.wmo.int", helpItem.getUrl());
            }
            if (helpItem.getLabel().equals("GSICS GCC Portal"))
            {
                count++;
                assertEquals(
                        "The GSICS Coordination Centre Portal, coordination information, product catalogue and user messaging "
                                + "service can be found on this website.", helpItem.getDescription());
                assertEquals("http://www.star.nesdis.noaa.gov/smcd/GCC/index.php", helpItem.getUrl());
            }
            if (helpItem.getLabel().equals("EUMETSAT GSICS Server"))
            {
                count++;
                assertEquals(
                        "The EUMETSAT GSICS Data and Products Server.  Source data sets, collocation data sets and GSICS products "
                                + "can be viewed and downloaded from this server.", helpItem.getDescription());
                assertEquals("http://gsics.eumetsat.int/thredds/catalog.html", helpItem.getUrl());
            }
        }

        assertEquals(4, count);
    }
}
