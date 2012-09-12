package de.fhg.fokus.odp.portal.datasets.searchresults;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;
import de.fhg.fokus.odp.portal.datasets.util.QueryParser;

/**
 * This class prepares the results, which we obtained from the searchPortlet,
 * for the JSP files.
 * 
 * @author pla
 * 
 */
public class BrowseDataSetsSearchResults {
	/** the Logger. */
	private static final Logger LOGGER = Logger
			.getLogger(BrowseDataSetsSearchResults.class);
	/** the CKANGateway. */
	private final CKANGateway ckanGW;
	/** the List with open Licenses. */
	private final List<String> openLicensesId;
	/** the comments string. */
	private static final String COMMENTS_STRING = "comments";

	/**
	 * Constructor initializes our {@link CKANGateway} and
	 * {@link #openLicensesId}.
	 */
	public BrowseDataSetsSearchResults() {
		this.ckanGW = new CKANGateway(PropsUtil.get("cKANurl") + "/",
				PropsUtil.get("authenticationKey"));
		this.openLicensesId = new ArrayList<String>();
		fillOpenLicensesId();
	}

	/**
	 * This method fills {@link #openLicensesId} with open licenses from our
	 * property file.
	 */
	private void fillOpenLicensesId() {
		for (String singleLicense : PropsUtil.getArray("openLicenses")) {
			this.openLicensesId.add(singleLicense);
		}
	}

	/**
	 * This method prepares the results for the latest jsp. It checks for each
	 * package in latest Datasets if the license is open and stores the value in
	 * a boolean array.
	 * 
	 * @param request
	 *            the {@link RenderRequest}
	 */
	public void prepareLatestDatasetsResults(RenderRequest request) {
		JSONObject latestPackagesData = (JSONObject) request
				.getAttribute("latestPackagesData");

		JSONArray results = (JSONArray) latestPackagesData.get("results");
		boolean[] arePackagesOpen = createArePackagesOpen(results);
		request.setAttribute("arePackagesOpen", arePackagesOpen);

		addNumberOfRatingsAndCommentsToRequest(request, results);

	}

	/**
	 * This method prepares the results for the packageDetails JSP. It adds the
	 * ratings average, the current user rating permission, a boolean
	 * representing, if the license is open and the screenNames of all
	 * commenters.
	 * 
	 * @see #getCommentersScreennames(RenderRequest, JSONObject)
	 * @see #addRatingAndPermissionToRequest(RenderRequest)
	 * @param request
	 *            the {@link RenderRequest}
	 * @param resultObject
	 *            the {@link JSONObject}
	 */
	public void preparePackageDetailsResults(RenderRequest request,
			JSONObject resultObject) {
		LOGGER.info("in preparePackageDetailsResults; action value = "
				+ request.getParameter("action"));

		String url = createURLForTwitter(request);
		request.setAttribute("twitterURL", url);

		addRatingAndPermissionToRequest(request);

		if (resultObject == null) {
			LOGGER.info(">>>>>DID NOT RECEIVE ANY RESULT FROM SEARCH PORTLET FOR PACKAGEDETAILS!!<<<<<");
		} else {
			String license_id = (String) resultObject.get("license_id");
			boolean isPackageOpen = isLicenseOpen(license_id);
			request.setAttribute("isPackageOpen", isPackageOpen);

			addCommentsAndNamesToRequest(request, resultObject);
			// addCommentersScreennamesToRequest(request, resultObject);

			LOGGER.info("PackageResult = " + resultObject.toJSONString());
		}
	}

