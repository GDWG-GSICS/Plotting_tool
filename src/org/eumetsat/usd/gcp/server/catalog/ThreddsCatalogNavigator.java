package org.eumetsat.usd.gcp.server.catalog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.eumetsat.usd.gcp.server.conf.ConfigManager;
import org.eumetsat.usd.gcp.server.util.DateUtils;
import org.eumetsat.usd.gcp.server.util.StringUtils;
import org.eumetsat.usd.gcp.shared.conf.NetcdfFilename;
import org.eumetsat.usd.gcp.shared.exception.DatasetNotFoundException;
import org.eumetsat.usd.gcp.shared.exception.InvalidCatalogException;
import org.eumetsat.usd.gcp.shared.exception.InvalidFilenameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import thredds.catalog.InvAccess;
import thredds.catalog.InvCatalogFactory;
import thredds.catalog.InvCatalogImpl;
import thredds.catalog.InvCatalogRef;
import thredds.catalog.InvDataset;
import thredds.catalog.ServiceType;

public class ThreddsCatalogNavigator implements CatalogNavigator
{
    /** Logger for this class. */
    private static Logger LOGGER = LoggerFactory.getLogger(ThreddsCatalogNavigator.class);

    /** Thredds catalog content. */
    private InvCatalogImpl catalog;

    /** Thredds catalog name. */
    private String catalogName;

    /** Thredds catalog URL. */
    private String catalogURL;

    /** Thredds catalog validate flag. */
    private boolean catalogValidateFlag;

    /** Name of GSICS Products folder. */
    private static final String PRODUCTS_FOLDER_NAME = "GSICS Products";

    /** Sat/Instr. separator in folder names. */
    private static final String SATINSTR_SEPARATOR = " CALIBRATED AGAINST ";
    
    /** Prime product identifier. */
    private static final String PRIME_IDENTIFIER = "PRIME";
    
    /** Name of Individual References folder. */
    private static final String INDIVIDUALREFS_FOLDER_NAME = "Individual Reference";

    /**
     * Constructs the Thredds Catalog Navigator based on the catalog name and its configuration.
     * 
     * @param configManager
     *            configuration manager.
     * @param catalogName
     *            name of the catalog.
     * @throws InvalidCatalogException
     *             if there is no valid THREDDS catalog for this configuration.
     */
    @Inject
    public ThreddsCatalogNavigator(final ConfigManager configManager, @Assisted final String catalogName)
            throws InvalidCatalogException
    {
        this.catalogName = catalogName;
        this.catalogURL = configManager.getCatalogURL(catalogName);
        this.catalogValidateFlag = configManager.getCatalogValidateFlag(catalogName);

        // Reads catalog.
        this.catalog = InvCatalogFactory.getDefaultFactory(catalogValidateFlag).readXML(catalogURL);

        // Checks catalog consistency.
        StringBuilder errorBuff = new StringBuilder();
        if (!catalog.check(errorBuff))
        {
            LOGGER.error("Invalid catalog <" + catalogURL + ">");
            LOGGER.error("Error message: " + errorBuff.toString());

            throw new InvalidCatalogException("Invalid catalog <" + catalogURL + ">. Error message: "
                    + errorBuff.toString());
        }

        // Log success.
        LOGGER.info("Catalog <" + catalogURL + "> read.");
    }

    @Override
    public List<String> getGPRCs() throws InvalidCatalogException
    {
        return namesOf(getGPRCDatasets());
    }

    @Override
    public List<String> getCorrTypes(String gprc) throws InvalidCatalogException
    {
        return namesOf(getCorrTypesDatasets(gprc));
    }

    @Override
    public List<String> getSatInstrs(String gprc, String corrType) throws InvalidCatalogException
    {
        List<String> satInstrPairsNames = namesOf(getSatInstrPairsDatasets(gprc, corrType));

        List<String> satInstrNames = new ArrayList<String>(satInstrPairsNames.size());

        for (String satInstrPairName : satInstrPairsNames)
        {
            String satInstrName;
            
            if(satInstrPairName.toUpperCase().contains(PRIME_IDENTIFIER))
            {
                satInstrName = satInstrPairName.toUpperCase().split(PRIME_IDENTIFIER)[0].trim();
                
            } else
            {
                satInstrName = satInstrPairName.toUpperCase().split(SATINSTR_SEPARATOR)[0].trim();
            }
            
            if (!satInstrNames.contains(satInstrName))
            {
                satInstrNames.add(satInstrName);
            }
            
            
        }

        return satInstrNames;
    }

