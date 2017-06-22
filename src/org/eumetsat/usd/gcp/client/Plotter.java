package org.eumetsat.usd.gcp.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.constraints.NotNull;

import org.eumetsat.usd.gcp.client.gin.ClientGinjector;
import org.realityforge.gwt.appcache.client.ApplicationCache;
import org.realityforge.gwt.appcache.client.event.UpdateReadyEvent;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.UmbrellaException;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.wallissoftware.pushstate.client.PushStateHistorian;

/**
 * Entry point of plotter application.
 * 
 * @author USD/C/PBe
 */
public class Plotter implements EntryPoint
{
    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger("");

    /** Client GIN Injector. */
    private final ClientGinjector ginjector = GWT.create(ClientGinjector.class);

    @Override
    public final void onModuleLoad()
    {
        // Needed for resetting URL when accessing through URL with parameters.
        PushStateHistorian.setRelativePath(GWT.getModuleName());
        
        // Properly catch client side exceptions.
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler()
        {
            public void onUncaughtException(final Throwable e)
            {
                Throwable unwrapped = unwrap(e);

                LOGGER.log(Level.SEVERE, "Client side exception caught!", unwrapped);
            }

            public Throwable unwrap(final Throwable e)
            {
                if (e instanceof UmbrellaException)
                {
                    UmbrellaException ue = (UmbrellaException) e;
                    if (ue.getCauses().size() == 1)
                    {
                        return unwrap(ue.getCauses().iterator().next());
                    }
                }
                return e;
            }
        });

        // Check Application Cache.
        final ApplicationCache cache = ApplicationCache.getApplicationCacheIfSupported();
        if (null != cache)
        {
            cache.addUpdateReadyHandler(new UpdateReadyEvent.Handler()
            {
                @Override
                public void onUpdateReadyEvent(@NotNull final UpdateReadyEvent event)
                {
                    // Force a cache update if new version is available.
                    cache.swapCache();
                }
            });

            // Ask the browser to recheck the cache.
            cache.requestUpdate();

        } else
        {
            LOGGER.log(Level.WARNING, "ApplicationCache not available!");
        }

        // This is required for Gwt-Platform proxy's generator.
        DelayedBindRegistry.bind(ginjector);

        ginjector.getPlaceManager().revealCurrentPlace();
    }
}
