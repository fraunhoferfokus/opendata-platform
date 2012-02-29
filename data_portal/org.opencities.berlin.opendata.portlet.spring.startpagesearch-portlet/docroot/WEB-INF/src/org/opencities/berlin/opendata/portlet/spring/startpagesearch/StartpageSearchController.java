package org.opencities.berlin.opendata.portlet.spring.startpagesearch;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;

@Controller(value = "startpageSearchController")
@SessionAttributes({ "searchQuery" })
@RequestMapping("VIEW")
public class StartpageSearchController {

	@RenderMapping
	public String view() {
		return "view";
	}

	@ActionMapping(params = "dcAction=search")
	public void search(@ModelAttribute("searchQuery") SearchQuery query,
			BindingResult bindingResult, ActionRequest request,
			ActionResponse response, SessionStatus sessionStatus)
			throws IOException {
		PortletURL url = PortletURLFactoryUtil.create(
				PortalUtil.getHttpServletRequest(request),
				"ocdatasetsportlet_WAR_ocdatasetsportlet",
				Long.valueOf(PropsUtil.get("page.datasets.plid")),
				PortletRequest.RENDER_PHASE);
		url.setParameter("externSearchQuery", query.getQuery());
		sessionStatus.setComplete();
		response.sendRedirect(url.toString());
	}

	@ModelAttribute(value = "searchQuery")
	public SearchQuery getSearchQuery(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return new SearchQuery();
	}

}