	/**
	 * This method creates an URL for our Twitter button.
	 * 
	 * portalUrl returns the first part (with Port) and getCurrentUrl adds the
	 * rest.
	 * 
	 * @param request
	 *            the {@link PortletRequest}
	 * @return URLEncoded twitterUrl
	 */
	private String createURLForTwitter(PortletRequest request) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			String url = themeDisplay.getPortalURL().concat(
					PortalUtil.getLayoutFriendlyURL(themeDisplay.getLayout(),
							themeDisplay));
			return URLEncoder.encode(url, "UTF-8");
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e);
		}
		return null;
	}

	/**
	 * This method retrieves the comments for a particular dataset. If we find
	 * some comments, reverse them (last to first) and add for each comment the
	 * correct screenname. Finally, store comments and screennames in
	 * {@link RenderRequest}
	 * 
	 * @param request
	 *            the {@link RenderRequest}
	 * @param resultObject
	 *            the {@link JSONObject}
	 */
	private void addCommentsAndNamesToRequest(RenderRequest request,
			JSONObject resultObject) {
		// get user comments
		JSONObject extras = (JSONObject) resultObject.get("extras");
		if (extras == null) {
			return;
		} else {
			JSONArray comments = null;
			if (extras.get(BrowseDataSetsSearchResults.COMMENTS_STRING) instanceof java.lang.String) {
				JSONParser parser = new JSONParser();
				try {
					comments = (JSONArray) parser.parse((String) extras
							.get(BrowseDataSetsSearchResults.COMMENTS_STRING));
				} catch (ParseException e) {
					LOGGER.error(e.getLocalizedMessage(), e);
				}
			} else {
				comments = (JSONArray) extras
						.get(BrowseDataSetsSearchResults.COMMENTS_STRING);
			}
			if (comments == null) {
				return;
			} else {
				comments = reverseJSONArray(comments);
				String[] screenNames = getCommentersScreennames(comments);
				request.setAttribute(
						BrowseDataSetsSearchResults.COMMENTS_STRING, comments);
				request.setAttribute("commentersNames", screenNames);
			}
		}
	}

	/**
	 * This method reverses a JSONArray.
	 * 
	 * @param comments
	 *            the JSONArray to be reversed
	 * @return the reversed JSONArray
	 */
	private JSONArray reverseJSONArray(JSONArray comments) {
		JSONArray toReturn = new JSONArray();
		for (int i = comments.size() - 1; i >= 0; i--) {
			toReturn.add(comments.get(i));
		}
		return toReturn;
	}

	/**
	 * This method adds the rating's average and the ratingPermission for the
	 * logged in user to the {@link RenderRequest}. The ratings average is
	 * obtained from the middleware.
	 * 
	 * @param request
	 *            the {@link RenderRequest}
	 */
	private void addRatingAndPermissionToRequest(RenderRequest request) {
		ThemeDisplay themedisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		String packageId = ParamUtil.getString(request, "packageId");

		// retrieve packageRating from ckanGW
		int packageRating = (int) ckanGW.getDataSetRatingsAverage(packageId);
		request.setAttribute("packageRating", packageRating);

		// check if current user voted already - add as attribute
		boolean hasPackageRatingPermission = ckanGW.hasPackageRatingPermission(
				packageId, Long.toString(themedisplay.getUserId()));
		request.setAttribute("packageRatingPermission",
				hasPackageRatingPermission);
	}

	/**
	 * This method adds the screenname of all users, which commented this
	 * dataset, to a String array and returns this array.
	 * 
	 * @param comments
	 *            the {@link JSONArray}
	 * @return array filled with screennames of commenters
	 */
	private String[] getCommentersScreennames(JSONArray comments) {

		String[] commentersNames = new String[comments.size()];
		for (int i = 0; i < comments.size(); i++) {
			String userId = (String) ((JSONObject) comments.get(i))
					.get("userId");
			try {
				commentersNames[i] = UserLocalServiceUtil.getUserById(
						Long.parseLong(userId)).getScreenName();
			} catch (NumberFormatException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} catch (PortalException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} catch (SystemException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
		}
		return commentersNames;
	}

	/**
	 * This method prepares the searchResult for pagination. It checks for each
	 * package, if the provided license_id is open or closed and stores the
	 * value in a boolean array. Furthermore, it saves for each package the
	 * number of comments and number of ratings in an array.
	 * 
	 * @param request
	 *            the {@link RenderRequest}
	 * @param resultObject
	 *            the {@link JSONObject}
	 */
	public void preparePackageSearchResults(RenderRequest request,
			JSONObject resultObject) {
		LOGGER.info("in preparePackageSearchResults; action value = "
				+ request.getParameter("action"));
		if (resultObject == null) {
			LOGGER.info(">>>>>DID NOT RECEIVE ANY RESULT FROM SEARCH PORTLET FOR PACKAGESEARCH!!<<<<<");
		} else {
			JSONArray arr = (JSONArray) resultObject.get("results");
			boolean[] arePackagesOpen = createArePackagesOpen(arr);
			request.setAttribute("arePackagesOpen", arePackagesOpen);

			addNumberOfRatingsAndCommentsToRequest(request, arr);

			String parsedSearchQuery = QueryParser.parseSearchQuery(request);
			request.setAttribute("parsedSearchQuery", parsedSearchQuery);

			LOGGER.info("PackageResult = " + resultObject.toJSONString());
		}
	}

	/**
	 * This method adds for each package in our {@link JSONArray} the correct
	 * number of comments and ratings in a specific array. These arrays will be
	 * saved our {@link RenderRequest};
	 * 
	 * @param request
	 *            the {@link RenderRequest}
	 * @param arr
	 *            the {@link JSONArray}
	 */
	private void addNumberOfRatingsAndCommentsToRequest(RenderRequest request,
			JSONArray arr) {
		int[] ratingsNumberArray = new int[arr.size()];
		int[] commentsNumberArray = new int[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			JSONObject dataset = (JSONObject) arr.get(i);
			JSONObject extras = (JSONObject) dataset.get("extras");
			// don't have any extras -> dont have ratings and comments
			if (extras == null) {
				ratingsNumberArray[i] = 0;
				commentsNumberArray[i] = 0;
			} else {
				String rating = (String) extras.get("ratings");
				String comments = (String) extras
						.get(BrowseDataSetsSearchResults.COMMENTS_STRING);
				// don't have any ratings -> ratings = 0
				if (rating == null) {
					ratingsNumberArray[i] = 0;
				} else {
					int ratingsNumber = countNumberofOccurences(rating, '{');
					ratingsNumberArray[i] = ratingsNumber;
				}
				// don't have any comments -> ratings = 0
				if (comments == null) {
					commentsNumberArray[i] = 0;
				} else {
					int commentsNumber = countNumberofOccurences(comments, '{');
					commentsNumberArray[i] = commentsNumber;
				}
			}
		}

		request.setAttribute("ratingsNumber", ratingsNumberArray);
		request.setAttribute("commentsNumber", commentsNumberArray);
	}

	/**
	 * This method returns the number of occurrences of character in string.
	 * 
	 * @param string
	 *            the string
	 * @param character
	 *            the character
	 * @return number of occurrences of character in string.
	 */
	private static int countNumberofOccurences(String string, char character) {
		int count = 0;
		if (string == null) {
			return count;
		}
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == character) {
				count++;
			}
		}
		return count;
	}

	/**
	 * This method iterates over the JSONArray and adds for each dataset an
	 * entry in arePackagesOpen and returns this boolean array.
	 * 
	 * @param array
	 *            the {@link JSONArray}
	 * @return filled boolean array
	 */
	private boolean[] createArePackagesOpen(JSONArray array) {

		boolean[] arePackagesOpen = new boolean[array.size()];
		for (int i = 0; i < array.size(); i++) {
			String license_id = (String) ((JSONObject) array.get(i))
					.get("license_id");
			arePackagesOpen[i] = isLicenseOpen(license_id);
		}
		return arePackagesOpen;
	}

	/**
	 * Check if provided license is a member of our List of open Licenses.
	 * 
	 * @param license
	 *            the license
	 * @return true, if license is open
	 */
	private boolean isLicenseOpen(String license) {
		if (license == null) {
			return false;
		} else {
			return this.openLicensesId.contains(license);
		}
	}
}