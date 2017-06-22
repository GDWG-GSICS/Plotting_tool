package org.eumetsat.usd.gcp.shared.util;

/**
 * This non-instantiable class contains all the constants definitions for the USD_DT application,
 * used both by the client and the server side.
 * 
 * @author USD/C/PBe
 */
public final class Constants
{
    /**
     * Constants definitions.
     * 
     */

    /** Name of user ID cookie. */
    public static final String CLIENT_ID_COOKIE = "uuid";

    /** Path where the files are stored in the web server. */
    public static final String SERVER_FILES_PATH = "files";
    
    /** Path of CSV file to be used for exporting calibration data in order to plot it. */
    public static final String CSV_FILE_PATH = SERVER_FILES_PATH + "/data_to_be_plotted";

    /**
     * Private constructor to make this class non-instantiable.
     * 
     */
    private Constants()
    {
    }

}
