//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
//@formatter:on
package org.eumetsat.usd.gcp.shared.conf;

import org.eumetsat.usd.gcp.shared.exception.InvalidFilenameException;

/**
 * Parses a Net CDF filename, which follows the WMO conventions as stated in EUM/OPS/STD/11/3120
 * Section 3.
 * <p>
 * WMO filename format: <br>
 * pflag_productidentifier_oflag_originator_yyyyMMddhhmmss_freeformat.type <br>
 * productidentifier = LocationIndicator,DataDesignator,FreeDescription <br>
 * freeformat = mode_version
 * 
 * @author USD/C/PBe
 */
public final class NetcdfFilename
{
    /** First element in the filename. */
    private String pflag;
    /** Second element in the filename. */
    private String locationIndication;
    /** Fifth element in the filename. */
    private String oflag;
    /** Sixth element in the filename. */
    private String originator;
    /** Seventh element in the filename. */
    private String timestamp;
    /** Eighth element in the filename. */
    private String mode;
    /** Ninth element in the filename. */
    private String version;

    /** Data Category. */
    private String dataCategory;
    /** International Data Subcategory. */
    private String internationalDataSubcategory;
    /** Local Data Subcategory. */
    private String localDataSubcategory;
    /** Monitored satellite. */
    private String satellite;
    /** Monitored instrument. */
    private String instrument;
    /** Reference satellite. */
    private String refSatellite;
    /** Reference instrument. */
    private String refInstrument;

    /**
     * Private default constructor. Called only from the static factory method.
     * 
     * @param pflag
     *            First element in the filename.
     * @param locationIndication
     *            Second element in the filename.
     * @param dataDesignator
     *            Third element in the filename.
     * @param freeDescription
     *            Fourth element in the filename.
     * @param oflag
     *            Fifth element in the filename.
     * @param originator
     *            Sixth element in the filename.
     * @param timestamp
     *            Seventh element in the filename.
     * @param mode
     *            Eighth element in the filename.
     * @param version
     *            Ninth element in the filename.
     */
    private NetcdfFilename(final String pflag, final String locationIndication, final String dataDesignator,
            final String freeDescription, final String oflag, final String originator, final String timestamp,
            final String mode, final String version)
    {
        this.pflag = pflag;
        this.locationIndication = locationIndication;
        this.oflag = oflag;
        this.originator = originator;
        this.timestamp = timestamp;
        this.mode = mode;
        this.version = version;

        this.parseFreeDescription(freeDescription);
        this.parseDataDesignator(dataDesignator);
    }

    /**
     * Creates a new NetcdfFilename object from the input filename string which follows the WMO
     * conventions as stated in EUM/OPS/STD/11/3120 Section 3.
     * 
     * @param netcdfFilename
     *            a NetCDF filename following the WMO conventions.
     * @return a new NetcdfFilename.
     * @throws InvalidFilenameException
     *             when the input filename does not follow the WMO conventions.
     */
    public static NetcdfFilename parse(final String netcdfFilename) throws InvalidFilenameException
    {
        String[] tokens = parseWMOFilename(netcdfFilename);

        String pflag = tokens[0];

        String[] subtokens = parseProductIdentifier(tokens[1]);

        String locationIndication = subtokens[0];
        String dataDesignator = subtokens[1];
        String freeDescription = subtokens[2];

        String oflag = tokens[2];
        String originator = tokens[3];

        String timestamp = tokens[4];

        String mode = "op"; // default mode is 'operational'
        if(tokens.length >= 7)
        {
            mode = tokens[tokens.length - 2];
        }
            
        String version = "01"; // default version is '01'
        if(tokens.length >= 6)
        {
            version = tokens[tokens.length - 1].split("\\.")[0]; // trim the ".type" part.
        }

        return new NetcdfFilename(pflag,
                locationIndication,
                dataDesignator,
                freeDescription,
                oflag,
                originator,
                timestamp,
                mode,
                version);
    }

    /**
     * Splits the productidentifier part of the filename into three subtokens. Delimiter should a
     * comma.
     * 
     * @param productidentifier
     *            part of the filename.
     * @return String[] three subtokens: locationIndicator, dataDesignator and freeDescription.
     * @throws InvalidFilenameException
     *             if productidentifier does not have 3 subtokens separated by commas.
     */
    private static String[] parseProductIdentifier(final String productidentifier) throws InvalidFilenameException
    {
        String[] subtokens = productidentifier.split(",");
        if (subtokens.length != 3)
        {
            throw new InvalidFilenameException("'" + productidentifier
                    + "' does not follow 'LocationIndicator,DataDesignator,FreeDescription' format.");
        }
        return subtokens;
    }

