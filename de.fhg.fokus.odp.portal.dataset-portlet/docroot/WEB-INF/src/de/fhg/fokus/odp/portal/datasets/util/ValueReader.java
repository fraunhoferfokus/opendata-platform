package de.fhg.fokus.odp.portal.datasets.util;

import java.util.HashMap;

import javax.portlet.RenderRequest;

import org.json.simple.JSONArray;

import com.liferay.portal.kernel.util.PropsUtil;

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;

/**
 * This class retrieves all needed values for our JSP files.
 * 
 * @author pla
 * 
 */
public class ValueReader {
	/** The ckan gw. */
	private final CKANGateway ckanGW;

	/**
	 * Constructor initializes our {@link CKANGateway}.
	 */
	public ValueReader() {
		ckanGW = new CKANGateway(PropsUtil.get("cKANurl") + "/",
				PropsUtil.get("authenticationKey"));
	}

	/**
	 * This method calls all other methods.
	 * 
	 * Information we retrieve: tags, most popular tags, latest datasets
	 * 
	 * @param request
	 *            the {@link RenderRequest}
	 * 
	 * @see org.opencities.berlin.opendata.ckan.gateway.CKANGateway
	 * @see #addLatestPackagesToRequest(RenderRequest)
	 * @see #addPopularTagsDataToRequest(RenderRequest)
	 * @see #addTagsDataToRequest(RenderRequest)
	 */
	public void retrieveValues(RenderRequest request) {

		// store information
		addPopularTagsDataToRequest(request);
		addLatestPackagesToRequest(request);
	}

	/**
	 * This method retrieves once per session popular tags data from ckan and
	 * stores them in our {@link PortletSession}. This method calls the
	 * CKANGateway.
	 * 
	 * @param request
	 *            the {@link RenderRequest}
	 */
	public void addPopularTagsDataToRequest(RenderRequest request) {
		if (request.getPortletSession().getAttribute("popularTagsData") == null) {
			JSONArray popularTagsData = ckanGW.getMostPopularTags(Integer
					.parseInt(PropsUtil.get("ckan.count.mostpopulartags")));
			request.getPortletSession().setAttribute("popularTagsData",
					popularTagsData);
		}
	}

	/**
	 * This method retrieves latest packages data from ckan and stores them in
	 * our {@link RenderRequest}. These information will be saved as an
	 * attribute in our request object.
	 * 
	 * @param request
	 *            the {@link RenderRequest}
	 */
	private void addLatestPackagesToRequest(RenderRequest request) {
		HashMap latestPackagesData = ckanGW.getLatestDatasets(Integer
				.parseInt(PropsUtil.get("ckan.count.latestdatasets")));
		request.setAttribute("latestPackagesData", latestPackagesData);
	}
}
