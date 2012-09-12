package de.fhg.fokus.odp.portal.search;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.liferay.portal.kernel.util.PropsUtil;

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;

public class Search {

	private static final Logger log = Logger.getLogger(Search.class);

	private static final String CKAN_AUTH_KEY_KEY = "authenticationKey";
	private static final String ALL_FIELDS_1 = "all_fields=1";
	private static final String CKANURL_KEY = "cKANurl";
	private static final String LIMIT = "limit=10000";
	private static final String SLASH = "/";

	private CKANGateway getCkanGw() {
		return new CKANGateway(PropsUtil.get(CKANURL_KEY) + SLASH,
				PropsUtil.get(CKAN_AUTH_KEY_KEY));
	}

	/**
	 * Search for package details via CKANGateway.
	 * 
	 * @param packageDetailsId
	 * @return packageDetails as <code>JSONObject</code>
	 */
	JSONObject getPackageDetails(String packageDetailsId) {
		CKANGateway ckanGW = this.getCkanGw();
		JSONObject packageDetails = (JSONObject) ckanGW
				.getDataSetDetails(packageDetailsId);
		log.debug("getPackageDetails( " + packageDetailsId + " ) -> "
				+ packageDetails);
		return packageDetails;
	}

	/**
	 * Do search for a package by package search query
	 * 
	 * @param packageSearchQuery
	 *            <code>String</code> of parameters for the package search.
	 * @return <code>JSONObject</code> containing the search results or
	 *         <code>null</code> if something has gone wrong.
	 */
	JSONObject doPackageSearch(String packageSearchQuery) {
		String params = addParam(packageSearchQuery, ALL_FIELDS_1);
		params = addParam(params, LIMIT);
		CKANGateway ckanGW = this.getCkanGw();
		log.debug("ckanGW.getPackageSearchResults( \"" + params + "\" )");
		return (JSONObject) ckanGW.getDataSetSearchResults(params);
	}

	private String addParam(String urlSoFar, String paramString) {
		return urlSoFar + "&" + paramString;
	}

}
