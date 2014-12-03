package cz.mallat.uasparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jregex.Matcher;
import jregex.Pattern;
import cz.mallat.uasparser.fileparser.Entry;
import cz.mallat.uasparser.fileparser.PHPFileParser;
import cz.mallat.uasparser.fileparser.Section;

/**
 * User agent parser.
 *
 * Thread-safe, however also see the {@link MultithreadedUASparser} for a faster variant.
 *
 * @author oli
 *
 */
public class UASparser {

    static final String INFO_URL = "http://user-agent-string.info";
    static final String ROBOT = "Robot";
    static final Long DEVICE_ID_OTHER = 1L;
    static final Long DEVICE_ID_DESKTOP = 2L;
    static final Long DEVICE_ID_SMARTPHONE = 3L;

    protected Map<String, RobotEntry> robotsMap;
    protected Map<Long, OsEntry> osMap;
    protected Map<Long, BrowserEntry> browserMap;
    protected Map<Long, String> browserTypeMap;
    protected Map<String, Long> browserRegMap;
    protected Map<Long, Long> browserOsMap;
    protected Map<String, Long> osRegMap;
    protected Map<Long, DeviceEntry> deviceMap;
    protected Map<String, Long> deviceRegMap;

    protected Map<Pattern, Long> compiledBrowserRegMap;
    protected Map<Pattern, Long> compiledOsRegMap;
    protected Map<Pattern, Long> compiledDeviceRegMap;

    protected UserAgentInfo unknownAgentInfo;

    /**
     * Create a new {@link UASparser} without initializing maps. Expects an updater to be
     * configured and run immediately.
     */
    public UASparser() {
    }

    /**
     * Use the given filename to load the definition file from the local filesystem
     *
     * @param localDefinitionFilename
     * @throws IOException
     */
    public UASparser(String localDefinitionFilename) throws IOException {
        loadDataFromFile(new File(localDefinitionFilename));
        unknownAgentInfo = new UserAgentInfo();
    }

    /**
     * Use the given inputstream to load the definition file from the local filesystem
     *
     * @param inputStreamToDefinitionFile
     * @throws IOException
     */
    public UASparser(InputStream inputStreamToDefinitionFile) throws IOException {
        loadDataFromFile(inputStreamToDefinitionFile);
        unknownAgentInfo = new UserAgentInfo();
    }

    /**
     * When a class inherits from this class, it probably has to override this method
     */
    @Deprecated
    protected void checkDataMaps() throws IOException {
        // empty for this base class
    }

    /**
     * Parse the given user agent string and returns a UserAgentInfo object with the related data
     *
     * @param useragent
     * @throws IOException
     *             may happen when the retrieval of the data file fails
     * @return
     */
    public UserAgentInfo parse(String useragent) throws IOException {
        if (useragent == null) {
            return unknownAgentInfo;
        }

        UserAgentInfo uaInfo = new UserAgentInfo();
        useragent = useragent.trim();

        // check that the data maps are up-to-date (deprecated)
        // checkDataMaps(); // DISABLED - upstream db is no longer free and updates are impossible

        // first check if it's a robot
        if (processRobot(useragent, uaInfo)) {
            return uaInfo;
        }

        // it's not a robot, so search for a browser on the browser regex patterns
        processBrowserRegex(useragent, uaInfo);
        if (!uaInfo.hasOsInfo()) {
            // search the OS regex patterns for the used OS
            processOsRegex(useragent, uaInfo);
        }

        // search the device regex patterns to set the according device
        processDeviceRegex(useragent, uaInfo);
        if (!uaInfo.hasDeviceInfo()) {
            guessDeviceType(uaInfo);
        }

        return uaInfo;
    }

    /**
     * Determine device type based on UA type field
     * @param uaInfo
     */
    protected void guessDeviceType(UserAgentInfo uaInfo) {
        if (compiledDeviceRegMap == null || deviceMap == null) {
            return;
        }

        String type = uaInfo.getType();
        if (type == null || type.isEmpty()) {
            return;
        }

        if (type.equals("Other") || type.equals("Library") || type.equals("Useragent Anonymizer")) {
            uaInfo.setDeviceEntry(deviceMap.get(DEVICE_ID_OTHER));
        } else if (type.equals("Mobile Browser") || type.equals("Wap Browser")) {
            uaInfo.setDeviceEntry(deviceMap.get(DEVICE_ID_SMARTPHONE));
        } else {
            uaInfo.setDeviceEntry(deviceMap.get(DEVICE_ID_DESKTOP));
        }
    }

    /**
     * Parse the given user agent string and returns a UserAgentInfo object
     * with only the related Browser data set.
     *
     * @param useragent
     * @return {@link UserAgentInfo}
     */
    public UserAgentInfo parseBrowserOnly(String useragent) {
        if (useragent == null) {
            return unknownAgentInfo;
        }

        UserAgentInfo uaInfo = new UserAgentInfo();
        processBrowserRegex(useragent, uaInfo);
        return uaInfo;
    }

