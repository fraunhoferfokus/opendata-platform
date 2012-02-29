package org.opencities.berlin.opendata.portlet.spring.managedatasets.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtilsOc {

	private static final String VALID_NAME_PATTERN = "^[\\d\\w-]{2,255}$";
	private static final String VALID_EMAIL_PATTERN = "^[\\w\\.=-]+@[\\w\\.-]+\\.[\\w]{2,5}$";
	private static final String VALID_URL_PATTERN = "(?:https?://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)(?:/(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*)(?:/(?:(?:(?:[a-zA-Z\\d$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*))*)(?:\\?(?:(?:(?:[a-zA-Z\\d$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*))?)?)";
	private static final int STANDARD_LENGTH = 255;

	public static boolean validDate(String date) {
		final String DATE_FORMAT = "yyyy-MM-dd";
		boolean result = true;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			sdf.parse(date);
		} catch (ParseException ex) {
			result = false;
		}

		return result;
	}

	public static boolean validPackageName(String name) {

		Pattern pattern = Pattern.compile(VALID_NAME_PATTERN);
		Matcher matcher = pattern.matcher(name);

		return matcher.find();
	}

	public static boolean validEmail(String email) {

		Pattern pattern = Pattern.compile(VALID_EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);

		return matcher.find();
	}

	public static boolean validUrl(String url) {

		Pattern pattern = Pattern.compile(VALID_URL_PATTERN);
		Matcher matcher = pattern.matcher(url);

		return matcher.find();
	}

	public static boolean validStandardLength(String string) {
		return string.length() <= STANDARD_LENGTH;
	}

}
