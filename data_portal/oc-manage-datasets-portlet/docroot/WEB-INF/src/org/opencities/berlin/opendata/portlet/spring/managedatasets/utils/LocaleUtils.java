package org.opencities.berlin.opendata.portlet.spring.managedatasets.utils;

import java.util.Locale;

import javax.portlet.PortletRequest;

public class LocaleUtils {

	private static Locale locale;

	public static void init(PortletRequest request) {
		locale = request.getLocale();
	}

	public static Locale getLocale() {
		return locale;
	}
}
