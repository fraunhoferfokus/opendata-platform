package de.fhg.fokus.odp.middleware.caching;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentHashMap;

public class Caching {
	ConcurrentHashMap<String, Date> dates = null;
	ConcurrentHashMap<String, Object> responses = null;

	// Private constructor prevents instantiation from other classes
	private Caching() {
		this.dates = new ConcurrentHashMap<String, Date>();
		this.responses = new ConcurrentHashMap<String, Object>();
	}

	private static class SingletonHolder {
		public static final Caching instance = new Caching();
	}

	/**
	 * 
	 * @return Singleton instance
	 */
	public static Caching getInstance() {
		return SingletonHolder.instance;
	}

	/** This method checks if the associated value for the given key is still up to date according to the given interval
	 * 
	 * @param key ID
	 * @param date current Date
	 * @param interval non-negative integer up to interval minutes are accepted as up to date
	 * @return null, if stored object is older than interval minutes or else object according to given key
	 */
	public Object areValuesStillUpToDate(String key, Date date, int interval) {
		if (interval <= 0 || key == null)
			return null;

		// try to find date of current key
		// do not need to compute anything - just return null
		if (!dates.containsKey(key))
			return null;
		else {
			GregorianCalendar oldTime = new GregorianCalendar();
			GregorianCalendar newTime = new GregorianCalendar();
			newTime.setTime(date);
			// compute diff
			Date lastDate = dates.get(key);
			oldTime.setTime(lastDate);
			long diff = newTime.getTimeInMillis() - oldTime.getTimeInMillis();
			System.out.println("Diff in Milli: " + diff);
			long diffMinutes = diff / (1000 * 60);
			System.out.println("Diff in Min: " + diffMinutes);
			if (diffMinutes >= interval) // older than interval
				return null;
			else {
				// still 'current' - get value for key
				return responses.get(key);
			}
		}
	}

	/**
	 * Adds new Object to store. If there is a value associated with the given key, the old value will be overwritten.
	 * @param key ID
	 * @param date current Date
	 * @param data result, which should be stored
	 */
	public void insertObject(String key, Date date, Object data) {
		synchronized (responses) {
			responses.put(key, data);
		}
		synchronized (dates) {
			dates.put(key, date);
		}
	}
}