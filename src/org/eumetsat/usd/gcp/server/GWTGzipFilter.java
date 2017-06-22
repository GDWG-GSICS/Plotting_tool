//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2014
 */
//@formatter:on
package org.eumetsat.usd.gcp.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Filter to serve a .gz file using Content-Encoding gzip if the file exists and content encoding is supported.
 *
 * For each request, this filter checks to see if:
 * 1) The browser accepts gzip encoding (via the Accept-Encoding header)
 *
 * 2) A file with the request url + .gz exists and is readable.
 *
 * 3) If those conditions match, it then sets the 'Content Encoding' header to gzip, and dispatches
 * the gzipped file instead of the non-gzipped version.
 *
 * Example: user goes to example.com/foo.js. If example.com/foo.js.gz exists, then
 * example.com/foo.js.gz is served instead of foo.js
 */
@Singleton
public class GWTGzipFilter implements Filter
{
    private static final String GZIP_EXTENSION = ".gz";

    private ServletContext servletContext;

    @Inject
    public GWTGzipFilter(final ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    public void destroy()
    {
    }

    public void init(FilterConfig config) throws ServletException
    {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException
    {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!acceptsGzip(httpRequest))
        {
            filterChain.doFilter(httpRequest, httpResponse);
        } else
        {
            final String resourcePath = httpRequest.getServletPath();
            final String realPath = servletContext.getRealPath(resourcePath);

            if (resourcePath.endsWith(GZIP_EXTENSION))
            {
                filterChain.doFilter(httpRequest, httpResponse);
            } else
            {
                final String gzippedPath = realPath + GZIP_EXTENSION;
                final File gzippedFile = new File(gzippedPath);

                if (!gzippedFile.isFile())
                {
                    filterChain.doFilter(httpRequest, response);
                } else
                {
                    final RequestDispatcher dispatcher = servletContext.getRequestDispatcher(resourcePath
                            + GZIP_EXTENSION);
                    httpResponse.setHeader("Content-Encoding", "gzip");
                    final String mimeType = servletContext.getMimeType(resourcePath);
                    if (null != mimeType)
                    {
                        httpResponse.setHeader("Content-Type", mimeType);
                    }
                    dispatcher.include(httpRequest, httpResponse);
                }
            }
        }
    }

    private boolean acceptsGzip(final HttpServletRequest request)
    {
        final String header = request.getHeader("Accept-Encoding");
        return null != header && header.contains("gzip");
    }
}