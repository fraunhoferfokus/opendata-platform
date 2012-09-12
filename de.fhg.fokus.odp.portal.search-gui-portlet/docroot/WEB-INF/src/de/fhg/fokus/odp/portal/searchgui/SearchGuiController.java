package de.fhg.fokus.odp.portal.searchgui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;

@Controller(value = "searchGuiController")
@RequestMapping("VIEW")
public class SearchGuiController {

	private static final Logger log = Logger
			.getLogger(SearchGuiController.class);

	private final String DATASET_PORTLET_NAME = "defhgfokusodpportaldataset_WAR_defhgfokusodpportaldatasetportlet";
	private final String EMPTY = "";
	private final String UTF8 = "UTF-8";
	private final String AMP = "&";

	@SuppressWarnings("unchecked")
	@RenderMapping
	public ModelAndView view(RenderRequest renderRequest,
			RenderResponse renderResponse) throws ParseException {

		// if there was a search action
		// -> "remember" search params for JSP
		HttpServletRequest originalRequest = PortalUtil
				.getOriginalServletRequest(PortalUtil
						.getHttpServletRequest(renderRequest));
		String datasetPortletNamesp = "_" + DATASET_PORTLET_NAME + "_";
		String query = ParamUtil.getString(originalRequest,
				datasetPortletNamesp + "query", EMPTY);

		ModelAndView mv = new ModelAndView("view");

		if (!query.isEmpty()) {
			JSONParser parser = new JSONParser();
			Map<String, String> qMap = (JSONObject) parser.parse(query);
			SearchForm sForm = new SearchForm();
			sForm.setQuery(qMap.get("query"));
			sForm.setAuthor(qMap.get("author"));
			sForm.setTags(qMap.get("tags"));
			sForm.setTitle(qMap.get("title"));
			sForm.setDescription(qMap.get("description"));
			sForm.setMaintainer(qMap.get("maintainer"));
			sForm.setGroup(qMap.get("group"));
			sForm.setGeographical_coverage(qMap.get("geographical_coverage"));
			sForm.setGeographical_granularity(qMap
					.get("geographical_granularity"));
			sForm.setTemporal_coverage_from(qMap.get("temporal_coverage_from"));
			sForm.setTemporal_coverage_to(qMap.get("temporal_coverage_to"));
			sForm.setTemporal_granularity(qMap.get("temporal_granularity"));
			mv.addObject("searchForm", sForm);
		}

		return mv;
	}

	@ModelAttribute(value = "groups")
	public List<Group> getCategories(PortletRequest request) {
		CKANGateway ckanGateway = new CKANGateway(PropsUtil.get("cKANurl"),
				PropsUtil.get("authenticationKey"));

		// return entries
		List<Group> groups = new ArrayList<Group>();

		HashMap<String, Object> groupsMap = ckanGateway.getGroupsData();

		// get keys as an array and sort it
		String[] keys = groupsMap.keySet().toArray(new String[0]);
		Arrays.sort(keys);

		groups.add(0,
				new Group("", LanguageUtil.get(request.getLocale(), "select")));

		for (String key : keys) {
			// TODO rename incorrectly named oc_category-* keys
			String translationProperty = "oc_category-" + key;
			String translatedName = LanguageUtil.get(request.getLocale(),
					translationProperty);
			groups.add(new Group(key, translatedName));
		}

		return groups;
	}

	@ModelAttribute(value = "searchForm")
	public SearchForm getSearchForm() {
		return new SearchForm();
	}

