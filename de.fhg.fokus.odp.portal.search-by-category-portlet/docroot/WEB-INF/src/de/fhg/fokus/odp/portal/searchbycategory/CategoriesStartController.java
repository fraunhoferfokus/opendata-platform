package de.fhg.fokus.odp.portal.searchbycategory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.xml.namespace.QName;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;

/**
 * @author bdi
 * 
 */
@Controller(value = "categoriesStartController")
@RequestMapping("VIEW")
public class CategoriesStartController {

	/**
	 * Redirect to portlet start page
	 * 
	 * @return the page to show
	 */
	@RenderMapping
	public String viewListDefault() {

		return "view";
	}

	/**
	 * Redirect to result page
	 * 
	 * @return the page to show
	 */
	@ModelAttribute(value = "plId")
	public String getPlId() {
		return PropsUtil.get("page.datasets.plid");
	}

	@ActionMapping(params = "action=packageSearchAction")
	public void search(ActionResponse actionResponse,
			ActionRequest actionRequest) throws IOException {
		final String DATASET_PORTLET_NAME = "defhgfokusodpportaldataset_WAR_defhgfokusodpportaldatasetportlet";
		PortletURL url = PortletURLFactoryUtil.create(

		PortalUtil.getHttpServletRequest(actionRequest), DATASET_PORTLET_NAME,
				Long.valueOf(PropsUtil.get("page.datasets.plid")),
				PortletRequest.RENDER_PHASE);
		QName qname = new QName("http://sun.com/tourevents", "packageSearch");

		Map<String, String> searchMap = new HashMap<String, String>();
		searchMap.put("group", actionRequest.getParameter("searchQuery"));
		actionResponse.setEvent(qname,
				"groups=" + actionRequest.getParameter("searchQuery"));

		actionResponse.sendRedirect(url.toString());
	}
}
