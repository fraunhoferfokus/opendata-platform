package de.fhg.fokus.odp.portal.managedatasets.utils;

import java.util.Locale;

import javax.portlet.PortletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class LocaleUtils.
 */
public class LocaleUtils {

	/** The locale. */
	private static Locale locale;

	/**
	 * Inits the.
	 *
	 * @param request the request
	 */
	public static void init(PortletRequest request) {
		locale = request.getLocale();
	}

	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public static Locale getLocale() {
		return locale;
	}
}