	/**
	 * Triggered by search submit in this portlets view. Gets search params from
	 * search form and builds query string. Redirects (switches page) to
	 * dataset-portlet with parameters action and packageSearchString (finally
	 * for event processing through search-portlet).
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@ActionMapping("packageSearchAction")
	public void packageSearchAction(ActionResponse actionResponse,
			ActionRequest actionRequest, @ModelAttribute SearchForm searchForm)
			throws UnsupportedEncodingException {

		String packageSearchString;
		try {
			packageSearchString = buildPackageSearchQueryString(searchForm);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		PortletURL url = PortletURLFactoryUtil.create(
				PortalUtil.getHttpServletRequest(actionRequest),
				DATASET_PORTLET_NAME,
				Long.valueOf(PropsUtil.get("page.datasets.plid")),
				PortletRequest.RENDER_PHASE);

		QName qname = new QName("http://sun.com/tourevents", "packageSearch");
		actionResponse.setEvent(qname, packageSearchString);
		url.setParameter("query", buildPackageSearchQueryHashMap(searchForm));

		log.debug("redirect url: " + url);
		try {
			actionResponse.sendRedirect(url.toString());
		} catch (IOException e) {
			e.printStackTrace();
			log.debug("sendRedirect( " + url + " ) failed");
		}
	}

	private String buildPackageSearchQueryHashMap(SearchForm searchForm)
			throws UnsupportedEncodingException {

		Map<String, String> searchMap = new HashMap<String, String>();

		String query = searchForm.getQuery();
		if (!query.equals(EMPTY)) {
			String encoded = URLEncoder.encode(query, UTF8);
			searchMap.put("query", encoded);
		}
		String title = searchForm.getTitle();
		if (!title.equals(EMPTY)) {
			String encoded = URLEncoder.encode(title, UTF8);
			searchMap.put("title", encoded);
		}
		String author = searchForm.getAuthor();
		if (!author.equals(EMPTY)) {
			String encoded = URLEncoder.encode(author, UTF8);
			searchMap.put("author", encoded);
		}

		if (!searchForm.getDescription().equals(EMPTY)) {
			String encoded = URLEncoder.encode(searchForm.getDescription(),
					UTF8);
			searchMap.put("description", encoded);
		}

		if (!searchForm.getMaintainer().equals(EMPTY)) {
			String encoded = URLEncoder
					.encode(searchForm.getMaintainer(), UTF8);
			searchMap.put("maintainer", encoded);
		}

		if (!searchForm.getGroup().equals(EMPTY)) {
			String encoded = URLEncoder.encode(searchForm.getGroup(), UTF8);
			searchMap.put("group", encoded);
		}

		if (!searchForm.getTags().equals(EMPTY)) {
			String encoded = URLEncoder.encode(searchForm.getTags(), UTF8);
			searchMap.put("tags", encoded);
		}

		if (!searchForm.getTemporal_coverage_from().equals(EMPTY)) {
			String encoded = URLEncoder.encode(
					searchForm.getTemporal_coverage_from(), UTF8);
			searchMap.put("temporal_coverage_from", encoded);
		}

		if (!searchForm.getTemporal_coverage_to().equals(EMPTY)) {
			String encoded = URLEncoder.encode(
					searchForm.getTemporal_coverage_to(), UTF8);
			searchMap.put("temporal_coverage_to", encoded);
		}

		if (!searchForm.getTemporal_granularity().equals(EMPTY)) {
			String encoded = URLEncoder.encode(
					searchForm.getTemporal_granularity(), UTF8);
			searchMap.put("temporal_granularity", encoded);
		}

		if (!searchForm.getGeographical_coverage().equals(EMPTY)) {
			String encoded = URLEncoder.encode(
					searchForm.getGeographical_coverage(), UTF8);
			searchMap.put("geographical_coverage", encoded);
		}

		if (!searchForm.getGeographical_granularity().equals(EMPTY)) {
			String encoded = URLEncoder.encode(
					searchForm.getGeographical_granularity(), UTF8);
			searchMap.put("geographical_granularity", encoded);
		}

		log.debug("SearchHashMap: " + searchMap.toString());

		return JSONValue.toJSONString(searchMap);
	}

	private String buildPackageSearchQueryString(SearchForm searchForm)
			throws UnsupportedEncodingException {

		StringBuilder packageSearchString = new StringBuilder("");

		// Get search parameters from search form and build search query string
		String query = searchForm.getQuery();
		if (!query.equals(EMPTY)) {
			String encoded = URLEncoder.encode(query, UTF8);
			packageSearchString.append("q=" + encoded + AMP);
		}
		String title = searchForm.getTitle();
		if (!title.equals(EMPTY)) {
			String encoded = URLEncoder.encode(title, UTF8);
			packageSearchString.append("title=" + encoded + AMP);
		}
		String author = searchForm.getAuthor();
		if (!author.equals(EMPTY)) {
			String encoded = URLEncoder.encode(author, UTF8);
			packageSearchString.append("author=" + encoded + AMP);
		}
		String group = searchForm.getGroup();
		if (!group.equals(EMPTY)) {
			String encoded = URLEncoder.encode(group, UTF8);
			packageSearchString.append("groups=" + encoded + AMP);
		}

		String temporalCoverageFrom = searchForm.getTemporal_coverage_from();
		if (!temporalCoverageFrom.equals(EMPTY)) {
			String encoded = URLEncoder.encode(temporalCoverageFrom, UTF8);
			packageSearchString.append("temporal_coverage_from=" + encoded
					+ AMP);
		}

		String temporalCoverageTo = searchForm.getTemporal_coverage_to();
		if (!temporalCoverageTo.equals(EMPTY)) {
			String encoded = URLEncoder.encode(temporalCoverageTo, UTF8);
			packageSearchString.append("temporal_coverage_to=" + encoded + AMP);
		}

		String temporalGranularity = searchForm.getTemporal_granularity();
		if (!temporalGranularity.equals(EMPTY)) {
			String encoded = URLEncoder.encode(temporalGranularity, UTF8);
			packageSearchString.append("temporal_granularity=" + encoded + AMP);
		}

		String tags = searchForm.getTags();
		if (!tags.equals(EMPTY)) {
			String encoded = URLEncoder.encode(tags, UTF8);
			packageSearchString.append("tags=" + encoded + AMP);
		}

		String geographicalCoverage = searchForm.getGeographical_coverage();
		if (!geographicalCoverage.equals(EMPTY)) {
			String encoded = URLEncoder.encode(geographicalCoverage, UTF8);
			packageSearchString
					.append("geographical_coverage=" + encoded + AMP);
		}

		String geographicalGranularity = searchForm
				.getGeographical_granularity();
		if (!geographicalGranularity.equals(EMPTY)) {
			String encoded = URLEncoder.encode(geographicalGranularity, UTF8);
			packageSearchString.append("geographical_granularity=" + encoded
					+ AMP);
		}

		// remove last AMP when existing
		int lastIndexOfAmp = packageSearchString.lastIndexOf(AMP);
		if (lastIndexOfAmp != -1) {
			packageSearchString.deleteCharAt(lastIndexOfAmp);
			log.debug("removed last \"" + AMP + "\"");
		}

		// if there was no search input, do empty search/search for all
		if (packageSearchString.length() == 0) {
			packageSearchString.append(" ");
			log.debug("do empty search / search for all");
		}

		log.debug("buildPackageSearchQueryString(..) query: "
				+ packageSearchString.toString());
		return packageSearchString.toString();
	}

}
