//@formatter:off
/*
 * PROJECT: plc-gui
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2014
 */
//@formatter:on
package org.eumetsat.usd.gcp.client.action;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Abstract custom asynchronous call back class that separates response action for a connection exception and no
 * connection available.
 * 
 * @author USC/C/PBe
 * 
 */
public abstract class CustomAsyncCallback<T> implements AsyncCallback<T>, HasHandlers
{
    /** Event Bus. */
    private final EventBus eventBus;
    
    private Throwable getRootCause(Throwable e) {
        Throwable lastCause;
        do {
          lastCause = e;
        } while ((e = e.getCause()) != null);
        return lastCause;
      }

    @Inject
    public CustomAsyncCallback(final EventBus eventBus)
    {
        this.eventBus = eventBus;
    }
    
    @Override
    public void fireEvent(GwtEvent<?> event)
    {
        eventBus.fireEventFromSource(event, this);
    }
    
    @Override
    public final void onFailure(Throwable caught)
    {
        if (caught instanceof InvocationException)
        {
            onDisconnected();

        } else if (getRootCause(caught).getMessage().contains("ConnectException"))
        {
            onCatalogNotReachable();

        } else
        {
            onException(caught);
        }
    }

    public abstract void onSuccess(T result);

    public abstract void onDisconnected();
    
    public abstract void onCatalogNotReachable();

    public abstract void onException(Throwable caught);
}
