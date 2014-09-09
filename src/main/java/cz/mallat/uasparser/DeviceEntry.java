package cz.mallat.uasparser;

import java.util.Iterator;
import java.util.List;

/**
 * Java bean that holds the data from the [device] section in the data file.
 *
 * @author Felix Siegrist, Inventage AG
 *
 */
class DeviceEntry {

	private String type;
	private String ico;
	private String infoUrl;

	public DeviceEntry(List<String> data) {
		Iterator<String> it = data.iterator();
		this.type = it.next();
		this.ico = it.next();
		this.infoUrl = it.next();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIco() {
		return ico;
	}

	public void setIco(String ico) {
		this.ico = ico;
	}

	public String getInfoUrl() {
		return infoUrl;
	}

	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}

    @Override
    public String toString() {
        return  "Device: \n" +
                "  Type: " + type + "\n" +
                "  ICO: " + ico + "\n" +
                "  Info URL: " + infoUrl;
    }

}