    /**
     * Precompile all regular regexes
     */
    protected void preCompileRegExes() {
        preCompileBrowserRegMap();
        preCompileOsRegMap();
        preCompileDeviceRegMap();
    }

    /**
     * Precompile browser regexes
     */
    protected void preCompileBrowserRegMap() {
        LinkedHashMap<Pattern, Long> compiledBrowserRegMap = new LinkedHashMap<Pattern, Long>(browserRegMap.size());
        for (Map.Entry<String, Long> entry : browserRegMap.entrySet()) {
            Pattern pattern = new Pattern(entry.getKey(), Pattern.IGNORE_CASE | Pattern.DOTALL);
            compiledBrowserRegMap.put(pattern, entry.getValue());
        }
        this.compiledBrowserRegMap = compiledBrowserRegMap;
    }

    /**
     * Precompile OS regexes
     */
    protected void preCompileOsRegMap() {
        LinkedHashMap<Pattern, Long> compiledOsRegMap = new LinkedHashMap<Pattern, Long>(osRegMap.size());
        for (Map.Entry<String, Long> entry : osRegMap.entrySet()) {
            Pattern pattern = new Pattern(entry.getKey(), Pattern.IGNORE_CASE | Pattern.DOTALL);
            compiledOsRegMap.put(pattern, entry.getValue());
        }
        this.compiledOsRegMap = compiledOsRegMap;
    }

    /**
     * Precompile device regexes
     */
    protected void preCompileDeviceRegMap() {
        if (deviceRegMap != null) {
	    	LinkedHashMap<Pattern, Long> compiledDeviceRegMap = new LinkedHashMap<Pattern, Long>(deviceRegMap.size());
	        for (Map.Entry<String, Long> entry : deviceRegMap.entrySet()) {
	            Pattern pattern = new Pattern(entry.getKey(), Pattern.IGNORE_CASE | Pattern.DOTALL);
	            compiledDeviceRegMap.put(pattern, entry.getValue());
	        }
	        this.compiledDeviceRegMap = compiledDeviceRegMap;
        }
    }

    /**
     * Checks if the User Agent matches that of a known Robot (crawler or other automated agent)
     *
     * @param useragent
     * @param uaInfo
     */
    protected boolean processRobot(String useragent, UserAgentInfo uaInfo) {
        // Robots UAs must match *exactly*, hence we use a simple hash lookup and not a regex match
        if (!robotsMap.containsKey(useragent)) {
            return false;
        }

        uaInfo.setType(ROBOT);
        RobotEntry robotEntry = robotsMap.get(useragent);
        uaInfo.setRobotEntry(robotEntry);
        if (robotEntry.getOsId() != null) {
            uaInfo.setOsEntry(osMap.get(robotEntry.getOsId()));
        }

        if (compiledDeviceRegMap != null && deviceMap != null) {
            // Set device to 'other'
            uaInfo.setDeviceEntry(deviceMap.get(DEVICE_ID_OTHER));
        }
        return true;
    }

    /**
     * Searchs in the browser regex table. if found a match copies the browser data and if possible os data
     *
     * @param useragent
     * @param uaInfo
     */
    protected void processBrowserRegex(String useragent, UserAgentInfo uaInfo) {
        for (Map.Entry<Pattern, Long> entry : compiledBrowserRegMap.entrySet()) {
            Matcher matcher = entry.getKey().matcher(useragent);
            if (matcher.find()) {
                Long idBrowser = entry.getValue();
                BrowserEntry be = browserMap.get(idBrowser);
                if (be != null) {
                    uaInfo.setType(browserTypeMap.get(be.getType()));;
                    if (matcher.groupCount() > 1) {
                        uaInfo.setBrowserVersionInfo(matcher.group(1));
                    }
                    uaInfo.setBrowserEntry(be);
                }
                // check if this browser has exactly one OS mapped
                Long idOs = browserOsMap.get(idBrowser);
                if (idOs != null) {
                    uaInfo.setOsEntry(osMap.get(idOs));
                }
                return;
            }
        }
    }

    /**
     * Searches in the os regex table. if found a match copies the os data
     *
     * @param useragent
     * @param uaInfo
     */
    protected void processOsRegex(String useragent, UserAgentInfo uaInfo) {
        for (Map.Entry<Pattern, Long> entry : compiledOsRegMap.entrySet()) {
            Matcher matcher = entry.getKey().matcher(useragent);
            if (matcher.find()) {
                uaInfo.setOsEntry(osMap.get(entry.getValue()));
                return;
            }
        }
    }

    /**
     * Searches in the devices regex table. if found a match copies the device data
     *
     * @param useragent
     * @param uaInfo
     */
    protected void processDeviceRegex(String useragent, UserAgentInfo uaInfo) {
        if (compiledDeviceRegMap != null && deviceMap != null) {
	    	for (Map.Entry<Pattern, Long> entry : compiledDeviceRegMap.entrySet()) {
	            Matcher matcher = entry.getKey().matcher(useragent);
	            if (matcher.find()) {
	                uaInfo.setDeviceEntry(deviceMap.get(entry.getValue()));
	                return;
	            }
	        }
        }
    }

