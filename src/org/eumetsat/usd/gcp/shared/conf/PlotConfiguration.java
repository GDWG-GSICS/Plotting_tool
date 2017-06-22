// @formatter:off
/*
 * PROJECT: USD_GCP 
 * AUTHOR: USD/C/PBe 
 * COPYRIGHT: EUMETSAT 2012
 */
// @formatter:on
package org.eumetsat.usd.gcp.shared.conf;

import java.util.ArrayList;
import java.util.Date;

import org.eumetsat.usd.gcp.client.place.NameTokens;
import org.eumetsat.usd.gcp.client.util.DateUtils;
import org.eumetsat.usd.gcp.shared.exception.InvalidFilenameException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author USD/C/PBe
 */
public class PlotConfiguration implements IsSerializable
{
    /**
     * Constant names of the parameters as defined in URL.
     * 
     * @author USD/C/PBe
     */
    public final class Names
    {
        // ---------------------------
        // Names of URL parameters.
        // ---------------------------
        /** Name of URL dataset URL parameter. */
        public static final String DATASET_URL_PARAM = "datasetUrl";
        /** Name of server URL parameter. */
        public static final String SERVER_URL_PARAM = "server";
        /** Name of source URL parameter. */
        public static final String GPRC_URL_PARAM = "gprc";
        /** Name of type URL parameter. */
        public static final String CORRTYPE_URL_PARAM = "correctionType";
        /** Name of sat/instr URL parameter. */
        public static final String SATINSTR_URL_PARAM = "satInstr";
        /** Name of ref sat/instr URL parameter. */
        public static final String REFSATINSTR_URL_PARAM = "refSatInstr";
        /** Name of mode URL parameter. */
        public static final String MODE_URL_PARAM = "mode";
        /** Name of year URL parameter. */
        public static final String YEAR_URL_PARAM = "year";
        /** Name of date URL parameter. */
        public static final String DATETIME_URL_PARAM = "date";
        /** Name of version URL parameter. */
        public static final String VERSION_URL_PARAM = "version";
        /** Name of channel URL parameter. */
        public static final String CHANNEL_URL_PARAM = "channel";
        /** Name of sceneTb URL parameter. */
        public static final String SCENETB_URL_PARAM = "sceneTb";
        /** Name of preiod URL parameter. */
        public static final String PERIOD_URL_PARAM = "period";

        /**
         * Private constructor to make this class non-instantiable.
         */
        private Names()
        {
        }
    }

    /**
     * Id, used for showing it in the user interface. Non-unique, several objects can share the same
     * id. Not used in comparison.
     */
    private String id;

    /** Server. */
    private String server;
    /** GPRC. */
    private String gprc;
    /** Correction Type. */
    private String corrType;
    /** Satellite/Instrument. */
    private String satInstr;
    /** Reference Satellite/Instrument. */
    private String refSatInstr;
    /** Mode. */
    private String mode;
    /** Year. */
    private String year;
    /** Date. */
    private String dateTime;
    /** Version. */
    private String version;
    /** Channel. */
    private String channel;
    /** Scene Brightness Temperature. */
    private double sceneTb;

    /**
     * Default constructor for serialisation online.
     */
    @SuppressWarnings("unused")
    private PlotConfiguration()
    {
        // For serialisation only
    }

    /**
     * Copy constructor.
     * 
     * @param plotConfig
     *            plot configuration to be copied.
     */
    public PlotConfiguration(final PlotConfiguration plotConfig)
    {
        this.id = plotConfig.getId();
        this.server = plotConfig.getServer();
        this.gprc = plotConfig.getGPRC();
        this.corrType = plotConfig.getCorrType();
        this.satInstr = plotConfig.getSatInstr();
        this.refSatInstr = plotConfig.getRefSatInstr();
        this.mode = plotConfig.getMode();
        this.year = plotConfig.getYear();
        this.dateTime = plotConfig.getDateTime();
        this.version = plotConfig.getVersion();
        this.channel = plotConfig.getChannel();
        this.sceneTb = plotConfig.getSceneTb();
    }

