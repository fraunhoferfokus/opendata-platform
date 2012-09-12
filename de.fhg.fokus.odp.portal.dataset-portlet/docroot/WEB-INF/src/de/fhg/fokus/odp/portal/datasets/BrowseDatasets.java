package de.fhg.fokus.odp.portal.datasets;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.liferay.portal.kernel.util.ParamUtil;

import de.fhg.fokus.odp.portal.datasets.searchresults.BrowseDataSetsSearchResults;
import de.fhg.fokus.odp.portal.datasets.util.AttributeHandler;
import de.fhg.fokus.odp.portal.datasets.util.GroupCreator;
import de.fhg.fokus.odp.portal.datasets.util.Interaction;
import de.fhg.fokus.odp.portal.datasets.util.ValueReader;

/**
 * This class is the main class in this Portlet. It handles the view , event and
 * action phase.
 * 
 * @see #doView(RenderRequest, RenderResponse)
 * @see #processEvent(EventRequest, EventResponse)
 * @see #processAction(ActionRequest, ActionResponse)
 * @author pla
 * 
 */
public class BrowseDatasets extends GenericPortlet {
	/** the Logger. */
	private static final Logger LOGGER = Logger.getLogger(BrowseDatasets.class);
	/** String for action key. */
	private static final String MY_ACTION = "action";
	/** String for searchQuery. */
	private static final String SEARCH_QUERY = "searchQuery";
	/** the result string. */
	private static final String RESULT_STRING = "result";
	/** the result string. */
	private static final String PACKAGE_ID_STRING = "packageId";
	/** URL for jsp. */
	private static final String URL = "/WEB-INF/jsp/view.jsp";
	/** the Namespace . */
	private static final String NAMESPACE = "http://sun.com/tourevents";
	/** the groupcreator. */
	private final GroupCreator groupcreator = new GroupCreator();
	/** the attributehandler. */
	private final AttributeHandler attr = new AttributeHandler();
	/** the search results object. */
	private final BrowseDataSetsSearchResults searchresults = new BrowseDataSetsSearchResults();
	/** the value reader. */
	private final ValueReader reader = new ValueReader();
	/** the interaction. */
	private final Interaction interactionUtil = new Interaction();

