//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
//@formatter:on
package org.eumetsat.usd.gcp.server.guice;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.eumetsat.usd.gcp.server.FileDownloadServlet;
import org.eumetsat.usd.gcp.server.catalog.CatalogNavigator;
import org.eumetsat.usd.gcp.server.catalog.ThreddsCatalogNavigator;
import org.eumetsat.usd.gcp.server.conf.ConfigManager;
import org.eumetsat.usd.gcp.server.conf.XmlConfigManager;
import org.eumetsat.usd.gcp.server.data.CalibrationDataManager;
import org.eumetsat.usd.gcp.server.data.NetCDFCalibrationDataManager;
import org.eumetsat.usd.gcp.server.persistence.HibernatePersistenceManager;
import org.eumetsat.usd.gcp.server.persistence.HibernateUtils;
import org.eumetsat.usd.gcp.server.persistence.PersistenceManager;
import org.eumetsat.usd.gcp.shared.util.Constants;
import org.hibernate.SessionFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.google.inject.servlet.RequestScoped;

/**
 * Guice Module.
 * 
 * @author USC/C/PBe
 */
public class GuiceModule extends AbstractModule
{
    /** Configuration file path. */
    private static final String CONFIG_XML_FILE_PATH = "server_resources" + File.separator + "conf" + File.separator
            + "conf.xml";

    /** Configuration file schema path. */
    private static final String CONFIG_XSD_FILE_PATH = "server_resources" + File.separator + "conf" + File.separator
            + "conf.xsd";

    @Override
    protected void configure()
    {
        bindConstant().annotatedWith(XmlConfigManager.ConfigXsdPath.class).to(CONFIG_XSD_FILE_PATH);
        bindConstant().annotatedWith(XmlConfigManager.ConfigXmlPath.class).to(CONFIG_XML_FILE_PATH);
        bindConstant().annotatedWith(NetCDFCalibrationDataManager.CsvFilePath.class).to(Constants.CSV_FILE_PATH);
        bindConstant().annotatedWith(FileDownloadServlet.ServerFilesPath.class).to(Constants.SERVER_FILES_PATH);

        bind(ConfigManager.class).to(XmlConfigManager.class).in(Singleton.class);
        bind(CalibrationDataManager.class).to(NetCDFCalibrationDataManager.class).in(Singleton.class);
        bind(PersistenceManager.class).to(HibernatePersistenceManager.class).in(RequestScoped.class);

        bind(CatalogNavigatorFactory.class).to(CatalogNavigatorSingletonFactory.class).in(Singleton.class);
        install(new FactoryModuleBuilder().implement(CatalogNavigator.class, ThreddsCatalogNavigator.class).build(
                Key.get(CatalogNavigatorFactory.class, Names.named("underlyingFactory"))));
    }

    @Provides
    SchemaFactory getSchemaFactory()
    {
        return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    @Provides
    SessionFactory getSessionFactory()
    {
        return HibernateUtils.getSessionFactory();
    }
}
