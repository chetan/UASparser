package cz.mallat.uasparser;

import java.util.Iterator;
import java.util.List;

/**
 * JavaBean that holds the data from the [os] section in the data file
 * 
 * @author oli
 * 
 */
class OsEntry {

	private String family;
	private String name;
	private String url;
	private String company;
	private String companyUrl;
	private String ico;

	public OsEntry(List<String> data) {
		Iterator<String> it = data.iterator();
		this.family = it.next();
		this.name = it.next();
		this.url = it.next();
		this.company = it.next();
		this.companyUrl = it.next();
		this.ico = it.next();
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getName() {
		return name;
	}

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

}