package org.eumetsat.usd.gcp.server.guice;

import org.eumetsat.usd.gcp.server.catalog.CatalogNavigator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class CatalogNavigatorSingletonFactory implements CatalogNavigatorFactory
{
    private final CatalogNavigatorFactory underlyingFactory;
    
    private final LoadingCache<String, CatalogNavigator> catalogNavigators = CacheBuilder.newBuilder().build(
            new CacheLoader<String, CatalogNavigator>()
            {
                @Override
                public CatalogNavigator load(String catalogName)
                {
                    return underlyingFactory.create(catalogName);
                }
            });

    @Inject
    CatalogNavigatorSingletonFactory(@Named("underlyingFactory") CatalogNavigatorFactory catalogNavigatorFactory)
    {
        this.underlyingFactory = catalogNavigatorFactory;
    }

    @Override
    public CatalogNavigator create(String catalogName)
    {
        return catalogNavigators.getUnchecked(catalogName);
    }

}
