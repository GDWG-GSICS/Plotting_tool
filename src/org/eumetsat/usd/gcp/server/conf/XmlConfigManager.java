package org.eumetsat.usd.gcp.server.conf;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eumetsat.usd.gcp.server.conf.jaxb.CatalogType;
import org.eumetsat.usd.gcp.server.conf.jaxb.Configuration;
import org.eumetsat.usd.gcp.server.conf.jaxb.HelpItemType;
import org.eumetsat.usd.gcp.server.conf.jaxb.ObjectFactory;
import org.eumetsat.usd.gcp.server.exception.InvalidConfigException;
import org.eumetsat.usd.gcp.server.guice.UnmarshallerFactory;
import org.eumetsat.usd.gcp.shared.conf.HelpItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;

/**
 * The Configuration Manager provides the functionality to read the remote and local configuration files and gives
 * persistence to some other parameters storing them in the local configuration file.
 * 
 * @author USD/C/PBe
 */
public final class XmlConfigManager implements ConfigManager
{
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlConfigManager.class);

    /** Configuration. */
    private Configuration config;
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @BindingAnnotation
    public @interface ConfigXmlPath
    {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @BindingAnnotation
    public @interface ConfigXsdPath
    {
    }

    /**
     * Constructs a new <code>XmlConfigManager</code> object loading the configuration file.
     * 
     * @param configXmlPath
     *            path to the configuration XML file.
     * @param configXsdPath
     *            path to XML schema to validate the configuration file.
     * @param unmarshallerFactory
     *            factory to create the JAXB unmarshaller to load the XML into a Java Object.
     * @param schemaFactory
     *            factory to create the schema to validate the XML.
     * @throws InvalidConfigException
     *             if configuration file was not found, or could not be loaded.
     */
    @Inject
    XmlConfigManager(final ServletContext servletContext, @ConfigXmlPath String configXmlPath,
            @ConfigXsdPath String configXsdPath, final SchemaFactory schemaFactory,
            final UnmarshallerFactory unmarshallerFactory) throws InvalidConfigException
    {
        // set path to schema and xml files.
        configXmlPath = servletContext.getRealPath(File.separator + configXmlPath);
        configXsdPath = servletContext.getRealPath(File.separator + configXsdPath);

        LOGGER.info("------------ Import Configuration from XML file ------------");

        // Create unmarshaller for this package.
        final Unmarshaller unmarshaller = unmarshallerFactory.create(ObjectFactory.class.getPackage().getName());

        // Set schema (if null, no validation will occur).
        Schema schema = null;
        try
        {
            schema = schemaFactory.newSchema(new StreamSource(configXsdPath));

        } catch (SAXException se)
        {
            LOGGER.warn("A SAX error occured during parsing the "
                    + "XML schema. Configuration XML file will not be validated against any schema.", se);

        }
        unmarshaller.setSchema(schema);

        // Unmarshal XML.
        try
        {
            this.config = (Configuration) unmarshaller.unmarshal(new InputSource(configXmlPath));

        } catch (JAXBException je)
        {
            // The configuration file could not be unmarshalled.
            LOGGER.error("Configuration XML file [" + configXmlPath + "] could not be unmarshalled.", je);

            throw new InvalidConfigException("Configuration XML file [" + configXmlPath
                    + "] could not be unmarshalled.", je);

        }

        // Log success.
        LOGGER.info("Configuration XML file [" + configXmlPath + "] parsed.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getConfiguredCatalogs()
    {
        final List<String> configuredCatalogs = new ArrayList<String>(config.getCatalogs().getCatalog().size());

        for (final CatalogType catalog : config.getCatalogs().getCatalog())
        {
            configuredCatalogs.add(catalog.getName());
        }

        return configuredCatalogs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCatalogURL(final String catalogName)
    {
        String catalogURL = "";

        for (final CatalogType catalog : config.getCatalogs().getCatalog())
        {
            if (catalog.getName().equalsIgnoreCase(catalogName))
            {
                catalogURL = catalog.getUrl();
                break;
            }
        }

        return catalogURL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getCatalogValidateFlag(String catalogName)
    {
        boolean catalogValidateFlag = false;

        for (final CatalogType catalog : config.getCatalogs().getCatalog())
        {
            if (catalog.getName().equalsIgnoreCase(catalogName))
            {
                catalogValidateFlag = catalog.isValidate();
                break;
            }
        }

        return catalogValidateFlag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GlobalAttributesNames getGlobalAttributesNames()
    {
        return new GlobalAttributesNames(config.getGlobalAttributesNames().getFilename(),
                config.getGlobalAttributesNames().getRadToTbConvFormula(),
                config.getGlobalAttributesNames().getTbToRadConvFormula(), config.getGlobalAttributesNames().getC1(),
                config.getGlobalAttributesNames().getC2());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GlobalAttributesDefaults getGlobalAttributesDefaults()
    {
        return new GlobalAttributesDefaults(config.getGlobalAttributesDefaults().getRadToTbConvFormula(),
                config.getGlobalAttributesDefaults().getTbToRadConvFormula(),
                config.getGlobalAttributesDefaults().getC1(), config.getGlobalAttributesDefaults().getC2());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VariablesNames getVariablesNames()
    {
        return new VariablesNames(config.getVariablesNames().getDate(), config.getVariablesNames().getChannelName(),
                config.getVariablesNames().getOffset(), config.getVariablesNames().getOffsetSe(),
                config.getVariablesNames().getSlope(), config.getVariablesNames().getSlopeSe(),
                config.getVariablesNames().getCovariance(), config.getVariablesNames().getStdSceneTb(),
                config.getVariablesNames().getAlpha(), config.getVariablesNames().getBeta(),
                config.getVariablesNames().getWnc());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HelpItem> getHelpItems()
    {
        final List<HelpItem> helpItems = new ArrayList<HelpItem>(config.getHelpItems().getHelpItem().size());

        for (final HelpItemType helpItem : config.getHelpItems().getHelpItem())
        {
            helpItems.add(new HelpItem(helpItem.getLabel(), helpItem.getDescription(), helpItem.getUrl()));
        }

        return helpItems;
    }

}
