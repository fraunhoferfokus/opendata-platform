package org.opencities.berlin.opendata.portlet.jsp;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.opencities.berlin.opendata.ckan.gateway.CKANGateway;
import org.opencities.berlin.opendata.util.JSONUtil;

import com.liferay.portal.kernel.dao.search.ResultRow;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.taglib.portlet.RenderURLTag;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;

public class BrowseDatasets extends GenericPortlet {

	private Logger logger = Logger.getLogger(BrowseDatasets.class);

	private CKANGateway ckanGW = null;
	
	private String prpGroups = null;
	private String prpPackageId = null;
	private String prpExternSearchQuery = null;
	
	public void doView(RenderRequest request, RenderResponse renderResponse)
			throws PortletException, IOException {

		// Prepare request dispatcher for the portlet view
		String viewJSP = "/view.jsp";
		PortletRequestDispatcher dispatcher = getPortletContext()
				.getRequestDispatcher(viewJSP);

		// Initialize CKAN gateway
		// TODO: get from properties ... 
		ckanGW = new CKANGateway(PropsUtil.get("cKANurl")+"/",
				PropsUtil.get("authenticationKey"));

		// Build list of all groups:
		if (request.getPortletSession().getAttribute("groupsData") == null) {
			// Groups list requested from API only once per session
			HashMap<String, Object> groupsData = ckanGW.getGroupsData();
			request.getPortletSession().setAttribute("groupsData", groupsData);
		}
		
		// Build list of all tags:
		if (request.getPortletSession().getAttribute("tagsData") == null) {
			// list requested from API only once per session
			JSONArray tagsData = ckanGW.getTagsData();
			request.getPortletSession().setAttribute("tagsData", tagsData);
		}
		// Get popular tags:
		if (request.getPortletSession().getAttribute("popularTagsData") == null) {
			// list requested from API only once per session
			JSONArray popularTagsData = ckanGW.getMostPopularTags(20);
			request.getPortletSession().setAttribute("popularTagsData", popularTagsData);
		}
		// Get latest packages:
		if (request.getPortletSession().getAttribute("latestPackagesData") == null) {
			// list requested from API only once per session
			HashMap<String, HashMap<String, String>> latestPackagesData = ckanGW.getLatestDatasets(10);
			request.getPortletSession().setAttribute("latestPackagesData", latestPackagesData);
		}
		
		// Include search containers in request attributes
		request.setAttribute(
					"groupsContainer",
					getSearchContainerGroups(
							request,
							(HashMap<String, Object>) request
									.getPortletSession().getAttribute(
											"groupsData"), renderResponse));
			

		// Check whether there are public render parameters set and we are asked to perform a search
		// Get public render parameter 
		this.prpGroups = (String) request.getParameter("groups");	
		this.prpPackageId = (String) request.getParameter("pId");	
		this.prpExternSearchQuery = (String) request.getParameter("externSearchQuery");	
		if (this.prpGroups != null || this.prpExternSearchQuery != null) {
			doPackageSearch(request);
		}
		else if (this.prpPackageId != null) {
			doPackageDetails(request);
		}
			
		
		// If there are search results - include a search container in the
		// "packages" request attribute
		//JSONArray searchResults = (JSONArray) request.getPortletSession().getAttribute("searchResults");
		JSONArray searchResults = (JSONArray) request.getAttribute("searchResults");
		
		if (searchResults != null) {
			// Prepare search results list (SearchContainer):
				request.setAttribute(
						"packagesContainer",
						getSearchContainerPackages(request, searchResults,
								renderResponse));
				request.setAttribute("searchResults", searchResults);				
		}

		// If package details were requested - include any accompanying
		// resources in the "packageResources" request attribute
		JSONObject packageDetails = (JSONObject) request
				.getAttribute("packageDetails");
		
		if (packageDetails != null) {
			// Build resources list (SearchContainer):
				request.setAttribute(
						"packageResourcesContainer",
						getSearchContainerResources(request,
								(JSONArray)packageDetails.get("resources"),
								renderResponse, (String)packageDetails.get("id")));
		}

		// Done
		dispatcher.include(request, renderResponse);
	}

