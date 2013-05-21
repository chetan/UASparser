package cz.mallat.uasparser;

/**
 * Encapsulates all information pertaining to a User Agent. Returned by calling
 * {@link UASparser#parse(String)}.
 *
 * <p>Note that all information comes from the database provided at
 * <a href="http://user-agent-string.info/">user-agent-string.info</a>. If you have problems with
 * or questions about the data returned, please contact the maintainer of the database directly.
 *
 * @author oli
 *
 */
public class UserAgentInfo {

    private static final String UNKNOWN = "unknown";

	private String type;
    private String browserVersionInfo;

	private RobotEntry robotEntry;
	private BrowserEntry browserEntry;
	private OsEntry osEntry;

	public UserAgentInfo() {
        this.type = UNKNOWN;
	}

	/**
	 * Returns true if this represents a Robot
	 * @return
	 */
	public boolean isRobot() {
	    return browserEntry == null && robotEntry != null;
	}

	/**
	 * Retrieve the type of UA. Can be one of the following:
	 *
	 * <ul>
	 *     <li>"Browser"
	 *     <li>"Offline Browser"
	 *     <li>"Mobile Browser"
	 *     <li>"Email client"
	 *     <li>"Library"
	 *     <li>"Wap Browser"
	 *     <li>"Validator"
	 *     <li>"Feed Reader"
	 *     <li>"Multimedia Player"
	 *     <li>"Other"
	 *     <li>"Useragent Anonymizer"
	 *     <li>"Robot"
	 * </ul>
	 *
	 * @return {@link String} type
	 */
	public String getType() {
	    if (type == null) {
	        return UNKNOWN;
	    }
		return type;
	}

	public void setType(String typ) {
		this.type = typ;
	}

    /**
     * Retrieve the product family; i.e., given the UA:
     *
     * <p>"Mozilla/5.0 (Windows; U; Windows NT 6.1; pt-BR; rv:1.9.1.1) Gecko/20090715 Firefox/3.5.1"</p>
     *
     * <p>it would return "Firefox"</p>
     *
     * @return {@link String} URL
     */
    public String getUaFamily() {
        if (browserEntry != null) {
            return browserEntry.getFamily();
        }
        if (robotEntry != null) {
            return robotEntry.getFamily();
        }
        return UNKNOWN;
    }

    /**
     * Retrieve the UA name and version; i.e., given the UA:
     *
     * <p>"Mozilla/5.0 (Windows; U; Windows NT 6.1; pt-BR; rv:1.9.1.1) Gecko/20090715 Firefox/3.5.1"</p>
     *
     * <p>it would return "Firefox 3.5.1"</p>
     *
     * @return {@link String} UA name
     */
    public String getUaName() {
        if (browserEntry != null) {
            if (browserVersionInfo != null && !browserVersionInfo.isEmpty()) {
                return getUaFamily() + " " + browserVersionInfo;
            }
            return getUaFamily();
        }
        if (robotEntry != null) {
            return robotEntry.getName();
        }
        return UNKNOWN;
    }

    /**
     * Retrieve the URL of the UA's product page; i.e., given the UA:
     *
     * <p>"Mozilla/5.0 (Windows; U; Windows NT 6.1; pt-BR; rv:1.9.1.1) Gecko/20090715 Firefox/3.5.1"</p>
     *
     * <p>it would return "http://www.firefox.com/"</p>
     *
     * @return {@link String} URL
     */
	public String getUaUrl() {
        if (browserEntry != null) {
            return browserEntry.getUrl();
        }
        if (robotEntry != null) {
            return robotEntry.getUrl();
        }
        return UNKNOWN;
	}

    /**
     * Retrieve the URL path for the given UA on user-agent-string.info; i.e., given the UA:
     *
     * <p>"Mozilla/5.0 (Windows; U; Windows NT 6.1; pt-BR; rv:1.9.1.1) Gecko/20090715 Firefox/3.5.1"</p>
     *
     * <p>it would return "/list-of-ua/browser-detail?browser=Firefox" which could then be accessed</p>
     * <p>at <a href="http://user-agent-string.info/list-of-ua/browser-detail?browser=Firefox">http://user-agent-string.info/list-of-ua/browser-detail?browser=Firefox</a></p>
     *
     * @return {@link String} URL path
     */
	public String getUaInfoUrl() {
	    if (browserEntry != null) {
	        return browserEntry.getInfoUrl();
	    }
	    if (robotEntry != null) {
	        return UASparser.INFO_URL + robotEntry.getInfoUrl();
	    }
	    return UNKNOWN;
	}

