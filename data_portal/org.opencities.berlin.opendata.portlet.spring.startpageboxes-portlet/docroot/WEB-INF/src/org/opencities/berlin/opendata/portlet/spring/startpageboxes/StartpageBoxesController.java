package org.opencities.berlin.opendata.portlet.spring.startpageboxes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import org.opencities.berlin.opendata.entity.model.BoxEntry;
import org.opencities.berlin.opendata.entity.service.BoxEntryLocalServiceUtil;
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

@Controller(value = "startpageBoxesController")
@RequestMapping("VIEW")
public class StartpageBoxesController {

	@RenderMapping
	public String view() {
		return "boxes";
	}

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

	@ModelAttribute(value = "datasets")
	public List<BoxEntry> getMostRecentDataSets(PortletRequest request,
			PortletResponse response) throws SystemException {

		List<BoxEntry> datasets = new ArrayList<BoxEntry>();

		for (BoxEntry entry : BoxEntryLocalServiceUtil.getBoxEntries(0, 3)) {

			PortletURL url = PortletURLFactoryUtil.create(
					PortalUtil.getHttpServletRequest(request),
					"ocdatasetsportlet_WAR_ocdatasetsportlet",
					Long.valueOf(PropsUtil.get("page.datasets.plid")),
					PortletRequest.RENDER_PHASE);
			url.setParameter("pId", entry.getCkanId());
			if (entry.getTitle().length() >= 35) {
				entry.setTitle(entry.getTitle().subSequence(0, 35) + "...");
			}
			entry.setUrl(url.toString());
			datasets.add(entry);
		}

		return datasets;
	}
}
