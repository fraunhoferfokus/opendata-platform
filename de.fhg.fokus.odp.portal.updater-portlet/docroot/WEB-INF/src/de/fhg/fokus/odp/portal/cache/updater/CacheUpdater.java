package de.fhg.fokus.odp.portal.cache.updater;

// imports

import java.util.logging.Level;
import java.util.logging.Logger;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;

/**
 * Portlet implementation class for updating the local cache.
 * 
 * @author Nikolay Tcholtchev, Fraunhofer FOKUS
 */
public class CacheUpdater extends MVCPortlet implements MessageListener {

	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * The function is triggered upon the raise of the scheduled event.
	 */
	public void receive(Message msg) {
		CKANGateway ckanGateway = new CKANGateway(PropsUtil.get("cKANurl"),
				PropsUtil.get("authenticationKey"));
		logger.log(Level.FINE, "Updating the local CKAN cache ....");
		ckanGateway.getMostPopularDatasets(Integer.parseInt(PropsUtil
				.get("ckan.count.mostpopulardatasets")));
		logger.log(Level.FINE, "Local CKAN cache updated.");
	}
}
