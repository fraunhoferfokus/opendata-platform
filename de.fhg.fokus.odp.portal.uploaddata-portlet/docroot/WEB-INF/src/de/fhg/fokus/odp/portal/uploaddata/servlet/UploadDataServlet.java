package de.fhg.fokus.odp.portal.uploaddata.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadDataServlet extends HttpServlet {
	private static final long serialVersionUID = 6141948500774706345L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String action = (String) request.getAttribute("myaction");

		if (action != null && action.equalsIgnoreCase("success")) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/WEB-INF/jsp/success.jsp");
			dispatcher.include(request, response);
		}
		if (action != null
				&& (action.equalsIgnoreCase("uploadForm") || action
						.equalsIgnoreCase("uploadAction"))) {
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/WEB-INF/jsp/uploadForm.jsp");
			dispatcher.include(request, response);
		}
		if (action != null && action.equalsIgnoreCase("error")) {
			request.setAttribute("exceptionMsg", request
					.getParameter("exceptionMsg"));
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/WEB-INF/jsp/error.jsp");
			dispatcher.include(request, response);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doGet(request, response);
	}
}