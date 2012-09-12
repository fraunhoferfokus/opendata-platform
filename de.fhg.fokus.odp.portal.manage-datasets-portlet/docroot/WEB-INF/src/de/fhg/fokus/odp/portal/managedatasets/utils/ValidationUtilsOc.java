package de.fhg.fokus.odp.portal.managedatasets.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * The Class ValidationUtilsOc.
 */
public class ValidationUtilsOc {

	/** The Constant VALID_NAME_PATTERN. */
	private static final String VALID_NAME_PATTERN = "^[\\d\\w-]{2,255}$";
	
	/** The Constant VALID_EMAIL_PATTERN. */
	private static final String VALID_EMAIL_PATTERN = "^[\\w\\.=-]+@[\\w\\.-]+\\.[\\w]{2,5}$";
	
	/** The Constant VALID_URL_PATTERN. */
	private static final String VALID_URL_PATTERN = "(?:https?://(?:(?:(?:(?:(?:[a-zA-Z\\d](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?)\\.)*(?:[a-zA-Z](?:(?:[a-zA-Z\\d]|-)*[a-zA-Z\\d])?))|(?:(?:\\d+)(?:\\.(?:\\d+)){3}))(?::(?:\\d+))?)(?:/(?:(?:(?:(?:[a-zA-Z\\d$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*)(?:/(?:(?:(?:[a-zA-Z\\d$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*))*)(?:\\?(?:(?:(?:[a-zA-Z\\d$\\-_.+!*'(),]|(?:%[a-fA-F\\d]{2}))|[;:@&=])*))?)?)";
	
	/** The Constant STANDARD_LENGTH. */
	private static final int STANDARD_LENGTH = 255;

	/**
	 * Valid date.
	 *
	 * @param date the date
	 * @return true, if successful
	 */
	public static boolean validDate(String inDate) {

		if (inDate == null)
			return false;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (inDate.trim().length() != dateFormat.toPattern().length())
			return false;

		dateFormat.setLenient(false);

		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	/**
	 * Valid package name.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public static boolean validPackageName(String name) {

		Pattern pattern = Pattern.compile(VALID_NAME_PATTERN);
		Matcher matcher = pattern.matcher(name);

		return matcher.find();
	}

	/**
	 * Valid email.
	 *
	 * @param email the email
	 * @return true, if successful
	 */
	public static boolean validEmail(String email) {

		Pattern pattern = Pattern.compile(VALID_EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);

		return matcher.find();
	}

	/**
	 * Valid url.
	 *
	 * @param url the url
	 * @return true, if successful
	 */
	public static boolean validUrl(String url) {

		Pattern pattern = Pattern.compile(VALID_URL_PATTERN);
		Matcher matcher = pattern.matcher(url);

		return matcher.find();
	}

	/**
	 * Valid standard length.
	 *
	 * @param string the string
	 * @return true, if successful
	 */
	public static boolean validStandardLength(String string) {
		return string.length() <= STANDARD_LENGTH;
	}

}