    @Override
    public List<String> getRefSatInstrs(String gprc, String corrType, String satInstr) throws InvalidCatalogException
    {
        List<String> satInstrPairsNames = namesOf(getSatInstrPairsDatasets(gprc, corrType));

        List<String> refSatInstrNames = new ArrayList<String>(satInstrPairsNames.size());

        for (String satInstrPairName : satInstrPairsNames)
        {
            String satInstrName;
            String refSatInstrName;
            
            if(satInstrPairName.toUpperCase().contains(PRIME_IDENTIFIER))
            {
                satInstrName = satInstrPairName.toUpperCase().split(PRIME_IDENTIFIER)[0].trim();
                refSatInstrName = PRIME_IDENTIFIER;
                
            } else
            {
                satInstrName = satInstrPairName.toUpperCase().split(SATINSTR_SEPARATOR)[0].trim();
                refSatInstrName = satInstrPairName.toUpperCase().split(SATINSTR_SEPARATOR)[1].trim();
            }
            
            if (satInstrName.equals(satInstr))
            {
                refSatInstrNames.add(refSatInstrName);
            }
        }

        return refSatInstrNames;
    }

    @Override
    public List<String> getModes(String gprc, String corrType, String satInstr, String refSatInstr)
            throws InvalidCatalogException
    {
        return namesOf(getModesDatasets(gprc, corrType, satInstr, refSatInstr));
    }

    @Override
    public List<String> getYears(String gprc, String corrType, String satInstr, String refSatInstr, String mode)
            throws InvalidCatalogException
    {
        List<String> years = new ArrayList<String>();

        List<InvDataset> childDatasets = getChildDatasets(gprc, corrType, satInstr, refSatInstr, mode);

        for (InvDataset childDataset : childDatasets)
        {
            // Skip latest.xml
            if (childDataset.getName().equals("latest.xml"))
            {
                continue;
            }

            Date datasetDate = null;
            try
            {
                NetcdfFilename childFilename = NetcdfFilename.parse(childDataset.getName());

                datasetDate = DateUtils.parse(childFilename.getTimestamp(), "yyyyMMddhhmmss", "GMT");

            } catch (InvalidFilenameException ife)
            {
                LOGGER.warn("'" + childDataset.getName() + "' has a format not compliant with WMO conventions.", ife);

                // Skip it
                continue;

            } catch (ParseException pe)
            {
                LOGGER.warn("Timestamp has not the expected format 'yyyyMMddhhmmss'.", pe);

                // Skip it
                continue;
            }

            String yearStr = DateUtils.format(datasetDate, "yyyy", "GMT");
            years.add(yearStr);
        }

        return years;
    }

    @Override
    public List<String> getDateTimes(String gprc, String corrType, String satInstr, String refSatInstr, String mode,
            String year) throws InvalidCatalogException
    {
        List<String> dates = new ArrayList<String>();

        List<InvDataset> childDatasets = this.getChildDatasets(gprc, corrType, satInstr, refSatInstr, mode);

        for (InvDataset childDataset : childDatasets)
        {
            // Skip latest.xml
            if (childDataset.getName().equals("latest.xml"))
            {
                continue;
            }

            Date childDate = null;
            try
            {
                NetcdfFilename childFilename = NetcdfFilename.parse(childDataset.getName());

                childDate = DateUtils.parse(childFilename.getTimestamp(), "yyyyMMddhhmmss", "GMT");

            } catch (InvalidFilenameException ife)
            {
                LOGGER.warn("'" + childDataset.getName() + "' has a format not compliant with WMO conventions.", ife);

                // Skip it
                continue;

            } catch (ParseException pe)
            {
                LOGGER.warn("Timestamp has not the expected format 'yyyyMMddhhmmss'.", pe);

                // Skip it
                continue;
            }

            String yearStr = DateUtils.format(childDate, "yyyy", "GMT");

            if (yearStr.equals(year))
            {
                DateFormat dateDf = new SimpleDateFormat("MM/dd HH:mm:ss");
                dateDf.setTimeZone(TimeZone.getTimeZone("GMT"));
                String dateStr = dateDf.format(childDate);
                dates.add(dateStr);
            }
        }

        return dates;
    }

