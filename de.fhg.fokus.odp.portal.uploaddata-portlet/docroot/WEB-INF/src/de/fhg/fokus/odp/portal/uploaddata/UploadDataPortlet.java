package de.fhg.fokus.odp.portal.uploaddata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.ProcessAction;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowStateException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;

import com.liferay.portal.kernel.util.PropsUtil;

import de.fhg.fokus.odp.portal.uploaddata.service.Worker;

public class UploadDataPortlet extends GenericPortlet {
	private static final long MAX_UPLOAD_FILE_SIZE = 1024 * 1024;
	private final String myaction="myaction"; 

	@RenderMode(name = "VIEW")
	public void showForm(RenderRequest request, RenderResponse response)
			throws IOException, PortletException {

		// generate all the URLs that will be used by the portlet
		generateUrls(request, response);

		String myaction = request.getParameter(this.myaction);
		if (myaction != null) {
			request.getPortletSession().setAttribute(this.myaction, myaction,
					PortletSession.PORTLET_SCOPE);
		} else {
			// if myaction is NULL then show form
			request.getPortletSession().setAttribute(this.myaction, "uploadForm",
					PortletSession.PORTLET_SCOPE);
		}

		// send myaction as a request attribute to the Servlet.
		request.setAttribute(this.myaction, request.getPortletSession()
				.getAttribute(this.myaction));

		response.setTitle("Upload Data");

		// its important to encode URLs
		PortletRequestDispatcher dispatcher = request
				.getPortletSession()
				.getPortletContext()
				.getRequestDispatcher(
						response.encodeURL("/myservlet/uploadServlet"));
		dispatcher.include(request, response);
	}

	/**
	 * allows user to download template.xlsx.
	 */
	@Override
	public void serveResource(ResourceRequest request, ResourceResponse response)
			throws IOException, PortletException {

		//retrieve dlfolder from Properties file
		String dlFolder = PropsUtil.get("downloadFolder");
		File file = new File(dlFolder + "template.xlsx");
		OutputStream outStream = response.getPortletOutputStream();
		if (!file.exists() || !file.canRead()) {
			outStream
					.write(("<i>Unable to find the specified file. Please contact an administrator. </i>")
							.getBytes());
		} else {
			FileInputStream inStream = new FileInputStream(file);
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setProperty("Content-disposition",
					"attachment; filename=\"" + request.getResourceID() + "\"");
			byte[] buffer = new byte[1024];
			try {
				while (true) {
					int bytes = inStream.read(buffer);
					if (bytes <= 0) {
						break;
					}
					outStream.write(buffer, 0, bytes);
				}
			} finally {
				inStream.close();
				outStream.flush();
				outStream.close();
			}
		}
	}

	/**
	 * uploads XLSX and processes it. Afterwards all generated files will be
	 * deleted.
	 * 
	 * @param request
	 * @param response
	 * @throws PortletException
	 * @throws IOException
	 * @throws FileUploadException
	 */
	@SuppressWarnings("rawtypes")
	@ProcessAction(name = "uploadAction")
	public void uploadXLSX(ActionRequest request, ActionResponse response)
			throws PortletException, IOException, FileUploadException {

		FileItemFactory factory = new DiskFileItemFactory();
		PortletFileUpload upload = new PortletFileUpload(factory);
		upload.setFileSizeMax(MAX_UPLOAD_FILE_SIZE);

		// retrieve key, ulFolder and ckan url from Properties file
		String key = PropsUtil.get("authenticationKey");
		String ulFolder = PropsUtil.get("uploadFolder");
		String ckan = PropsUtil.get("cKANurl");
		List items = upload.parseRequest(request);

		try {
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (!item.isFormField()) {
					File uploadedFile = new File(ulFolder + "file.xlsx");
					item.write(uploadedFile);
				}
			}
			String result = null;
			if (key != null) { // retrieved an api key

				result = new Worker(ckan, key, ulFolder).readXlsx();
			}

			if (result.equalsIgnoreCase("")){
				response.setRenderParameter(this.myaction, "success");
			} else {
				response.setRenderParameter(this.myaction, "error");
				response.setRenderParameter("exceptionMsg",
						"Error in the following datasets: " + result);
			}
		} catch (Exception e) {
			// error in item.write(uploadedFile)
			response.setRenderParameter(this.myaction, "error");
			response.setRenderParameter("exceptionMsg",
					"Error while uploading xlsx file.");
		} finally {
			// delete generated file
			File file = new File(ulFolder + "file.xlsx");
			if (file != null && file.canRead() && file.canWrite()) {
				file.delete();
			}
		}
	}

	/*
	 * Generates URLs that will be used by the portlet.
	 */
	private void generateUrls(RenderRequest request, RenderResponse response)
			throws PortletModeException, WindowStateException {
		// Action URL for upload action
		PortletURL uploadActionUrl = response.createActionURL();
		uploadActionUrl.setParameter(this.myaction, "uploadAction");
		uploadActionUrl.setParameter(ActionRequest.ACTION_NAME, "uploadAction");
		request.setAttribute("uploadActionUrl", uploadActionUrl);

		// Render URL for Home link
		PortletURL homeUrl = response.createRenderURL();
		homeUrl.setPortletMode(PortletMode.VIEW);
		request.setAttribute("homeUrl", homeUrl);
	}
}