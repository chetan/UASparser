package cz.mallat.uasparser;

import java.util.Iterator;
import java.util.List;

/**
 * JavaBean that holds the data from the [browser] section in the data file
 *
 * @author oli
 *
 */
class BrowserEntry {

	private Long type;
	private String family;
	@Deprecated
	private String name;
	private String url;
	private String company;
	private String companyUrl;
	private String ico;
	private String infoUrl;

	public BrowserEntry(List<String> data) {
		Iterator<String> it = data.iterator();
		this.type = Long.parseLong(it.next());
		this.family = it.next();
		this.url = it.next();
		this.company = it.next();
		this.companyUrl = it.next();
		this.ico = it.next();
		this.infoUrl = it.next();
		// this.name stays empty, will be filled with family + version
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	/**
	 * This field is never used
	 * @return
	 */
	@Deprecated
	public String getName() {
		return name;
	}

	/**
	 * This field is never used
	 * @param name
	 */
	@Deprecated
	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompanyUrl() {
		return companyUrl;
	}

	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
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
        return  "Browser: \n" +
                "  Family: " + family + "\n" +
                "  Type: " + type + "\n" +
                "  URL: " + url + "\n" +
                "  Company: " + company + "\n" +
                "  Company URL: " + companyUrl + "\n" +
                "  ICO: " + ico + "\n" +
                "  Info URL: " + infoUrl;
    }

}