    /**
     * Constructor with single parameters.
     * 
     * @param id
     *            Unique Id.
     * @param server
     *            Server.
     * @param gprc
     *            GPRC.
     * @param corrType
     *            Correction Type.
     * @param satInstr
     *            Satellite/Instrument.
     * @param refSatInstr
     *            Reference Satellite/Instrument.
     * @param mode
     *            Mode.
     * @param year
     *            Year.
     * @param dateTime
     *            Date-time.
     * @param version
     *            Version.
     * @param channel
     *            Channel.
     * @param sceneTb
     *            Scene Brightness Temperature.
     */
    public PlotConfiguration(final String id, final String server, final String gprc, final String corrType,
            final String satInstr, final String refSatInstr, final String mode, final String year, final String dateTime,
            final String version, final String channel, final double sceneTb)
    {
        this.id = id;

        if (server != null)
        {
            this.server = server;

        } else
        {
            this.server = Defaults.DEFAULT_SERVER;
        }

        if (gprc != null)
        {
            this.gprc = gprc;

        } else
        {
            this.gprc = Defaults.DEFAULT_GPRC;
        }

        if (corrType != null)
        {
            this.corrType = corrType;

        } else
        {
            this.corrType = Defaults.DEFAULT_CORR_TYPE;
        }

        if (satInstr != null)
        {
            this.satInstr = satInstr;

        } else
        {
            this.satInstr = Defaults.DEFAULT_SAT_INSTR;
        }

        if (refSatInstr != null)
        {
            this.refSatInstr = refSatInstr;

        } else
        {
            this.refSatInstr = Defaults.DEFAULT_REF_SAT_INSTR;
        }

        if (channel != null)
        {
            this.channel = channel;

        } else
        {
            this.channel = Defaults.DEFAULT_CHANNEL;
        }

        // default values for the following are not constant.
        this.mode = mode;
        this.year = year;
        this.dateTime = dateTime;
        this.version = version;
        this.sceneTb = sceneTb;
    }

