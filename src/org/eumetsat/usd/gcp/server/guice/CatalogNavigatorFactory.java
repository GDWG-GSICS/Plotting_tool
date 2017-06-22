// @formatter:off
/*
 * PROJECT: gcp 
 * AUTHOR: USC/C/PBe 
 * COPYRIGHT: EUMETSAT 2015
 */
// @formatter:on
package org.eumetsat.usd.gcp.server.guice;

import org.eumetsat.usd.gcp.server.catalog.CatalogNavigator;

/**
 * Factory for creating <code>CatalogNavigator</code> through Guice assisted inject.
 * 
 * @author USC/C/PBe
 */
public interface CatalogNavigatorFactory
{
    CatalogNavigator create(final String catalogName);
}
