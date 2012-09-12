package de.fhg.fokus.odp.portal.managedatasets.controller;

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

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;
import de.fhg.fokus.odp.portal.managedatasets.domain.MetaDataBean;
import de.fhg.fokus.odp.portal.managedatasets.domain.Resource;
import de.fhg.fokus.odp.portal.managedatasets.domain.ValueLabelEntry;
import de.fhg.fokus.odp.portal.managedatasets.utils.HashMapUtils;
import de.fhg.fokus.odp.portal.managedatasets.utils.HtmlComponentUtils;
import de.fhg.fokus.odp.portal.managedatasets.utils.LocaleUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class MetaDataController.
 */
@Controller(value = "metaDataController")
@SessionAttributes({ "metaData" })
@RequestMapping("VIEW")
public class MetaDataController {

	/** The meta data validator. */
	@Autowired
	@Qualifier("metaDataValidator")
	private Validator metaDataValidator;

	/** The ckan gateway. */
	CKANGateway ckanGateway;

	/** The logger. */
	Logger logger = Logger.getLogger(MetaDataController.class.getName());

	/**
	 * Inits the.
	 * 
	 * @param request
	 *            the request
	 */
	private void init(PortletRequest request) {
		ckanGateway = new CKANGateway(PropsUtil.get("cKANurl"),
				PropsUtil.get("authenticationKey"));
		LocaleUtils.init(request);
	}

	/**
	 * View create meta data.
	 * 
	 * @param response
	 *            the response
	 * @param request
	 *            the request
	 * @return the model and view
	 */
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

	/**
	 * View edit meta date.
	 * 
	 * @param response
	 *            the response
	 * @param request
	 *            the request
	 * @return the string
	 */
	@RenderMapping(params = "ocAction=editMetaDataRender")
	public String viewEditMetaDate(PortletResponse response,
			PortletRequest request) {
		init(request);
		return "metaData";
	}

	/**
	 * Gets the heading.
	 * 
	 * @param value
	 *            the value
	 * @param request
	 *            the request
	 * @return the heading
	 */
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

	/**
	 * Checks if is creates the.
	 * 
	 * @param value
	 *            the value
	 * @return true, if is creates the
	 */
	@ModelAttribute(value = "create")
	public boolean isCreate(
			@RequestParam(required = false, value = "ocAction") String value) {
		boolean result = true;

		if (value != null && value.contains("editMetaDataRender")) {
			result = false;
		}

		return result;
	}

	/**
	 * Cancel action.
	 * 
	 * @param request
	 *            the request
	 * @param sessionStatus
	 *            the session status
	 * @param response
	 *            the response
	 * @param metaData
	 *            the meta data
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@ActionMapping(params = "ocAction=cancelAction")
	public void cancelAction(ActionRequest request,
			SessionStatus sessionStatus, ActionResponse response,
			@ModelAttribute("metaData") MetaDataBean metaData)
			throws IOException {

		PortletURL url = PortletURLFactoryUtil.create(
				PortalUtil.getHttpServletRequest(request),
				"defhgfokusodpportaldataset_WAR_defhgfokusodpportaldataset",
				Long.valueOf(PropsUtil.get("page.datasets.plid")),
				PortletRequest.RENDER_PHASE);

		if (metaData != null && metaData.getCkanId() != null) {
			url.setParameter("pId", metaData.getCkanId());
		}

		sessionStatus.setComplete();
		response.sendRedirect(url.toString());
	}

	/**
	 * Creates the metadata.
	 * 
	 * @param metaData
	 *            the meta data
	 * @param bindingResult
	 *            the binding result
	 * @param action
	 *            the action
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

		if (action.contains("createMetaData")) {
			metaData.setMetadata_created(dateNow);
			ckanGateway.createDataSet(HashMapUtils.metaDataToMap(metaData));
		} else {
			// Restore Linked Data
			MetaDataBean metaDatalinkedData = HashMapUtils
					.mapToMetaData((Map) ckanGateway.getDataSetDetails(metaData
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
			ckanGateway.updateDataSet(metaData.getCkanId(),
					HashMapUtils.metaDataToMap(metaData));
		}

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

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
		binder.registerCustomEditor(Date.class, dateEditor);
	}

	/**
	 * Gets the meta data.
	 * 
	 * @param value
	 *            the value
	 * @param pId
	 *            the id
	 * @param sessionStatus
	 *            the session status
	 * @param response
	 *            the response
	 * @param request
	 *            the request
	 * @return the meta data
	 * @throws ParseException
	 *             the parse exception
	 */
	@SuppressWarnings({ "rawtypes" })
	@ModelAttribute(value = "metaData")
	public MetaDataBean getMetaData(
			@RequestParam(required = false, value = "ocAction") String value,
			@RequestParam(required = false, value = "packageId") String pId,
			SessionStatus sessionStatus, PortletResponse response,
			PortletRequest request) throws ParseException {
		MetaDataBean metaData = new MetaDataBean();

		if (value != null && value.equals("editMetaDataRender")) {
			init(request);
			metaData = HashMapUtils.mapToMetaData((Map) ckanGateway
					.getDataSetDetails(pId));
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
		}

		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());
		metaData.getResources().add(new Resource());
		sessionStatus.setComplete();

		return metaData;
	}

	/**
	 * Gets the licences.
	 * 
	 * @return the licences
	 */
	@ModelAttribute(value = "licences")
	public List<ValueLabelEntry> getLicences() {

		return HtmlComponentUtils.createLicenses();
	}

	/**
	 * Gets the categories.
	 * 
	 * @param request
	 *            the request
	 * @return the categories
	 */
	@ModelAttribute(value = "categories")
	public List<ValueLabelEntry> getCategories(PortletRequest request) {

		return HtmlComponentUtils.createCategories(request);
	}
}
