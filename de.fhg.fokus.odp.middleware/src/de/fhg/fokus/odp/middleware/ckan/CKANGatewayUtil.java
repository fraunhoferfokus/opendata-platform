/**
 * 
 */
package de.fhg.fokus.odp.middleware.ckan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class is the gateway towards the CKAN instance The util class is for non
 * simple request like metrics and special
 * 
 * 
 * @author Nikolay Tcholtchev, Fraunhofer Fokus
 * 
 * @author Thomas Scheel, Fraunhofer Fokus
 */
public class CKANGatewayUtil {
    /** The url to the CKAN gateway instance. */
    private static String url = null;

    /** The local authentication key. */
    private static String authenticationKey = null;

    /** The relative path to the CKAN package API */
    private static final String API_URI = "api/rest/package";

    /** The local logger instance. */
    private static final Logger log = Logger.getLogger(CKANGatewayUtil.class
            .getName());

    /** A singleton instance. */
    private static CKANGatewayUtil instance = null;

    /** A singleton instance. */
    private static CKANGatewayApiConnector connectorInstance = null;

    /** A singleton instance. */
    private static CKANGatewayCore coreInstance = null;

    /**
     * Function to prepare a singleton instance.
     * 
     * @param CKANurl
     *            a string with the url of the CKAN instance.
     * @param authKey
     *            the authentication key for CKAN communication.
     * 
     */
    public static void prepareInstance(String CKANurl, String authKey) {
        instance = new CKANGatewayUtil(CKANurl, authKey);
    }

    /**
     * Function to deliver the singleton instance.
     * 
     * @return the (hopefully) pre-configured CKAN Gateway.
     */
    public static CKANGatewayUtil getInstance() {
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
    private CKANGatewayUtil(String url, String authenticationKey) {

        CKANGatewayUtil.url = url;
        if (!CKANGatewayUtil.url.endsWith("/")) {
            CKANGatewayUtil.url += "/";
        }

        CKANGatewayUtil.authenticationKey = authenticationKey;

        if (CKANGatewayApiConnector.getInstance() == null) {
            CKANGatewayApiConnector.prepareInstance(CKANGatewayUtil.url,
                    CKANGatewayUtil.authenticationKey);
        }

        connectorInstance = CKANGatewayApiConnector.getInstance();

        if (CKANGatewayCore.getInstance() == null) {
            CKANGatewayCore.prepareInstance(this.url, this.authenticationKey);
        }

        coreInstance = CKANGatewayCore.getInstance();
    }

    /**
     * The function returns all meta data sets filtered according to the
     * maintainer email. The function returns a HashMap with key=metadataSetId
     * and value=HashMap(with Key = MetaDataEntryKey and value =
     * MetaDataEntryValue).
     * 
     * @param maintainerEmail
     *            the maintainer email.
     * @param fileFormat
     *            the file format to filter for.
     * 
     * @return the the hash map as described in the general comments of the
     *         method or null if anything has gone wrong.
     */
    @SuppressWarnings("rawtypes")
    public static HashMap<String, HashMap> viewDataSets(String maintainerEmail,
            String fileFormat) {

        // check the input parameters
        if (maintainerEmail == null || maintainerEmail.equals("")
                || fileFormat == null || fileFormat.trim().equals("")) {
            return null;
        }

        // prepare the REST API call
        String RESTcall = API_URI + "?maintainer_email=";
        maintainerEmail = maintainerEmail.replaceAll("@", "%40;");
        RESTcall += maintainerEmail;

        // the variable to return
        HashMap<String, HashMap> toreturn = new HashMap<String, HashMap>();

        try {
            // run the REST call to obtain all packages
            String packageListString = connectorInstance.restCall(RESTcall);
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
                String RESTcallPackage = API_URI + "/" + array.get(i);
                String packageStr = connectorInstance.restCall(RESTcallPackage);

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
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + RESTcall + "\" !!!");
            return null;
        } catch (IOException e) {
            return null;
        }

        return toreturn;
    }