	/**
	 * This method prepares the view. It creates the groups and adds some
	 * additional attributes to our Request (e.g. isUserLoggedIn). Based on
	 * action parameter the method adds additional information.
	 * 
	 * @param request
	 *            the {@link RenderRequest}
	 * @param response
	 *            the {@link RenderResponse}
	 * 
	 * @see ValueReader#retrieveValuesFromDataBase(RenderRequest,
	 *      RenderResponse)
	 */
	@RenderMode(name = "VIEW")
	public void doView(RenderRequest request, RenderResponse response) {

		String actionParameter = retrieveActionParameter(request);

		LOGGER.info("in doView; action value = " + actionParameter);

		if (actionParameter == null
				|| actionParameter.equals("packageDeleteAction")) {
			actionParameter = "defaultView";
		}

		// save parameter for context switching in jsp files
		request.setAttribute(BrowseDatasets.MY_ACTION, actionParameter);

		JSONObject resultObject = retrieveResultObject(request);
		request.setAttribute(BrowseDatasets.RESULT_STRING, resultObject);

		groupcreator.createSearchContainerForGroups(request, response);

		// set attributes like isLoggedIn, isAdminUser
		attr.addUserAttributes(request);
		attr.addManageDatasetsPlID(request);

		if (actionParameter.equalsIgnoreCase("defaultView")) {
			// retrieve latestDatasets and tags from DB
			reader.retrieveValues(request);
			searchresults.prepareLatestDatasetsResults(request);
		} else if (actionParameter.equalsIgnoreCase("packageDetailsAction")) {
			reader.addPopularTagsDataToRequest(request);
			searchresults.preparePackageDetailsResults(request, resultObject);
		} else if (actionParameter.equalsIgnoreCase("packageSearchAction")) {
			reader.addPopularTagsDataToRequest(request);
			searchresults.preparePackageSearchResults(request, resultObject);
		}
		try {
			PortletRequestDispatcher dispatcher = getPortletContext()
					.getRequestDispatcher(
							response.encodeURL(BrowseDatasets.URL));
			dispatcher.include(request, response);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (PortletException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	private String retrieveActionParameter(RenderRequest request) {
		String actionParameter = request.getParameter(BrowseDatasets.MY_ACTION);

		// search IPC
		PortletSession session = request.getPortletSession();
		String sessionActionParam = (String) session.getAttribute(MY_ACTION,
				PortletSession.APPLICATION_SCOPE);
		if (sessionActionParam != null) {
			actionParameter = sessionActionParam;
			session.setAttribute(MY_ACTION, null,
					PortletSession.APPLICATION_SCOPE);
		}

		return actionParameter;
	}

	/**
	 * @param request
	 * @param session
	 * @return
	 */
	private JSONObject retrieveResultObject(RenderRequest request) {
		PortletSession session = request.getPortletSession();
		// retrieve result from EventPhase
		String resultString = request
				.getParameter(BrowseDatasets.RESULT_STRING);

		// retrieve result from search IPC
		String sessionResultString = (String) session.getAttribute(
				BrowseDatasets.RESULT_STRING, PortletSession.APPLICATION_SCOPE);
		if (sessionResultString != null) {
			resultString = sessionResultString;
			session.setAttribute(BrowseDatasets.RESULT_STRING, null,
					PortletSession.APPLICATION_SCOPE);
		}

		JSONParser parser = new JSONParser();
		JSONObject resultObject = null;
		if (resultString != null) {
			try {
				resultObject = (JSONObject) parser.parse(resultString);
			} catch (ParseException e) {
				LOGGER.error(e);
			}
		}

		return resultObject;
	}

	/**
	 * This method calls the correct method from SearchPortlet (based on the
	 * current action value). Additionally, if the supplied action equals
	 * packageDetailsAction, we check if the User posted a comment or a rating.
	 * Those cases will be handled by SocialInteraction.
	 * 
	 * @param request
	 *            the {@link ActionRequest}
	 * @param response
	 *            the {@link ActionResponse}
	 * 
	 * @see Interaction
	 */
	@Override
	public void processAction(ActionRequest request, ActionResponse response) {
		// retrieve action and save for rendering phase
		String action = request.getParameter(BrowseDatasets.MY_ACTION);
		response.setRenderParameter(BrowseDatasets.MY_ACTION, action);

		// retrieve package id and save for rendering phase
		String packageId = ParamUtil.getString(request,
				BrowseDatasets.PACKAGE_ID_STRING);
		response.setRenderParameter(BrowseDatasets.PACKAGE_ID_STRING, packageId);

		if (action.equalsIgnoreCase("packageSearchAction")) {
			// retrieve correct values from user and send to search portlet
			String searchQuery = ParamUtil.getString(request, SEARCH_QUERY);
			response.setRenderParameter(SEARCH_QUERY, searchQuery);

			// send to search portlet via event
			QName qname = new QName(BrowseDatasets.NAMESPACE, "packageSearch");
			response.setEvent(qname, searchQuery);
		} else if (action.equalsIgnoreCase("packageDetailsAction")) {
			// check if socialInteractin is set; if true, fulfill social
			// interaction
			String socialInteraction = ParamUtil.getString(request,
					"socialInteraction");
			if (socialInteraction.equalsIgnoreCase("comment")) {
				interactionUtil.postComment(request);
			} else if (socialInteraction.equalsIgnoreCase("rating")) {
				interactionUtil.postRating(request);
			}
			// send to search portlet
			QName qname = new QName(BrowseDatasets.NAMESPACE, "packageDetails");
			response.setEvent(qname, packageId);
		} else if (action.equalsIgnoreCase("packageDeleteAction")) {
			Interaction interactionUtil = new Interaction();
			if (AttributeHandler.isUserDataOwner(request)) {
				interactionUtil.deleteDataset(packageId);
			}
		}
	}

	/**
	 * This method is called after execution of search from our search portlet.
	 * The result will be saved in response as a renderparameter.
	 * 
	 * @param request
	 *            the {@link EventRequest}
	 * @param response
	 *            the {@link EventResponse}
	 */
	@Override
	public void processEvent(EventRequest request, EventResponse response) {
		Event event = request.getEvent();
		// retrieve package id and save for rendering phase
		String packageId = ParamUtil.getString(request,
				BrowseDatasets.PACKAGE_ID_STRING);
		response.setRenderParameter(BrowseDatasets.PACKAGE_ID_STRING, packageId);
		String searchQuery = ParamUtil.getString(request, SEARCH_QUERY);
		response.setRenderParameter(SEARCH_QUERY, searchQuery);

		if (event.getName().equals("packageSearchResult")) {
			LOGGER.info("retrieved PackageSearchResult: " + event.getValue());
			response.setRenderParameter(BrowseDatasets.RESULT_STRING,
					(String) event.getValue());
			response.setRenderParameter(MY_ACTION, "packageSearchAction");
			// use PortletSession to store action parameter
			// and search result
			PortletSession session = request.getPortletSession();
			session.setAttribute(MY_ACTION, "packageSearchAction",
					PortletSession.APPLICATION_SCOPE);
			session.setAttribute(BrowseDatasets.RESULT_STRING,
					(String) event.getValue(), PortletSession.APPLICATION_SCOPE);

		} else if (event.getName().equals("packageDetailsResult")) {
			LOGGER.info("retrieved PackageDetailsResult: " + event.getValue());
			response.setRenderParameter(BrowseDatasets.RESULT_STRING,
					(String) event.getValue());
			response.setRenderParameter(MY_ACTION, "packageDetailsAction");
		}
	}
}