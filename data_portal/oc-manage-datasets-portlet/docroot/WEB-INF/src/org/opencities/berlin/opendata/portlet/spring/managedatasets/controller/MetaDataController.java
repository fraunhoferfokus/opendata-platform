package org.opencities.berlin.opendata.portlet.spring.managedatasets.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import org.opencities.berlin.opendata.ckan.gateway.CKANGateway;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.domain.MetaDataBean;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.domain.Resource;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.domain.ValueLabelEntry;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.utils.HashMapUtils;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.utils.HtmlComponentUtils;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.utils.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;

@Controller(value = "metaDataController")
@SessionAttributes({ "metaData" })
@RequestMapping("VIEW")
public class MetaDataController {

	@Autowired
	@Qualifier("metaDataValidator")
	private Validator metaDataValidator;

	CKANGateway ckanGateway;
	Logger logger = Logger.getLogger(MetaDataController.class.getName());

	private void init(PortletRequest request) {
		ckanGateway = new CKANGateway(PropsUtil.get("cKANurl"),
				PropsUtil.get("authenticationKey"));
		LocaleUtils.init(request);
	}

	@RenderMapping(params = "ocAction=createMetaDataRender")
	public ModelAndView viewCreateMetaData(PortletResponse response,
			PortletRequest request) {
		init(request);

		ModelAndView mv = new ModelAndView("metaData");

		MetaDataBean metaData = new MetaDataBean();
		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());

		mv.addObject("metaData", metaData);
		return mv;
	}

	@RenderMapping(params = "ocAction=editMetaDataRender")
	public String viewEditMetaDate(PortletResponse response,
			PortletRequest request) {
		init(request);
		return "metaData";
	}

	@ModelAttribute(value = "heading")
	public String getHeading(
			@RequestParam(required = false, value = "ocAction") String value,
			PortletRequest request) {

		String heading = LanguageUtil.get(request.getLocale(),
				"oc_create_metadata");

		if (value != null && value.equals("editMetaDataRender")) {
			heading = LanguageUtil.get(request.getLocale(), "oc_edit_metadata");
		} else if (value != null && value.equals("editLinkedMetaDataRender")) {
			heading = LanguageUtil.get(request.getLocale(),
					"oc_edit_linked_metadata");
		} else if (value != null && value.equals("createLinkedMetaDataRender")) {
			heading = LanguageUtil.get(request.getLocale(),
					"oc_create_linked_metadata");
		}

		return heading;
	}

	@ModelAttribute(value = "create")
	public boolean isCreate(
			@RequestParam(required = false, value = "ocAction") String value) {
		boolean result = true;

		if (value != null && value.contains("editMetaDataRender")) {
			result = false;
		}

		return result;
	}

	@ActionMapping(params = "ocAction=cancelAction")
	public void cancelAction(ActionRequest request,
			SessionStatus sessionStatus, ActionResponse response,
			@ModelAttribute("metaData") MetaDataBean metaData)
			throws IOException {

		PortletURL url = PortletURLFactoryUtil.create(
				PortalUtil.getHttpServletRequest(request),
				"ocdatasetsportlet_WAR_ocdatasetsportlet",
				Long.valueOf(PropsUtil.get("page.datasets.plid")),
				PortletRequest.RENDER_PHASE);

		if (metaData != null && metaData.getCkanId() != null) {
			url.setParameter("pId", metaData.getCkanId());
		}

		sessionStatus.setComplete();
		response.sendRedirect(url.toString());
	}

	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "ocAction=metaDataAction")
	public void createMetadata(
			@ModelAttribute("metaData") MetaDataBean metaData,
			BindingResult bindingResult, @RequestParam("action") String action,
			ActionResponse response, SessionStatus sessionStatus,
			ActionRequest request) throws IOException, PortalException,
			SystemException, ParseException {

		metaDataValidator.validate(metaData, bindingResult);

		if (bindingResult.hasErrors()) {
			response.setRenderParameter("ocAction", "createMetaDataRender");
			response.setRenderParameter("action", action);
			return;
		}

		Date dateNow = new Date();
		ThemeDisplay td = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		User user = UserLocalServiceUtil.getUserById(td.getUserId());

		metaData.setMaintainer(user.getFullName());
		metaData.setMaintainer_email(user.getEmailAddress());

		if (action.equals("createMetaData")) {
			metaData.setMetadata_created(dateNow);
			ckanGateway.createMetaDataSet(HashMapUtils.metaDataToMap(metaData));
		} else {
			// Restore Linked Data
			MetaDataBean metaDatalinkedData = HashMapUtils
					.mapToMetaData((Map) ckanGateway.getPackageDetails(metaData
							.getCkanId()));
			List<Resource> linkedDataList = new ArrayList<Resource>();
			Iterator<Resource> it = metaDatalinkedData.getResources()
					.iterator();
			while (it.hasNext()) {
				Resource resource = it.next();
				if (resource.getFormat().equalsIgnoreCase("RDF")) {
					linkedDataList.add(resource);
				}
			}
			metaData.getResources().addAll(linkedDataList);

			metaData.setDate_updated(dateNow);
			metaData.setMetadata_modified(dateNow);
			ckanGateway.updateMetaDataSet(metaData.getCkanId(),
					HashMapUtils.metaDataToMap(metaData));
		}

		PortletURL url = PortletURLFactoryUtil.create(
				PortalUtil.getHttpServletRequest(request),
				"ocdatasetsportlet_WAR_ocdatasetsportlet",
				Long.valueOf(PropsUtil.get("page.datasets.plid")),
				PortletRequest.RENDER_PHASE);

		Map newPackage = (Map) ckanGateway
				.getPackageDetails(metaData.getName());

		String id = (String) newPackage.get("id");
		url.setParameter("pId", id);

		sessionStatus.setComplete();
		response.sendRedirect(url.toString());

	}

	@InitBinder
	protected void initBinder(PortletRequest request,
			PortletRequestDataBinder binder) throws Exception {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
		binder.registerCustomEditor(Date.class, dateEditor);
	}

	@SuppressWarnings({ "rawtypes" })
	@ModelAttribute(value = "metaData")
	public MetaDataBean getMetaData(
			@RequestParam(required = false, value = "ocAction") String value,
			@RequestParam(required = false, value = "pId") String pId,
			SessionStatus sessionStatus, PortletResponse response,
			PortletRequest request) throws ParseException {
		MetaDataBean metaData = new MetaDataBean();

		if (value != null && value.equals("editMetaDataRender")) {
			init(request);
			metaData = HashMapUtils.mapToMetaData((Map) ckanGateway
					.getPackageDetails(pId));
			List<Resource> toDelete = new ArrayList<Resource>();
			Iterator<Resource> it = metaData.getResources().iterator();
			while (it.hasNext()) {
				Resource resource = it.next();
				if (resource.getFormat().equalsIgnoreCase("RDF")) {
					toDelete.add(resource);
				}
			}
			for (Resource resource : toDelete) {
				metaData.getResources().remove(resource);
			}
		} else {
			sessionStatus.setComplete();
		}

		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());

		return metaData;
	}

	@ModelAttribute(value = "licences")
	public List<ValueLabelEntry> getLicences() {

		return HtmlComponentUtils.createLicenses();
	}

	@ModelAttribute(value = "categories")
	public List<ValueLabelEntry> getCategories(PortletRequest request) {

		return HtmlComponentUtils.createCategories(request);
	}
}
