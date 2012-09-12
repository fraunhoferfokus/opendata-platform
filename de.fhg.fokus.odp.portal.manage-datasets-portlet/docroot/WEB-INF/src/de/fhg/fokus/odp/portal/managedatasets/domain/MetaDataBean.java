package de.fhg.fokus.odp.portal.managedatasets.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class MetaDataBean.
 */
public class MetaDataBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The title. */
	private String title = "";
	
	/** The name. */
	private String name = "";
	
	/** The author. */
	private String author = "";
	
	/** The author_email. */
	private String author_email = "";
	
	/** The url. */
	private String url = "";
	
	/** The notes. */
	private String notes = "";
	
	/** The license_id. */
	private String license_id = "";
	
	/** The date_released. */
	private Date date_released;
	
	/** The ckan id. */
	private String ckanId = "";
	
	/** The groups. */
	private List<String> groups = new ArrayList<String>();
	
	/** The tags. */
	private String tags;
	
	/** The version. */
	private String version = "";
	
	/** The temporal_coverage_from. */
	private String temporal_coverage_from;
	
	/** The temporal_coverage_to. */
	private String temporal_coverage_to;
	
	/** The temporal_granularity. */
	private String temporal_granularity = "";
	
	/** The geographical_coverage. */
	private String geographical_coverage = "";
	
	/** The geographical_granularity. */
	private String geographical_granularity = "";
	
	/** The others. */
	private String others = "";
	
	/** The resources. */
	private List<Resource> resources = new ArrayList<Resource>();
	
	/** The maintainer. */
	private String maintainer = "";
	
	/** The maintainer_email. */
	private String maintainer_email = "";
	
	/** The metadata_created. */
	private Date metadata_created;
	
	/** The metadata_modified. */
	private Date metadata_modified;
	
	/** The date_updated. */
	private Date date_updated;

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the author.
	 *
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the author.
	 *
	 * @param author the new author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Gets the author_email.
	 *
	 * @return the author_email
	 */
	public String getAuthor_email() {
		return author_email;
	}

	/**
	 * Sets the author_email.
	 *
	 * @param author_email the new author_email
	 */
	public void setAuthor_email(String author_email) {
		this.author_email = author_email;
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

	/**
	 * Gets the notes.
	 *
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Sets the notes.
	 *
	 * @param notes the new notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Gets the license_id.
	 *
	 * @return the license_id
	 */
	public String getLicense_id() {
		return license_id;
	}

	/**
	 * Sets the license_id.
	 *
	 * @param license_id the new license_id
	 */
	public void setLicense_id(String license_id) {
		this.license_id = license_id;
	}

	/**
	 * Gets the date_released.
	 *
	 * @return the date_released
	 */
	public Date getDate_released() {
		return date_released;
	}

	/**
	 * Sets the date_released.
	 *
	 * @param date_released the new date_released
	 */
	public void setDate_released(Date date_released) {
		this.date_released = date_released;
	}

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * Sets the tags.
	 *
	 * @param tags the new tags
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the temporal_coverage_from.
	 *
	 * @return the temporal_coverage_from
	 */
	public String getTemporal_coverage_from() {
		return temporal_coverage_from;
	}

	/**
	 * Sets the temporal_coverage_from.
	 *
	 * @param temporal_coverage_from the new temporal_coverage_from
	 */
	public void setTemporal_coverage_from(String temporal_coverage_from) {
		this.temporal_coverage_from = temporal_coverage_from;
	}

	/**
	 * Gets the temporal_coverage_to.
	 *
	 * @return the temporal_coverage_to
	 */
	public String getTemporal_coverage_to() {
		return temporal_coverage_to;
	}

	/**
	 * Sets the temporal_coverage_to.
	 *
	 * @param temporal_coverage_to the new temporal_coverage_to
	 */
	public void setTemporal_coverage_to(String temporal_coverage_to) {
		this.temporal_coverage_to = temporal_coverage_to;
	}

	/**
	 * Gets the temporal_granularity.
	 *
	 * @return the temporal_granularity
	 */
	public String getTemporal_granularity() {
		return temporal_granularity;
	}

	/**
	 * Sets the temporal_granularity.
	 *
	 * @param temporal_granularity the new temporal_granularity
	 */
	public void setTemporal_granularity(String temporal_granularity) {
		this.temporal_granularity = temporal_granularity;
	}

	/**
	 * Gets the geographical_coverage.
	 *
	 * @return the geographical_coverage
	 */
	public String getGeographical_coverage() {
		return geographical_coverage;
	}

	/**
	 * Sets the geographical_coverage.
	 *
	 * @param geographical_coverage the new geographical_coverage
	 */
	public void setGeographical_coverage(String geographical_coverage) {
		this.geographical_coverage = geographical_coverage;
	}

	/**
	 * Gets the geographical_granularity.
	 *
	 * @return the geographical_granularity
	 */
	public String getGeographical_granularity() {
		return geographical_granularity;
	}

	/**
	 * Sets the geographical_granularity.
	 *
	 * @param geographical_granularity the new geographical_granularity
	 */
	public void setGeographical_granularity(String geographical_granularity) {
		this.geographical_granularity = geographical_granularity;
	}

	/**
	 * Gets the others.
	 *
	 * @return the others
	 */
	public String getOthers() {
		return others;
	}

	/**
	 * Sets the others.
	 *
	 * @param others the new others
	 */
	public void setOthers(String others) {
		this.others = others;
	}

	/**
	 * Gets the maintainer.
	 *
	 * @return the maintainer
	 */
	public String getMaintainer() {
		return maintainer;
	}

	/**
	 * Sets the maintainer.
	 *
	 * @param maintainer the new maintainer
	 */
	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;
	}

	/**
	 * Gets the maintainer_email.
	 *
	 * @return the maintainer_email
	 */
	public String getMaintainer_email() {
		return maintainer_email;
	}

	/**
	 * Sets the maintainer_email.
	 *
	 * @param maintainer_email the new maintainer_email
	 */
	public void setMaintainer_email(String maintainer_email) {
		this.maintainer_email = maintainer_email;
	}

	/**
	 * Gets the metadata_created.
	 *
	 * @return the metadata_created
	 */
	public Date getMetadata_created() {
		return metadata_created;
	}

	/**
	 * Sets the metadata_created.
	 *
	 * @param metadata_created the new metadata_created
	 */
	public void setMetadata_created(Date metadata_created) {
		this.metadata_created = metadata_created;
	}

	/**
	 * Gets the metadata_modified.
	 *
	 * @return the metadata_modified
	 */
	public Date getMetadata_modified() {
		return metadata_modified;
	}

	/**
	 * Sets the metadata_modified.
	 *
	 * @param metadata_modified the new metadata_modified
	 */
	public void setMetadata_modified(Date metadata_modified) {
		this.metadata_modified = metadata_modified;
	}

	/**
	 * Gets the date_updated.
	 *
	 * @return the date_updated
	 */
	public Date getDate_updated() {
		return date_updated;
	}

	/**
	 * Sets the date_updated.
	 *
	 * @param date_updated the new date_updated
	 */
	public void setDate_updated(Date date_updated) {
		this.date_updated = date_updated;
	}

	/**
	 * Gets the groups.
	 *
	 * @return the groups
	 */
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * Sets the groups.
	 *
	 * @param groups the new groups
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * Gets the ckan id.
	 *
	 * @return the ckan id
	 */
	public String getCkanId() {
		return ckanId;
	}

	/**
	 * Sets the ckan id.
	 *
	 * @param ckanId the new ckan id
	 */
	public void setCkanId(String ckanId) {
		this.ckanId = ckanId;
	}

	/**
	 * Gets the resources.
	 *
	 * @return the resources
	 */
	public List<Resource> getResources() {
		return resources;
	}

	/**
	 * Sets the resources.
	 *
	 * @param resources the new resources
	 */
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

}