    @Override
    public List<String> getVersions(String gprc, String corrType, String satInstr, String refSatInstr, String mode,
            String year, String dateTime) throws InvalidCatalogException
    {
        List<String> versions = new ArrayList<String>();

        List<InvDataset> childDatasets = getChildDatasets(gprc, corrType, satInstr, refSatInstr, mode);

        for (InvDataset childDataset : childDatasets)
        {
            // Skip latest.xml
            if (childDataset.getName().equals("latest.xml"))
            {
                continue;
            }

            Date childDateTime = null;
            String childVersion = null;
            try
            {
                NetcdfFilename childFilename = NetcdfFilename.parse(childDataset.getName());

                childDateTime = DateUtils.parse(childFilename.getTimestamp(), "yyyyMMddhhmmss", "GMT");
                childVersion = childFilename.getVersion();

            } catch (InvalidFilenameException ife)
            {
                LOGGER.warn("'" + childDataset.getName() + "' has a format not compliant with WMO conventions.", ife);

                // Skip it
                continue;

            } catch (ParseException pe)
            {
                LOGGER.warn("Timestamp has not the expected format 'yyyyMMddhhmmss'.", pe);

                // Skip it
                continue;
            }

            String yearStr = DateUtils.format(childDateTime, "yyyy", "GMT");
            String dateTimeStr = DateUtils.format(childDateTime, "MM/dd HH:mm:ss", "GMT");

            if (yearStr.equals(year) && dateTimeStr.equals(dateTime))
            {
                versions.add(childVersion);
            }
        }

        return versions;
    }

    @Override
    public String getDatasetURL(final String gprc, final String corrType, final String satInstr,
            final String refSatInstr, String mode, String year, String dateTime, String version)
            throws DatasetNotFoundException, InvalidCatalogException
    {
        String targetURL = null;

        // Get defaults for mode, year, date, version and sceneTb if not defined.
        if (mode == null)
        {
            mode = getModeWithData(gprc, corrType, satInstr, refSatInstr, "op");

        } else if (!modeHasData(gprc, corrType, satInstr, refSatInstr, mode))
        {
            throw new DatasetNotFoundException("No data for mode '" + mode + "'. Selections = [" + catalogName + "/"
                    + gprc + "/" + corrType + "/" + satInstr + "/" + refSatInstr + "]");
        }

        if (year == null)
        {
            List<String> availableYears = getYears(gprc, corrType, satInstr, refSatInstr, mode);

            if (!availableYears.isEmpty())
            {
                year = Collections.max(availableYears);

            } else
            {
                throw new DatasetNotFoundException("No data for mode '" + mode + "'. Selections = [" + catalogName
                        + "/" + gprc + "/" + corrType + "/" + satInstr + "/" + refSatInstr + "]");
            }
        }

        if (dateTime == null)
        {
            List<String> availableDateTimes = getDateTimes(gprc, corrType, satInstr, refSatInstr, mode, year);

            if (!availableDateTimes.isEmpty())
            {
                dateTime = Collections.max(availableDateTimes);

            } else
            {
                throw new DatasetNotFoundException("No data for year " + year + ". Selections = [" + catalogName + "/"
                        + gprc + "/" + corrType + "/" + satInstr + "/" + refSatInstr + "/" + mode + "]");
            }
        }

        if (version == null)
        {
            List<String> availableVersions = getVersions(gprc, corrType, satInstr, refSatInstr, mode, year, dateTime);

            if (!availableVersions.isEmpty())
            {
                version = Collections.max(availableVersions);

            } else
            {
                throw new DatasetNotFoundException("No data for date-time " + dateTime + ". Selections = ["
                        + catalogName + "/" + gprc + "/" + corrType + "/" + satInstr + "/" + refSatInstr + "/" + mode
                        + "/" + year + "]");
            }
        }

        if (dateTime.equals("latest"))
        {
            targetURL = getLatestDatasetURL(gprc, corrType, satInstr, refSatInstr, mode, year);

        } else
        {
            targetURL = getSingleDatasetURL(gprc, corrType, satInstr, refSatInstr, mode, year, dateTime, version);
        }

        return targetURL;
    }

