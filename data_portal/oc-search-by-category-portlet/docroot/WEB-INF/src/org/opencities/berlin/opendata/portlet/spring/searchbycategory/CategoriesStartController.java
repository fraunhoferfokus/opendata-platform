package org.opencities.berlin.opendata.portlet.spring.searchbycategory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.util.PropsUtil;

@Controller(value = "categoriesStartController")
@RequestMapping("VIEW")
public class CategoriesStartController {

	@RenderMapping
	public String viewListDefault() {

		return "view";
	}

	@ModelAttribute(value = "plId")
	public String getPlId() {
		return PropsUtil.get("page.datasets.plid");
	}
}
