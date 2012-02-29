package org.opencities.berlin.opendata.cache.updater;

// imports
import org.opencities.berlin.opendata.ckan.gateway.CKANGateway;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
* Portlet implementation class for updating the local cache.
* 
* @author Nikolay Tcholtchev, Fraunhofer FOKUS
*/
public class CacheUpdater extends MVCPortlet implements MessageListener {

	/**
	 * The function is triggered upon the raise of the scheduled event.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void receive(Message msg) {
			System.out.println ("Updating the local CKAN cache ....");
			int NUMBER_OF_DATA_SETS = 10;
			int NUMBER_OF_TAGS = 20;
			CKANGateway.getInstance().getLatestDatasets(NUMBER_OF_DATA_SETS);
			CKANGateway.getInstance().getMostPopularDatasets(NUMBER_OF_DATA_SETS);
			CKANGateway.getInstance().getMostPopularTags(NUMBER_OF_TAGS);
			System.out.println ("Local CKAN cache updated.");
	}
}