    /**
     * Get the default mode as the first one with data, with this preference in mind: operational > pre-operational >
     * demo.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type of data (RAC, NRTC...)
     * @param satInstr
     *            satellite/instrument.
     * @param refSatInstr
     *            reference satellite/instrument.
     * @param targetMode
     *            mode which will be selected as default if there is data available under its folder in the server.
     * @return mode with data.
     * @throws DatasetNotFoundException
     *             if no dataset for these parameters is found in the catalog.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    private String getModeWithData(final String gprc, final String corrType, final String satInstr,
            final String refSatInstr, final String targetMode) throws DatasetNotFoundException, InvalidCatalogException
    {
        String defaultMode;

        List<String> availableModes = getModes(gprc, corrType, satInstr, refSatInstr);

        int indexFound = StringUtils.indexOf(availableModes, "^" + targetMode + ".*");

        if (indexFound != -1)
        {
            List<String> availableYears = getYears(gprc, corrType, satInstr, refSatInstr,
                    availableModes.get(indexFound));

            if (!availableYears.isEmpty())
            {
                defaultMode = availableModes.get(indexFound);

            } else
            {
                if (targetMode.equals("op"))
                {
                    defaultMode = getModeWithData(gprc, corrType, satInstr, refSatInstr, "pre-op");

                } else if (targetMode.equals("pre-op"))
                {
                    defaultMode = getModeWithData(gprc, corrType, satInstr, refSatInstr, "demo");

                } else
                {
                    LOGGER.error("No mode available with data for selections = [" + catalogName + "/" + gprc + "/"
                            + corrType + "/" + satInstr + "/" + refSatInstr + "]");
                    throw new DatasetNotFoundException("No mode available with data for selections = [" + catalogName
                            + "/" + gprc + "/" + corrType + "/" + satInstr + "/" + refSatInstr + "]");
                }
            }
        } else
        // targetMode not found in the server.
        {
            if (targetMode.equals("op"))
            {
                defaultMode = getModeWithData(gprc, corrType, satInstr, refSatInstr, "pre-op");

            } else if (targetMode.equals("pre-op"))
            {
                defaultMode = getModeWithData(gprc, corrType, satInstr, refSatInstr, "demo");

            } else
            {
                LOGGER.error("No mode available with data for selections = [" + catalogName + "/" + gprc + "/"
                        + corrType + "/" + satInstr + "/" + refSatInstr + "]");
                throw new DatasetNotFoundException("No mode available with data for selections = [" + catalogName + "/"
                        + gprc + "/" + corrType + "/" + satInstr + "/" + refSatInstr + "]");
            }
        }

        return defaultMode;
    }

    /**
     * Check if a mode has some data.
     * 
     * @param plotConfig
     *            plot configuration being plotted.
     * @param mode
     *            mode whose contents will be checked.
     * @return boolean true if mode has some data.
     * @throws InvalidCatalogException
     *             getYears(gprc, corrType, satInstr, refSatInstr, mode).
     */
    private boolean modeHasData(final String gprc, final String corrType, final String satInstr,
            final String refSatInstr, final String mode) throws InvalidCatalogException
    {
        List<String> availableYears = getYears(gprc, corrType, satInstr, refSatInstr, mode);

        return !availableYears.isEmpty();
    }

