package de.fhg.fokus.odp.portal.startpageboxes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.messageboards.model.MBCategory;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBCategoryLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.ratings.model.RatingsEntry;
import com.liferay.portlet.ratings.service.RatingsEntryLocalServiceUtil;

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;

// TODO: Auto-generated Javadoc
/**
 * The Class StartpageBoxesController.
 */
@Controller(value = "startpageBoxesController")
@RequestMapping("VIEW")
public class StartpageBoxesController {

	/**
	 * View.
	 * 
	 * @return the string
	 */
	@RenderMapping
	public String view() {
		return "boxes";
	}

	/**
	 * Gets the most recent descussions.
	 * 
	 * @param request
	 *            the request
	 * @return the most recent descussions
	 * @throws SystemException
	 *             the system exception
	 */
	@SuppressWarnings("unchecked")
	@ModelAttribute(value = "discussions")
	public List<DiscussionEntry> getMostRecentDescussions(PortletRequest request)
			throws SystemException {

		String categoryIdeasName = PropsUtil
				.get("messageboard.category.ideas.title");

		DynamicQuery queryCategories = DynamicQueryFactoryUtil.forClass(
				MBCategory.class, PortalClassLoaderUtil.getClassLoader()).add(
				PropertyFactoryUtil.forName("name").eq(categoryIdeasName));

		List<MBCategory> categories = MBCategoryLocalServiceUtil
				.dynamicQuery(queryCategories);

		MBCategory category = categories.get(0);
		long categoryId = category.getCategoryId();

		DynamicQuery queryMessages = DynamicQueryFactoryUtil.forClass(
				MBMessage.class, PortalClassLoaderUtil.getClassLoader()).add(
				PropertyFactoryUtil.forName("categoryId").eq(categoryId));

		List<DiscussionEntry> entries = new ArrayList<DiscussionEntry>();

		List<MBMessage> messages = MBMessageLocalServiceUtil
				.dynamicQuery(queryMessages);
		ThemeDisplay td = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);

		String basicUrl = td.getPortalURL()
				+ "/c/message_boards/find_message?messageId=";

		for (MBMessage message : messages) {
			if (entries.size() >= 3) {
				break;
			}
			DiscussionEntry dataset = new DiscussionEntry();
			List<RatingsEntry> ratings = RatingsEntryLocalServiceUtil
					.getEntries(MBMessage.class.getName(),
							message.getMessageId());
			double score = 0;
			for (RatingsEntry rating : ratings) {
				score += rating.getScore();
			}
			if (message.getSubject().length() >= 35) {
				dataset.setTitle(message.getSubject().subSequence(0, 35)
						+ "...");
			} else {
				dataset.setTitle(message.getSubject());
			}

			dataset.setUrl(basicUrl + message.getMessageId());
			dataset.setVotes(new Double(score).intValue());
			entries.add(dataset);
		}

		Collections.sort(entries);
		return entries;
	}

	/**
	 * Gets the most recent data sets.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the most recent data sets
	 * @throws SystemException
	 *             the system exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ModelAttribute(value = "datasets")
	public List<BoxEntry> getMostRecentDataSets(PortletRequest request,
			PortletResponse response) throws SystemException {

		CKANGateway ckanGW = new CKANGateway(PropsUtil.get("cKANurl"),
				PropsUtil.get("authenticationKey"));

		HashMap hm = ckanGW.getMostPopularDatasets(Integer.parseInt(PropsUtil
				.get("ckan.count.mostpopulardatasets")));
		List<BoxEntry> datasets = new ArrayList<BoxEntry>();
		for (String key : (Set<String>) hm.keySet()) {
			Map m = (Map) hm.get(key);
			BoxEntry dataset;
			dataset = new BoxEntry((String) m.get("id"),
					(String) m.get("title"), "", 0, Double.valueOf((String) m
							.get("rating")));
			if (dataset.getTitle().length() >= 35) {
				dataset.setTitle(dataset.getTitle().subSequence(0, 35) + "...");
			}

			if (dataset.getRating() < 0) {
				dataset.setRating(0);
			}

			datasets.add(dataset);
		}

		for (BoxEntry entry : datasets) {

			PortletURL url = PortletURLFactoryUtil
					.create(PortalUtil.getHttpServletRequest(request),
							"defhgfokusodpportaldataset_WAR_defhgfokusodpportaldatasetportlet",
							Long.valueOf(PropsUtil.get("page.datasets.plid")),
							PortletRequest.ACTION_PHASE);
			url.setParameter("packageId", entry.getCkanId());
			url.setParameter("action", "packageDetailsAction");
			if (entry.getTitle().length() >= 35) {
				entry.setTitle(entry.getTitle().subSequence(0, 35) + "...");
			}
			entry.setUrl(url.toString());
		}

		Collections.sort(datasets);

		return datasets;
	}
}
