package cz.mallat.uasparser;

/**
 * JavaBean that returns the data to the calling user from UAParser.parse()
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

	public boolean isRobot() {
	    return browserEntry == null && robotEntry != null;
	}

	public String getType() {
	    if (type == null) {
	        return UNKNOWN;
	    }
		return type;
	}

	public void setType(String typ) {
		this.type = typ;
	}

    public String getUaFamily() {
        if (browserEntry != null) {
            return browserEntry.getFamily();
        }
        if (robotEntry != null) {
            return robotEntry.getFamily();
        }
        return UNKNOWN;
    }

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

	public String getUaUrl() {
        if (browserEntry != null) {
            return browserEntry.getUrl();
        }
        if (robotEntry != null) {
            return robotEntry.getUrl();
        }
        return UNKNOWN;
	}

	public String getUaInfoUrl() {
	    if (browserEntry != null) {
	        return browserEntry.getInfoUrl();
	    }
	    if (robotEntry != null) {
	        return UASparser.INFO_URL + robotEntry.getInfoUrl();
	    }
	    return UNKNOWN;
	}

	public String getUaCompany() {
        if (browserEntry != null) {
            return browserEntry.getCompany();
        }
        if (robotEntry != null) {
            return robotEntry.getCompany();
        }
        return UNKNOWN;
	}

	public String getUaCompanyUrl() {
        if (browserEntry != null) {
            return browserEntry.getCompanyUrl();
        }
        if (robotEntry != null) {
            return robotEntry.getCompanyUrl();
        }
        return UNKNOWN;
	}

	public String getUaIcon() {
        if (browserEntry != null) {
            return browserEntry.getIco();
        }
        if (robotEntry != null) {
            return robotEntry.getIco();
        }
        return UNKNOWN;
	}

	public String getOsFamily() {
		if (osEntry != null) {
		    return osEntry.getFamily();
		}
		return UNKNOWN;
	}

	public String getOsName() {
        if (osEntry != null) {
            return osEntry.getName();
        }
        return UNKNOWN;
	}

	public String getOsUrl() {
        if (osEntry != null) {
            return osEntry.getUrl();
        }
        return UNKNOWN;
	}

	public String getOsCompany() {
        if (osEntry != null) {
            return osEntry.getCompany();
        }
        return UNKNOWN;
	}

	public String getOsCompanyUrl() {
        if (osEntry != null) {
            return osEntry.getCompanyUrl();
        }
        return UNKNOWN;
	}

	public String getOsIcon() {
        if (osEntry != null) {
            return osEntry.getIco();
        }
        return UNKNOWN;
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