    /**
     * Gets single NetCDF dataset URL for a certain set of parameters.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type of data (RAC, NRTC...)
     * @param satInstr
     *            satellite/instrument.
     * @param refSatInstr
     *            reference satellite/instrument.
     * @param mode
     *            mode of the data (demonstration, pre-operational or operational).
     * @param year
     *            of the data.
     * @param dateTime
     *            of the data.
     * @param version
     *            version of the data.
     * @return String the single dataset URL for the specified parameters.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    private String getSingleDatasetURL(String gprc, String corrType, String satInstr, String refSatInstr, String mode,
            String year, String dateTime, String version) throws InvalidCatalogException
    {
        String targetSingleURL = null;

        List<String> datasetsFoundWithNewFilename = new ArrayList<String>();
        List<String> datasetsFoundWithOldFilename = new ArrayList<String>();

        List<InvDataset> childDatasets = getChildDatasets(gprc, corrType, satInstr, refSatInstr, mode);
        for (InvDataset childDataset : childDatasets)
        {
            // Skip latest.xml
            if (childDataset.getName().equals("latest.xml"))
            {
                continue;
            }

            // Search in the rest of datasets
            Date childDateTime = null;
            String childVersion = null;
            NetcdfFilename childFilename = null;
            try
            {
                childFilename = NetcdfFilename.parse(childDataset.getName());

                childDateTime = DateUtils.parse(childFilename.getTimestamp(), "yyyyMMddhhmmss", "GMT");
                childVersion = childFilename.getVersion();

            } catch (InvalidFilenameException ife)
            {
                LOGGER.warn("'" + childDataset.getName() + "' has a format not compliant with WMO conventions.", ife);

                // Skip it
                continue;

            } catch (ParseException pe)
            {
                LOGGER.warn("Timestamp has not the expected format 'yyyyMMddhhmmss'.", pe);

                // Skip it
                continue;
            }

            String yearStr = DateUtils.format(childDateTime, "yyyy", "GMT");
            String dateTimeStr = DateUtils.format(childDateTime, "MM/dd HH:mm:ss", "GMT");

            if (yearStr.equals(year) && dateTimeStr.equals(dateTime) && childVersion.equals(version))
            {
                // dataset found.
                InvAccess httpServerAccess = childDataset.getAccess(ServiceType.HTTPServer);

                if (childFilename.getLocalDataSubcategory() == null)
                {
                    // old filename
                    datasetsFoundWithOldFilename.add(httpServerAccess.getStandardUrlName());

                } else
                {
                    // new filename
                    datasetsFoundWithNewFilename.add(httpServerAccess.getStandardUrlName());

                }
            }
        }

        // Datasets with new WMO filename for GSICS [12/06/2012] have preference
        if (!datasetsFoundWithNewFilename.isEmpty())
        {
            // get first, no preference criteria set yet.
            targetSingleURL = datasetsFoundWithNewFilename.get(0);

        } else if (!datasetsFoundWithOldFilename.isEmpty())
        {
            // get first, no preference criteria set yet.
            targetSingleURL = datasetsFoundWithOldFilename.get(0);
        }

        if (targetSingleURL == null)
        {
            throw new InvalidCatalogException("No dataset found for [" + gprc + ", " + corrType + ", " + satInstr
                    + ", " + refSatInstr + ", " + mode + ", " + year + ", " + dateTime + "," + version + "]");
        }

        return targetSingleURL;
    }

    /**
     * Gets latest NetCDF dataset URL for a certain set of parameters.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type of data (RAC, NRTC...)
     * @param satInstr
     *            satellite/instrument.
     * @param refSatInstr
     *            reference satellite/instrument.
     * @param mode
     *            mode of the data (demonstration, pre-operational or operational).
     * @param year
     *            of the data.
     * @return String the single dataset URL for the specified parameters.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    private String getLatestDatasetURL(String gprc, String corrType, String satInstr, String refSatInstr, String mode,
            String year) throws InvalidCatalogException
    {
        String targetLatestURL = null;

        List<InvDataset> datasets = getChildDatasets(gprc, corrType, satInstr, refSatInstr, mode);
        for (InvDataset dataset : datasets)
        {
            if (dataset.getName().equals("latest.xml"))
            {
                InvAccess httpServerAccess = dataset.getAccess(ServiceType.HTTPServer);

                targetLatestURL = httpServerAccess.getStandardUrlName();

                // found, stop searching.
                break;
            }
        }

        if (targetLatestURL == null)
        {
            throw new InvalidCatalogException("No 'latest.xml' found for [" + gprc + ", " + corrType + ", " + satInstr
                    + ", " + refSatInstr + ", " + mode + ", " + year + "]");
        }

        targetLatestURL = "thredds:resolve:" + targetLatestURL;

        return targetLatestURL;
    }

    /**
     * Gets the gprc datasets as a List<InvDataset>.
     * 
     * @return List<InvDataset> available gprc datasets; list may be empty, not <code>null</code>.
     * @throws InvalidCatalogException
     *             if there is no GSICS Products folder in the catalog.
     */
    private List<InvDataset> getGPRCDatasets() throws InvalidCatalogException
    {
        List<InvDataset> gprcFolders = null;

        List<InvDataset> rootFolders = catalog.getDatasets();

        for (InvDataset rootFolder : rootFolders)
        {
            if (Pattern.compile(Pattern.quote(PRODUCTS_FOLDER_NAME), Pattern.CASE_INSENSITIVE).matcher(
                    rootFolder.getName()).find())
            {
                gprcFolders = rootFolder.getDatasets();

                // found, stop searching.
                break;
            }
        }

        if (gprcFolders == null)
        {
            throw new InvalidCatalogException("'" + PRODUCTS_FOLDER_NAME + "' not in the <" + catalogURL + ">");
        }

        return gprcFolders;
    }

