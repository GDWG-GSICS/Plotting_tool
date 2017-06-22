// @formatter:off
/*
 * PROJECT: gcp 
 * AUTHOR: USC/C/PBe 
 * COPYRIGHT: EUMETSAT 2015
 */
// @formatter:on
package org.eumetsat.usd.gcp.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The File servlet for serving from absolute path.
 * <p>
 * Modification by USC/C/PBe at EUMETSAT: delete the file once it's been successfully served to the client.
 * 
 * @author BalusC
 * @link http://balusc.blogspot.com/2007/07/fileservlet.html
 */
@Singleton
public class FileDownloadServlet extends HttpServlet
{
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadServlet.class);

    /** Auto-generated ID. */
    private static final long serialVersionUID = 7669153690366989481L;

    /** Default buffer size. */
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    /** Path to the directory where the files are served to the client. */
    private final String serverFilesPath;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.PARAMETER })
    @BindingAnnotation
    public @interface ServerFilesPath
    {
    }

    /**
     * Constructor.
     * 
     * @param servletContext
     *            servlet context.
     * @param csvFilePath
     *            path to the CSV file.
     */
    @Inject
    public FileDownloadServlet(final ServletContext servletContext, @ServerFilesPath final String serverFilesPath)
    {
        // set path to CSV file.
        this.serverFilesPath = servletContext.getRealPath(File.separator + serverFilesPath);
    }

    // Actions ------------------------------------------------------------------------------------
    @Override
    protected final void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        // Get requested file by path info.
        String requestedFile = request.getPathInfo();

        // Check if file is actually supplied to the request URI.
        if (requestedFile == null)
        {
            // Do your thing if the file is not supplied to the request URI.
            // Throw an exception, or send 404, or show default/warning page, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        LOGGER.info("File '" + requestedFile + "' requested.");

        // Decode the file name (might contain spaces and on) and prepare file object.
        File file = new File(serverFilesPath, URLDecoder.decode(requestedFile, "UTF-8"));

        // Check if file actually exists in filesystem.
        if (!file.exists())
        {
            LOGGER.error("File '" + requestedFile + "' does not exist.");

            // Do your thing if the file appears to be non-existing.
            // Throw an exception, or send 404, or show default/warning page, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        // Get content type by filename.
        String contentType = getServletContext().getMimeType(file.getName());

        // If content type is unknown, then set the default value.
        // For all content types, see: http://www.w3schools.com/media/media_mimeref.asp
        // To add new content types, add new mime-mapping entry in web.xml.
        if (contentType == null)
        {
            contentType = "application/octet-stream";
        }

        // Init servlet response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType(contentType);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        // Avoid aggressive caching in IE8
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.

        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try
        {
            // Open streams.
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0)
            {
                output.write(buffer, 0, length);
            }

            // Log success.
            LOGGER.info("'" + requestedFile + "' successfully transferred.");

        } finally
        {
            // Flush output.
            output.flush();

            // Gently close streams.
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(input);
        }

        // Delete the file once it's been successfully transferred.
        FileUtils.forceDelete(file);

        LOGGER.info("'" + requestedFile + "' successfully deleted.");
    }
}