    /**
     * Retrieve the name of the company which developed the given UA; i.e., given the UA:
     *
     * <p>"Mozilla/5.0 (Windows; U; Windows NT 6.1; pt-BR; rv:1.9.1.1) Gecko/20090715 Firefox/3.5.1"</p>
     *
     * <p>it would return "Mozilla Foundation"</p>
     *
     * @return {@link String} URL
     */
	public String getUaCompany() {
        if (browserEntry != null) {
            return browserEntry.getCompany();
        }
        if (robotEntry != null) {
            return robotEntry.getCompany();
        }
        return UNKNOWN;
	}

    /**
     * Retrieve the URL of the company which developed the given UA; i.e., given the UA:
     *
     * <p>"Mozilla/5.0 (Windows; U; Windows NT 6.1; pt-BR; rv:1.9.1.1) Gecko/20090715 Firefox/3.5.1"</p>
     *
     * <p>it would return "http://www.mozilla.org/"</p>
     *
     * @return {@link String} URL
     */
	public String getUaCompanyUrl() {
        if (browserEntry != null) {
            return browserEntry.getCompanyUrl();
        }
        if (robotEntry != null) {
            return robotEntry.getCompanyUrl();
        }
        return UNKNOWN;
	}

    /**
     * Retrieve the icon filename, if available; i.e., given the UA:
     *
     * <p>"Mozilla/5.0 (Windows; U; Windows NT 6.1; pt-BR; rv:1.9.1.1) Gecko/20090715 Firefox/3.5.1"</p>
     *
     * <p>it would return "firefox.png"</p>
     *
     * @return {@link String} URL
     * @see <a href="http://user-agent-string.info/download">http://user-agent-string.info/download</a>
     */
	public String getUaIcon() {
        if (browserEntry != null) {
            return browserEntry.getIco();
        }
        if (robotEntry != null) {
            return robotEntry.getIco();
        }
        return UNKNOWN;
	}

	/**
	 * Retrieve the OS family name
	 * @return
	 */
	public String getOsFamily() {
		if (osEntry != null) {
		    return osEntry.getFamily();
		}
		return UNKNOWN;
	}

	/**
	 * Retrieve the OS name
	 * @return
	 */
	public String getOsName() {
        if (osEntry != null) {
            return osEntry.getName();
        }
        return UNKNOWN;
	}

	/**
	 * Retrieve the URL to the OS vendor's product page
	 * @return
	 */
	public String getOsUrl() {
        if (osEntry != null) {
            return osEntry.getUrl();
        }
        return UNKNOWN;
	}

	/**
	 * Retrieve the name of the OS vendor
	 * @return
	 */
	public String getOsCompany() {
        if (osEntry != null) {
            return osEntry.getCompany();
        }
        return UNKNOWN;
	}

	/**
	 * Retrieve the URL to the OS vendor's homepage
	 * @return
	 */
	public String getOsCompanyUrl() {
        if (osEntry != null) {
            return osEntry.getCompanyUrl();
        }
        return UNKNOWN;
	}

	/**
	 * Retrieve the filename of the OS icon
	 * @return
	 * @see <a href="http://user-agent-string.info/download">http://user-agent-string.info/download</a>
	 */
	public String getOsIcon() {
        if (osEntry != null) {
            return osEntry.getIco();
        }
        return UNKNOWN;
	}

	/**
	 * Retrieve the UA version number; i.e., given the UA:
	 *
	 * <p>"Mozilla/5.0 (Windows; U; Windows NT 6.1; pt-BR; rv:1.9.1.1) Gecko/20090715 Firefox/3.5.1"</p>
	 *
	 * <p>it would return "3.5.1"</p>
	 *
	 * @return {@link String} version number
	 */
    public String getBrowserVersionInfo() {
        return browserVersionInfo;
    }


	// setters

    public void setBrowserEntry(BrowserEntry browserEntry) {
        this.browserEntry = browserEntry;
    }

    public void setBrowserVersionInfo(String browserVersionInfo) {
        this.browserVersionInfo = browserVersionInfo;
    }

    public void setOsEntry(OsEntry osEntry) {
        this.osEntry = osEntry;
    }

    public void setRobotEntry(RobotEntry robotEntry) {
        this.robotEntry = robotEntry;
    }

}