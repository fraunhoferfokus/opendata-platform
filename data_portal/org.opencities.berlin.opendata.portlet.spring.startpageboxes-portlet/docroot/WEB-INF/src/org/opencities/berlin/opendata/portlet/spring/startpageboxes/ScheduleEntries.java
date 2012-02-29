package org.opencities.berlin.opendata.portlet.spring.startpageboxes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencities.berlin.opendata.ckan.gateway.CKANGateway;
import org.opencities.berlin.opendata.entity.model.BoxEntry;
import org.opencities.berlin.opendata.entity.service.BoxEntryLocalServiceUtil;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.PropsUtil;

public class ScheduleEntries implements MessageListener {

	Logger logger = Logger.getLogger(ScheduleEntries.class.getName());

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void receive(Message arg0) {

		try {

			logger.log(Level.INFO, "Starting startpage caching...");
			logger.log(Level.INFO, "... Deleting old startpage entries.");
			for (BoxEntry entry : BoxEntryLocalServiceUtil.getBoxEntries(0,
					BoxEntryLocalServiceUtil.getBoxEntriesCount())) {
				BoxEntryLocalServiceUtil.deleteBoxEntry(entry);
			}

			logger.log(Level.INFO, "... Receiving new startpage entries");
			CKANGateway ckanGW = new CKANGateway(PropsUtil.get("cKANurl"),
					PropsUtil.get("authenticationKey"));

			HashMap hm = ckanGW.getMostPopularDatasets(3);
			List<BoxEntry> datasets = new ArrayList<BoxEntry>();
			for (String key : (Set<String>) hm.keySet()) {
				Map m = (Map) hm.get(key);
				BoxEntry dataset;
				dataset = BoxEntryLocalServiceUtil.createBoxEntry(
						(String) m.get("id"), (String) m.get("name"), "", 0,
						Double.valueOf((String) m.get("rating")));
				if (dataset.getTitle().length() >= 35) {
					dataset.setTitle(dataset.getTitle().subSequence(0, 35)
							+ "...");
				}

				if (dataset.getRating() < 0) {
					dataset.setRating(0);
				}

				datasets.add(dataset);
			}
			logger.log(Level.INFO, "Finished startpage caching...");
		} catch (SystemException e) {
			logger.log(Level.INFO, "Error on startpage caching...");
			e.printStackTrace();
		}
	}

}
