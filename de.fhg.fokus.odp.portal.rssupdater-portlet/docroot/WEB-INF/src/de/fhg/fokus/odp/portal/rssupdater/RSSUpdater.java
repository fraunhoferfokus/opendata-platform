package de.fhg.fokus.odp.portal.rssupdater;

// import
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * Portlet implementation class for the RSSUpdater.
 * 
 * @author Nikolay Tcholtchev, Fraunhofer FOKUS
 */
public class RSSUpdater extends MVCPortlet implements MessageListener {

	/** Local field to hold the RSS Updater Task. */
	private RSSUpdaterTask rssUpdaterTask = null;

	/**
	 * The function is triggered upon the raise of the scheduled event.
	 */
	@Override
	public void receive(Message msg) {

		long period = 7200000; // 7200 secs = 120 min
		long sincePeriod = 7200000; // 7200 secs = 120 min

		String cKANurl = PropsUtil.get("cKANurl");
		try {
			period = Long.parseLong(PropsUtil.get("period").trim());
		} catch (Exception e) {
			e.printStackTrace();
			period = 7200000;
		}

		try {
			sincePeriod = Long.parseLong(PropsUtil.get("sincePeriod").trim());
		} catch (Exception e) {
			e.printStackTrace();
			sincePeriod = 7200000;
		}

		String authKey = PropsUtil.get("authenticationKey");
		String serverTimeZone = PropsUtil.get("serverTimeZone");

		String packageRssFileName = PropsUtil.get("packageRssFileName");

		String packageRssTitle = PropsUtil.get("packageRssTitle");
		String packageRssDescription = PropsUtil.get("packageRssDescription");
		String packageRssLink = PropsUtil.get("packageRssLink");

		String categoryRssFileName = PropsUtil.get("categoryRssFileName");

		String categoriesRssTitle = PropsUtil.get("categoriesRssTitle");
		String categoryRssDescription = PropsUtil.get("categoryRssDescription");
		String categoryRssLink = PropsUtil.get("categoryRssLink");

		// create an RSS updater task
		rssUpdaterTask = new RSSUpdaterTask(cKANurl, period, sincePeriod,
				serverTimeZone, packageRssFileName, packageRssTitle,
				packageRssDescription, packageRssLink, categoryRssFileName,
				categoriesRssTitle, categoryRssDescription, categoryRssLink,
				authKey);

		rssUpdaterTask.run();
	}
}
