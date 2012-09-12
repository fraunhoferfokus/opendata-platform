package de.fhg.fokus.odp.portal.datasets.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PropsUtil;

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;

/**
 * This class retrieves categories from CKAN and stores them. Afterwards, a
 * SearchContainer will be created and returned.
 * 
 * @author pla
 * 
 */
public class GroupCreator {
	/** the Logger. */
	private static final Logger LOGGER = Logger.getLogger(GroupCreator.class);
	/** The ckan gw. */
	private final CKANGateway ckanGW;
	/** a String representing the begin of a HTML img tag. */
	private static final String IMG_TAG_BEGIN = "<img src=\"";
	/** a String representing the end of a HTML img tag. */
	private static final String IMG_TAG_END = ".png\" class=\"test\"/>";

	/**
	 * Constructor initializes our {@link CKANGateway}.
	 */
	public GroupCreator() {
		ckanGW = new CKANGateway(PropsUtil.get("cKANurl") + "/",
				PropsUtil.get("authenticationKey"));
	}

	/**
	 * This method calls retrieveCategories and creates the requested
	 * SearchContainer.
	 * 
	 * @param request
	 *            the RenderRequest
	 * @param response
	 *            the RenderResponse
	 * @see #retrieveCategories(RenderRequest, RenderResponse)
	 * @see #createSearchContainer(javax.portlet.PortletRequest, HashMap,
	 *      RenderResponse)
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void createSearchContainerForGroups(RenderRequest request,
			RenderResponse response) {
		if (request.getPortletSession().getAttribute("groupsContainer") == null) {
			retrieveCategories(request);
			HashMap<String, Object> groupsData = (HashMap<String, Object>) request
					.getPortletSession().getAttribute("groupsData");
			SearchContainer<String> groupSearchContainer = createSearchContainer(
					request, groupsData, response);
			request.getPortletSession().setAttribute("groupsContainer",
					groupSearchContainer);
			LOGGER.info("groupsContainer created and stored in session");
		}
	}

	/**
	 * This method calls the CKANGateway and retrieves all CKAN categories.
	 * 
	 * @param request
	 *            the RenderRequest
	 */
	private void retrieveCategories(RenderRequest request) {
		HashMap<String, Object> groupsData = ckanGW.getGroupsData();
		request.getPortletSession().setAttribute("groupsData", groupsData);
	}

	/**
	 * This method creates the SearchContainer used to display the categories in
	 * every view. This method iterates over every group and displays the
	 * following information: Icon, Title and number of packages in this group.
	 * 
	 * @param request
	 *            the PortletRequest
	 * @param groupsData
	 *            The information about the groups from CKAN
	 * @param renderResponse
	 *            the RenderResponse
	 * @return SearchContainer with categories from CKAN
	 */
	private SearchContainer<String> createSearchContainer(
			PortletRequest request, HashMap<String, Object> groupsData,
			RenderResponse renderResponse) {

		PortletURL portletURL = renderResponse.createRenderURL();
		List<String> headerNames = new ArrayList<String>();

		// create search container, used to display table
		SearchContainer<String> searchContainer = new SearchContainer<String>(
				request, null, null, SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(request.getLocale(),
						"oc-datasets_no-categories"));
		searchContainer.setTotal(groupsData.size());

		// fill table
		List<ResultRow> resultRows = searchContainer.getResultRows();
		int end = searchContainer.getEnd();
		if (groupsData.size() < searchContainer.getEnd()) {
			end = groupsData.size();
		}

		Object[] groups = groupsData.values().toArray();
		// StringBuilder used for creation of <img tag> String
		StringBuilder builder = new StringBuilder();

		for (int i = searchContainer.getStart(); i < end; i++) {

			JSONObject groupDetails = (JSONObject) groups[i];
			// retrieve packages and save number of packages
			JSONArray packages = (JSONArray) groupDetails.get("packages");
			String numberOfPackages = String.valueOf(packages.size());
			String groupName = (String) groupDetails.get("name");

			ResultRow row = new ResultRow(groupName, groupName, i);

			// create PortletURL for search with correct package name
			PortletURL detailURL = renderResponse.createActionURL();
			detailURL.setParameter("searchQuery", "groups=" + groupName);
			detailURL.setParameter("action", "packageSearchAction");

			String contextPath = request.getContextPath();

			builder.append(IMG_TAG_BEGIN);
			builder.append(contextPath);
			builder.append("/images/green/");
			builder.append(groupName);
			builder.append(IMG_TAG_END);

			// adds image text to row
			row.addText(builder.toString());

			builder.setLength(0);

			builder.append((String) groupDetails.get("title"));
			builder.append(" (");
			builder.append(numberOfPackages);
			builder.append(")");
			// add title and number of packages to ResultRow
			row.addText(builder.toString(), detailURL);
			builder.setLength(0);

			resultRows.add(row);
		}
		return searchContainer;
	}
}