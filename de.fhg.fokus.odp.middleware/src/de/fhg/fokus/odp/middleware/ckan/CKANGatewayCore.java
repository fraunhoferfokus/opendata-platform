package de.fhg.fokus.odp.middleware.ckan;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This class provides all methods for CRUD operations on the CKAN Instance.
 * uses {@link eu.engage.core.services.middleware.ckan.CKANGatewayApiConnector}
 * for sending and receiving data.
 * 
 * 
 * @author Nikolay Tcholtchev, Fraunhofer Fokus
 * 
 * @author Thomas Scheel, Fraunhofer Fokus
 * 
 * @see CKANGatewayApiConnector
 */
public class CKANGatewayCore {

    /** The url to the CKAN gateway instance. */
    private static String url = null;

    /** The local authentication key. */
    private static String authenticationKey = null;

    /** The relative path to the CKAN package API */
    private static final String API_URI = "api/rest/package";

    /** The local logger instance. */
    private static final Logger log = Logger.getLogger(CKANGatewayCore.class
            .getName());

    /** A singleton instance. */
    private static CKANGatewayCore instance = null;

    /** A singleton instance. */
    private static CKANGatewayApiConnector connectorInstance = null;

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
        instance = new CKANGatewayCore(CKANurl, authKey);
    }

    /**
     * Function to deliver the singleton instance.
     * 
     * @return the (hopefully) pre-configured CKAN Gateway.
     */
    public static CKANGatewayCore getInstance() {
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
    private CKANGatewayCore(String url, String authenticationKey) {

        CKANGatewayCore.url = url;
        if (!CKANGatewayCore.url.endsWith("/")) {
            CKANGatewayCore.url += "/";
        }

        CKANGatewayCore.authenticationKey = authenticationKey;

        if (CKANGatewayApiConnector.getInstance() == null) {
            CKANGatewayApiConnector.prepareInstance(CKANGatewayCore.url,
                    CKANGatewayCore.authenticationKey);
        }

        connectorInstance = CKANGatewayApiConnector.getInstance();
    }

    /**
     * Get package details.
     * 
     * @param dataSetId
     *            the id of the package.
     * @return a json object containing the package details or "null" in case of
     *         an error.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getDataSetDetails(String dataSetId) {

        // check the input
        if (dataSetId == null || dataSetId.equals("")) {
            return null;
        }

        // the toreturn object
        Object toreturn = null;

        // get details for each single package
        String RESTcallPackage = "api/2/rest/package/" + dataSetId;
        try {

            // get the json output
            String packageStr = connectorInstance.restCallWithAuthorization(
                    RESTcallPackage, null);

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
                String groupStr = connectorInstance.restCallWithAuthorization(
                        RESTcallGroup, null);
                if (groupStr == null) {
                    continue;
                }
                Map m = (Map) JSONValue.parse(groupStr);
                arr.set(i, (m.get("name")));
            }

        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + API_URI + "\" !!!");
            return null;
        } catch (IOException e) {
            return null;
        }

        return toreturn;
    }

    /**
     * View all MetaData entries. The function returns a HashMap with
     * key=dataSetID and value=HashMap(with Key = MetaDataEntryKey and value =
     * MetaDataEntryValue).
     * 
     * @return the hash map as described in the general comments of the method
     *         or null if anything has gone wrong.
     */
    @SuppressWarnings("rawtypes")
    public static HashMap<String, HashMap> viewDataSets() {

        // the variable to return
        HashMap<String, HashMap> toreturn = new HashMap<String, HashMap>();

        try {
            // run the REST call to obtain all packages
            String packageListString = connectorInstance.restCall(API_URI);
            if (packageListString == null) {
                log.log(Level.SEVERE, "Failed to realize api call \"" + url
                        + API_URI + "\" !!!");
                return null;
            }

            // parse the JSON string and obtain an array of JSON objects
            Object obj = JSONValue.parse(packageListString);
            JSONArray array = (JSONArray) obj;

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

                toreturn.put((String) array.get(i), helpMap);
            }
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + API_URI + "\" !!!");
            return null;
        } catch (IOException e) {
            log.log(Level.SEVERE, "IOExeption");
            return null;
        }

        return toreturn;
    }

    /**
     * Create a new MetaDataSet in CKAN. In the HashMap<String, String>, the key
     * = metadataentry, and value = value corresponding to the meta dataentry.
     * 
     * @param hm_dataSet
     *            hash map as described in the general comments of the method.
     * @return null in case of an error, otherwise a hash map of arrays and maps
     *         reflecting the structure of the submitted meta data set.
     */
    @SuppressWarnings("rawtypes")
    public static HashMap<String, HashMap> createDataSet(
            HashMap<String, String> hm_dataSet) {

        // check the submitted parameters
        if (hm_dataSet == null) {
            return null;
        }

        String name = hm_dataSet.get("name");
        if (name == null) {
            return null;
        }

        // get the set of keys
        Set<String> s = hm_dataSet.keySet();
        Iterator<String> it = s.iterator();

        // prepare the JSON string
        String jsonStr = "";

        // iterate over the set of keys
        while (it.hasNext()) {
            String key = it.next();
            if (!jsonStr.equals("")) {
                jsonStr += ",";
            }
            if (key.equals("notes") || key.equals("description")
                    || key.equals("comment")) {
                if (hm_dataSet.get(key).contains("\n")) {
                    hm_dataSet.put(key,
                            hm_dataSet.get(key).replaceAll("\n", "\\\\n"));
                }

            }
            jsonStr += "\"" + key + "\":" + hm_dataSet.get(key);
        }

        jsonStr = "{" + jsonStr + "}";
        log.info("CREATE DATASET json:" + jsonStr);
        // System.out.println("############## CREATE DATASET json:" + jsonStr);

        try {
            connectorInstance.restCallWithAuthorization(API_URI, jsonStr);
        } catch (MalformedURLException e) {
            log.log(Level.INFO, "Failed to realize api call \"" + url + API_URI
                    + "\" with this JSON string " + jsonStr + "!!!");
            return null;
        } catch (IOException e) {
            return null;
        }

        // add the package to the groups
        JSONArray grNewArr = null;
        String groupsStr = hm_dataSet.get("groups");
        if (groupsStr != null) {
            grNewArr = (JSONArray) (JSONValue.parse(groupsStr));

            // add package to groups
            for (int i = 0; i < grNewArr.size(); i++) {
                addDataSetToGroup((String) (grNewArr.get(i)), name);
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
     * @param dataSetID
     *            the id of the MetaDataSet in CKAN.
     * @param hm_dataSet
     *            hash map as described in the general comments of the method.
     * @return null in case of an error, otherwise a hash map of arrays and maps
     *         reflecting the structure of the submitted meta data set.
     */
    @SuppressWarnings("rawtypes")
    public static HashMap<String, HashMap> updateDataSet(String dataSetID,
            HashMap<String, String> hm_dataSet) {

        // check the passed parameters
        if (dataSetID == null || hm_dataSet == null) {
            return null;
        }

        // obtain the current data in the meta data set
        Object obj = getDataSetDetails(dataSetID);
        if (obj == null) {
            return null;
        }
        Map objM = (Map) obj;
        ArrayList grOldArr = (ArrayList) objM.get("groups");

        // get the groups string
        JSONArray grNewArr = null;
        String groupsStr = hm_dataSet.get("groups");
        if (groupsStr != null) {
            grNewArr = (JSONArray) (JSONValue.parse(groupsStr));
        }

        // obtain the packages to delete from and add the group to
        ArrayList arrToDeletePackageFrom = getGroupsToDeleteDataSetFrom(
                grOldArr, grNewArr);
        ArrayList arrToAddPackageTo = getGroupsToAddDataSetTo(grOldArr,
                grNewArr);

        // get the set of keys
        Set<String> s = hm_dataSet.keySet();
        Iterator<String> it = s.iterator();

        // prepare the JSON string
        String jsonStr = "";

        // iterate over the set of keys
        while (it.hasNext()) {
            String key = it.next();
            if (!jsonStr.equals("")) {
                jsonStr += ",";
            }
            jsonStr += "\"" + key + "\":" + hm_dataSet.get(key) + "";
        }
        jsonStr = "{" + jsonStr + "}";

        log.info("update JSON String" + jsonStr);

        // prepare the rest call
        String RESTCall = API_URI + "/" + dataSetID;

        try {
            // String returnStr = coreInstance.restCall(RESTCall, jsonStr);
            connectorInstance.restCallWithAuthorization(RESTCall, jsonStr);

            // remove package from groups where it was before
            for (int i = 0; i < arrToDeletePackageFrom.size(); i++) {
                deleteDataSetFromGroup(
                        (String) (arrToDeletePackageFrom.get(i)), dataSetID);
            }

            // add package to groups
            for (int i = 0; i < arrToAddPackageTo.size(); i++) {
                addDataSetToGroup((String) (arrToAddPackageTo.get(i)),
                        dataSetID);
            }

        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + API_URI + "\" !!!");
            return null;
        } catch (IOException e) {
            return null;
        }

        // prepare the object to return
        HashMap<String, HashMap> toreturn = new HashMap<String, HashMap>();
        Object packageObj = JSONValue.parse(jsonStr);
        HashMap helpMap = (HashMap) packageObj;

        toreturn.put(dataSetID, helpMap);

        return toreturn;
    }

    /**
     * Delete a MetaDataEntry. In order to select which MetaDataSet is to be
     * deleted, the id of the MetaDataSet in CKAN would be provided. A boolean
     * needs to be returned as ack to indicate whether the MetaDataSet deletion
     * has been successful or not.
     * 
     * @param dataSetID
     *            the id of the MetaDataSet in CKAN.
     * @return a boolean indicating the success or failure of the operation.
     */
    public static boolean deleteDataSet(String dataSetID) {

        // check the passed parameters
        if (dataSetID == null) {
            return false;
        }

        Object obj = CKANGatewayCore.getDataSetDetails(dataSetID);
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

        CKANGatewayCore.updateDataSet(dataSetID, hm);

        return true;
    }

    /**
     * Delete a MetaDataEntry. In order to select which MetaDataSet is to be
     * deleted, the id of the MetaDataSet in CKAN would be provided. A boolean
     * needs to be returned as ack to indicate whether the MetaDataSet deletion
     * has been successful or not.
     * 
     * @param dataSetIDs
     *            the id of the MetaDataSet in CKAN.
     * @return a boolean indicating the success or failure of the operation.
     */
    public static boolean deleteDataSets(Vector<String> dataSetIDs) {

        // check the submitted parameters
        if (dataSetIDs == null) {
            return false;
        }

        // the variable to return
        boolean toreturn = true;

        // invoke deleteMetaDataSet for each set ID
        for (int i = 0; i < dataSetIDs.size(); i++) {
            if (!deleteDataSet(dataSetIDs.get(i))) {
                toreturn = false;
            }
        }

        return toreturn;
    }

    /**
     * Undelete a MetaDataEntry. In order to select which MetaDataSet is to be
     * deleted, the id of the MetaDataSet in CKAN would be provided. A boolean
     * needs to be returned as ack to indicate whether the MetaDataSet
     * undeletion has been successful or not.
     * 
     * @param dataSetID
     *            the id of the MetaDataSet in CKAN.
     * @return a boolean indicating the success or failure of the operation.
     */
    public static boolean undeleteDataSet(String dataSetID) {

        // check the passed parameters
        if (dataSetID == null) {
            return false;
        }

        Object obj = CKANGatewayCore.getDataSetDetails(dataSetID);
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

        // HashMap<String, HashMap> ret =
        // CKANGatewayCore.updateDataSet(dataSetID,
        // hm);
        // log.fine(ret.toString());
        return true;
    }

    /**
     * tries to purge all deleted datasets. Success may depend on CKAN version.
     * 
     * @return true if purge is successful, false if not.
     */
    public static boolean purgeAllDataSets() {
        String purgeUri = "ckan-admin/trash?purge-packages=purge";

        try {
            String ret = connectorInstance.restCallWithAuthorization(purgeUri,
                    null);
            log.fine("### purge output: " + ret);
            return ret.contains("<ul class=\"datasets\">      </ul>");
            // TODO hier weiter wenn URL bekannt
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + API_URI + "\" !!!");
            return false;
        } catch (IOException e) {
            return false;
        }
        // return false;
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
    private static ArrayList getGroupsToAddDataSetTo(ArrayList grOldArr,
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
    private static ArrayList getGroupsToDeleteDataSetFrom(ArrayList grOldArr,
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
     * The method updates a group by adding an additional meta data set.
     * 
     * @param grpName
     *            the name of the group.
     * @param dataSetID
     *            the meta data set ID.
     * 
     * @return true in case of a success, otherwise false.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static boolean addDataSetToGroup(String grpName, String dataSetID) {

        // check the input parameters
        if (grpName == null || dataSetID == null) {
            return false;
        }

        // trim the meta data-set ID in case it is put in quotes
        if (dataSetID.startsWith("\"")) {
            dataSetID = dataSetID.substring(1, dataSetID.length() - 1);
        }

        if (dataSetID.endsWith("\"")) {
            dataSetID = dataSetID.substring(0, dataSetID.length() - 2);
        }

        // prepare the REST call for getting the groups
        String RESTcall = "api/rest/group/" + grpName;
        try {

            // get the information about the group
            String returnStr = connectorInstance.restCallWithAuthorization(
                    RESTcall, null);
            if (returnStr == null) {
                return false;
            }

            // parse
            Map group = (Map) (JSONValue.parse(returnStr));

            // get the packages
            JSONArray pkgs = (JSONArray) group.get("packages");
            pkgs.add(dataSetID);
            group.put("packages", pkgs);

            // add the packages
            connectorInstance.restCallWithAuthorization(RESTcall,
                    ((JSONObject) group).toJSONString());

        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + API_URI + "\" !!!");
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * The method updates a group by removing meta data set from it.
     * 
     * @param grpName
     *            the name of the group.
     * @param dataSetID
     *            the meta data set ID.
     * 
     * @return true in case of a success, otherwise false.
     */
    @SuppressWarnings({ "rawtypes" })
    private static boolean deleteDataSetFromGroup(String grpName,
            String dataSetID) {

        // prepare the REST call for getting the groups
        String RESTcall = "api/rest/group/" + grpName;
        try {

            // get the information about the group
            String returnStr = connectorInstance.restCallWithAuthorization(
                    RESTcall, null);
            if (returnStr == null) {
                return false;
            }

            // parse
            Map group = (Map) (JSONValue.parse(returnStr));

            // get the packages
            JSONArray pkgs = (JSONArray) group.get("packages");
            pkgs.remove(dataSetID);

            // add the packages
            connectorInstance.restCallWithAuthorization(RESTcall,
                    ((JSONObject) group).toJSONString());

        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + API_URI + "\" !!!");
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Get groups list including details.
     * 
     * @return a hash map of JSON objects containing the data for each group.
     */
    public static HashMap<String, Object> getGroupsData() {
        // refactor with this request
        // http://172.19.4.170/api/3/action/group_list and data
        // {
        // "sort": "title",
        // "all_fields": true
        // }

        // prepare the REST call for getting the groups
        String RESTcall = "api/rest/group";

        // the variable to return
        HashMap<String, Object> toreturn = new HashMap<String, Object>();

        try {
            // get the results for all groups
            String groupResults = connectorInstance.restCallWithAuthorization(
                    RESTcall, null);

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
                String groupDetailsStr = connectorInstance
                        .restCallWithAuthorization(groupRestCall, null);

                // parse the returned JSON string
                Object obj = JSONValue.parse(groupDetailsStr);

                // store in the variable to return
                toreturn.put((String) groupsDataObj.get(i), obj);

            }
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + API_URI + "\" !!!");
            return null;
        } catch (IOException e) {
            return null;
        }
        // log.fine("####" + toreturn);
        return toreturn;
    }

    /**
     * Get tags list.
     * 
     * @return a JSON array containing the list of all tags.
     */
    public static JSONArray getTagsData() {

        // prepare the REST call
        String RESTcall = "api/rest/tag";

        // the JSON array to return
        JSONArray toreturn = null;

        try {
            // get the search results
            String tagResults = connectorInstance.restCallWithAuthorization(
                    RESTcall, null);

            // check the API results
            if (tagResults == null) {
                return null;
            }

            // parse the string for the tag results
            Object tagObj = JSONValue.parse(tagResults);
            toreturn = (JSONArray) tagObj;

        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Failed to realize api call \"" + url
                    + API_URI + "\" !!!");
            return null;
        } catch (IOException e) {
            return null;
        }

        return toreturn;
    }

}
