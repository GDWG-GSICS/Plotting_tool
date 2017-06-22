package org.eumetsat.usd.gcp.server;

import javax.servlet.http.HttpServletRequest;

import org.realityforge.gwt.appcache.server.propertyprovider.PropertyProvider;

public class PushStateSupportedPropertyProvider implements PropertyProvider
{
    @Override
    public String getPropertyValue(final HttpServletRequest request)
    {
        return getPushStateSupported();
    }

    private native String getPushStateSupported()
    /*-{ 
        if (typeof(window.history.pushState) == "function")
        { 
           return "yes"; 
        } else 
        {
           return "no"; 
        }
    }-*/;

    @Override
    public String getPropertyName()
    {
        return "history.pushStateSupported";
    }
}
