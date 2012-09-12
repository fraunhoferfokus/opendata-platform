package de.fhg.fokus.odp.portal.managedatasets.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class DateUtils.
 */
public class DateUtils {
	
	/**
	 * Date to string temporal coverage.
	 *
	 * @param date the date
	 * @return the string
	 */
	public static String dateToStringTemporalCoverage(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.format(date);
		} catch (NullPointerException ex) {
			return "";
		}
	}

	/**
	 * String to date temporal coverage.
	 *
	 * @param dateString the date string
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	public static Date stringToDateTemporalCoverage(String dateString)
			throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			return sdf.parse(dateString);
		} catch (NullPointerException ex) {
			return null;
		} catch (ParseException ex) {
			return null;
		}

	}

	/**
	 * Date to string meta date.
	 *
	 * @param date the date
	 * @return the string
	 */
	public static String dateToStringMetaDate(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S");
		try {
			return sdf.format(date);
		} catch (NullPointerException ex) {
			return "";
		}
	}

	/**
	 * String to date meta data.
	 *
	 * @param dateString the date string
	 * @return the date
	 * @throws ParseException the parse exception
	 */
	public static Date stringToDateMetaData(String dateString)
			throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S");

		try {
			return sdf.parse(dateString);
		} catch (NullPointerException ex) {
			return null;
		} catch (ParseException ex) {
			return null;
		}
	}
}
