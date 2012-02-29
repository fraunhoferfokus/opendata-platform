package org.opencities.berlin.opendata.middleware.adapter;

// imports
import javax.portlet.PortletException;

import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import org.opencities.berlin.opendata.ckan.gateway.CKANGateway;
import org.opencities.berlin.opendata.virtuoso.gateway.VirtuosoGateway;

/**
 * The portlet configures the middleware such that it can be straight used by
 * other portlets.
 * 
 * @author Nikolay Tcholtchev, Fraunhofer FOKUS
 * 
 */
public class LiferayAdapter extends MVCPortlet {

	/**
	 * The init function of the portlet
	 */
	public void init() throws PortletException {

		System.out.println("Configuring the middleware ...");

		// configure the CKAN gateway
		String CKANurl = PropsUtil.get("cKANurl");
		String autheticationKey = PropsUtil.get("authenticationKey");
		
		if (CKANurl != null && !CKANurl.equals("") && autheticationKey != null
				&& !autheticationKey.equals("")) {
			CKANGateway.prepareInstance(CKANurl, autheticationKey);
		}

		// configure the Virtuoso gateway
		String VirtuosoUrl = PropsUtil.get("VirtuosoUrl");
		String VirtuosoUser = PropsUtil.get("VirtuosoUser");
		String VirtuosoPasswd = PropsUtil.get("VirtuosoPasswd");

		if (VirtuosoUrl != null && !VirtuosoUrl.equals("")
				&& VirtuosoUser != null && !VirtuosoUser.equals("")
				&& VirtuosoPasswd != null && !VirtuosoPasswd.equals("")) {
			VirtuosoGateway.prepareInstance(VirtuosoUrl, VirtuosoUser,
					VirtuosoPasswd);
		}

		System.out.println("Middleware configuration completed.");

	}
}
