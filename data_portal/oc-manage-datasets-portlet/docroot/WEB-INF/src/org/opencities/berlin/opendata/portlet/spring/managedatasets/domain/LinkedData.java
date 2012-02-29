package org.opencities.berlin.opendata.portlet.spring.managedatasets.domain;

public class LinkedData {
	private String language;
	private final String FORMAT = "RDF";
	private String description;
	private String packageId;
	private boolean validFile = false;
	private String url;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getFormat() {
		return FORMAT;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public boolean isValidFile() {
		return validFile;
	}

	public void setValidFile(boolean validFile) {
		this.validFile = validFile;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
