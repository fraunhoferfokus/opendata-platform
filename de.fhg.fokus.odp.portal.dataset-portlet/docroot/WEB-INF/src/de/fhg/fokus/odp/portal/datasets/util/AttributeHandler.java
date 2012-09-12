package de.fhg.fokus.odp.portal.datasets.util;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * This class provides a helper method, which sets some attributes like
 * isUserLoggedIn.
 * 
 * @see #addUserAttributes(RenderRequest)
 * @author pla
 * 
 */
public class AttributeHandler {
	/** the Logger. */
	private static final Logger LOGGER = Logger
			.getLogger(AttributeHandler.class);
	/** the manage datasets plid. */
	private static final int MANAGE_DATASETS_PL_ID = Integer.parseInt(PropsUtil
			.get("page.managedatasets.plid"));;

	/**
	 * This method adds some attributes to the request object (e.g.
	 * isUserLoggedIn, isUserDataSteward).
	 * 
	 * @param request
	 *            the {@link RenderRequest}
	 */
	public void addUserAttributes(RenderRequest request) {

		boolean isUserLoggedIn = isUserLoggedIn(request);
		request.setAttribute("isUserLoggedIn", isUserLoggedIn);

		boolean isUserDataOwner = isUserDataOwner(request);
		request.setAttribute("isUserDataOwner", isUserDataOwner);

		boolean isUserDataSteward = isUserDataSteward(request);
		request.setAttribute("isUserDataSteward", isUserDataSteward);

		String usersFullName = getUsersFullName(request);
		request.setAttribute("usersFullName", usersFullName);
	}

	/**
	 * This method returns the users full name.
	 * 
	 * @param request
	 *            the {@link PortletRequest}
	 * @return the users full name
	 */
	public static String getUsersFullName(PortletRequest request) {
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		return themeDisplay.getUser().getFullName();
	}

	/**
	 * This method returns true, if the current user has the role data owner.
	 * 
	 * @param request
	 *            the {@link PortletRequest}
	 * @return true, if user is data owner
	 */
	public static boolean isUserDataOwner(PortletRequest request) {
		boolean isUserDataOwner = false;

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			long companyId = CompanyLocalServiceUtil.getCompanies().get(0)
					.getCompanyId();

			long dataOwnerRoleId = RoleLocalServiceUtil.getRole(companyId,
					"DataOwner").getRoleId();
			isUserDataOwner = UserLocalServiceUtil.hasRoleUser(dataOwnerRoleId,
					themeDisplay.getUserId());
		} catch (SystemException e) {
			LOGGER.error(e);
		} catch (PortalException e) {
			LOGGER.error(e);
		}

		return isUserDataOwner;

	}

	/**
	 * This method returns true, if the current user has the role data steward.
	 * 
	 * @param request
	 *            the {@link PortletRequest}
	 * @return true, if user is data steward
	 */
	public static boolean isUserDataSteward(PortletRequest request) {
		boolean isUserDataSteward = false;

		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			long companyId = CompanyLocalServiceUtil.getCompanies().get(0)
					.getCompanyId();

			long dataStewardRoleId = RoleLocalServiceUtil.getRole(companyId,
					"DataSteward").getRoleId();
			isUserDataSteward = UserLocalServiceUtil.hasRoleUser(
					dataStewardRoleId, themeDisplay.getUserId());
		} catch (SystemException e) {
			LOGGER.error(e);
		} catch (PortalException e) {
			LOGGER.error(e);
		}

		return isUserDataSteward;

	}

	/**
	 * This method returns true, when the user is currently logged in, otherwise
	 * false.
	 * 
	 * @param request
	 *            the {@link PortletRequest}
	 * @return true, if user is logged in.
	 */

	public static boolean isUserLoggedIn(PortletRequest request) {
		boolean isUserLoggedIn = false;
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		long userId = themeDisplay.getUserId();
		try {
			isUserLoggedIn = userId != (themeDisplay.getDefaultUser()
					.getUserId());
		} catch (PortalException e) {
			LOGGER.error(e);
		} catch (SystemException e) {
			LOGGER.error(e);
		}
		return isUserLoggedIn;
	}

	/**
	 * This method adds the ManageDatasets page ID to {@link PortletRequest}.
	 * 
	 * @param request
	 *            the {@link PortletRequest}
	 */
	public void addManageDatasetsPlID(PortletRequest request) {
		// add ID from ManageDatasets Portlet to attributes
		int managaDatasetsPlId = getManageDatasetsPlId();
		request.setAttribute("manageDatasetsPlId", managaDatasetsPlId);
	}

	/**
	 * getter for {@link #MANAGE_DATASETS_PL_ID}.
	 * 
	 * @return {@link #MANAGE_DATASETS_PL_ID}
	 */
	private int getManageDatasetsPlId() {
		return MANAGE_DATASETS_PL_ID;
	}
}
