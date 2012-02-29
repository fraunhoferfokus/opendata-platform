package org.opencities.berlin.opendata.portlet.spring.managedatasets.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	public static String dateToStringTemporalCoverage(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.format(date);
		} catch (NullPointerException ex) {
			return "";
		}
	}

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

	public static String dateToStringMetaDate(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S");
		try {
			return sdf.format(date);
		} catch (NullPointerException ex) {
			return "";
		}
	}

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
