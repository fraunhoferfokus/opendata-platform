package de.fhg.fokus.odp.portal.searchgui;

import java.io.Serializable;

public class SearchForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String query;

	private String title;
	private String author;
	private String description;
	private String maintainer;
	private String group;
	private String license;

	private String tags;

	// CKAN extras
	private String date_released;

	private String geographical_coverage;
	private String geographical_granularity;
	private String temporal_granularity;
	private String temporal_coverage_from;
	private String temporal_coverage_to;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getDate_released() {
		return date_released;
	}

	public void setDate_released(String date_released) {
		this.date_released = date_released;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMaintainer() {
		return maintainer;
	}

	public void setMaintainer(String maintainer) {
		this.maintainer = maintainer;
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

	public String getTemporal_granularity() {
		return temporal_granularity;
	}

	public void setTemporal_granularity(String temporal_granularity) {
		this.temporal_granularity = temporal_granularity;
	}

	public String getTemporal_coverage_from() {
		return temporal_coverage_from;
	}

	public void setTemporal_coverage_from(String temporal_coverage_from) {
		this.temporal_coverage_from = temporal_coverage_from;
	}

	public String getTemporal_coverage_to() {
		return temporal_coverage_to;
	}

	public void setTemporal_coverage_to(String temporal_coverage_to) {
		this.temporal_coverage_to = temporal_coverage_to;
	}

}