    @Override
    public final int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
        result = prime * result + ((refSatInstr == null) ? 0 : refSatInstr.hashCode());
        result = prime * result + ((satInstr == null) ? 0 : satInstr.hashCode());
        long temp;
        temp = Double.toString(sceneTb).hashCode();
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((server == null) ? 0 : server.hashCode());
        result = prime * result + ((gprc == null) ? 0 : gprc.hashCode());
        result = prime * result + ((corrType == null) ? 0 : corrType.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result + ((year == null) ? 0 : year.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        PlotConfiguration other = (PlotConfiguration) obj;
        if (channel == null)
        {
            if (other.channel != null)
            {
                return false;
            }
        } else if (!channel.equals(other.channel))
        {
            return false;
        }
        if (dateTime == null)
        {
            if (other.dateTime != null)
            {
                return false;
            }
        } else if (!dateTime.equals(other.dateTime))
        {
            return false;
        }
        if (mode == null)
        {
            if (other.mode != null)
            {
                return false;
            }
        } else if (!mode.equals(other.mode))
        {
            return false;
        }
        if (refSatInstr == null)
        {
            if (other.refSatInstr != null)
            {
                return false;
            }
        } else if (!refSatInstr.equals(other.refSatInstr))
        {
            return false;
        }
        if (satInstr == null)
        {
            if (other.satInstr != null)
            {
                return false;
            }
        } else if (!satInstr.equals(other.satInstr))
        {
            return false;
        }
        if (Double.compare(sceneTb, other.sceneTb) != 0)
        {
            return false;
        }
        if (server == null)
        {
            if (other.server != null)
            {
                return false;
            }
        } else if (!server.equals(other.server))
        {
            return false;
        }
        if (gprc == null)
        {
            if (other.gprc != null)
            {
                return false;
            }
        } else if (!gprc.equals(other.gprc))
        {
            return false;
        }
        if (corrType == null)
        {
            if (other.corrType != null)
            {
                return false;
            }
        } else if (!corrType.equals(other.corrType))
        {
            return false;
        }
        if (version == null)
        {
            if (other.version != null)
            {
                return false;
            }
        } else if (!version.equals(other.version))
        {
            return false;
        }
        if (year == null)
        {
            if (other.year != null)
            {
                return false;
            }
        } else if (!year.equals(other.year))
        {
            return false;
        }
        return true;
    }

    /**
     * @param id
     *            the id to set.
     */
    public final void setId(final String id)
    {
        this.id = id;
    }

    /** @return the id. */
    public final String getId()
    {
        return id;
    }

    /** @return the server. */
    public final String getServer()
    {
        return server;
    }

    /** @return the source. */
    public final String getGPRC()
    {
        return gprc;
    }

    /** @return the type. */
    public final String getCorrType()
    {
        return corrType;
    }

    /** @return the satInstr. */
    public final String getSatInstr()
    {
        return satInstr;
    }

    /** @return the refSatInstr. */
    public final String getRefSatInstr()
    {
        return refSatInstr;
    }

    /** @return the mode. */
    public final String getMode()
    {
        return mode;
    }

    /**
     * @param mode
     *            the mode to set.
     */
    public final void setMode(final String mode)
    {
        this.mode = mode;
    }

    /** @return the year. */
    public final String getYear()
    {
        return year;
    }

    /**
     * @param year
     *            the year to set.
     */
    public final void setYear(final String year)
    {
        this.year = year;
    }

    /** @return the date-time. */
    public final String getDateTime()
    {
        return dateTime;
    }

    /**
     * @param dateTime
     *            the date-time to set.
     */
    public final void setDate(final String dateTime)
    {
        this.dateTime = dateTime;
    }

    /** @return the version. */
    public final String getVersion()
    {
        return version;
    }

    /**
     * @param version
     *            the version to set.
     */
    public final void setVersion(final String version)
    {
        this.version = version;
    }

    /** @return the channel. */
    public final String getChannel()
    {
        return channel;
    }

    /** @return the sceneTb. */
    public final double getSceneTb()
    {
        return sceneTb;
    }

    /**
     * @param sceneTb
     *            the sceneTb to set.
     */
    public final void setSceneTb(final double sceneTb)
    {
        this.sceneTb = sceneTb;
    }

    /**
     * Returns the URL to access a set of plot configurations directly.
     * 
     * @param plotConfigs
     *            set of plot configurations to be accessed with the returned URL.
     * @return URL to access a set of plot configurations directly.
     */
    public static String toURL(final ArrayList<PlotConfiguration> plotConfigs)
    {
        String server = "";
        String gprc = "";
        String correctionType = "";
        String satInstr = "";
        String refSatInstr = "";
        String mode = "";
        String year = "";
        String date = "";
        String version = "";
        String channel = "";
        String sceneTb = "";

        // Start loop to write every plot on the URL.
        int plotCounter = 1;
        for (final PlotConfiguration plotConfig : plotConfigs)
        {
            // If it is not the first plot, add "&" to each parameter.
            if (plotCounter > 1)
            {
                server += "&";
                gprc += "&";
                correctionType += "&";
                satInstr += "&";
                refSatInstr += "&";
                mode += "&";
                year += "&";
                date += "&";
                version += "&";
                channel += "&";
                sceneTb += "&";
            }

            // Add to server parameter.
            server += Names.SERVER_URL_PARAM + plotCounter + "=" + plotConfig.getServer().toLowerCase();

            // Add to source parameter.
            gprc += Names.GPRC_URL_PARAM + plotCounter + "=" + plotConfig.getGPRC().toLowerCase();

            // Add to type parameter.
            correctionType += Names.CORRTYPE_URL_PARAM + plotCounter + "=";
            if (plotConfig.getCorrType().toLowerCase().matches(".*rac.*"))
            {
                correctionType += "rac";

            } else if (plotConfig.getCorrType().toLowerCase().matches(".*nrtc.*"))
            {
                correctionType += "nrtc";
            }

            // Add to sat/instr. parameter (\u2011 non-breaking hyphen).
            satInstr += Names.SATINSTR_URL_PARAM + plotCounter + "=" + plotConfig.getSatInstr().replace(" ", "\u2011");

            // Add to ref sat/instr. parameter.
            refSatInstr +=
                    Names.REFSATINSTR_URL_PARAM + plotCounter + "="
                            + plotConfig.getRefSatInstr().replace(" ", "\u2011"); // \u2011
                                                                                  // non-breaking
                                                                                  // hyphen

            // Add to mode parameter.
            mode += Names.MODE_URL_PARAM + plotCounter + "=";
            if (plotConfig.getMode().toLowerCase().matches("^op.*"))
            {
                mode += "op";

            } else if (plotConfig.getMode().toLowerCase().matches("^pre-op.*"))
            {
                mode += "pre\u2011op"; // \u2011 non-breaking hyphen

            } else if (plotConfig.getMode().toLowerCase().matches("^demo.*"))
            {
                mode += "demo";
            }

            // Add to year parameter.
            year += Names.YEAR_URL_PARAM + plotCounter + "=" + plotConfig.getYear();

            // Add to date parameter.
            date +=
                    Names.DATETIME_URL_PARAM + plotCounter + "="
                            + DateUtils.format(DateUtils.parse(plotConfig.getDateTime(), "MM/dd HH:mm:ss"), "MMdd_HHmmss");

            // Add to version parameter.
            version +=
                    Names.VERSION_URL_PARAM + plotCounter + "=" + Integer.valueOf(plotConfig.getVersion()).toString();

            // Add to channel parameter.
            channel += Names.CHANNEL_URL_PARAM + plotCounter + "=" + plotConfig.getChannel();

            // Add to scene Tb parameter.
            sceneTb += Names.SCENETB_URL_PARAM + plotCounter + "=" + plotConfig.getSceneTb();

            // Update counter.
            plotCounter++;
        }

        // Compose url.
        String url =
                GWT.getHostPageBaseURL() + "?" + server + "&" + gprc + "&" + correctionType + "&" + satInstr + "&"
                        + refSatInstr + "&" + mode + "&" + year + "&" + date + "&" + version + "&" + channel + "&"
                        + sceneTb;

        return url;
    }

    /**
     * Returns the URL to access a set of plot configurations directly (an image, non-interactive
     * plot).
     * 
     * @param plotConfigs
     *            set of plot configurations to be accessed with the returned URL.
     * @return URL to access a set of plot configurations directly (an image, non-interactive plot).
     */
    public static String toURLStatic(final ArrayList<PlotConfiguration> plotConfigs)
    {
        String urlDynamic = PlotConfiguration.toURL(plotConfigs);

        return urlDynamic + "#" + NameTokens.STATIC_PLOT;
    }

    /**
     * Parses the entered URL and translate it to a set of plot configurations.
     * 
     * @return set of plot configurations as configured in the URL, empty if not defined.
     */
    public static ArrayList<PlotConfiguration> fromURL()
    {
        ArrayList<PlotConfiguration> plotConfigs = new ArrayList<PlotConfiguration>();

        int plotCounter = 1;
        boolean morePlots = true;

        while (morePlots)
        {
            // Read parameters from URL.
            String serverFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.SERVER_URL_PARAM + plotCounter);
            String sourceFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.GPRC_URL_PARAM + plotCounter);
            String typeFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.CORRTYPE_URL_PARAM + plotCounter);
            String satInstrFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.SATINSTR_URL_PARAM + plotCounter);
            String refSatInstrFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.REFSATINSTR_URL_PARAM + plotCounter);
            String modeFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.MODE_URL_PARAM + plotCounter);
            String yearFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.YEAR_URL_PARAM + plotCounter);
            String dateFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.DATETIME_URL_PARAM + plotCounter);
            String versionFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.VERSION_URL_PARAM + plotCounter);
            String channelFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.CHANNEL_URL_PARAM + plotCounter);
            String sceneTbFromUrl =
                    com.google.gwt.user.client.Window.Location.getParameter(Names.SCENETB_URL_PARAM + plotCounter);

            // If no parameters defined for this plotCounter, get out of loop and return.
            if (serverFromUrl == null && sourceFromUrl == null && typeFromUrl == null && satInstrFromUrl == null
                    && refSatInstrFromUrl == null && modeFromUrl == null && yearFromUrl == null && dateFromUrl == null
                    && versionFromUrl == null && channelFromUrl == null && sceneTbFromUrl == null)
            {
                morePlots = false;

            } else
            {
                // Some format conversions.
                double sceneTb;
                if (sceneTbFromUrl != null)
                {
                    sceneTb = Double.valueOf(sceneTbFromUrl);

                } else
                {
                    sceneTb = -1;
                }

                if (satInstrFromUrl != null)
                {
                    // normal or non-breaking hyphen.
                    satInstrFromUrl = satInstrFromUrl.replaceAll("(-|\\u2011)", " ");
                }

                if (refSatInstrFromUrl != null)
                {
                    // normal or non-breaking hyphen.
                    refSatInstrFromUrl = refSatInstrFromUrl.replaceAll("(-|\\u2011)", " ");
                }
                
                if (modeFromUrl != null)
                {
                    // normal or non-breaking hyphen.
                    modeFromUrl = modeFromUrl.replaceAll("\\u2011", "-");
                }

                if (dateFromUrl != null)
                {
                    dateFromUrl = DateUtils.format(DateUtils.parse(dateFromUrl, "MMdd_HHmmss"), "MM/dd HH:mm:ss");
                }

                if (versionFromUrl != null)
                {
                    versionFromUrl = NumberFormat.getFormat("00").format(Integer.valueOf(versionFromUrl));
                }

                // Create new Plot configuration
                PlotConfiguration plotConfig =
                        new PlotConfiguration("From URL",
                                serverFromUrl,
                                sourceFromUrl,
                                typeFromUrl,
                                satInstrFromUrl,
                                refSatInstrFromUrl,
                                modeFromUrl,
                                yearFromUrl,
                                dateFromUrl,
                                versionFromUrl,
                                channelFromUrl,
                                sceneTb);

                // Add it to list to be returned.
                plotConfigs.add(plotConfig);

                // Update counter.
                plotCounter++;
            }
        }

        return plotConfigs;
    }

    /**
     * Parses the entered dataset URL and creates a PlotConfiguration object.
     * 
     * @return PlotConfiguration, <code>null</code> if not defined.
     * @throws InvalidFilenameException
     *             when the dataset filename is invalid.
     */
    public static PlotConfiguration fromDatasetURL() throws InvalidFilenameException
    {
        PlotConfiguration plotConfig = null;

        // Read parameter from URL.
        String datasetUrl = com.google.gwt.user.client.Window.Location.getParameter(Names.DATASET_URL_PARAM);

        if (datasetUrl != null)
        {
            // Get Server.
            // - 'http://<folder>.<server>.<domain>:<port>/...'
            String[] hostElements = datasetUrl.split("/")[2].split(":")[0].split("\\.");
            String server = "unknown";
            if (hostElements.length > 1)
            {
                server = hostElements[hostElements.length - 2];

            } else
            {
                server = hostElements[0];
            }

            // Get source.
            String[] urlElements = datasetUrl.split("/");
            NetcdfFilename ncfilename = NetcdfFilename.parse(urlElements[urlElements.length - 1].replace(" ", "+"));

            String source = ncfilename.getLocationIndication().split("-")[1];

            // Get type.
            String type = ncfilename.getCorrectionType();

            // Get satInstr and refSatInstr.
            String satInstr = ncfilename.getSatellite() + " " + ncfilename.getInstrument();
            String refSatInstr = ncfilename.getRefSatellite() + " " + ncfilename.getRefInstrument();

            // Get year and date.
            Date timestamp = DateUtils.parse(ncfilename.getTimestamp(), "yyyyMMddhhmmss");

            String year = DateUtils.format(timestamp, "yyyy");
            String date = DateUtils.format(timestamp, "MM/dd HH:mm:ss");

            // Get mode.
            String mode = "op";
            if (ncfilename.getMode().equals("preop"))
            {
                mode = "pre-op"; // add a hyphen to preop.
            } else
            {
                mode = ncfilename.getMode();
            }

            // Get version.
            String version = ncfilename.getVersion();

            // Set channel and sceneTb.
            String channel = "All";
            double sceneTb = -1;

            plotConfig =
                    new PlotConfiguration("From URL",
                            server,
                            source,
                            type,
                            satInstr,
                            refSatInstr,
                            mode,
                            year,
                            date,
                            version,
                            channel,
                            sceneTb);
        }

        return plotConfig;
    }
}