    /**
     * Gets the correction types datasets as a List<InvDataset> for a certain gprc.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @return List<InvDataset> available type datasets; list may be empty, not <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    private List<InvDataset> getCorrTypesDatasets(final String gprc) throws InvalidCatalogException
    {
        List<InvDataset> correctionTypeFolders = null;

        List<InvDataset> gprcFolders = getGPRCDatasets();

        for (InvDataset gprcFolder : gprcFolders)
        {
            if (Pattern.compile(Pattern.quote(gprc), Pattern.CASE_INSENSITIVE).matcher(gprcFolder.getName()).find())
            {
                InvCatalogRef gprcCatalogRef = (InvCatalogRef) gprcFolder;

                String xmlURL = null;
                if (gprcCatalogRef.getXlinkHref().startsWith("http"))
                {
                    // Get target catalog URL.
                    xmlURL = gprcCatalogRef.getXlinkHref();

                } else
                {
                    // Get parent base URL.
                    String parentBaseURL = gprcCatalogRef.getParentCatalog().getUriString();
                    parentBaseURL = parentBaseURL.substring(0, parentBaseURL.lastIndexOf("/") + 1);

                    // Get relative catalog URL.
                    String catalogRelativeURL = gprcCatalogRef.getXlinkHref();

                    // Get target catalog URL.
                    xmlURL = StringUtils.joinWithoutIntersection(parentBaseURL, catalogRelativeURL);
                }

                // Workaround to avoid DNS problems in the EUMETSAT ICT DMZ environment.
                // Replace EUMETSAT GSICS server DNS name with the external IP.
                xmlURL = xmlURL.replace("gsics.eumetsat.int", "193.17.10.39");
                xmlURL = xmlURL.replace("vgsics.eumetsat.int", "193.17.10.43");

                LOGGER.debug("xmlURL=" + xmlURL);

                InvCatalogImpl gprcCatalog = InvCatalogFactory.getDefaultFactory(catalogValidateFlag).readXML(xmlURL);

                if (gprcCatalog.getDatasets().isEmpty())
                {
                    correctionTypeFolders = new ArrayList<InvDataset>(); // return empty list.
                } else
                {
                    correctionTypeFolders = gprcCatalog.getDatasets().get(0).getDatasets();
                }

                // found, stop searching.
                break;
            }
        }

        if (correctionTypeFolders == null)
        {
            throw new InvalidCatalogException("No '" + gprc + "' folder inside '" + PRODUCTS_FOLDER_NAME
                    + "' in the catalog <" + catalogURL + ">");
        }

        return correctionTypeFolders;
    }

    /**
     * Gets the sat/instr pairs datasets as a List<InvDataset> for a certain gprc, and type.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type (RAC, NRTC...).
     * @return List<InvDataset> available satellite/instrument pairs datasets; list may be empty, not <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    private List<InvDataset> getSatInstrPairsDatasets(final String gprc, final String corrType)
            throws InvalidCatalogException
    {
        List<InvDataset> corrTypeFolders = getCorrTypesDatasets(gprc);

        InvDataset corrTypeDataset = getDataset(corrTypeFolders, corrType);
        
        if (corrTypeDataset == null)
        {
            throw new InvalidCatalogException("No '" + corrType + "' folder inside '" + PRODUCTS_FOLDER_NAME + "/"
                    + gprc + "' in the catalog <" + catalogURL + ">");
        }
        
        List<InvDataset> satInstrPairFolders =  corrTypeDataset.getDatasets();
        
        // Add individual references if they are separated from the "Prime" products.
        InvDataset individualRefs = getDataset(satInstrPairFolders, INDIVIDUALREFS_FOLDER_NAME);
        if(individualRefs != null)
        {
            satInstrPairFolders.addAll(individualRefs.getDatasets());
            satInstrPairFolders.remove(individualRefs);
        }
        
        return satInstrPairFolders;
    }

    /**
     * Gets the modes datasets as a List<InvDataset> for a certain gprc, type and sat/instr pair.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param corrType
     *            correction type of data (RAC, NRTC...)
     * @param satInstr
     *            satellite/instrument.
     * @param refSatInstr
     *            reference satellite/instrument.
     * @return List<InvDataset> available mode datasets; list may be empty, not <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    private List<InvDataset> getModesDatasets(final String gprc, final String corrType, final String satInstr,
            final String refSatInstr) throws InvalidCatalogException
    {
        List<InvDataset> modeFolders = null;

        // Build folder name from satInstr and refSatInstr.
        String satInstrPair;
        if(refSatInstr.equals(PRIME_IDENTIFIER))
        {
            satInstrPair = satInstr + " " + PRIME_IDENTIFIER; 
            
        } else
        {
            satInstrPair = satInstr + SATINSTR_SEPARATOR + refSatInstr;
        }
        
        // Find folder name in catalog.
        List<InvDataset> satInstrPairFolders = this.getSatInstrPairsDatasets(gprc, corrType);

        for (InvDataset satInstrPairFolder : satInstrPairFolders)
        {
            if (Pattern.compile(Pattern.quote(satInstrPair), Pattern.CASE_INSENSITIVE).matcher(
                    satInstrPairFolder.getName()).find())
            {
                InvCatalogRef satInstrPairCatalogRef = (InvCatalogRef) satInstrPairFolder;

                String xmlURL = null;
                if (satInstrPairCatalogRef.getXlinkHref().startsWith("http"))
                {
                    // Get target catalog URL.
                    xmlURL = satInstrPairCatalogRef.getXlinkHref();

                } else
                {
                    // Get parent base URL.
                    String parentBaseURL = satInstrPairCatalogRef.getParentCatalog().getUriString();
                    parentBaseURL = parentBaseURL.substring(0, parentBaseURL.lastIndexOf("/") + 1);

                    // Get relative catalog URL.
                    String catalogRelativeURL = satInstrPairCatalogRef.getXlinkHref();

                    // Get target catalog URL.
                    xmlURL = StringUtils.joinWithoutIntersection(parentBaseURL, catalogRelativeURL);
                }

                // Workaround to avoid DNS problems in the EUMETSAT ICT DMZ environment.
                // Replace EUMETSAT GSICS server DNS name with the external IP.
                xmlURL = xmlURL.replace("gsics.eumetsat.int", "193.17.10.39");
                xmlURL = xmlURL.replace("vgsics.eumetsat.int", "193.17.10.43");

                LOGGER.debug("xmlURL=" + xmlURL);

                InvCatalogImpl satInstrPairCatalog = InvCatalogFactory.getDefaultFactory(catalogValidateFlag).readXML(
                        xmlURL);

                if (satInstrPairCatalog.getDatasets().isEmpty()
                        || satInstrPairCatalog.getDatasets().get(0).getDatasets().isEmpty())
                {
                    modeFolders = new ArrayList<InvDataset>(); // return empty list.
                } else
                {
                    modeFolders = satInstrPairCatalog.getDatasets().get(0).getDatasets().get(0).getDatasets();
                }

                // found, stop searching.
                break;
            }
        }

        if (modeFolders == null)
        {
            throw new InvalidCatalogException("No '" + satInstrPair + "' folder inside '" + PRODUCTS_FOLDER_NAME + "/"
                    + gprc + "/" + corrType + "' in the catalog <" + catalogURL + ">");
        }

        return modeFolders;
    }

    /**
     * Get the child datasets as a List<InvDataset> for a certain gprc, type, sat/instr pair, and mode.
     * 
     * @param gprc
     *            GPRC (GSICS Processing and Research Centre) where the data comes from.
     * @param correctionType
     *            correction type of data (RAC, NRTC...)
     * @param satInstr
     *            satellite/instrument.
     * @param refSatInstr
     *            reference satellite/instrument.
     * @param mode
     *            mode of the data (demonstration, pre-operational or operational).
     * @return List<InvDataset> available datasets; list may be empty, not <code>null</code>.
     * @throws InvalidCatalogException
     *             if some folder has not been found in the catalog.
     */
    private List<InvDataset> getChildDatasets(final String gprc, final String corrType, final String satInstr,
            final String refSatInstr, final String mode) throws InvalidCatalogException
    {
        List<InvDataset> childDatasets = null;

        List<InvDataset> modeFolders = getModesDatasets(gprc, corrType, satInstr, refSatInstr);

        for (InvDataset modeFolder : modeFolders)
        {
            if (Pattern.compile("(^|[^-])" + mode, Pattern.CASE_INSENSITIVE).matcher(modeFolder.getName()).find())
            {
                InvCatalogRef modeCatalogRef = (InvCatalogRef) modeFolder;

                String xmlURL = null;
                if (modeCatalogRef.getXlinkHref().startsWith("http"))
                {
                    // Get target catalog URL.
                    xmlURL = modeCatalogRef.getXlinkHref();

                } else
                {
                    // Get parent base URL.
                    String parentBaseURL = modeCatalogRef.getParentCatalog().getUriString();
                    parentBaseURL = parentBaseURL.substring(0, parentBaseURL.lastIndexOf("/") + 1);

                    // Get relative catalog URL.
                    String catalogRelativeURL = modeCatalogRef.getXlinkHref();

                    // Get target catalog URL.
                    xmlURL = StringUtils.joinWithoutIntersection(parentBaseURL, catalogRelativeURL);
                }

                // Workaround to avoid DNS problems in the EUMETSAT ICT DMZ environment.
                // Replace EUMETSAT GSICS server DNS name with the external IP.
                xmlURL = xmlURL.replace("gsics.eumetsat.int", "193.17.10.39");
                xmlURL = xmlURL.replace("vgsics.eumetsat.int", "193.17.10.43");

                LOGGER.debug("xmlURL=" + xmlURL);

                InvCatalogImpl targetCatalog = InvCatalogFactory.getDefaultFactory(catalogValidateFlag).readXML(xmlURL);

                if (targetCatalog.getDatasets().isEmpty())
                {
                    childDatasets = new ArrayList<InvDataset>(); // return empty list.
                } else
                {
                    childDatasets = targetCatalog.getDatasets().get(0).getDatasets();
                }

                // found, stop searching.
                break;
            }
        }

        if (childDatasets == null)
        {
            throw new InvalidCatalogException("No '" + mode + "' folder inside '" + PRODUCTS_FOLDER_NAME + "/" + gprc
                    + "/" + corrType + "/" + satInstr + "-" + refSatInstr + "' in the catalog <" + catalogURL + ">");
        }

        return childDatasets;
    }

    /**
     * Get a specific dataset from a list of datasets.
     * 
     * @param datasetsList
     *            list of datasets.
     * @param datasetName
     *            name of the dataset to get.
     * @return dataset, <code>null</code> if not found.
     */
    private InvDataset getDataset(final List<InvDataset> datasetsList, final String datasetName)
    {
        InvDataset foundDataset = null;

        for (InvDataset dataset : datasetsList)
        {
            if (Pattern.compile(Pattern.quote(datasetName), Pattern.CASE_INSENSITIVE).matcher(dataset.getName()).find())
            {
                foundDataset = dataset;
                break;
            }
        }

        return foundDataset;
    }

    /**
     * Convert a list of <code>InvDataset</code> to a vector with the dataset names.
     * 
     * @param datasetsList
     *            list of datasets.
     * @return list with the dataset names; may be empty but never <code>null</code>.
     */
    private List<String> namesOf(final List<InvDataset> datasetsList)
    {
        List<String> names = new ArrayList<String>(datasetsList.size());

        for (InvDataset dataset : datasetsList)
        {
            names.add(dataset.getName());
        }

        return names;
    }
}