    /**
     * Splits the filename into 7 tokens separated by underscore.
     * 
     * @param netcdfFilename
     *            filename to be parsed, which should have 7 tokens divided by underscores.
     * @return String[] seven subtokens: pflag, productidentifier, oflag, originator, timestamp,
     *         mode, version.
     * @throws InvalidFilenameException
     *             if the filename does not follow the WMO conventions.
     */
    private static String[] parseWMOFilename(final String netcdfFilename) throws InvalidFilenameException
    {
        String[] tokens = netcdfFilename.split("_");

        if (tokens.length < 5)
        {
            throw new InvalidFilenameException("'" + netcdfFilename + "' does not follow WMO filename conventions.");
        }
        return tokens;
    }

    /**
     * Gets detailed information in free description.
     * 
     * @param freeDescription
     *            free description part of the filename.
     */
    private void parseFreeDescription(final String freeDescription)
    {
        String[] elements = freeDescription.split("-");
        String[] satInstr = elements[0].split("\\+");
        String[] refSatInstr = elements[1].split("\\+");

        // Changed to be robust when no instrument is included in the filename (e.g. PRIME products).
        // W_XX-EUMETSAT-Darmstadt,SATCAL+RAC+GEOLEOIR,MSG1+SEVIRI-PRIME_C_EUMG_20080601000000_demo_00.nc
        
        this.satellite = satInstr[0];
        this.instrument = "NA";
        if(satInstr.length > 1)
        {
            this.instrument = satInstr[1];
        }
        
        this.refSatellite = refSatInstr[0];
        this.refInstrument = "NA";
        if(refSatInstr.length > 1)
        {
            this.refInstrument = refSatInstr[1];
        }
    }

    /**
     * Gets detailed information in data designator.
     * 
     * @param dataDesignator
     *            data designator part of the filename.
     */
    private void parseDataDesignator(final String dataDesignator)
    {
        String[] elements = dataDesignator.split("\\+");

        this.dataCategory = elements[0];
        this.internationalDataSubcategory = elements[1];

        // present on new WMO filenames for GSICS [12/06/2012]
        if (elements.length == 3)
        {
            this.localDataSubcategory = elements[2];
        }
    }

    /**
     * Gets the pflag of the filename, i.e. "W".
     * 
     * @return the pflag.
     */
    public String getPflag()
    {
        return this.pflag;
    }

    /**
     * Gets the locationIndication part of the filename, i.e. "XX-EUMETSAT-Darmstadt".
     * 
     * @return the locationIndication.
     */
    public String getLocationIndication()
    {
        return this.locationIndication;
    }

    /**
     * Gets the oflag of the filename, i.e. "C"
     * 
     * @return the oflag.
     */
    public String getOflag()
    {
        return this.oflag;
    }

    /**
     * Gets the originator part of the filename, i.e. "EUMG"
     * 
     * @return the originator.
     */
    public String getOriginator()
    {
        return this.originator;
    }

    /**
     * Gets the timestamp as a String "yyyyMMddhhmmss".
     * 
     * @return the timestamp as a Date object.
     */
    public String getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * Gets the mode of the filename, i.e. "demo".
     * 
     * @return the mode.
     */
    public String getMode()
    {
        return this.mode;
    }

    /**
     * Gets the version of the filename, i.e. "03".
     * 
     * @return the version.
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * Gets the dataCategory part of the dataDesignator, i.e. "SATCAL".
     * 
     * @return the dataCategory.
     */
    public String getDataCategory()
    {
        return this.dataCategory;
    }

    /**
     * Gets the internationalDataSubcategory part of the dataDesignator, i.e. "NRTC".
     * 
     * @return the internationalDataSubcategory.
     */
    public String getCorrectionType()
    {
        return this.internationalDataSubcategory;
    }

    /**
     * Gets the localDataSubcategory part of the dataDesignator, i.e. "GEOLEOIR".
     * 
     * @return the dataDesignator.
     */
    public String getLocalDataSubcategory()
    {
        return this.localDataSubcategory;
    }

    /**
     * Gets the monitored satellite.
     * 
     * @return the satellite.
     */
    public String getSatellite()
    {
        return this.satellite;
    }

    /**
     * Gets the monitored instrument.
     * 
     * @return the instrument.
     */
    public String getInstrument()
    {
        return this.instrument;
    }

    /**
     * Gets the reference satellite.
     * 
     * @return the refSatellite.
     */
    public String getRefSatellite()
    {
        return this.refSatellite;
    }

    /**
     * Gets the reference instrument.
     * 
     * @return the refInstrument.
     */
    public String getRefInstrument()
    {
        return this.refInstrument;
    }
}
