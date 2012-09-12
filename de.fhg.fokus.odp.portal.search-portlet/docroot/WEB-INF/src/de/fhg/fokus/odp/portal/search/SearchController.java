package de.fhg.fokus.odp.portal.search;

import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller(value = "searchController")
@RequestMapping("VIEW")
public class SearchController {

	private static final Logger log = Logger.getLogger(SearchController.class);

	@RenderMapping
	public String view() {
		return "view";
	}

	@ModelAttribute(value = "search")
	public Search getSearch() {
		return new Search();
	}

	/**
	 * Test method for the IPC event mechanism.<br />
	 * Simulates external firing of a packageDetails event.
	 */
	/*
	 * @ActionMapping("packageDetailsAction") public void
	 * packageDetailsAction(ActionResponse response) { QName qname = new
	 * QName("http://sun.com/tourevents","packageDetails"); String
	 * packageDetailsId = "6b9a65dc-51ea-44f2-8863-3ac0936e36f7";
	 * response.setEvent(qname, packageDetailsId);
	 * log.debug("packageDetailsAction(..) -> fired event "+ qname); }
	 */

	/**
	 * Recieves a <code>{http://sun.com/tourevents}packageDetails</code> event. <br />
	 * Searches for packageDetails via <code>CKANGateway</code> and fires a
	 * <code>{http://sun.com/tourevents}packageDetailsResult</code> event param
	 * of type <code>JSONObject</code> containing the result.
	 */
	@EventMapping(value = "{http://sun.com/tourevents}packageDetails")
	public void processPackageDetailsEvent(EventRequest request,
			EventResponse response, @ModelAttribute Search search) {
		Event event = request.getEvent();
		String packageDetailsId = (String) event.getValue();
		String msg = "processPackageDetailsEvent(..) -> recieved "
				+ event.getQName() + " with packageDetailsId "
				+ packageDetailsId.toString();
		log.debug(msg);
		JSONObject packageDetailsResult = search
				.getPackageDetails(packageDetailsId);
		// send event PackageDetailsResult
		QName qname = new QName("http://sun.com/tourevents",
				"packageDetailsResult");
		response.setEvent(qname, packageDetailsResult.toJSONString());
		response.setRenderParameter("searchQuery",
				((String) event.getValue()).replaceAll(" ", "+"));
		log.debug("fired event " + qname);
	}

	/**
	 * Recieves a <code>{http://sun.com/tourevents}packageSearch</code> event
	 * containing the package search query as a <code>String</code>. <br />
	 * Spaces will be replaced by "+".<br />
	 * Does a package search via <code>CKANGateway</code> and fires a
	 * <code>{http://sun.com/tourevents}packageSearchResult</code> event param
	 * of type <code>JSONObject</code> containing the result.
	 */
	@EventMapping(value = "{http://sun.com/tourevents}packageSearch")
	public void processPackageSearchEvent(@ModelAttribute Search search,
			BindingResult bindingResult, EventRequest request,
			EventResponse response) {
		Event event = request.getEvent();
		String packageSearchQuery = ((String) event.getValue()).replaceAll(" ",
				"+");
		log.debug("processPackageSearchEvent(..) recieved " + event.getQName()
				+ " with value " + packageSearchQuery);
		JSONObject packageSearchResult = search
				.doPackageSearch(packageSearchQuery);
		// send event PackageSearchResults
		QName qname = new QName("http://sun.com/tourevents",
				"packageSearchResult");
		response.setEvent(qname, packageSearchResult.toJSONString());
		response.setRenderParameter("searchQuery",
				((String) event.getValue()).replaceAll(" ", "+"));
	}
}