    /**
     * loads the data file and creates all internal data structures
     *
     * @param definitionFile
     * @throws IOException
     */
    protected void loadDataFromFile(File definitionFile) throws IOException {
        PHPFileParser fp = new PHPFileParser(definitionFile);
        createInternalDataStructure(fp.getSections());
    }

    /**
     * loads the data file and creates all internal data structs
     *
     * @param is
     * @throws IOException
     */
    protected void loadDataFromFile(InputStream is) throws IOException {
        PHPFileParser fp = new PHPFileParser(is);
        createInternalDataStructure(fp.getSections());
    }

    /**
     * Creates the internal data structures from the sectionList
     *
     * @param sectionList
     */
    protected void createInternalDataStructure(List<Section> sectionList) {
        for (Section sec : sectionList) {
            if ("robots".equals(sec.getName())) {
                Map<String, RobotEntry> robotsMapTmp = new HashMap<String, RobotEntry>();
                for (Entry en : sec.getEntries()) {
                    RobotEntry re = new RobotEntry(en.getData());
                    robotsMapTmp.put(re.getUserAgentString(), re);
                }
                robotsMap = robotsMapTmp;
            } else if ("os".equals(sec.getName())) {
                Map<Long, OsEntry> osMapTmp = new HashMap<Long, OsEntry>();
                for (Entry en : sec.getEntries()) {
                    OsEntry oe = new OsEntry(en.getData());
                    osMapTmp.put(Long.parseLong(en.getKey()), oe);
                }
                osMap = osMapTmp;
            } else if ("browser".equals(sec.getName())) {
                Map<Long, BrowserEntry> browserMapTmp = new HashMap<Long, BrowserEntry>();
                for (Entry en : sec.getEntries()) {
                    BrowserEntry be = new BrowserEntry(en.getData());
                    browserMapTmp.put(Long.parseLong(en.getKey()), be);
                }
                browserMap = browserMapTmp;
            } else if ("browser_type".equals(sec.getName())) {
                Map<Long, String> browserTypeMapTmp = new HashMap<Long, String>();
                for (Entry en : sec.getEntries()) {
                    browserTypeMapTmp.put(Long.parseLong(en.getKey()), en.getData().iterator().next());
                }
                browserTypeMap = browserTypeMapTmp;
            } else if ("browser_reg".equals(sec.getName())) {
                Map<String, Long> browserRegMapTmp = new LinkedHashMap<String, Long>();
                for (Entry en : sec.getEntries()) {
                    Iterator<String> it = en.getData().iterator();
                    browserRegMapTmp.put(convertPerlToJavaRegex(it.next()), Long.parseLong(it.next()));
                }
                browserRegMap = browserRegMapTmp;
            } else if ("browser_os".equals(sec.getName())) {
                Map<Long, Long> browserOsMapTmp = new HashMap<Long, Long>();
                for (Entry en : sec.getEntries()) {
                    browserOsMapTmp.put(Long.parseLong(en.getKey()), Long.parseLong(en.getData().iterator().next()));
                }
                browserOsMap = browserOsMapTmp;
            } else if ("os_reg".equals(sec.getName())) {
                Map<String, Long> osRegMapTmp = new LinkedHashMap<String, Long>();
                for (Entry en : sec.getEntries()) {
                    Iterator<String> it = en.getData().iterator();
                    osRegMapTmp.put(convertPerlToJavaRegex(it.next()), Long.parseLong(it.next()));
                }
                osRegMap = osRegMapTmp;
            } else if ("device".equals(sec.getName())) {
            	Map<Long, DeviceEntry> deviceMapTmp = new HashMap<Long, DeviceEntry>();
                for (Entry en : sec.getEntries()) {
                	DeviceEntry de = new DeviceEntry(en.getData());
                	deviceMapTmp.put(Long.parseLong(en.getKey()), de);
                }
                deviceMap = deviceMapTmp;
            } else if ("device_reg".equals(sec.getName())) {
                Map<String, Long> deviceRegMapTmp = new LinkedHashMap<String, Long>();
                for (Entry en : sec.getEntries()) {
                    Iterator<String> it = en.getData().iterator();
                    deviceRegMapTmp.put(convertPerlToJavaRegex(it.next()), Long.parseLong(it.next()));
                }
                deviceRegMap = deviceRegMapTmp;
            }
        }
        preCompileRegExes();
    }

    /**
     * Converts a PERL style regex into the Java style. That means in removes the leading and the last / and removes the modifiers
     *
     * @param regex
     * @return
     */
    protected String convertPerlToJavaRegex(String regex) {
        regex = regex.substring(1);
        int lastIndex = regex.lastIndexOf('/');
        regex = regex.substring(0, lastIndex);
        return regex;
    }

}