	@Override
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws PortletException,
			java.io.IOException {

		// Get action parameter
		String action = (String) actionRequest.getParameter("action");
		if (action == null) {
			action = (String) actionRequest
					.getParameter("javax.portlet.action");
		}
		
		if (actionRequest.getParameter("searchResults") != null) {
			Object jsonObj = JSONValue.parse(actionRequest.getParameter("searchResults"));
			actionRequest.setAttribute("searchResults", (JSONArray) jsonObj);
		}
		if (actionRequest.getParameter("searchparams") != null) {
			actionRequest.setAttribute("searchparams", actionRequest.getParameter("searchparams"));
		}
		
		// Remove existing public render parameters
		actionResponse.setRenderParameter("groups", "");
		actionResponse.setRenderParameter("pId", "");
		actionResponse.setRenderParameter("externSearchQuery", "");
			
		
		if (action.equals("packageSearchAction")) {

			// Perform search according to the portlet user's query
			doPackageSearch(actionRequest);
			
		} else if (action.equals("clearSearchAction")) {
			
			doClearSearch(actionRequest);
			
		} else if (action.equals("packageDetailsAction")) {
			
			// Get package details according to the request			
			doPackageDetails(actionRequest);

		} 

	}
	
	private void doClearSearch(PortletRequest request) {
		
		//request.getPortletSession().removeAttribute("searchparams");
		request.getPortletSession().removeAttribute("searchMode");
		//request.getPortletSession().removeAttribute("searchResults");
		
	}
	
