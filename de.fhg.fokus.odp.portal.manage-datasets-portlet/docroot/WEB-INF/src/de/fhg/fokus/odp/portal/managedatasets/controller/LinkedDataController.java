package de.fhg.fokus.odp.portal.managedatasets.controller;

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

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;
import de.fhg.fokus.odp.portal.cache.gateway.VirtuosoGateway;
import de.fhg.fokus.odp.portal.managedatasets.domain.LinkedData;
import de.fhg.fokus.odp.portal.managedatasets.domain.MetaDataBean;
import de.fhg.fokus.odp.portal.managedatasets.domain.Resource;
import de.fhg.fokus.odp.portal.managedatasets.utils.HashMapUtils;
import de.fhg.fokus.odp.portal.managedatasets.utils.LocaleUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class LinkedDataController.
 */
@Controller(value = "linkedDataController")
@SessionAttributes({ "linkedData" })
@RequestMapping("VIEW")
public class LinkedDataController {

	/** The linked data validator. */
	@Autowired
	@Qualifier("linkedDataValidator")
	private Validator linkedDataValidator;

	/** The ckan gateway. */
	CKANGateway ckanGateway;

	/** The virtuoso gateway. */
	VirtuosoGateway virtuosoGateway;

	/** The logger. */
	Logger logger = Logger.getLogger(LinkedDataController.class.getName());

	/**
	 * Inits the.
	 */
	private void init() {
		ckanGateway = new CKANGateway(PropsUtil.get("cKANurl"),
				PropsUtil.get("authenticationKey"));
		virtuosoGateway = new VirtuosoGateway(PropsUtil.get("VirtuosoUrl"),
				PropsUtil.get("VirtuosoUser"), PropsUtil.get("VirtuosoPasswd"));
	}

	/**
	 * View create meta data.
	 * 
	 * @param response
	 *            the response
	 * @return the string
	 */
	@RenderMapping(params = "ocAction=createLinkedMetaDataRender")
	public String viewCreateMetaData(PortletResponse response) {
		init();
		return "linkedData";
	}

	/**
	 * Creates the metadata.
	 * 
	 * @param linkedData
	 *            the linked data
	 * @param bindingResult
	 *            the binding result
	 * @param response
	 *            the response
	 * @param sessionStatus
	 *            the session status
	 * @param request
	 *            the request
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws PortalException
	 *             the portal exception
	 * @throws SystemException
	 *             the system exception
	 * @throws ParseException
	 *             the parse exception
	 */
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
				.getDataSetDetails(linkedData.getPackageId()));
		metaData.setDate_updated(dateNow);
		metaData.setMetadata_modified(dateNow);

		Resource resource = new Resource();
		resource.setDescription(linkedData.getDescription());
		resource.setFormat(linkedData.getFormat());
		resource.setLanguage(linkedData.getLanguage());
		resource.setUrl(linkedData.getUrl());

		metaData.getResources().add(resource);

		ckanGateway.updateDataSet(metaData.getCkanId(),
				HashMapUtils.metaDataToMap(metaData));

		PortletURL url = PortletURLFactoryUtil
				.create(PortalUtil.getHttpServletRequest(request),
						"defhgfokusodpportaldataset_WAR_defhgfokusodpportaldatasetportlet",
						Long.valueOf(PropsUtil.get("page.datasets.plid")),
						PortletRequest.ACTION_PHASE);

		Map newPackage = (Map) ckanGateway
				.getDataSetDetails(metaData.getName());

		String id = (String) newPackage.get("id");
		url.setParameter("packageId", id);
		url.setParameter("action", "packageDetailsAction");

		sessionStatus.setComplete();
		response.sendRedirect(url.toString());

	}

	/**
	 * Inits the binder.
	 * 
	 * @param request
	 *            the request
	 * @param binder
	 *            the binder
	 * @throws Exception
	 *             the exception
	 */
	@InitBinder
	protected void initBinder(PortletRequest request,
			PortletRequestDataBinder binder) throws Exception {

		binder.registerCustomEditor(byte[].class,
				new ByteArrayMultipartFileEditor());
	}

	/**
	 * Gets the linked data.
	 * 
	 * @param pId
	 *            the id
	 * @param sessionStatus
	 *            the session status
	 * @param response
	 *            the response
	 * @return the linked data
	 * @throws ParseException
	 *             the parse exception
	 */
	@ModelAttribute(value = "linkedData")
	public LinkedData getLinkedData(
			@RequestParam(required = false, value = "packageId") String pId,
			SessionStatus sessionStatus, PortletResponse response)
			throws ParseException {

		sessionStatus.setComplete();
		LinkedData linkedData = new LinkedData();
		linkedData.setPackageId(pId);

		return linkedData;
	}
}
