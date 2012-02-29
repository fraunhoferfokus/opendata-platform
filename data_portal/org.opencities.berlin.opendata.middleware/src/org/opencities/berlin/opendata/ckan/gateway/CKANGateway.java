package org.opencities.berlin.opendata.ckan.gateway;

// imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencities.berlin.opendata.caching.Caching;

/**
 * This class is the gateway towards the CKAN instance
 * 
 * 
 * @author Nikolay Tcholtchev, nikolay.tcholtchev@fraunhofer.fokus.de
 * 
 */
public class CKANGateway {

	/** The url to the CKAN gateway instance. */
	private String url = null;

	/** The local authentication key. */
	private String authenticationKey = null;

	/** The local logger instance. */
	private final static Logger log = Logger.getLogger(CKANGateway.class
			.getName());

	/** The constants for the keys to be used for caching. */
	private final String GET_MOST_POPULAR_TAGS_KEY = "GET_MOST_POPULAR_TAGS_KEY";
	private final String GET_MOST_POPULAR_DATASETS_KEY = "GET_MOST_POPULAR_DATASETS";
	private final String GET_LATEST_DATASETS_KEY = "GET_MOST_LATEST_DATASETS_KEY";

	/** The constants holds the cashing interval. */
	private final int CASHING_INTERVAL = 60;

	/** A singleton instance. */
	private static CKANGateway instance = null;

	/**
	 * Function to prepare a singleton instance.
	 * 
	 * @param url
	 *            a string with the url of the CKAN instance.
	 * @param authenticationKey
	 *            the authentication key for CKAN communication.
	 * 
	 */
	public static void prepareInstance(String CKANurl, String authKey) {
		instance = new CKANGateway(CKANurl, authKey);
	}

	/**
	 * Function to deliver the singleton instance.
	 * 
	 * @return the (hopefully) pre-configured CKAN Gateway.
	 */
	public static CKANGateway getInstance() {
		return instance;
	}

	/**
	 * Constructor.
	 * 
	 * @param url
	 *            a string with the url of the CKAN instance.
	 * @param authenticationKey
	 *            the authentication key for CKAN communication.
	 * 
	 */
	public CKANGateway(String url, String authenticationKey) {

		this.url = url;
		if (!this.url.endsWith("/")) {
			this.url += "/";
		}

		this.authenticationKey = authenticationKey;
	}