    /**
     * Check whether a user should be allowed to rate a package (i.e. if there
     * is no previous rating by that user)
     * 
     * @param dataSetId
     *            the ID of the dataset.
     * @param userId
     *            the ID of the user.
     * 
     * @return true in case the user has a permission, otherwise false.
     */
    @SuppressWarnings("rawtypes")
    public static boolean hasDataSetRatingPermission(String dataSetId,
            String userId) {

        // check the parameters
        if (dataSetId == null || userId == null) {
            return false;
        }

        // get the details for the package
        Object obj = CKANGatewayCore.getDataSetDetails(dataSetId);
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
                    arr = (JSONArray) parser
                            .parse((String) eMap.get("ratings"));
                } else
                    arr = (JSONArray) eMap.get("ratings");
            } catch (ParseException e) {
                log.log(Level.SEVERE,
                        "Failed to parse result" + (String) eMap.get("ratings"));
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
    public static String getRevisions(String serverTimeZone, long offset) {

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
            while ((inputLine = in.readLine()) != null) {
                returnStr += inputLine;
            }
            in.close();

        }
        // process exceptions
        catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + CKANurl + "\" !!!");
            return null;
        } catch (IOException e) {
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
    public static Vector<String> getUpdatedDatasets(String revisionStr) {

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
                while ((inputLine = in.readLine()) != null) {
                    revisionDescriptionStr += inputLine;
                }
                in.close();

                // the description
                toreturnVector.add(revisionDescriptionStr);

            }
            // process exceptions
            catch (MalformedURLException e) {
                log.log(Level.SEVERE, "Failed to realize api call \"" + url
                        + CKANurl + "\" !!!");
                toreturnVector.add(null);
                continue;
            } catch (IOException e) {
                toreturnVector.add(null);
                continue;
            }
        }

        return toreturnVector;
    }

    /**
     * Returns a list of the most popular tags.
     * 
     * @param numberOfTags
     *            the number of popular tags to return.
     * @return the most popular tags or null if an error occurred.
     */
    @SuppressWarnings("unchecked")
    public static JSONArray getMostPopularTags(int numberOfTags) {
        // check the parameters
        if (numberOfTags <= 0) {
            return null;
        }

        // the JSON array to return
        JSONArray toReturn = new JSONArray();

        // prepare the REST API call
        String RESTcall = "api/tag_counts";

        try {
            String tagListString = connectorInstance.restCall(RESTcall);

            if (tagListString == null) {
                log.log(Level.SEVERE, "Failed to realize api call \"" + url
                        + RESTcall + "\" !!!");
                return null;
            }

            // parse the JSON string and obtain an array of JSON objects
            Object obj = JSONValue.parse(tagListString);
            JSONArray array = (JSONArray) obj;

            HashMap<String, Long> map = new HashMap<String, Long>();

            // fill unsorted HashMap with all keys and values
            for (Object tag : array) {
                JSONArray tagArray = (JSONArray) tag;
                map.put((String) tagArray.get(0), (Long) tagArray.get(1));
            }

            // call sortHashMapByValues
            HashMap<String, Long> sortedHashMap = sortHashMapByValues(map);

            // calculate number of return array size
            if (sortedHashMap.size() < numberOfTags) {
                numberOfTags = sortedHashMap.size();
            }

            // iterate over n fields and fill toReturn
            if (sortedHashMap.size() >= numberOfTags) {
                List<String> mapKeys = new ArrayList<String>(
                        sortedHashMap.keySet());
                Iterator<String> keyIt = mapKeys.iterator();
                int i = 0;
                while (keyIt.hasNext() && i < numberOfTags) {
                    String key = keyIt.next();
                    // (key, (Long) sortedHashMap.get(key));
                    JSONObject tag = new JSONObject();
                    tag.put("count", sortedHashMap.get(key));
                    tag.put("tag_name", key);
                    toReturn.add(tag);
                    i++;
                }
            }
        }
        // catch potential exceptions
        catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Malformed URL \"" + url + RESTcall
                    + "\" !!!");
            return null;
        } catch (IOException e) {
            return null;
        }

        return toReturn;
    }

    /**
     * Sort the passed Map by the values
     * 
     * @param passedMap
     *            the HashMap to sort.
     * @return the sorted HashMap.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static LinkedHashMap sortHashMapByValues(
            HashMap<String, Long> passedMap) {
        List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
        List<Long> mapValues = new ArrayList<Long>(passedMap.values());

        Comparator comparator = Collections.reverseOrder();
        Collections.sort(mapValues, comparator);
        Collections.sort(mapKeys, comparator);

        LinkedHashMap<String, Long> sortedMap = new LinkedHashMap<String, Long>();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String) key, (Long) val);
                    break;
                }

            }

        }
        return sortedMap;

    }

    /**
     * Returns a list of the most popular tags.
     * 
     * @param numberOfTags
     *            the number of popular tags to return.
     * @return the most popular tags or null if an error occurred.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static JSONArray getMostPopularTagsOld(int numberOfTags) {

        // check the parameters
        if (numberOfTags <= 0) {
            return null;
        }

        // the json array to return
        JSONArray toreturn = new JSONArray();

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
            String tagListString = connectorInstance.restCallWithAuthorization(
                    RESTcall, null);
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
                String pListString = connectorInstance
                        .restCallWithAuthorization(tRESTcall, null);

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
                        double aRatingValue = CKANGatewayUtil
                                .getDataSetRatingsAverage(pName);
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
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + RESTcall + "\" !!!");
            return null;
        } catch (IOException e) {
            return null;
        }

        return toreturn;
    }

    /**
     * Get the minimum from a double array.
     * 
     * @param data
     *            the array.
     * @return the minimum.
     */
    private static int minDoubleArray(double[] data) {
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
     * Get the average of a package’s ratings [rounded to 0.5 steps]
     * 
     * @param dataSetId
     *            the ID of the dataset.
     * @return the averaged rating or -1 if smth. has gone wrong.
     */
    @SuppressWarnings("rawtypes")
    public static double getDataSetRatingsAverage(String dataSetId) {

        // check the input
        if (dataSetId == null) {
            return -1;
        }

        // get the details for the package
        Object obj = CKANGatewayCore.getDataSetDetails(dataSetId);
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
                    arr = (JSONArray) parser
                            .parse((String) eMap.get("ratings"));
                } else {
                    arr = (JSONArray) eMap.get("ratings");
                }
            } catch (ParseException e) {
                log.log(Level.SEVERE,
                        "Failed to parse result" + (String) eMap.get("ratings"));
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
    protected static double calculateAverageRating(JSONArray arr) {
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
            toreturn = ((int) toreturn) + 0.5;
        } else if ((toreturn - (int) toreturn) >= 0.75) {
            toreturn = ((int) toreturn) + 1.0;
        } else if ((toreturn - (int) toreturn) <= 0.75) {
            toreturn = ((int) toreturn);
        }

        return toreturn;
    }

    /**
     * Submit rating for a package [rating: number of stars, 1..5]
     * 
     * @param dataSetId
     *            the id of the dataset to rate.
     * @param userId
     *            the ID of the user submitting the rating.
     * @param date
     *            the date at which the rating takes place.
     * @param rating
     *            the rating value.
     * @return true in case of success, false otherwise.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean postDataSetRating(String dataSetId, String userId,
            Date date, int rating) {

        // check the parameters
        if (dataSetId == null || dataSetId.equals("") || userId == null
                || userId.equals("") || rating < 0 || rating > 5) {
            return false;
        }

        // get the package details
        Object obj = CKANGatewayCore.getDataSetDetails(dataSetId);
        if (obj == null) {
            return false;
        }

        // prepare the extras string

        // pick the extras
        Map mMap = (Map) obj;
        Map eMap = (Map) mMap.get("extras");
        String resource = mMap.get("resources").toString();
        JSONArray tArray = (JSONArray) mMap.get("tags");
        JSONArray gArray = (JSONArray) mMap.get("groups");

        // check weather extras is defined
        if (eMap != null) {

            JSONArray ratings = null;
            try {
                if (eMap.get("ratings") instanceof java.lang.String) {
                    JSONParser parser = new JSONParser();
                    ratings = (JSONArray) parser.parse((String) eMap
                            .get("ratings"));
                } else {
                    ratings = (JSONArray) eMap.get("ratings");
                }
            } catch (ParseException e) {
                log.log(Level.SEVERE,
                        "Failed to parse result" + (String) eMap.get("ratings"));
            }

            if (ratings == null) {
                ratings = new JSONArray();
            }

            Map<String, String> toadd = new HashMap<String, String>();

            toadd.put("userId", userId);
            toadd.put("ratingValue", Integer.valueOf(rating).toString());
            toadd.put("date", date.toString());

            ratings.add(toadd);
            eMap.put("ratings", ratings);
        }

        // prepare the hash map for the update method
        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("extras", eMap.toString());
        hm.put("resources", resource);
        hm.put("tags", tArray.toJSONString());
        hm.put("groups", gArray.toJSONString());

        // update the meta data set
        HashMap<String, HashMap> res = CKANGatewayCore.updateDataSet(dataSetId,
                hm);
        boolean toreturn = (res != null);

        return toreturn;
    }

    /**
     * Submit comment for a package.
     * 
     * @param dataSetId
     *            the id of the dataset.
     * @param userId
     *            the user ID.
     * @param date
     *            the date of the commenting
     * @param comment
     *            the comment.
     * @return true if everything is fine, false otherwise.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean postDatasetComment(String dataSetId, String userId,
            Date date, String comment) {

        // check the parameters
        if (dataSetId == null || dataSetId.equals("") || userId == null
                || userId.equals("") || comment == null || comment.equals("")) {
            return false;
        }

        // get the package details
        Object obj = CKANGatewayCore.getDataSetDetails(dataSetId);
        if (obj == null) {
            return false;
        }

        // prepare the extras string

        // pick the extras
        Map mMap = (Map) obj;

        String resource = mMap.get("resources").toString();
        Map eMap = (Map) mMap.get("extras");
        JSONArray tArray = (JSONArray) mMap.get("tags");
        JSONArray gArray = (JSONArray) mMap.get("groups");

        // check weather extras is defined
        if (eMap != null) {

            JSONArray comments = null;
            try {
                if (eMap.get("comments") instanceof java.lang.String) {
                    JSONParser parser = new JSONParser();
                    comments = (JSONArray) parser.parse((String) eMap
                            .get("comments"));
                } else {
                    comments = (JSONArray) eMap.get("comments");
                }
            } catch (ParseException e) {
                log.log(Level.SEVERE,
                        "Failed to parse result"
                                + (String) eMap.get("comments"));
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
        hm.put("tags", tArray.toJSONString());
        hm.put("groups", gArray.toJSONString());

        // update the meta data set
        HashMap<String, HashMap> res = CKANGatewayCore.updateDataSet(dataSetId,
                hm);
        boolean toreturn = (res != null);

        return toreturn;
    }

    /**
     * Get the number of existing comments for a package.
     * 
     * @param dataSetId
     *            the dataset ID.
     * 
     * @return the number of existing comments or -1 in case of an error.
     */
    public static int getDataSetCommentsCount(String dataSetId) {

        JSONArray arr = CKANGatewayUtil.getDataSetComments(dataSetId);
        if (arr == null) {
            return -1;
        }

        return arr.size();
    }

    /**
     * Get the comments for a package.
     * 
     * @param dataSetId
     *            the dataset ID.
     * @return a JSON array with the comments or null in case of an error.
     */
    @SuppressWarnings("rawtypes")
    public static JSONArray getDataSetComments(String dataSetId) {

        // check the parameter
        if (dataSetId == null) {
            return null;
        }

        // get the package details
        Object obj = CKANGatewayCore.getDataSetDetails(dataSetId);
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
                } else {
                    toreturn = (JSONArray) eMap.get("comments");
                }
            } catch (ParseException e) {
                log.log(Level.SEVERE,
                        "Failed to parse result"
                                + (String) eMap.get("comments"));
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
    public static Vector getUpdatedDataSetsDetails(
            Vector<String> revisionsDetails) {

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
                Object pkgObject = CKANGatewayCore.getDataSetDetails(pkg);

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
    public static Vector<Map> getUpdatedCategoriesDetails(
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
                    String restResponse = connectorInstance
                            .restCallWithAuthorization(RESTcall, null);
                    Map grMap = (Map) JSONValue.parse(restResponse);

                    toreturn.add(grMap);

                } catch (MalformedURLException e) {
                    log.log(Level.SEVERE, "Failed to realize api call \"" + url
                            + RESTcall + "\" !!!");
                    continue;
                } catch (IOException e) {
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
    public static HashMap getLatestDatasets(int numberOfDatasets) {

        // check the parameter
        if (numberOfDatasets <= 0) {
            return null;
        }

        // prepare the variable to return
        // HashMap toreturn = new HashMap();

        Object result = CKANGatewaySearch
                .getDataSetSearchResults("sort=metadata_modified+desc&all_fields=1&limit="
                        + numberOfDatasets);

        // JSONObject test = (JSONObject) result;

        // toreturn = (JSONObject) result;

        return (JSONObject) result;
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
    @Deprecated
    public static HashMap getLatestDatasetsOld(int numberOfDatasets) {

        // check the parameter
        if (numberOfDatasets <= 0) {
            return null;
        }

        HashMap hm = CKANGatewayCore.viewDataSets();

        if (hm == null) {
            return null;
        }

        // prepare the variable to return
        HashMap toreturn = new HashMap();

        // help variables for the sorting according to the metadata_modification
        // date
        HashMap<String, String> relationMap = new HashMap<String, String>();
        ArrayList<String> sList = new ArrayList<String>();

        // iterate over the data sets
        Set<String> keys = (hm.keySet());
        Iterator<String> it = keys.iterator();

        while (it.hasNext()) {
            String key = it.next();

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
            String dStr = sList.get(i);

            // obtain the belonging key from the relations help map
            String key = relationMap.get(dStr);

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
            value.put("author", helpMap.get("author"));
            value.put("license_id", helpMap.get("license_id"));
            value.put("resources", helpMap.get("resources"));

            // extract relevant data from the extras
            Map eMap = ((Map) helpMap.get("extras"));
            if (eMap != null) {
                // extract information about the ratings
                JSONArray arr = null;
                try {
                    if (eMap.get("ratings") instanceof java.lang.String) {
                        JSONParser parser = new JSONParser();
                        arr = (JSONArray) parser.parse((String) eMap
                                .get("ratings"));
                    } else {
                        arr = (JSONArray) eMap.get("ratings");
                    }
                } catch (ParseException e) {
                    log.log(Level.SEVERE, "Failed to parse result"
                            + (String) eMap.get("ratings"));
                }

                if (arr != null) {
                    double averagedRating = calculateAverageRating(arr);
                    String aRatingStr = new Double(averagedRating).toString();

                    // add the average rating to the map
                    value.put("rating", aRatingStr);
                    value.put("ratings_average", aRatingStr);
                    value.put("ratings_count", arr.size());
                } else {
                    // put a -1 rating
                    value.put("rating", "-1");
                    value.put("ratings_count", 0);
                    value.put("ratings_average", "-1");
                }

                // extract information about the comments
                JSONArray commentsArray = null;
                try {
                    if (eMap.get("comments") instanceof java.lang.String) {
                        JSONParser parser = new JSONParser();
                        commentsArray = (JSONArray) parser.parse((String) eMap
                                .get("comments"));
                    } else {
                        commentsArray = (JSONArray) eMap.get("comments");
                    }
                } catch (ParseException e) {
                    log.log(Level.SEVERE, "Failed to parse result"
                            + (String) eMap.get("comments"));
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
    public static HashMap getMostPopularDatasets(int numberOfDatasets) {
        // check the parameter
        if (numberOfDatasets <= 0) {
            return null;
        }

        // get all the meta data sets
        HashMap hm = CKANGatewayCore.viewDataSets();

        if (hm == null) {
            return null;
        }

        // prepare the variable to return
        HashMap toreturn = new HashMap();

        // help variables for the sorting according to the averaged rating
        HashMap<String, String> relationMap = new HashMap<String, String>();
        ArrayList<String> sList = new ArrayList<String>();

        // iterate over the data sets
        Set<String> keys = (hm.keySet());
        Iterator<String> it = keys.iterator();

        while (it.hasNext()) {
            String key = it.next();

            // get the Map for the next object
            Map m = (Map) hm.get(key);

            // get the JSONArray with the ratings and pass it to the method for
            // calculating the average
            Map eMap = ((Map) m.get("extras"));
            String aRatingStr = "-1";
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
                    log.log(Level.SEVERE, "Failed to parse result"
                            + (String) eMap.get("ratings"));
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
            String key = relationMap.get(aRatingStr);

            // get the data set belonging to this key
            Map dSetMap = (Map) hm.get(key);

            // prepare a new hash that represents the map
            Map value = new HashMap();

            value.put("name", dSetMap.get("name"));
            value.put("id", dSetMap.get("id"));
            value.put("title", dSetMap.get("title"));

            StringTokenizer tk = new StringTokenizer(aRatingStr, ":");
            value.put("rating", tk.nextElement());

            // add the data to the to return HashMap
            toreturn.put(key, value);

            // increase the counter for the data sets
            countDataSets++;
        }

        return toreturn;
    }

    /**
     * Returns the count of all active data sets in the registry.
     * 
     * @return null in case of an error, or a long value with the data set
     *         count.
     */
    public static Long getAllDataSetsCount() {
        // the JSON object to return
        Long count = 0L;

        // prepare the REST call
        String RESTcall = "api/search/dataset";

        try {
            // get the search results
            String searchResultsStr = connectorInstance.restCall(RESTcall);

            // check the API results
            if (searchResultsStr == null) {
                return null;
            }

            // parse the string for the search results
            JSONObject toreturn = (JSONObject) JSONValue
                    .parse(searchResultsStr);
            count = (Long) toreturn.get("count");
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + RESTcall + "\" !!!");
            return null;
        } catch (IOException e) {
            return null;
        }

        return count;
    }

    /**
     * Returns the count of all active data sets in the provided group of the
     * registry.
     * 
     * @param groupName
     *            the name of the group
     * 
     * @return null in case of an error, or a long value with the data set
     *         count.
     */
    public static Long getGroupDataSetsCount(String groupName) {
        Long count = 0L;

        // check the submitted parameters
        if (groupName == null) {
            return null;
        }

        // prepare the REST call
        String RESTcall = "api/search/dataset?groups=" + groupName;

        try {
            // get the search results
            String searchResultsStr = connectorInstance.restCall(RESTcall);

            // check the API results
            if (searchResultsStr == null) {
                return null;
            }

            // parse the string for the search results
            JSONObject toreturn = (JSONObject) JSONValue
                    .parse(searchResultsStr);
            count = (Long) toreturn.get("count");
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + RESTcall + "\" !!!");
            return null;
        } catch (IOException e) {
            return null;
        }

        return count;
    }

    /**
     * Returns the count of all ratings associated with this dataSetId.
     * 
     * @param dataSetId
     *            the dataset ID
     * 
     * @return null in case of an error, or a long value with the the rating
     *         count.
     * 
     */
    @SuppressWarnings("rawtypes")
    public static Long getRatingCount(String dataSetId) {
        Long count = 0L;

        // check the submitted parameters
        if (dataSetId == null) {
            return null;
        }

        // get the details for the package
        Object obj = CKANGatewayCore.getDataSetDetails(dataSetId);
        if (obj == null) {
            return null;
        }

        // get the JSONArray with the ratings and pass it to the method for
        // calculating the average
        Map eMap = (Map) ((Map) obj).get("extras");
        if (eMap != null) {
            JSONArray arr = null;
            try {
                if (eMap.get("ratings") instanceof java.lang.String) {
                    JSONParser parser = new JSONParser();
                    arr = (JSONArray) parser
                            .parse((String) eMap.get("ratings"));
                } else
                    arr = (JSONArray) eMap.get("ratings");
            } catch (ParseException e) {
                log.log(Level.SEVERE,
                        "Failed to parse result" + (String) eMap.get("ratings"));
            }

            count = Long.valueOf(arr.size());
        }
        return count;
    }
}

/**
 * {@inheritDoc}
 * 
 * Comparator class which is used in the sortHashMapByValue function.
 * 
 * TODO put in extra util class
 * 
 * @author tsc
 * 
 */
class ValueComparator implements Comparator<String> {

    /** temp Map for sort */
    Map<String, Long> base;

    /**
     * compares the two values
     * 
     * @param base
     *            temp map for compare
     */
    public ValueComparator(Map<String, Long> base) {
        this.base = base;
    }

    /**
     * {@inheritDoc}
     * 
     * overrides compare method
     */
    @Override
    public int compare(String a, String b) {
        return base.get(a).compareTo(base.get(b));
    }
}