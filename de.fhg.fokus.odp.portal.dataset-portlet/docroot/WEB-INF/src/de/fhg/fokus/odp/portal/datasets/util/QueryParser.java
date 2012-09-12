package de.fhg.fokus.odp.portal.datasets.util;

import java.util.Locale;

import javax.portlet.RenderRequest;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * This class provides a method for parsing the searchQuery.
 * 
 * @author pla
 * 
 */
public final class QueryParser {
	/** the conjunction. */
	private static final String SEARCH_QUERY_CONJUNCTION = "oc-datasets_conjunction";

	/**
	 * prevent initiation.
	 */
	private QueryParser() {
	}

	/**
	 * This method replaces all occurences of & and q= and builds the result
	 * string.
	 * 
	 * @param request
	 *            the {@link RenderRequest} containing the complete SearchQuery
	 *            and themeDisplay.
	 * @return the result string.
	 */
	public static String parseSearchQuery(RenderRequest request) {
		String searchQuery = ParamUtil.getString(request, "searchQuery");
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		Locale locale = themeDisplay.getLocale();
		String conjunction = LanguageUtil.get(locale, SEARCH_QUERY_CONJUNCTION);

		String result = searchQuery.replaceAll("&limit=\\d+", "");
		result = result.replaceAll("&", " " + conjunction + " ");
		result = result.replaceAll("q=", "");
		return result;
	}
}
