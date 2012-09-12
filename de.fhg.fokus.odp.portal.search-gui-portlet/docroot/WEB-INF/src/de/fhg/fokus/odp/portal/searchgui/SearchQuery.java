package de.fhg.fokus.odp.portal.searchgui;

import java.io.Serializable;

public class SearchQuery implements Serializable {

	private static final long serialVersionUID = 2115917927844981650L;
	private String query;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
