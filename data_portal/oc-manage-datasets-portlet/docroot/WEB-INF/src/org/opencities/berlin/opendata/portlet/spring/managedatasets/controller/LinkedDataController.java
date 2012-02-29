package org.opencities.berlin.opendata.portlet.spring.managedatasets.controller;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;

import org.opencities.berlin.opendata.ckan.gateway.CKANGateway;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.domain.LinkedData;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.domain.MetaDataBean;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.domain.Resource;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.utils.HashMapUtils;
import org.opencities.berlin.opendata.portlet.spring.managedatasets.utils.LocaleUtils;
import org.opencities.berlin.opendata.virtuoso.gateway.VirtuosoGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.multipart.MultipartActionRequest;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;

@Controller(value = "linkedDataController")
@SessionAttributes({ "linkedData" })
@RequestMapping("VIEW")
public class LinkedDataController {

	@Autowired
	@Qualifier("linkedDataValidator")
	private Validator linkedDataValidator;

	CKANGateway ckanGateway;
	VirtuosoGateway virtuosoGateway;
	Logger logger = Logger.getLogger(LinkedDataController.class.getName());

	private void init() {
		ckanGateway = new CKANGateway(PropsUtil.get("cKANurl"),
				PropsUtil.get("authenticationKey"));
		virtuosoGateway = new VirtuosoGateway(PropsUtil.get("VirtuosoUrl"),
				PropsUtil.get("VirtuosoUser"), PropsUtil.get("VirtuosoPasswd"));
	}

	@RenderMapping(params = "ocAction=createLinkedMetaDataRender")
	public String viewCreateMetaData(PortletResponse response) {
		init();
		return "linkedData";
	}

	@SuppressWarnings("rawtypes")
	@ActionMapping(params = "ocAction=linkedDataAction")
	public void createMetadata(
			@ModelAttribute("linkedData") LinkedData linkedData,
			BindingResult bindingResult, ActionResponse response,
			SessionStatus sessionStatus, MultipartActionRequest request)
			throws IOException, PortalException, SystemException,
			ParseException {

		MultipartFile mFile = request.getFile("file");
		URL mFileUrl;
		if (mFile != null && mFile.getBytes().length > 0) {
			linkedData.setValidFile(true);
		}
		LocaleUtils.init(request);
		linkedDataValidator.validate(linkedData, bindingResult);

		if (bindingResult.hasErrors()) {
			response.setRenderParameter("ocAction",
					"createLinkedMetaDataRender");
			return;
		}

		mFileUrl = virtuosoGateway.uploadRDFResource(mFile.getInputStream(),
				mFile.getOriginalFilename());
		linkedData.setUrl(mFileUrl.toString());
		Date dateNow = new Date();
		MetaDataBean metaData = HashMapUtils.mapToMetaData((Map) ckanGateway
				.getPackageDetails(linkedData.getPackageId()));
		metaData.setDate_updated(dateNow);
		metaData.setMetadata_modified(dateNow);

		Resource resource = new Resource();
		resource.setDescription(linkedData.getDescription());
		resource.setFormat(linkedData.getFormat());
		resource.setLanguage(linkedData.getLanguage());
		resource.setUrl(linkedData.getUrl());

		metaData.getResources().add(resource);

		ckanGateway.updateMetaDataSet(metaData.getCkanId(),
				HashMapUtils.metaDataToMap(metaData));

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

		binder.registerCustomEditor(byte[].class,
				new ByteArrayMultipartFileEditor());
	}

	@ModelAttribute(value = "linkedData")
	public LinkedData getLinkedData(
			@RequestParam(required = false, value = "pId") String pId,
			SessionStatus sessionStatus, PortletResponse response)
			throws ParseException {

		sessionStatus.setComplete();
		LinkedData linkedData = new LinkedData();
		linkedData.setPackageId(pId);

		return linkedData;
	}
}
