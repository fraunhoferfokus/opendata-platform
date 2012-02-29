package org.opencities.berlin.opendata.portlet.spring.managedatasets.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MetaDataBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String title = "";
	private String name = "";
	private String author = "";
	private String author_email = "";
	private String url = "";
	private String notes = "";
	private String license_id = "";
	private Date date_released;
	private String ckanId = "";
	private List<String> groups = new ArrayList<String>();
	private String tags;
	private String version = "";
	private Date temporal_coverage_from;
	private Date temporal_coverage_to;
	private String temporal_granularity = "";
	private String geographical_coverage = "";
	private String geographical_granularity = "";
	private String others = "";
	private List<Resource> resources = new ArrayList<Resource>();
	private String maintainer = "";
	private String maintainer_email = "";
	private Date metadata_created;
	private Date metadata_modified;
	private Date date_updated;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor_email() {
		return author_email;
	}

	public void setAuthor_email(String author_email) {
		this.author_email = author_email;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getLicense_id() {
		return license_id;
	}

	public void setLicense_id(String license_id) {
		this.license_id = license_id;
	}

	public Date getDate_released() {
		return date_released;
	}

	public void setDate_released(Date date_released) {
		this.date_released = date_released;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getTemporal_coverage_from() {
		return temporal_coverage_from;
	}

	public void setTemporal_coverage_from(Date temporal_coverage_from) {
		this.temporal_coverage_from = temporal_coverage_from;
	}

	public Date getTemporal_coverage_to() {
		return temporal_coverage_to;
	}

	public void setTemporal_coverage_to(Date temporal_coverage_to) {
		this.temporal_coverage_to = temporal_coverage_to;
	}

	public String getTemporal_granularity() {
		return temporal_granularity;
	}

	public void setTemporal_granularity(String temporal_granularity) {
		this.temporal_granularity = temporal_granularity;
	}

	public String getGeographical_coverage() {
		return geographical_coverage;
	}

	public void setGeographical_coverage(String geographical_coverage) {
		this.geographical_coverage = geographical_coverage;
	}

	public String getGeographical_granularity() {
		return geographical_granularity;
	}

	public void setGeographical_granularity(String geographical_granularity) {
		this.geographical_granularity = geographical_granularity;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

	public String getMaintainer() {
		return maintainer;
	}

	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;
	}

	public String getMaintainer_email() {
		return maintainer_email;
	}

	public void setMaintainer_email(String maintainer_email) {
		this.maintainer_email = maintainer_email;
	}

	public Date getMetadata_created() {
		return metadata_created;
	}

	public void setMetadata_created(Date metadata_created) {
		this.metadata_created = metadata_created;
	}

	public Date getMetadata_modified() {
		return metadata_modified;
	}

	public void setMetadata_modified(Date metadata_modified) {
		this.metadata_modified = metadata_modified;
	}

	public Date getDate_updated() {
		return date_updated;
	}

	public void setDate_updated(Date date_updated) {
		this.date_updated = date_updated;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public String getCkanId() {
		return ckanId;
	}

	public void setCkanId(String ckanId) {
		this.ckanId = ckanId;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

}
