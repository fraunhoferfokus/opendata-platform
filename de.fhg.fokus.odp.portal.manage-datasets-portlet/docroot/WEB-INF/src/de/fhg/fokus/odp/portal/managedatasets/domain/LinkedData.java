package de.fhg.fokus.odp.portal.managedatasets.domain;

// TODO: Auto-generated Javadoc
/**
 * The Class LinkedData.
 */
public class LinkedData {
	
	/** The language. */
	private String language;
	
	/** The FORMAT. */
	private final String FORMAT = "RDF";
	
	/** The description. */
	private String description;
	
	/** The package id. */
	private String packageId;
	
	/** The valid file. */
	private boolean validFile = false;
	
	/** The url. */
	private String url;

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language.
	 *
	 * @param language the new language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Gets the format.
	 *
	 * @return the format
	 */
	public String getFormat() {
		return FORMAT;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the package id.
	 *
	 * @return the package id
	 */
	public String getPackageId() {
		return packageId;
	}

	/**
	 * Sets the package id.
	 *
	 * @param packageId the new package id
	 */
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	/**
	 * Checks if is valid file.
	 *
	 * @return true, if is valid file
	 */
	public boolean isValidFile() {
		return validFile;
	}

	/**
	 * Sets the valid file.
	 *
	 * @param validFile the new valid file
	 */
	public void setValidFile(boolean validFile) {
		this.validFile = validFile;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
