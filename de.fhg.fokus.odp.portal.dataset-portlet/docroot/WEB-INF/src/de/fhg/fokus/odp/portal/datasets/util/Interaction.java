package de.fhg.fokus.odp.portal.datasets.util;

import java.util.Date;

import javax.portlet.ActionRequest;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;

/**
 * This class provides methods for posting comments and ratings for particular
 * datasets. Furthermore it provides a method for deleting datasets.
 * 
 * @author pla
 * 
 */
public class Interaction {
	/** the Logger. */
	private static final Logger LOGGER = Logger.getLogger(Interaction.class);
	/** the CKANGateway. */
	private final CKANGateway ckanGW;

	/**
	 * Constructor initializes our {@link CKANGateway}.
	 */
	public Interaction() {
		this.ckanGW = new CKANGateway(PropsUtil.get("cKANurl") + "/",
				PropsUtil.get("authenticationKey"));
	}

	/**
	 * This method tries to post a comment to CKAN. First, the method checks, if
	 * the user is logged in, if that's true, try to post the comment to ckan
	 * via middleware.
	 * 
	 * @param request
	 *            the {@link ActionRequest}
	 */
	public void postComment(ActionRequest request) {
		boolean postedComment = false;

		if (AttributeHandler.isUserLoggedIn(request)) {
			ThemeDisplay themedisplay = (ThemeDisplay) request
					.getAttribute(WebKeys.THEME_DISPLAY);
			Long userId = themedisplay.getUserId();
			String packageId = ParamUtil.getString(request, "packageId");
			String comment = ParamUtil.getString(request, "userComment");
			postedComment = ckanGW.postDataSetComment(packageId,
					Long.toString(userId), new Date(), comment);
			if (postedComment) {
				LOGGER.info("comment submitted!");
			} else {
				LOGGER.info("comment not submitted! Negative ckan gateway response");
			}
		} else {
			LOGGER.info("comment not submitted! No permission");
		}
	}

	/**
	 * This method tries to post a rating to CKAN. First, it checks, if the user
	 * is logged in. AFterwards, it checks if the user has a package rating
	 * permission. Finally, the rating will be submitted.
	 * 
	 * @param request
	 *            the {@link ActionRequest}
	 */
	public void postRating(ActionRequest request) {
		boolean postedRating = false;
		try {
			if (AttributeHandler.isUserLoggedIn(request)) {
				ThemeDisplay themedisplay = (ThemeDisplay) request
						.getAttribute(WebKeys.THEME_DISPLAY);
				Long userId = themedisplay.getUserId();
				String packageId = ParamUtil.getString(request, "packageId");
				if (ckanGW.hasPackageRatingPermission(packageId,
						Long.toString(userId))) {
					String rating = ParamUtil.getString(request, "userRating");
					postedRating = ckanGW.postDataSetRating(packageId,
							Long.toString(userId), new Date(),
							Integer.parseInt(rating));
					if (postedRating) {
						LOGGER.info("rating submitted!");
					} else {
						LOGGER.info("rating not submitted! userid: " + userId
								+ ", packageId: " + packageId
								+ " - negative ckan gateway response");
					}
				} else {
					LOGGER.info("rating not submitted! Already rated this dataset");
				}
			} else {
				LOGGER.info("rating not submitted! No permission");
			}
		} catch (NumberFormatException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * This method deletes the dataset given by packageId
	 * 
	 * @param packageId
	 *            the id of package to be deleted
	 */
	public void deleteDataset(String packageId) {
		if (ckanGW.deleteDataSet(packageId)) {
			LOGGER.info("deleted package with id : " + packageId);
		} else {
			LOGGER.info("did not delete package with id : " + packageId);
		}
	}

}