	private void doPackageSearch(PortletRequest request) {
		
		// Get search parameters from request (input fields'/checkboxes'
		// values)
		String q = request.getParameter("searchquery");
		String title = request.getParameter("searchquery_title");
		String notes = request.getParameter("searchquery_notes");
		String author = request.getParameter("searchquery_author");
		String maintainer = request
				.getParameter("searchquery_maintainer");
		String groups = request.getParameter("searchquery_groups");
		String tags = request.getParameter("searchquery_tags");
		String openness = request
				.getParameter("searchquery_openness");
		String downloadable = request
				.getParameter("searchquery_downloadable");

		
		// Apply public render parameter if no user query present 
		if (q == null && groups == null && this.prpGroups != null) {
			groups = this.prpGroups;
		}
		if (q == null && this.prpExternSearchQuery != null) {
			q = this.prpExternSearchQuery;
		}
		
			
		
		/*
		 * if (q != null && q.equals("Suchbegriff...")) q = "";
		 */

		// Build query string of search parameters
		String searchparams = "";
		if (q != null && !q.isEmpty())
			searchparams = searchparams.concat("q=" + URLEncoder.encode(q)
					+ "&");
		if (title != null && !title.isEmpty())
			searchparams = searchparams.concat("title="
					+ URLEncoder.encode(title) + "&");
		if (notes != null && !notes.isEmpty())
			searchparams = searchparams.concat("notes="
					+ URLEncoder.encode(notes) + "&");
		if (author != null && !author.isEmpty())
			searchparams = searchparams.concat("author="
					+ URLEncoder.encode(author) + "&");
		if (maintainer != null && !maintainer.isEmpty())
			searchparams = searchparams.concat("maintainer="
					+ URLEncoder.encode(maintainer) + "&");
		if (groups != null && !groups.isEmpty())
			searchparams = searchparams.concat("groups="
					+ URLEncoder.encode(groups) + "&");
		if (tags != null && !tags.isEmpty())
			searchparams = searchparams.concat("tags="
					+ URLEncoder.encode(tags) + "&");
		if (openness != null && !openness.isEmpty())
			searchparams = searchparams.concat("filter_by_openness=1&");
		if (downloadable != null && !downloadable.isEmpty())
			searchparams = searchparams.concat("filter_by_downloadable=1&");

		// Determine search mode (will be passed to view)
		String searchMode = "";
		if (searchparams.isEmpty()) {
			// if no query given, continue in simple mode...
			searchMode = "simple";
		} else if (searchparams.contains("q=")
				&& searchparams.split("=").length == 2) {
			// if "q" is the only parameter:
			searchMode = "simple";
		} else if (searchparams.contains("groups=")
				&& searchparams.split("=").length == 2) {
			// if "groups" is the only parameter:
			searchMode = "group";
		} else if (searchparams.contains("tags=")
				&& searchparams.split("=").length == 2) {
			// if "tags" is the only parameter:
			searchMode = "tag";
		} else {
			// if there is another or more than one parameter:
			searchMode = "extended";
		}

		// Search request through CKAN API

		JSONArray jsonArray = null;

		// Packages requested from API only if different from previous
		// request
		if (!searchparams.equals(request.
				getAttribute("searchparams"))) {

			if (!searchparams.isEmpty()) {
				
				ThemeDisplay themedisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
				long userId = themedisplay.getUserId();
				//String userName = themedisplay.getUser().getScreenName();
				
				// Determine if user can maintain datasets, i.e. is a member of the DataOwner/DataSteward roles
				long companyId;
				boolean isUserDataOwner = false;
				//boolean isUserDataSteward = false;
				
				try {
					
					companyId = CompanyLocalServiceUtil.getCompanies().get(0).getCompanyId();//TODO: move to config?
					
					long dataOwnerRoleId = RoleLocalServiceUtil.getRole(companyId, "DataOwner").getRoleId();
					isUserDataOwner = UserLocalServiceUtil.hasRoleUser(dataOwnerRoleId, userId);
					
					//long dataStewardRoleId = RoleLocalServiceUtil.getRole(companyId, "DataSteward").getRoleId();
					//isUserDataSteward =  UserLocalServiceUtil.hasRoleUser(dataStewardRoleId, userId);
			
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (PortalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				

				JSONObject searchResults;
				
				if (!isUserDataOwner) {
					logger.info("search/package?" + searchparams
							+ "all_fields=1&limit=100" + " (user role)");				
					searchResults = (JSONObject)ckanGW.getPackageSearchResults(searchparams+ "all_fields=1&limit=100");

				}
				else {
					logger.info("search/package?" + searchparams
							+ "all_fields=1&limit=100" + " (data owner role)");				
					searchResults = (JSONObject)ckanGW.getPackageSearchResultsAsOwner(searchparams+ "all_fields=1&limit=100");

				}
				
					jsonArray = (JSONArray)searchResults.get("results");

			} else {
				jsonArray = new JSONArray();
			}

		} else {
			jsonArray = (JSONArray) request.
					getAttribute("searchResults");
		}
		
		
		// Filter results for RDF format resources if only Linked Data results were requested
		if (downloadable != null && !downloadable.isEmpty()) {
			JSONArray jsonArray_linkedonly = new JSONArray();
			for (int i=0; i<jsonArray.size(); i++) {
				JSONObject pkg = (JSONObject) jsonArray.get(i);
				boolean matchesFormat = false;
				JSONArray resources = (JSONArray) pkg.get("resources");
				for (int j = 0; j < resources.size(); j++) {
					HashMap rMap = (HashMap) resources.get(j);
					String format = (String) rMap.get("format");
					if (format == null) {
						format = "";
					}
					format = format.trim();
					if (format.toLowerCase().equals("rdf")) {
						matchesFormat = true;
						break;
					}
				}
				if (matchesFormat) {
					jsonArray_linkedonly.add(pkg);
				}
			}
			jsonArray = jsonArray_linkedonly;
		}

		

		// Populate session variables
		//request.getPortletSession().setAttribute("searchparams", searchparams);
		request.setAttribute("searchparams", searchparams);
		request.getPortletSession().setAttribute("searchMode", searchMode);
		//request.getPortletSession().setAttribute("searchResults", jsonArray);
		request.setAttribute("searchResults", jsonArray);
		

		// Remove render parameter (if set)
		request.removeAttribute("groups");
		request.removeAttribute("externSearchQuery");
		
	}
	
	private void doPackageDetails(PortletRequest request) {
		
		String packageId = request.getParameter("packageId");
		
		// Apply public render parameter if no user query present 
		if (packageId == null && this.prpPackageId != null) {
			packageId = this.prpPackageId;
			request.removeAttribute("pId");
		}
		
		ThemeDisplay themedisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		long userId = themedisplay.getUserId();
		//String userName = themedisplay.getUser().getScreenName();
		
		// Determine if user can maintain datasets, i.e. is a member of the DataOwner/DataSteward roles
		long companyId;
		boolean isUserDataOwner = false;
		boolean isUserDataSteward = false;
		
		try {
			
			companyId = CompanyLocalServiceUtil.getCompanies().get(0).getCompanyId();//TODO: move to config?
			
			long dataOwnerRoleId = RoleLocalServiceUtil.getRole(companyId, "DataOwner").getRoleId();
			isUserDataOwner = UserLocalServiceUtil.hasRoleUser(dataOwnerRoleId, userId);
			
			long dataStewardRoleId = RoleLocalServiceUtil.getRole(companyId, "DataSteward").getRoleId();
			isUserDataSteward =  UserLocalServiceUtil.hasRoleUser(dataStewardRoleId, userId);
	
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Submit rating if userRating parameter present 
		if (request.getParameter("userRating") != null) {
			int userRating = Integer.parseInt(request.getParameter("userRating"));
			System.out.println("rating received:" + userRating);
			

			boolean hasRatingPermission = true;
			
			// check if the user is logged in, i.e. not the default "guest" user
			try {
				if (userId == themedisplay.getDefaultUser().getUserId()) {
					hasRatingPermission = false;
				}
			} catch (PortalException e) {
				System.out.println("Exception encountered while trying to get the portal's default user!");
				hasRatingPermission = false;
				e.printStackTrace();
			} catch (SystemException e) {
				System.out.println("Exception encountered while trying to get the portal's default user!");
				hasRatingPermission = false;
				e.printStackTrace();
			}				
			
			// check ckan gw permission to catch duplicates
			if (!ckanGW.hasPackageRatingPermission(packageId, Long.toString(userId))) {
				hasRatingPermission = false;
			}
			
		
			if (hasRatingPermission) {
				if (ckanGW.postPackageRating(packageId, Long.toString(userId), new Date(), userRating)) {
					System.out.println("rating submitted!");
				}
				else {
					System.out.println("rating not submitted! userid: "+userId+", packageId: "+packageId+" - negative ckan gateway response");
				}
			} 
			else {
				System.out.println("rating not submitted! userid: "+userId+", packageId: "+packageId+" - no permission");
			}
		}
				
		// Submit comment if userComment parameter present 
		else if (request.getParameter("userComment") != null) {
			String userComment = (String) request.getParameter("userComment");
			
			boolean hasCommentingPermission = true;
			
			// check if the user is logged in, i.e. not the default "guest" user
			try {
				if (userId == themedisplay.getDefaultUser().getUserId()) {
					hasCommentingPermission = false;
				}
			} catch (PortalException e) {
				System.out.println("Exception encountered while trying to get the portal's default user!");
				hasCommentingPermission = false;
				e.printStackTrace();
			} catch (SystemException e) {
				System.out.println("Exception encountered while trying to get the portal's default user!");
				hasCommentingPermission = false;
				e.printStackTrace();
			}	
			
			if (hasCommentingPermission) {
				if (ckanGW.postPackageComment(packageId, Long.toString(userId), new Date(), userComment)) {
					System.out.println("comment submitted!");
				}
				else {
					System.out.println("comment not submitted! userid: "+userId+", packageId: "+packageId+" - negative ckan gateway response");
				}
			}
			else {
				System.out.println("comment not submitted! userid: "+userId+", packageId: "+packageId+" - no permission");
			}
			
		}		
		// Handle delete / undelete if "packageState" param present
		else if (request.getParameter("packageState") != null) {			
			if (isUserDataOwner) {				
				if (request.getParameter("packageState").equals("deleted")) {					
					ckanGW.deleteMetaDataSet(packageId);
				}				
				else if (request.getParameter("packageState").equals("active")) {
					ckanGW.undeleteMetaDataSet(packageId);
				}
			}
		}


		JSONObject packageDetails = (JSONObject) ckanGW.getPackageDetails(packageId);
		request.setAttribute("packageDetails", packageDetails);
		
		
		
		// Get ratings average + rating permission info
		double packageRating = ckanGW.getPackageRatingsAverage(packageId);
		boolean packageRatingPermission = ckanGW.hasPackageRatingPermission(packageId, Long.toString(userId));
		request.setAttribute("packageRating", packageRating);
		request.setAttribute("packageRatingPermission", packageRatingPermission);
		
		// Get comments count
		int packageCommentsCount = ckanGW.getPackageCommentsCount(packageId);
		request.setAttribute("packageCommentsCount", packageCommentsCount);
		// Get comments
		JSONArray packageComments = ckanGW.getPackageComments(packageId);
		request.setAttribute("packageComments", packageComments);
		 
		System.out.println(request.getAttribute("searchResults"));		
		
	}

	private SearchContainer<String> getSearchContainerGroups(
			PortletRequest request, HashMap<String, Object> groupsData,
			RenderResponse renderResponse) {

		PortletURL portletURL = renderResponse.createRenderURL();
		List<String> headerNames = new ArrayList<String>();
		headerNames.add(LanguageUtil.get(request.getLocale(), "oc-datasets_metadata-category"));

		// create search container, used to display table
		SearchContainer<String> searchContainer = new SearchContainer<String>(
				request, null, null, SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(request.getLocale(), "oc-datasets_no-categories"));
		searchContainer.setTotal(groupsData.size());		

		// fill table
		List<ResultRow> resultRows = searchContainer.getResultRows();
		int end = searchContainer.getEnd();
		if (groupsData.size() < searchContainer.getEnd()) {
			end = groupsData.size();
		}

		Object[] groups = groupsData.values().toArray();

		for (int i = searchContainer.getStart(); i < end; i++) {

			// if (i < searchContainer.getStart() || i >
			// searchContainer.getEnd()) continue;

			JSONObject groupDetails = (JSONObject) groups[i];

			ResultRow row = new ResultRow((String)groupDetails.get("name"),
					(String)groupDetails.get("name"), i);

			PortletURL detailURL = renderResponse.createActionURL();
			detailURL.setParameter("searchquery_groups",
					(String)groupDetails.get("name"));
			detailURL.setParameter("action", "packageSearchAction");

			// row.addText(tag);
			row.addText((String)groupDetails.get("title"), detailURL);
			
			// Add CSS class to support styling of rows for active categories
			if ((request.getAttribute("searchparams") != null && ((String) request.getAttribute("searchparams")).contains("groups=" + groupDetails.get("name"))) || request.getParameter("groups") != null && request.getParameter("groups").contains((String)groupDetails.get("name"))) {
				row.setClassName("activeresult");
				row.setClassHoverName("activeresult");
			}

			resultRows.add(row);
		}
		
		return searchContainer;
	}

	private SearchContainer<String> getSearchContainerTags(
			PortletRequest request, JSONArray jsonArray,
			RenderResponse renderResponse)  {

		PortletURL portletURL = renderResponse.createRenderURL();
		List<String> headerNames = new ArrayList<String>();
		headerNames.add(LanguageUtil.get(request.getLocale(), "oc-datasets_metadata-tag"));

		// create search container, used to display table
		SearchContainer<String> searchContainer = new SearchContainer<String>(
				request, null, null, SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(request.getLocale(), "oc-datasets_no-tags"));
		searchContainer.setTotal(jsonArray.size());

		// fill table
		List<ResultRow> resultRows = searchContainer.getResultRows();
		int end = searchContainer.getEnd();
		if (jsonArray.size() < searchContainer.getEnd()) {
			end = jsonArray.size();
		}
		for (int i = searchContainer.getStart(); i < end; i++) {

			// if (i < searchContainer.getStart() || i >
			// searchContainer.getEnd()) continue;

			String tag = (String)jsonArray.get(i);
			ResultRow row = new ResultRow(tag, tag, i);

			PortletURL detailURL = renderResponse.createActionURL();
			detailURL.setParameter("searchquery_tags", tag);
			detailURL.setParameter("action", "packageSearchAction");

			// row.addText(tag);
			row.addText(tag, detailURL);

			resultRows.add(row);
		}
		return searchContainer;
	}

	private SearchContainer<String> getSearchContainerPackages(
			PortletRequest request, JSONArray jsonArray,
			RenderResponse renderResponse) {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		
		// Determine if user can maintain datasets, i.e. is a member of the DataOwner/DataSteward roles
		long companyId;
		boolean isUserDataOwner = false;
		//boolean isUserDataSteward = false;
		
		try {
			
			companyId = CompanyLocalServiceUtil.getCompanies().get(0).getCompanyId();//TODO: move to config?
			
			long dataOwnerRoleId = RoleLocalServiceUtil.getRole(companyId, "DataOwner").getRoleId();
			isUserDataOwner = UserLocalServiceUtil.hasRoleUser(dataOwnerRoleId, themeDisplay.getUserId());
			
			//long dataStewardRoleId = RoleLocalServiceUtil.getRole(companyId, "DataSteward").getRoleId();
			//isUserDataSteward =  UserLocalServiceUtil.hasRoleUser(dataStewardRoleId, themeDisplay.getUserId());;
	
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		PortletURL portletURL = renderResponse.createRenderURL();
		List<String> headerNames = new ArrayList<String>();
		// headerNames.add("ID");
		headerNames.add(LanguageUtil.get(request.getLocale(), "oc-datasets_package"));
		headerNames.add(LanguageUtil.get(request.getLocale(), "oc-datasets_metadata-description"));
		headerNames.add(LanguageUtil.get(request.getLocale(), "oc-datasets_metadata-modified"));
		if (isUserDataOwner) {
			headerNames.add(""); // Column for edit links
			//headerNames.add(""); // Column for delete links
		}
		
		
		// create search container, used to display table
		SearchContainer<String> searchContainer = new SearchContainer<String>(
				request, null, null, SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(request.getLocale(), "oc-datasets_search-no-packages-found"));
		searchContainer.setTotal(jsonArray.size());

		// fill table
		List<ResultRow> resultRows = searchContainer.getResultRows();
		int end = searchContainer.getEnd();
		if (jsonArray.size() < searchContainer.getEnd()) {
			end = jsonArray.size();
		}
		for (int i = searchContainer.getStart(); i < end; i++) {

			JSONObject result = (JSONObject) jsonArray.get(i);
			ResultRow row = new ResultRow((String)result.get("id"),
					(String)result.get("title"), i);

			PortletURL detailURL = renderResponse.createActionURL();
			detailURL.setParameter("packageId", (String)result.get("id"));
			detailURL.setParameter("action", "packageDetailsAction");
			//if (request.getAttribute("searchResults") != null)
				//detailURL.setParameter("searchResults", ((JSONArray) request.getAttribute("searchResults")).toString());
			if (request.getAttribute("searchparams") != null)				
				detailURL.setParameter("searchparams", (String) request.getAttribute("searchparams"));


			String notes = (String)result.get("notes");
			final int NOTES_MAX_LENGTH = 75;
			if (notes != null && notes.length() > NOTES_MAX_LENGTH) {
				notes = notes.substring(0, NOTES_MAX_LENGTH - 1) + "&#8230;";
			}
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat df_de = new SimpleDateFormat("dd.MM.yyyy");
			String date = (String)((JSONObject)result.get("extras")).get("date_updated");
			if (date==null || date.equals(""))
				date = (String)((JSONObject)result.get("extras")).get("date_released");
			Date changeDate = null;
			if (date!=null && !date.equals("")) {
				try {
					changeDate = df_de.parse(date.substring(0, 10));
				} catch (ParseException pe) {
					System.out.println("Could not parse date; skipped.");
				}
			}
			
			date = (changeDate != null) ? df_de.format(changeDate) : "";
			
			row.addText((String)result.get("title"), detailURL);
			row.addText(notes);
			row.addText(date);
			if (isUserDataOwner) {		
				
				int managaDatasetsPlId = Integer.parseInt(PropsUtil.get("page.managedatasets.plid"));
				PortletURL editURL = PortletURLFactoryUtil.create(PortalUtil.getHttpServletRequest(request), "ocmanagedatasetsportlet_WAR_ocmanagedatasetsportlet", managaDatasetsPlId, PortletRequest.RENDER_PHASE);
				editURL.setParameter("ocAction", "editMetaDataRender");
				editURL.setParameter("pId", (String)result.get("id"));
				
				/*PortletURL deleteURL = renderResponse.createActionURL();
				detailURL.setParameter("packageId", (String)result.get("id"));
				detailURL.setParameter("action", "packageDetailsAction");
				detailURL.setParameter("packageState", "delete");*/
								
				row.addText(LanguageUtil.get(request.getLocale(), "oc-datasets_edit"), editURL);
				row.getEntries().get(3).setAlign("right");
				
				//row.addText(LanguageUtil.get(request.getLocale(), "oc-datasets_hide"), "javascript:if(confirm('Do you really want to hide this dataset?')) alert('Implementation in progress...')");//TODO: L10n				
				//row.getEntries().get(4).setAlign("right");
			}
			
			// Add CSS class to support styling of rows for active results
			if (request.getAttribute("packageDetails") != null && ((String) ((JSONObject) request.getAttribute("packageDetails")).get("id")).equals((String)result.get("id"))) {
				row.setClassName("activeresult");
				row.setClassHoverName("activeresult");				
			}
			
			resultRows.add(row);

		}
		return searchContainer;
	}

	private SearchContainer<String> getSearchContainerResources(
			PortletRequest request, JSONArray jsonArray,
			RenderResponse renderResponse, String packageId) {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		
		// Determine if user can maintain datasets, i.e. is a member of the DataOwner/DataSteward roles
		long companyId;
		//boolean isUserDataOwner = false;
		boolean isUserDataSteward = false;		
		
		if (PropsUtil.get("VirtuosoUrl") != null && !PropsUtil.get("VirtuosoUrl").equals("")) {
			try {
				
				companyId = CompanyLocalServiceUtil.getCompanies().get(0).getCompanyId();//TODO: move to config?
				
				//long dataOwnerRoleId = RoleLocalServiceUtil.getRole(companyId, "DataOwner").getRoleId();
				//isUserDataOwner = UserLocalServiceUtil.hasRoleUser(dataOwnerRoleId, themeDisplay.getUserId());
				
				long dataStewardRoleId = RoleLocalServiceUtil.getRole(companyId, "DataSteward").getRoleId();
				isUserDataSteward =  UserLocalServiceUtil.hasRoleUser(dataStewardRoleId, themeDisplay.getUserId());;
		
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		PortletURL portletURL = renderResponse.createRenderURL();
		List<String> headerNames = new ArrayList<String>();
		headerNames.add(LanguageUtil.get(request.getLocale(), "oc-datasets_metadata-description"));
		headerNames.add(LanguageUtil.get(request.getLocale(), "oc-datasets_details-download-format"));
		headerNames.add("");
		if (isUserDataSteward) {
			headerNames.add(""); // Column for edit links
			headerNames.add(""); // Column for delete links
		}

		// create search container, used to display table
		SearchContainer<String> searchContainer = new SearchContainer<String>(
				request, null, null, SearchContainer.DEFAULT_CUR_PARAM,
				SearchContainer.DEFAULT_DELTA, portletURL, headerNames,
				LanguageUtil.get(request.getLocale(), "oc-datasets_details-downloads-and-resources-not-available"));
		searchContainer.setTotal(jsonArray.size());

		// fill table
		List<ResultRow> resultRows = searchContainer.getResultRows();
		int end = searchContainer.getEnd();
		if (jsonArray.size() < searchContainer.getEnd()) {
			end = jsonArray.size();
		}
		for (int i = searchContainer.getStart(); i < end; i++) {

			JSONObject resource = (JSONObject)jsonArray.get(i);
			if (((String)resource.get("description")).isEmpty())
				resource.put("description", LanguageUtil.get(request.getLocale(), "oc-datasets_metadata-description-not-available"));

			ResultRow row = new ResultRow((String)resource.get("id"),
					(String)resource.get("description"), i);
			
			row.addText((String)resource.get("description"));
			row.addText((String)resource.get("format"));
			row.addText(LanguageUtil.get(request.getLocale(), "oc-datasets_details-download"), (String)resource.get("url"));
						
			if (isUserDataSteward) {		
				
				int managaDatasetsPlId = Integer.parseInt(PropsUtil.get("page.managedatasets.plid"));
				PortletURL editURL = PortletURLFactoryUtil.create(PortalUtil.getHttpServletRequest(request), "ocmanagedatasetsportlet_WAR_ocmanagedatasetsportlet", managaDatasetsPlId, PortletRequest.RENDER_PHASE);
				editURL.setParameter("ocAction", "editLinkedMetaDataRender");
				editURL.setParameter("pId", packageId);
				editURL.setParameter("resourceUrl", (String)resource.get("url"));
		
				if (((String)resource.get("format")).toLowerCase().equals("rdf")) {
					row.addText(LanguageUtil.get(request.getLocale(), "oc-datasets_edit"), editURL);
					//row.addText(LanguageUtil.get(request.getLocale(), "oc-datasets_delete"), "javascript:if(confirm('Do you really want to delete this resource?')) alert('Implementation in progress...')");//TODO: L10n
					row.addText(""); // remove this when deleting is implemented (uncomment the line above)!
				}
				else 
				{
					row.addText("");
					row.addText("");

				}
				row.getEntries().get(3).setAlign("right");
				//row.getEntries().get(4).setAlign("right");
				
			}
	
			resultRows.add(row);
		}
		
		return searchContainer;
	}

}