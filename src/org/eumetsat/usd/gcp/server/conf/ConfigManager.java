//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
//@formatter:on
package org.eumetsat.usd.gcp.server.conf;

import java.util.List;

import org.eumetsat.usd.gcp.shared.conf.HelpItem;

/**
 * The Configuration Manager provides the functionality to read the configuration file.
 * 
 * @author USC/C/PBe
 */
public interface ConfigManager
{
    /**
     * Get names of the configured catalogs.
     * 
     * @return names of the configured catalogs.
     */
    List<String> getConfiguredCatalogs();

    /**
     * Get URL for a certain catalog.
     * 
     * @param catalogName
     *            name.
     * @return catalog URL.
     */
    String getCatalogURL(final String catalogName);
    
    /**
     * Get validate flag for a certain catalog.
     * 
     * @param catalogName
     *            name.
     * @return catalog validate flag.
     */
    boolean getCatalogValidateFlag(final String catalogName);

    /**
     * Get global attribute defaults.
     * 
     * @return global attribute defaults.
     */
    GlobalAttributesDefaults getGlobalAttributesDefaults();
    
    /**
     * Get global attribute names.
     * 
     * @return global attribute names.
     */
    GlobalAttributesNames getGlobalAttributesNames();


    /**
     * Get variables names.
     * 
     * @return variables names.
     */
    VariablesNames getVariablesNames();

    /**
     * Get help items.
     * 
     * @return help items.
     */
    List<HelpItem> getHelpItems();
}