	/**
	 * Check whether a user should be allowed to rate a package (i.e. if there
	 * is no previous rating by that user)
	 * 
	 * @param packageId
	 *            the ID of the package.
	 * @param userId
	 *            the ID of the user.
	 * 
	 * @return true in case the user has a permission, otherwise false.
	 */
	@SuppressWarnings("rawtypes")
	public boolean hasPackageRatingPermission(String packageId, String userId) {

		// check the parameters
		if (packageId == null || userId == null) {
			return false;
		}

		// get the details for the package
		Object obj = getPackageDetails(packageId);
		if (obj == null) {
			return false;
		}

		// get the JSONArray with the ratings and pass it to the method for
		// calculating the average
		Map eMap = (Map) ((Map) obj).get("extras");
		if (eMap != null) {
			JSONArray arr = null;
			try {
				if (eMap.get("ratings") instanceof java.lang.String) {
					JSONParser parser = new JSONParser();
					arr = (JSONArray) parser.parse((String) eMap
							.get("ratings"));
				} else
					arr = (JSONArray) eMap.get("ratings");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			// iterate over the array
			if (arr != null) {
				for (int i = 0; i < arr.size(); i++) {

					// check the user ID locally
					Map m = (Map) arr.get(i);
					String uId = (String) m.get("userId");
					if (uId.equals(userId)) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * The method gets the latest revisions from the CKAN instance.
	 * 
	 * @param serverTimeZone
	 *            a string that stands for the time zone of the CKAN server.
	 * @param offset
	 *            the offset for the time.
	 * 
	 * @return a string containing the JSON representation of the revisions.
	 */
	public String getRevisions(String serverTimeZone, long offset) {

		// build the URL string
		String CKANurl = url + "/api/search/revision?since_time=";

		// prepare the since_time parameter of the API

		// get the current date
		Date currentTime = new Date();

		// prepare a calendar with the belonging server time zone
		Calendar serverTimeZoneCalendar = new GregorianCalendar(
				TimeZone.getTimeZone(serverTimeZone));

		// set the since_time value as milliseconds in the new calendar
		serverTimeZoneCalendar.setTimeInMillis(currentTime.getTime() - offset);

		// set the calendar values in the corresponding instance and format
		// them accordingly
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, serverTimeZoneCalendar.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, serverTimeZoneCalendar.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH,
				serverTimeZoneCalendar.get(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY,
				serverTimeZoneCalendar.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, serverTimeZoneCalendar.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, serverTimeZoneCalendar.get(Calendar.SECOND));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String since_time = formatter.format(cal.getTime());

		// complete the url
		CKANurl += since_time;

		// read from the CKAN API
		URL CKANurlInstance;
		String returnStr = "";
		try {

			// open a connection to the CKAN API
			CKANurlInstance = new URL(CKANurl);
			URLConnection CKANconnection = CKANurlInstance.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					CKANconnection.getInputStream()));

			// read the output from the API
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				returnStr += inputLine;
			in.close();

		}
		// process exceptions
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return returnStr;
	}

	/**
	 * The function receives the revisions as a JSON string and obtains the
	 * details for the revisions in a list of strings.
	 * 
	 * @param revisionStr
	 *            the string containing the revisions.
	 * 
	 * @return a vector with JSON strings describing the updated data sets.
	 */
	public Vector<String> getUpdatedDatasets(String revisionStr) {

		// check the parameters
		if (revisionStr == null) {
			return null;
		}

		// the vector to return
		Vector<String> toreturnVector = new Vector<String>();

		// parse the JSON string and obtain an array of JSON objects
		Object obj = JSONValue.parse(revisionStr);
		JSONArray array = (JSONArray) obj;

		// prepare the URL string
		String CKANurl = url + "/api/rest/revision/";

		// iterate over the JSON objects
		for (int i = 0; i < array.size(); i++) {
			String revisionUrl = CKANurl + array.get(i).toString();

			// read the information for the revision from the CKAN API
			URL RevisionUrlInstance;
			String revisionDescriptionStr = "";
			try {

				// open a connection to the CKAN API
				RevisionUrlInstance = new URL(revisionUrl);
				URLConnection RevisionUrlInstanceConnection = RevisionUrlInstance
						.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						RevisionUrlInstanceConnection.getInputStream()));

				// read the output from the API
				String inputLine;
				while ((inputLine = in.readLine()) != null)
					revisionDescriptionStr += inputLine;
				in.close();

				// the description
				toreturnVector.add(revisionDescriptionStr);

			}
			// process exceptions
			catch (MalformedURLException e) {
				e.printStackTrace();
				toreturnVector.add(null);
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				toreturnVector.add(null);
				continue;
			}
		}

		return toreturnVector;
	}

	/**
	 * The function executes a REST API call on the belonging CKAN instance.
	 * Thereby the API key is used.
	 * 
	 * @param RESTCall
	 *            the api string to use for the call.
	 * @param jsonString
	 *            a json string to be put in the request part - if null it is
	 *            just ignored.
	 * 
	 * @return the corresponding result.
	 * @throws IOException
	 *             , MalformedURLException the exceptions of the REST call.
	 */
	private String restCallWithAuthorization(String RESTcall, String jsonString)
			throws IOException, MalformedURLException {

		// read from the CKAN API
		URL CKANurlInstance;
		String returnStr = "";

		// open a connection to the CKAN API
		CKANurlInstance = new URL(url + RESTcall);
		URLConnection CKANconnection = CKANurlInstance.openConnection();
		CKANconnection.setRequestProperty("authorization", authenticationKey);
		if (jsonString != null) {
			CKANconnection.setRequestProperty("Content-Type",
					"application/json; charset=utf-8");
			CKANconnection.setDoOutput(true);
			CKANconnection.setDoInput(true);

			OutputStreamWriter wr = new OutputStreamWriter(
					CKANconnection.getOutputStream());
			wr.write(jsonString);
			wr.flush();
		}

		// read the output from the API
		BufferedReader in = new BufferedReader(new InputStreamReader(
				CKANconnection.getInputStream()));

		String inputLine;
		while ((inputLine = in.readLine()) != null)
			returnStr += inputLine;
		in.close();

		return returnStr;

	}

	/**
	 * The function executes a REST API call on the belonging CKAN instance.
	 * 
	 * @param RESTCall
	 *            the api string to use for the call.
	 * @param jsonString
	 *            a json string to be put in the request part - if null it is
	 *            just ignored.
	 * 
	 * @return the corresponding result.
	 * @throws IOException
	 *             , MalformedURLException the exceptions of the REST call.
	 */
	private String restCall(String RESTcall) throws IOException,
			MalformedURLException {

		// read from the CKAN API
		URL CKANurlInstance;
		String returnStr = "";

		// open a connection to the CKAN API
		CKANurlInstance = new URL(url + RESTcall);
		URLConnection CKANconnection = CKANurlInstance.openConnection();

		// read the output from the API
		BufferedReader in = new BufferedReader(new InputStreamReader(
				CKANconnection.getInputStream()));

		String inputLine;
		while ((inputLine = in.readLine()) != null)
			returnStr += inputLine;
		in.close();

		return returnStr;

	}

	/**
	 * The function returns all meta data sets filtered according to the
	 * maintainer email. The function returns a HashMap with key=metadataSetId
	 * and value=HashMap(with Key = MetaDataEntryKey and value =
	 * MetaDataEntryValue).
	 * 
	 * @param mainterEmail
	 *            the maintainer email.
	 * @param fileFormat
	 *            the file format to filter for.
	 * 
	 * @return the the hash map as described in the general comments of the
	 *         method or null if anything has gone wrong.
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, HashMap> viewMetaDataSets(String maintainerEmail,
			String fileFormat) {

		// check the input parameters
		if (maintainerEmail == null || maintainerEmail.equals("")
				|| fileFormat == null || fileFormat.trim().equals("")) {
			return null;
		}

		// prepare the REST API call
		String RESTcall = "api/search/package?maintainer_email=";
		maintainerEmail = maintainerEmail.replaceAll("@", "%40;");
		RESTcall += maintainerEmail;

		// the variable to return
		HashMap<String, HashMap> toreturn = new HashMap<String, HashMap>();

		try {
			// run the REST call to obtain all packages
			String packageListString = restCall(RESTcall);
			if (packageListString == null) {
				log.log(Level.SEVERE, "Failed to realize api call \"" + url
						+ RESTcall + "\" !!!");
				return null;
			}

			// parse the JSON string and obtain an array of JSON objects
			Object obj = JSONValue.parse(packageListString);
			Map m = (Map) obj;
			JSONArray array = (JSONArray) (m.get("results"));
			if (array == null) {
				return null;
			}

			// move over the JSON array
			for (int i = 0; i < array.size(); i++) {

				// get details for each single package
				String RESTcallPackage = "api/rest/package/" + array.get(i);
				String packageStr = restCall(RESTcallPackage);

				if (packageStr == null) {
					log.log(Level.SEVERE,
							"Failed to obtain details for package \""
									+ packageStr + "\"");
					continue;
				}

				// parse the string for the package
				Object packageObj = JSONValue.parse(packageStr);
				HashMap helpMap = (HashMap) packageObj;

				// check whether at least one of the given resources matches the
				// required file format
				boolean matchesFormat = false;
				JSONArray arr = (JSONArray) (helpMap.get("resources"));
				for (int j = 0; j < arr.size(); j++) {
					HashMap rMap = (HashMap) arr.get(j);
					String format = (String) rMap.get("format");
					if (format == null) {
						format = "";
					}
					format = format.trim();
					if (format.equals(fileFormat)) {
						matchesFormat = true;
					}
				}

				if (matchesFormat == false) {
					continue;
				}

				// if all filters passed --> add to the hashmap to return
				toreturn.put((String) array.get(i), helpMap);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return toreturn;
	}

	/**
	 * Returns a list of the most popular tags.
	 * 
	 * @param numberOfTags
	 *            the number of popular tags to return.
	 * @return the most popular tags or null if an error occurred.
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getMostPopularTags(int numberOfTags) {

		// check the parameters
		if (numberOfTags <= 0) {
			return null;
		}

		// prepare a key for the cashing
		String cachingKey = GET_MOST_POPULAR_TAGS_KEY + "_" + numberOfTags;
		// get a cashing instance and check whether the data is up-to-date
		Caching cInstance = Caching.getInstance();
		JSONArray toreturn = (JSONArray) cInstance.areValuesStillUpToDate(
				cachingKey, new Date(), CASHING_INTERVAL);
		if (toreturn != null) {
			// if the data is up-to-data --> return it
			return toreturn;
		}

		// the json array to return
		toreturn = new JSONArray();

		// the array to store rating values
		double[] ratingValues = new double[numberOfTags];
		// even tough Java does it automatically, we set the values to zero
		for (int i = 0; i < ratingValues.length; i++) {
			ratingValues[i] = 0.0;
		}

		// a help array for the package ratings
		HashMap<String, Double> packageRatings = new HashMap<String, Double>();

		// prepare the REST API call
		String RESTcall = "api/rest/tag";

		try {
			// run the REST call to obtain all tags
			String tagListString = restCallWithAuthorization(RESTcall, null);
			if (tagListString == null) {
				log.log(Level.SEVERE, "Failed to realize api call \"" + url
						+ RESTcall + "\" !!!");
				return null;
			}

			// parse the JSON string and obtain an array of JSON objects
			Object obj = JSONValue.parse(tagListString);
			JSONArray array = (JSONArray) obj;

			// move over the tags in the array
			for (int i = 0; i < array.size(); i++) {

				// pick the tag name
				String tagName = (String) array.get(i);

				// run the REST call to obtain all packages for the tag in
				// question
				String tRESTcall = RESTcall + "/" + tagName;
				String pListString = restCallWithAuthorization(tRESTcall, null);

				if (pListString == null) {
					log.log(Level.SEVERE, "Failed to realize api call \"" + url
							+ RESTcall + "\" !!!");
					return null;
				}

				// parse the JSON string
				Object pObj = JSONValue.parse(pListString);
				JSONArray pArray = (JSONArray) pObj;

				// iterate over the JSON array
				double tagRating = 0.0;
				for (int j = 0; j < pArray.size(); j++) {
					// pick the name of the package
					String pName = (String) (pArray.get(j));

					// check whether the average rating value has already been
					// obtained
					Double aRatingValueD = packageRatings.get(pName);

					if (aRatingValueD == null) {
						// in case it was not obtained until now --> get it and
						// store it locally
						double aRatingValue = getPackageRatingsAverage(pName);
						aRatingValueD = new Double(aRatingValue);
						packageRatings.put(pName, aRatingValueD);
					}

					// update the tag rating
					tagRating += aRatingValueD.doubleValue();
				}

				// update the toreturn array
				if (toreturn.size() < numberOfTags) {
					toreturn.add(tagName);
					ratingValues[toreturn.size() - 1] = tagRating;

				} else {
					// get the smallest value in the rating values
					int minIndex = minDoubleArray(ratingValues);
					if (ratingValues[minIndex] < tagRating) {
						ratingValues[minIndex] = tagRating;
						toreturn.set(minIndex, tagName);
					}
				}
			}
		}
		// catch potential exceptions
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// store the toreturn variable in the cashing component
		cInstance.insertObject(cachingKey, new Date(), toreturn);

		return toreturn;
	}

	/**
	 * Get the minimum from a double array.
	 * 
	 * @param data
	 *            the array.
	 * @return the minimum.
	 */
	private int minDoubleArray(double[] data) {
		double minimum = data[0];
		int minIndex = 0;
		for (int i = 1; i < data.length; i++) {
			if (data[i] < minimum) {
				minimum = data[i];
				minIndex = i;
			}
		}
		return minIndex;
	}

	/**
	 * View all MetaData entries. The function returns a HashMap with
	 * key=metadataSetId and value=HashMap(with Key = MetaDataEntryKey and value
	 * = MetaDataEntryValue).
	 * 
	 * @return the hash map as described in the general comments of the method
	 *         or null if anything has gone wrong.
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, HashMap> viewMetaDataSets() {

		// prepare the REST API call
		String RESTcall = "api/rest/package";

		// the variable to return
		HashMap<String, HashMap> toreturn = new HashMap<String, HashMap>();

		try {
			// run the REST call to obtain all packages
			String packageListString = restCall(RESTcall);
			if (packageListString == null) {
				log.log(Level.SEVERE, "Failed to realize api call \"" + url
						+ RESTcall + "\" !!!");
				return null;
			}

			// parse the JSON string and obtain an array of JSON objects
			Object obj = JSONValue.parse(packageListString);
			JSONArray array = (JSONArray) obj;

			// move over the JSON array
			for (int i = 0; i < array.size(); i++) {

				// get details for each single package
				String RESTcallPackage = RESTcall + "/" + array.get(i);
				String packageStr = restCall(RESTcallPackage);

				if (packageStr == null) {
					log.log(Level.SEVERE,
							"Failed to obtain details for package \""
									+ packageStr + "\"");
					continue;
				}

				// parse the string for the package
				Object packageObj = JSONValue.parse(packageStr);
				HashMap helpMap = (HashMap) packageObj;

				toreturn.put((String) array.get(i), helpMap);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return toreturn;
	}

	/**
	 * Create a new MetaDataSet in CKAN. In the HashMap<String, String>, the key
	 * = metadataentry, and value = value corresponding to the meta dataentry.
	 * 
	 * @param hm_MetaData
	 *            hash map as described in the general comments of the method.
	 * @return null in case of an error, otherwise a hash map of arrays and maps
	 *         reflecting the structure of the submitted meta data set.
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, HashMap> createMetaDataSet(
			HashMap<String, String> hm_MetaData) {

		// check the submitted parameters
		if (hm_MetaData == null) {
			return null;
		}

		String name = hm_MetaData.get("name");
		if (name == null) {
			return null;
		}

		// get the set of keys
		Set<String> s = hm_MetaData.keySet();
		Iterator<String> it = s.iterator();

		// prepare the JSON string
		String jsonStr = "";

		// iterate over the set of keys
		while (it.hasNext()) {
			String key = it.next();
			if (!jsonStr.equals("")) {
				jsonStr += ",";
			}
			jsonStr += "\"" + key + "\":" + hm_MetaData.get(key);
		}

		jsonStr = "{" + jsonStr + "}";

		// prepare the rest call
		String RESTCall = "api/rest/package";

		try {
			restCallWithAuthorization(RESTCall, jsonStr);
		} catch (MalformedURLException e) {
			// e.printStackTrace();
			return null;
		} catch (IOException e) {
			// e.printStackTrace();

			return null;
		}

		// add the package to the groups
		JSONArray grNewArr = null;
		String groupsStr = hm_MetaData.get("groups");
		if (groupsStr != null) {
			grNewArr = (JSONArray) (JSONValue.parse(groupsStr));

			// add package to groups
			for (int i = 0; i < grNewArr.size(); i++) {
				addPackageToGroup((String) (grNewArr.get(i)), name);
			}
		}

		// prepare the object to return
		HashMap<String, HashMap> toreturn = new HashMap<String, HashMap>();
		Object packageObj = JSONValue.parse(jsonStr);
		HashMap helpMap = (HashMap) packageObj;

		toreturn.put(name, helpMap);

		return toreturn;
	}

	/**
	 * Update a selected MetaDataSet in CKAN. In order to select which
	 * MetaDataSet is to be updated, the id of the MetaDataSet in CKAN would be
	 * provided.
	 * 
	 * @param metaDataSetID
	 *            the id of the MetaDataSet in CKAN.
	 * @param hm_MetaData
	 *            hash map as described in the general comments of the method.
	 * @return null in case of an error, otherwise a hash map of arrays and maps
	 *         reflecting the structure of the submitted meta data set.
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, HashMap> updateMetaDataSet(String metaDataSetID,
			HashMap<String, String> hm_MetaData) {

		// check the passed parameters
		if (metaDataSetID == null || hm_MetaData == null) {
			return null;
		}

		// obtain the current data in the meta data set
		Object obj = getPackageDetails(metaDataSetID);
		if (obj == null) {
			return null;
		}
		Map objM = (Map) obj;
		ArrayList grOldArr = (ArrayList) objM.get("groups");

		// get the groups string
		JSONArray grNewArr = null;
		String groupsStr = hm_MetaData.get("groups");
		if (groupsStr != null) {
			grNewArr = (JSONArray) (JSONValue.parse(groupsStr));
		}

		// obtain the packages to delete from and add the group to
		ArrayList arrToDeletePackageFrom = getGroupsToDeletePackageFrom(
				grOldArr, grNewArr);
		ArrayList arrToAddPackageTo = getGroupsToAddPackageTo(grOldArr,
				grNewArr);

		// get the set of keys
		Set<String> s = hm_MetaData.keySet();
		Iterator<String> it = s.iterator();

		// prepare the JSON string
		String jsonStr = "";

		// iterate over the set of keys
		while (it.hasNext()) {
			String key = it.next();
			if (!jsonStr.equals("")) {
				jsonStr += ",";
			}
			jsonStr += "\"" + key + "\":" + hm_MetaData.get(key) + "";
		}
		jsonStr = "{" + jsonStr + "}";

		// prepare the rest call
		String RESTCall = "api/rest/package/" + metaDataSetID;

		try {
			// String returnStr = restCall(RESTCall, jsonStr);
			restCallWithAuthorization(RESTCall, jsonStr);

			// remove package from groups where it was before
			for (int i = 0; i < arrToDeletePackageFrom.size(); i++) {
				deletePackageFromGroup(
						(String) (arrToDeletePackageFrom.get(i)), metaDataSetID);
			}

			// add package to groups
			for (int i = 0; i < arrToAddPackageTo.size(); i++) {
				addPackageToGroup((String) (arrToAddPackageTo.get(i)),
						metaDataSetID);
			}

		} catch (MalformedURLException e) {
			// e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// prepare the object to return
		HashMap<String, HashMap> toreturn = new HashMap<String, HashMap>();
		Object packageObj = JSONValue.parse(jsonStr);
		HashMap helpMap = (HashMap) packageObj;

		toreturn.put(metaDataSetID, helpMap);

		return toreturn;
	}

	/**
	 * The function compares two group arrays and obtains those who are new and
	 * where the package has to be added to.
	 * 
	 * @param grOldArr
	 *            the array with the old groups.
	 * @param grNewArr
	 *            the array with the new groups.
	 * @return the new groups.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList getGroupsToAddPackageTo(ArrayList grOldArr,
			JSONArray grNewArr) {

		ArrayList toreturn = new ArrayList();
		if (grOldArr == null || grNewArr == null) {
			return toreturn;
		}

		for (int i = 0; i < grNewArr.size(); i++) {
			if (!grOldArr.contains(grNewArr.get(i))) {
				toreturn.add(grNewArr.get(i));
			}
		}
		return toreturn;
	}

	/**
	 * The function compares two group arrays and obtains those from which the
	 * package has to be removed.
	 * 
	 * @param grOldArr
	 *            the array with the old groups.
	 * @param grNewArr
	 *            the array with the new groups.
	 * @return groups to remove.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList getGroupsToDeletePackageFrom(ArrayList grOldArr,
			JSONArray grNewArr) {
		ArrayList toreturn = new ArrayList();
		if (grOldArr == null || grNewArr == null) {
			return toreturn;
		}

		for (int i = 0; i < grOldArr.size(); i++) {
			if (!grNewArr.contains(grOldArr.get(i))) {
				toreturn.add(grOldArr.get(i));
			}
		}
		return toreturn;
	}

	/**
	 * The method updates a group by removing meta data set from it.
	 * 
	 * @param grpName
	 *            the name of the group.
	 * @param metaDataSetID
	 *            the meta data set ID.
	 * 
	 * @return true in case of a success, otherwise false.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean deletePackageFromGroup(String grpName, String metaDataSetID) {

		// prepare the REST call for getting the groups
		String RESTcall = "/api/rest/group/" + grpName;
		try {

			// get the information about the group
			String returnStr = restCallWithAuthorization(RESTcall, null);
			if (returnStr == null) {
				return false;
			}

			// parse
			Map group = (Map) (JSONValue.parse(returnStr));

			// get the packages
			JSONArray pkgs = (JSONArray) group.get("packages");
			pkgs.remove(metaDataSetID);

			// add the packages
			restCallWithAuthorization(RESTcall,
					((JSONObject) group).toJSONString());

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * The method updates a group by adding an additional meta data set.
	 * 
	 * @param grpName
	 *            the name of the group.
	 * @param metaDataSetID
	 *            the meta data set ID.
	 * 
	 * @return true in case of a success, otherwise false.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean addPackageToGroup(String grpName, String metaDataSetID) {

		// check the input parameters
		if (grpName == null || metaDataSetID == null) {
			return false;
		}

		// trim the meta data-set ID in case it is put in quotes
		if (metaDataSetID.startsWith("\"")) {
			metaDataSetID = metaDataSetID.substring(1,
					metaDataSetID.length() - 1);
		}

		if (metaDataSetID.endsWith("\"")) {
			metaDataSetID = metaDataSetID.substring(0,
					metaDataSetID.length() - 2);
		}

		// prepare the REST call for getting the groups
		String RESTcall = "/api/rest/group/" + grpName;
		try {

			// get the information about the group
			String returnStr = restCallWithAuthorization(RESTcall, null);
			if (returnStr == null) {
				return false;
			}

			// parse
			Map group = (Map) (JSONValue.parse(returnStr));

			// get the packages
			JSONArray pkgs = (JSONArray) group.get("packages");
			pkgs.add(metaDataSetID);
			group.put("packages", pkgs);

			// add the packages
			restCallWithAuthorization(RESTcall,
					((JSONObject) group).toJSONString());

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Delete a MetaDataEntry. In order to select which MetaDataSet is to be
	 * deleted, the id of the MetaDataSet in CKAN would be provided. A boolean
	 * needs to be returned as ack to indicate whether the MetaDataSet deletion
	 * has been successful or not.
	 * 
	 * @param metaDataSetID
	 *            the id of the MetaDataSet in CKAN.
	 * @return a boolean indicating the success or failure of the operation.
	 */
	public boolean deleteMetaDataSet(String metaDataSetID) {

		// check the passed parameters
		if (metaDataSetID == null) {
			return false;
		}

		Object obj = getPackageDetails(metaDataSetID);
		if (obj == null) {
			return false;
		}

		// pick the extras and resources
		Map mMap = (Map) obj;

		String resource = mMap.get("resources").toString();
		Map eMap = (Map) mMap.get("extras");

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("extras", eMap.toString());
		hm.put("resources", resource);
		hm.put("state", "\"deleted\"");

		updateMetaDataSet(metaDataSetID, hm);

		return true;
	}

	/**
	 * Undelete a MetaDataEntry. In order to select which MetaDataSet is to be
	 * deleted, the id of the MetaDataSet in CKAN would be provided. A boolean
	 * needs to be returned as ack to indicate whether the MetaDataSet
	 * undeletion has been successful or not.
	 * 
	 * @param metaDataSetID
	 *            the id of the MetaDataSet in CKAN.
	 * @return a boolean indicating the success or failure of the operation.
	 */
	public boolean undeleteMetaDataSet(String metaDataSetID) {

		// check the passed parameters
		if (metaDataSetID == null) {
			return false;
		}

		Object obj = getPackageDetails(metaDataSetID);
		if (obj == null) {
			return false;
		} // pick the extras and resources
		Map mMap = (Map) obj;

		String resource = mMap.get("resources").toString();
		Map eMap = (Map) mMap.get("extras");

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("extras", eMap.toString());
		hm.put("resources", resource);
		hm.put("state", "\"active\"");

		updateMetaDataSet(metaDataSetID, hm);

		return true;
	}

	/**
	 * Delete a MetaDataEntry. In order to select which MetaDataSet is to be
	 * deleted, the id of the MetaDataSet in CKAN would be provided. A boolean
	 * needs to be returned as ack to indicate whether the MetaDataSet deletion
	 * has been successful or not.
	 * 
	 * @param metaDataSetID
	 *            the id of the MetaDataSet in CKAN.
	 * @return a boolean indicating the success or failure of the operation.
	 */
	public boolean deleteMetaDataSets(Vector<String> metaDataSetIDs) {

		// check the submitted parameters
		if (metaDataSetIDs == null) {
			return false;
		}

		// the variable to return
		boolean toreturn = true;

		// invoke deleteMetaDataSet for each set ID
		for (int i = 0; i < metaDataSetIDs.size(); i++) {
			if (!deleteMetaDataSet(metaDataSetIDs.get(i))) {
				toreturn = false;
			}
		}

		return toreturn;
	}

	/**
	 * Get groups list including details.
	 * 
	 * @return a hash map of JSON objects containing the data for each group.
	 */
	public HashMap<String, Object> getGroupsData() {

		// prepare the REST call for getting the groups
		String RESTcall = "/api/rest/group";

		// the variable to return
		HashMap<String, Object> toreturn = new HashMap<String, Object>();

		try {
			// get the results for all groups
			String groupResults = restCallWithAuthorization(RESTcall, null);

			// check the API results
			if (groupResults == null) {
				return null;
			}

			// parse the string for the tag results
			JSONArray groupsDataObj = (JSONArray) JSONValue.parse(groupResults);

			// iterate over the available groups
			for (int i = 0; i < groupsDataObj.size(); i++) {

				// get the details for the specific group
				String groupRestCall = RESTcall + "/"
						+ (String) groupsDataObj.get(i);
				String groupDetailsStr = restCallWithAuthorization(
						groupRestCall, null);

				// parse the returned JSON string
				Object obj = JSONValue.parse(groupDetailsStr);

				// store in the variable to return
				toreturn.put((String) groupsDataObj.get(i), obj);

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return toreturn;
	}

	/**
	 * Get tags list.
	 * 
	 * @return a JSON array containing the list of all tags.
	 */
	public JSONArray getTagsData() {

		// prepare the REST call
		String RESTcall = "/api/rest/tag";

		// the JSON array to return
		JSONArray toreturn = null;

		try {
			// get the search results
			String tagResults = restCallWithAuthorization(RESTcall, null);

			// check the API results
			if (tagResults == null) {
				return null;
			}

			// parse the string for the tag results
			Object tagObj = JSONValue.parse(tagResults);
			toreturn = (JSONArray) tagObj;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return toreturn;
	}

	/**
	 * Get package search results.
	 * 
	 * @param params
	 *            string of parameters for the package search.
	 * 
	 * @return JSON object containing the search results or "null" if something
	 *         has gone wrong.
	 */
	public Object getPackageSearchResults(String params) {

		// check the submitted parameters
		if (params == null) {
			return null;
		}

		// prepare the REST call
		String RESTcall = "api/search/package?" + params;

		// the JSON object to return
		Object toreturn = null;

		try {
			// get the search results
			// String searchResultsStr = restCallWithAuthorization(RESTcall,
			// null);
			String searchResultsStr = restCall(RESTcall);

			// check the API results
			if (searchResultsStr == null) {
				return null;
			}

			// parse the string for the search results
			toreturn = JSONValue.parse(searchResultsStr);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return toreturn;
	}

	/**
	 * Get package search results as owner. This means, that you see deleted
	 * packages, too.
	 * 
	 * @param params
	 *            string of parameters for the package search.
	 * 
	 * @return JSON object containing the search results or "null" if something
	 *         has gone wrong.
	 */
	public Object getPackageSearchResultsAsOwner(String params) {

		// check the submitted parameters
		if (params == null) {
			return null;
		}

		// prepare the REST call
		String RESTcall = "api/search/package?" + params;

		// the JSON object to return
		Object toreturn = null;

		try {
			// get the search results
			String searchResultsStr = restCallWithAuthorization(RESTcall, null);

			// check the API results
			if (searchResultsStr == null) {
				return null;
			}

			// parse the string for the search results
			toreturn = JSONValue.parse(searchResultsStr);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return toreturn;
	}

	/**
	 * Get package details.
	 * 
	 * @param packageId
	 *            the id of the package.
	 * @return a json object containing the package details or "null" in case of
	 *         an error.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getPackageDetails(String packageId) {

		// check the input
		if (packageId == null || packageId.equals("")) {
			return null;
		}

		// the toreturn object
		Object toreturn = null;

		// get details for each single package
		String RESTcallPackage = "api/2/rest/package/" + packageId;
		try {

			// get the json output
			String packageStr = restCallWithAuthorization(RESTcallPackage, null);

			// check the API results
			if (packageStr == null) {
				return null;
			}

			// parse the string for the package
			toreturn = JSONValue.parse(packageStr);

			// obtain the right group name for each group
			JSONArray arr = (JSONArray) ((Map) toreturn).get("groups");
			for (int i = 0; i < arr.size(); i++) {
				// get the json output
				String RESTcallGroup = "api/rest/group/" + (String) arr.get(i);
				String groupStr = restCallWithAuthorization(RESTcallGroup, null);
				if (groupStr == null) {
					continue;
				}
				Map m = (Map) JSONValue.parse(groupStr);
				arr.set(i, (String) (m.get("name")));
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return toreturn;
	}

	/**
	 * Submit rating for a package [rating: number of stars, 1..5]
	 * 
	 * @param packageId
	 *            the id of the package to rate.
	 * @param userId
	 *            the ID of the user submitting the rating.
	 * @param date
	 *            the date at which the rating takes place.
	 * @param rating
	 *            the rating value.
	 * @return true in case of success, false otherwise.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean postPackageRating(String packageId, String userId,
			Date date, int rating) {

		// check the parameters
		if (packageId == null || packageId.equals("") || userId == null
				|| userId.equals("") || rating < 0 || rating > 5) {
			return false;
		}

		// get the package details
		Object obj = getPackageDetails(packageId);
		if (obj == null) {
			return false;
		}

		// prepare the extras string

		// pick the extras
		Map mMap = (Map) obj;
		Map eMap = (Map) mMap.get("extras");
		String resource = mMap.get("resources").toString();

		// check weather extras is defined
		if (eMap != null) {

			JSONArray ratings = null;
			try {
				if (eMap.get("ratings") instanceof java.lang.String) {
					JSONParser parser = new JSONParser();
					ratings = (JSONArray) parser.parse((String) eMap
							.get("ratings"));
				} else
					ratings = (JSONArray) eMap.get("ratings");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (ratings == null) {
				ratings = new JSONArray();
			}

			Map<String, String> toadd = new HashMap<String, String>();

			toadd.put("userId", userId);
			toadd.put("ratingValue", new Integer(rating).toString());
			toadd.put("date", date.toString());

			ratings.add(toadd);
			eMap.put("ratings", ratings);
		}

		// prepare the hash map for the update method
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("extras", eMap.toString());
		hm.put("resources", resource);

		// update the meta data set
		HashMap<String, HashMap> res = updateMetaDataSet(packageId, hm);
		boolean toreturn = (res != null);

		return toreturn;
	}

	/**
	 * Get the average of a package’s ratings [rounded to 0.5 steps]
	 * 
	 * @param packageId
	 *            the ID of the package.
	 * @return the averaged rating or -1 if smth. has gone wrong.
	 */
	@SuppressWarnings("rawtypes")
	public double getPackageRatingsAverage(String packageId) {

		// check the input
		if (packageId == null) {
			return -1;
		}

		// get the details for the package
		Object obj = getPackageDetails(packageId);
		if (obj == null) {
			return -1;
		}

		double toreturn = -1;
		// get the JSONArray with the ratings and pass it to the method for
		// calculating the average
		Map eMap = (Map) ((Map) obj).get("extras");
		if (eMap != null) {
			JSONArray arr = null;
			try {
				if (eMap.get("ratings") instanceof java.lang.String) {
					JSONParser parser = new JSONParser();
					arr = (JSONArray) parser.parse((String) eMap
							.get("ratings"));
				} else
					arr = (JSONArray) eMap.get("ratings");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			toreturn = calculateAverageRating(arr);
		}

		return toreturn;
	}

	/**
	 * The function gets a JSON array with the ratings and returns an average
	 * rating value, which is rounded to 0.5.
	 * 
	 * @param arr
	 *            the array with the ratings.
	 * 
	 * @return the averaged rating value rounded to 0.5-
	 */
	public double calculateAverageRating(JSONArray arr) {
		// check the parameter
		if (arr == null) {
			return -1;
		}

		int accumulatedRatingValue = 0;
		for (int i = 0; i < arr.size(); i++) {
			Map rating = (Map) arr.get(i);
			String ratingValue = (String) rating.get("ratingValue");
			accumulatedRatingValue += Integer.parseInt(ratingValue);
		}

		// round to 0.5
		double toreturn = ((double) accumulatedRatingValue)
				/ ((double) arr.size());
		if ((toreturn - (int) toreturn) > 0.25
				&& (toreturn - (int) toreturn) < 0.75) {
			toreturn = (double) ((int) toreturn) + 0.5;
		} else if ((toreturn - (int) toreturn) >= 0.75) {
			toreturn = (double) ((int) toreturn) + 1.0;
		} else if ((toreturn - (int) toreturn) <= 0.75) {
			toreturn = (double) ((int) toreturn);
		}

		return toreturn;
	}

	/**
	 * Submit comment for a package.
	 * 
	 * @param packageId
	 *            the id of the package.
	 * @param userId
	 *            the user ID.
	 * @param date
	 *            the date of the commenting
	 * @param comment
	 *            the comment.
	 * @return true if everything is fine, false otherwise.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean postPackageComment(String packageId, String userId,
			Date date, String comment) {

		// check the parameters
		if (packageId == null || packageId.equals("") || userId == null
				|| userId.equals("") || comment == null || comment.equals("")) {
			return false;
		}

		// get the package details
		Object obj = getPackageDetails(packageId);
		if (obj == null) {
			return false;
		}

		// prepare the extras string

		// pick the extras
		Map mMap = (Map) obj;

		String resource = mMap.get("resources").toString();
		Map eMap = (Map) mMap.get("extras");

		// check weather extras is defined
		if (eMap != null) {

			JSONArray comments = null;
			try {
				if (eMap.get("comments") instanceof java.lang.String){
					JSONParser parser = new JSONParser();
					comments = (JSONArray) parser.parse((String) eMap
							.get("comments"));
				}
				else
					comments = (JSONArray) eMap.get("comments");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (comments == null) {
				comments = new JSONArray();
			}

			Map<String, String> toadd = new HashMap<String, String>();

			toadd.put("userId", userId);
			toadd.put("comment", comment);
			toadd.put("date", date.toString());

			comments.add(toadd);
			eMap.put("comments", comments);
		}

		// prepare the hash map for the update method
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("extras", eMap.toString());
		hm.put("resources", resource);

		// update the meta data set
		HashMap<String, HashMap> res = updateMetaDataSet(packageId, hm);
		boolean toreturn = (res != null);

		return toreturn;
	}

	/**
	 * Get the number of existing comments for a package.
	 * 
	 * @param packageId
	 *            the package ID.
	 * 
	 * @return the number of existing comments or -1 in case of an error.
	 */
	public int getPackageCommentsCount(String packageId) {

		JSONArray arr = getPackageComments(packageId);
		if (arr == null) {
			return -1;
		}

		return arr.size();
	}

	/**
	 * Get the comments for a package.
	 * 
	 * @param packageId
	 *            the package ID.
	 * @return a JSON array with the comments or null in case of an error.
	 */
	@SuppressWarnings("rawtypes")
	public JSONArray getPackageComments(String packageId) {

		// check the parameter
		if (packageId == null) {
			return null;
		}

		// get the package details
		Object obj = getPackageDetails(packageId);
		if (obj == null) {
			return null;
		}

		// get the JSONArray with the comments
		JSONArray toreturn = null;
		Map eMap = (Map) ((Map) obj).get("extras");
		if (eMap != null) {
			try {
				if (eMap.get("comments") instanceof java.lang.String) {
					JSONParser parser = new JSONParser();
					toreturn = (JSONArray) parser.parse((String) eMap
							.get("comments"));
				} else
					toreturn = (JSONArray) eMap.get("comments");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return toreturn;
	}

	/**
	 * The function receives a vector with details for a set of revisions and
	 * returns the details for the packages affected by these revisions.
	 * 
	 * @param revisionsDetails
	 *            a vector of strings containing the JSON details for the
	 *            revisions.
	 * @return a vector of maps with the details for each affected package.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getUpdatedDataSetsDetails(Vector<String> revisionsDetails) {

		// check the input packages
		if (revisionsDetails == null) {
			return null;
		}

		// the vector to store return results
		Vector toreturn = new Vector();

		// a variable to hold the already visited packages
		Vector<String> visitedPackages = new Vector<String>();

		// iterate over each single package
		for (int i = 0; i < revisionsDetails.size(); i++) {

			// parse the JSON string and obtain an array of JSON objects
			Object obj = JSONValue.parse(revisionsDetails.get(i));
			Map array = (Map) obj;

			// get the packages
			JSONArray arr = (JSONArray) (array.get("packages"));

			// iterate over all the packages
			for (int j = 0; j < arr.size(); j++) {

				// get the name of the next package
				String pkg = (String) arr.get(j);

				// check whether the package was already visited
				if (visitedPackages.contains(pkg)) {
					continue;
				}

				// add the package to the list of visited packages
				visitedPackages.add(pkg);

				// get the package details
				Object pkgObject = getPackageDetails(pkg);

				// add the package details to the toreturn object
				if (pkgObject != null) {
					toreturn.add(pkgObject);
				}
			}
		}

		return toreturn;
	}

	/**
	 * The function receives a vector with details for a set of revisions and
	 * returns the details for the packages affected by these revisions.
	 * 
	 * @param revisionsDetails
	 *            a vector of strings containing the JSON details for the
	 *            revisions.
	 * @return a vector of maps with the details for each affected package.
	 */
	@SuppressWarnings("rawtypes")
	public Vector<Map> getUpdatedCategoriesDetails(
			Vector<String> revisionsDetails) {

		// pass the request to the function for the updated data sets
		Vector uDataSetResults = getUpdatedDataSetsDetails(revisionsDetails);
		if (uDataSetResults == null) {
			return null;
		}

		// the vector to contain the visited groups
		Vector<String> visitedGroups = new Vector<String>();

		// the variable which will be returned
		Vector<Map> toreturn = new Vector<Map>();

		// iterate over the data set results
		for (int i = 0; i < uDataSetResults.size(); i++) {

			// get the groups which where updated as a result of the data set
			// update
			Map m = (Map) uDataSetResults.get(i);
			JSONArray arr = (JSONArray) m.get("groups");

			for (int j = 0; j < arr.size(); j++) {

				// get the next group and check if its data was already obtained
				String grp = (String) arr.get(j);
				if (visitedGroups.contains(grp)) {
					continue;
				}

				visitedGroups.add(grp);

				// prepare the next rest call
				String RESTcall = "api/rest/group/" + grp;

				try {
					String restResponse = restCallWithAuthorization(RESTcall,
							null);
					Map grMap = (Map) JSONValue.parse(restResponse);

					toreturn.add(grMap);

				} catch (MalformedURLException e) {
					// e.printStackTrace();
					continue;
				} catch (IOException e) {
					// e.printStackTrace();
					continue;
				}
			}
		}

		return toreturn;
	}

	/**
	 * Returns a list of the X latest data sets. The title, id and rating of
	 * each data set are provided.
	 * 
	 * @param numberOfDatasets
	 *            the number of data sets to return.
	 * 
	 * @return null in case of an error, or a hash map with the latest data sets
	 *         including id, rating, title.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap getLatestDatasets(int numberOfDatasets) {

		// check the parameter
		if (numberOfDatasets <= 0) {
			return null;
		}

		// prepare a key for the cashing
		String cachingKey = GET_LATEST_DATASETS_KEY + "_" + numberOfDatasets;
		// get a cashing instance and check whether the data is up-to-date
		Caching cInstance = Caching.getInstance();
		HashMap toreturn = (HashMap) cInstance.areValuesStillUpToDate(
				cachingKey, new Date(), CASHING_INTERVAL);
		if (toreturn != null) {
			// if the data is up-to-data --> return it
			return toreturn;
		}
		// if the data is not up to date

		// get all the meta data sets
		HashMap hm = viewMetaDataSets();

		if (hm == null) {
			return null;
		}

		// prepare the variable to return
		toreturn = new HashMap();

		// help variables for the sorting according to the metadata_modification
		// date
		HashMap<String, String> relationMap = new HashMap<String, String>();
		ArrayList<String> sList = new ArrayList<String>();

		// iterate over the data sets
		Set<String> keys = (Set<String>) (hm.keySet());
		Iterator<String> it = keys.iterator();

		while (it.hasNext()) {
			String key = (String) it.next();

			// get the Map for the next object
			Map m = (Map) hm.get(key);

			// extract the modification date
			String modificationDate = (String) m.get("metadata_modified");

			// put the belonging values in the help variables
			relationMap.put(modificationDate + ":" + key, key);
			sList.add(modificationDate + ":" + key);
		}

		// sort the dates
		Collections.sort(sList);

		// the variable to count the obtained data sets
		int countDataSets = 1;

		// iterate over the sorted dates
		for (int i = sList.size() - 1; i >= 0; i--) {

			// check whether the requi
			if (countDataSets > numberOfDatasets) {
				break;
			}

			// get the next sorted date
			String dStr = (String) sList.get(i);

			// obtain the belonging key from the relations help map
			String key = (String) relationMap.get(dStr);

			// get the map containing the package information
			Map helpMap = (Map) hm.get(key);

			Map value = new HashMap();
			value.put("name", helpMap.get("name"));
			value.put("id", helpMap.get("id"));
			value.put("title", helpMap.get("title"));
			value.put("notes", helpMap.get("notes"));
			value.put("tags", helpMap.get("tags"));
			value.put("groups", helpMap.get("groups"));
			value.put("metadata_created", helpMap.get("metadata_created"));
			value.put("metadata_modified", helpMap.get("metadata_modified"));

			// extract relevant data from the extras
			Map eMap = ((Map) ((Map) helpMap).get("extras"));
			if (eMap != null) {
				// extract information about the ratings
				JSONArray arr = null;
				try {
					if (eMap.get("ratings") instanceof java.lang.String) {
						JSONParser parser = new JSONParser();
						arr = (JSONArray) parser.parse((String) eMap
								.get("ratings"));
					} else
						arr = (JSONArray) eMap.get("ratings");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (arr != null) {
					double averagedRating = calculateAverageRating(arr);
					String aRatingStr = new Double(averagedRating).toString();

					// add the average rating to the map
					value.put("rating", aRatingStr);
				} else {
					// put a -1 rating
					value.put("rating", "-1");
				}

				// extract information about the comments
				JSONArray commentsArray = null;
				try {
					if (eMap.get("comments") instanceof java.lang.String) {
						JSONParser parser = new JSONParser();
						commentsArray = (JSONArray) parser.parse((String) eMap
								.get("comments"));
					} else
						commentsArray = (JSONArray) eMap.get("comments");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (commentsArray != null) {
					// put number of comments
					value.put("numberOfComments", "" + commentsArray.size());
				} else {
					// put 0 number of comments
					value.put("numberOfComments", "0");
				}

			} else {
				// if no extras -->

				// put a -1 rating
				value.put("rating", "-1");

				// put 0 number of comments
				value.put("numberOfComments", "0");

			}

			// add the data to the toreturn HashMap
			toreturn.put(key, value);

			// increase the counter for the data sets
			countDataSets++;

		}

		// store the toreturn variable in the cashing component
		cInstance.insertObject(cachingKey, new Date(), toreturn);

		return toreturn;
	}

	/**
	 * Returns a list of the most popular data sets. The title, id and rating of
	 * each data set are provided.
	 * 
	 * @param numberOfDatasets
	 *            the number of data sets to return.
	 * 
	 * @return null in case of an error, or a hash map with the latest data sets
	 *         including id, rating, title.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap getMostPopularDatasets(int numberOfDatasets) {
		// check the parameter
		if (numberOfDatasets <= 0) {
			return null;
		}

		// prepare a key for the cashing
		String cachingKey = GET_MOST_POPULAR_DATASETS_KEY + "_"
				+ numberOfDatasets;
		// get a cashing instance and check whether the data is up-to-date
		Caching cInstance = Caching.getInstance();
		HashMap toreturn = (HashMap) cInstance.areValuesStillUpToDate(
				cachingKey, new Date(), CASHING_INTERVAL);
		if (toreturn != null) {
			// if the data is up-to-data --> return it
			return toreturn;
		}

		// if the data is not up to date

		// get all the meta data sets
		HashMap hm = viewMetaDataSets();

		if (hm == null) {
			return null;
		}

		// prepare the variable to return
		toreturn = new HashMap();

		// help variables for the sorting according to the averaged rating
		HashMap<String, String> relationMap = new HashMap<String, String>();
		ArrayList<String> sList = new ArrayList<String>();

		// iterate over the data sets
		Set<String> keys = (Set<String>) (hm.keySet());
		Iterator<String> it = keys.iterator();

		while (it.hasNext()) {
			String key = (String) it.next();

			// get the Map for the next object
			Map m = (Map) hm.get(key);

			// get the JSONArray with the ratings and pass it to the method for
			// calculating the average
			Map eMap = ((Map) ((Map) m).get("extras"));
			String aRatingStr = "-1";
			if (eMap != null) {
				JSONArray arr = null;
				try {
					if (eMap.get("ratings") instanceof java.lang.String){
						JSONParser parser = new JSONParser();
						arr = (JSONArray) parser.parse((String) eMap
								.get("ratings"));
					}
					else
						arr = (JSONArray) eMap.get("ratings");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				double averagedRating = calculateAverageRating(arr);
				aRatingStr = new Double(averagedRating).toString();
			}
			// set the data in the help variables
			relationMap.put(aRatingStr + ":" + key, key);
			sList.add(aRatingStr + ":" + key);
		}

		// sort the list with the averaged
		Collections.sort(sList);

		// the variable for counting the added data sets
		int countDataSets = 1;

		// iterate over the sorted list
		for (int i = sList.size() - 1; i >= 0; i--) {

			// check whether the required number of data sets are obtained
			if (countDataSets > numberOfDatasets) {
				break;
			}

			// get the current averaged rating string
			String aRatingStr = sList.get(i);

			// get the corresponding key
			String key = (String) relationMap.get(aRatingStr);

			// get the data set belonging to this key
			Map dSetMap = (Map) hm.get(key);

			// prepare a new hash that represents the map
			Map value = new HashMap();

			value.put("name", dSetMap.get("name"));
			value.put("id", dSetMap.get("id"));
			value.put("title", dSetMap.get("title"));

			StringTokenizer tk = new StringTokenizer(aRatingStr, ":");
			value.put("rating", (String) tk.nextElement());

			// add the data to the to return HashMap
			toreturn.put(key, value);

			// increase the counter for the data sets
			countDataSets++;
		}

		// store the toreturn variable in the cashing component
		cInstance.insertObject(cachingKey, new Date(), toreturn);

		return toreturn;
	}
}
