package de.fhg.fokus.odp.middleware.ckan;

// imports
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.json.simple.JSONArray;

/**
 * This class is the gateway towards the CKAN instance
 * 
 * 
 * @author Nikolay Tcholtchev, nikolay.tcholtchev@fraunhofer.fokus.de
 * @author Thomas Scheel, Fraunhofer Fokus
 * 
 */
public class CKANGateway implements ICKANGateway {

    /** The url to the CKAN gateway instance. */
    private String url = null;

    /** The local authentication key. */
    private String authenticationKey = null;

    /** The local logger instance. */
    private static final Logger log = Logger.getLogger(CKANGateway.class
            .getName());

    /** A singleton instance. */
    private static CKANGateway instance = null;

    // TODO connectorInstanz entfernen
    /** instance of the connector */
    private static CKANGatewayApiConnector connectorInstance = null;

    /** instance of the core */
    private static CKANGatewayCore coreInstance = null;

    /** instance of the search */
    private static CKANGatewaySearch searchInstance = null;

    /** instance of the util */
    private static CKANGatewayUtil utilInstance = null;

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
        /*
         * if (CKANGatewayCore.getInstance() == null)
         * CKANGatewayCore.prepareInstance(CKANurl, authKey);
         * 
         * if (CKANGatewayApiConnector.getInstance() == null)
         * CKANGatewayApiConnector.prepareInstance(CKANurl, authKey);
         * 
         * connectorInstance = CKANGatewayApiConnector.getInstance(); //
         * .prepareInstance(CKANurl, // authKey);
         */
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

        if (CKANGatewayCore.getInstance() == null) {
            CKANGatewayCore.prepareInstance(this.url, this.authenticationKey);
        }

        coreInstance = CKANGatewayCore.getInstance();

        // TODO Connector entfernen - connector Instanz muss bei Authentication
        // Methoden geholt werden. Error wenn nicht initialisiert. Oder static
        // ganz entfernen.
        if (CKANGatewayApiConnector.getInstance() == null) {
            CKANGatewayApiConnector.prepareInstance(this.url,
                    this.authenticationKey);
        }

        connectorInstance = CKANGatewayApiConnector.getInstance();

        if (CKANGatewaySearch.getInstance() == null) {
            CKANGatewaySearch.prepareInstance(this.url, this.authenticationKey);
        }

        searchInstance = CKANGatewaySearch.getInstance();

        if (CKANGatewayUtil.getInstance() == null) {
            CKANGatewayUtil.prepareInstance(this.url, this.authenticationKey);
        }

        utilInstance = CKANGatewayUtil.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public Object getDataSetDetails(String DataSetId) {
        return CKANGatewayCore.getDataSetDetails(DataSetId);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public HashMap<String, HashMap> viewDataSets() {
        return CKANGatewayCore.viewDataSets();
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public HashMap<String, HashMap> createDataSet(
            HashMap<String, String> hm_DataSet) {
        return CKANGatewayCore.createDataSet(hm_DataSet);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public HashMap<String, HashMap> updateDataSet(String dataSetID,
            HashMap<String, String> hm_DataSet) {
        return CKANGatewayCore.updateDataSet(dataSetID, hm_DataSet);
    }

    /** {@inheritDoc} */
    @Override
    public boolean deleteDataSet(String dataSetID) {
        return CKANGatewayCore.deleteDataSet(dataSetID);
    }

    /** {@inheritDoc} */
    @Override
    public boolean deleteDataSets(Vector<String> dataSetIDs) {
        return CKANGatewayCore.deleteDataSets(dataSetIDs);
    }

    /** {@inheritDoc} */
    @Override
    public boolean undeleteDataSet(String dataSetID) {
        return CKANGatewayCore.undeleteDataSet(dataSetID);
    }

    /** {@inheritDoc} */
    @Override
    public boolean purgeAllDataSets() {
        return CKANGatewayCore.purgeAllDataSets();
    }

    /** {@inheritDoc} */
    @Override
    public HashMap<String, Object> getGroupsData() {
        return CKANGatewayCore.getGroupsData();
    }

    /** {@inheritDoc} */
    @Override
    public JSONArray getTagsData() {
        return CKANGatewayCore.getTagsData();
    }

    /** {@inheritDoc} */
    @Override
    public Object getDataSetSearchResults(String params) {
        return CKANGatewaySearch.getDataSetSearchResults(params);
    }

    /**
     * Get dataset search results.
     * 
     * @param params
     *            string of parameters for the dataset search.
     * 
     * @return JSON object containing the search results or "null" if something
     *         has gone wrong.
     */
    @Deprecated
    public Object getPackageSearchResults(String params) {
        return CKANGatewaySearch.getDataSetSearchResults(params);
    }

    /** {@inheritDoc} */
    @Override
    @Deprecated
    public Object getDataSetSearchResultsAsOwner(String params) {
        return CKANGatewaySearch.getDataSetSearchResultsAsOwner(params);
    }

    /**
     * Get dataset search results as owner. This means, that you see deleted
     * datasets, too.
     * 
     * @param params
     *            string of parameters for the dataset search.
     * 
     * @return JSON object containing the search results or "null" if something
     *         has gone wrong.
     */
    @Deprecated
    public Object getPackageSearchResultsAsOwner(String params) {
        return CKANGatewaySearch.getDataSetSearchResultsAsOwner(params);
    }

    /**
     * Check whether a user should be allowed to rate a dataset (i.e. if there
     * is no previous rating by that user)
     * 
     * @param dataSetId
     *            the ID of the dataset.
     * @param userId
     *            the ID of the user.
     * 
     * @return true in case the user has a permission, otherwise false.
     */
    @Deprecated
    public boolean hasPackageRatingPermission(String dataSetId, String userId) {
        return CKANGatewayUtil.hasDataSetRatingPermission(dataSetId, userId);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasDataSetRatingPermission(String dataSetId, String userId) {
        return CKANGatewayUtil.hasDataSetRatingPermission(dataSetId, userId);
    }

    /** {@inheritDoc} */
    @Override
    public String getRevisions(String serverTimeZone, long offset) {
        return CKANGatewayUtil.getRevisions(serverTimeZone, offset);
    }

    /** {@inheritDoc} */
    @Override
    public Vector<String> getUpdatedDatasets(String revisionStr) {
        return CKANGatewayUtil.getUpdatedDatasets(revisionStr);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public HashMap<String, HashMap> viewDataSets(String maintainerEmail,
            String fileFormat) {
        return CKANGatewayUtil.viewDataSets(maintainerEmail, fileFormat);
    }

    /** {@inheritDoc} */
    @Override
    public JSONArray getMostPopularTags(int numberOfTags) {
        return CKANGatewayUtil.getMostPopularTags(numberOfTags);
    }

    /**
     * Submit rating for a dataset [rating: number of stars, 1..5]
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
    @Deprecated
    public boolean postPackageRating(String dataSetId, String userId,
            Date date, int rating) {
        return CKANGatewayUtil.postDataSetRating(dataSetId, userId, date,
                rating);
    }

    /** {@inheritDoc} */
    @Override
    public boolean postDataSetRating(String dataSetId, String userId,
            Date date, int rating) {
        return CKANGatewayUtil.postDataSetRating(dataSetId, userId, date,
                rating);
    }

    /** {@inheritDoc} */
    @Override
    public double getDataSetRatingsAverage(String dataSetId) {
        return CKANGatewayUtil.getDataSetRatingsAverage(dataSetId);
    }

    /**
     * Get the average of a dataset’s ratings [rounded to 0.5 steps]
     * 
     * @param dataSetId
     *            the ID of the dataset.
     * @return the averaged rating or -1 if smth. has gone wrong.
     */
    @Deprecated
    public double getPackageRatingsAverage(String dataSetId) {
        return CKANGatewayUtil.getDataSetRatingsAverage(dataSetId);
    }

    /** {@inheritDoc} */
    @Override
    public double calculateAverageRating(JSONArray arr) {
        return CKANGatewayUtil.calculateAverageRating(arr);
    }

    /**
     * Submit comment for a dataset.
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
    @Deprecated
    public boolean postPackageComment(String dataSetId, String userId,
            Date date, String comment) {
        return CKANGatewayUtil.postDatasetComment(dataSetId, userId, date,
                comment);
    }

    /** {@inheritDoc} */
    @Override
    public boolean postDataSetComment(String dataSetId, String userId,
            Date date, String comment) {
        return CKANGatewayUtil.postDatasetComment(dataSetId, userId, date,
                comment);
    }

    /**
     * Get the number of existing comments for a dataset.
     * 
     * @param dataSetId
     *            the dataset ID.
     * 
     * @return the number of existing comments or -1 in case of an error.
     */
    @Deprecated
    public int getPackageCommentsCount(String dataSetId) {
        return CKANGatewayUtil.getDataSetCommentsCount(dataSetId);
    }

    /** {@inheritDoc} */
    @Override
    public int getDataSetCommentsCount(String dataSetId) {
        return CKANGatewayUtil.getDataSetCommentsCount(dataSetId);
    }

    /**
     * Get the comments for a dataset.
     * 
     * @param dataSetId
     *            the dataset ID.
     * @return a JSON array with the comments or null in case of an error.
     */
    @Deprecated
    public JSONArray getPackageComments(String dataSetId) {
        return CKANGatewayUtil.getDataSetComments(dataSetId);
    }

    /** {@inheritDoc} */
    @Override
    public JSONArray getDataSetComments(String dataSetId) {
        return CKANGatewayUtil.getDataSetComments(dataSetId);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings({ "rawtypes" })
    public Vector getUpdatedDataSetsDetails(Vector<String> revisionsDetails) {
        return CKANGatewayUtil.getUpdatedDataSetsDetails(revisionsDetails);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("rawtypes")
    public Vector<Map> getUpdatedCategoriesDetails(
            Vector<String> revisionsDetails) {
        return CKANGatewayUtil.getUpdatedCategoriesDetails(revisionsDetails);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings({ "rawtypes" })
    public HashMap getLatestDatasets(int numberOfDatasets) {
        return CKANGatewayUtil.getLatestDatasets(numberOfDatasets);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings({ "rawtypes" })
    public HashMap getMostPopularDatasets(int numberOfDatasets) {
        return CKANGatewayUtil.getMostPopularDatasets(numberOfDatasets);
    }

    /** {@inheritDoc} */
    @Override
    public Long getAllDataSetsCount() {
        return CKANGatewayUtil.getAllDataSetsCount();
        // http://172.19.4.170/api/search/dataset
    }

    /** {@inheritDoc} */
    @Override
    public Long getGroupDataSetsCount(String groupName) {
        // http://172.19.4.170/api/rest/group/business
        // http://172.19.4.170/api/search/dataset?groups=tourism
        return CKANGatewayUtil.getGroupDataSetsCount(groupName);
    }

    /** {@inheritDoc} */
    @Override
    public Long getRatingCount(String dataSetId) {
        return CKANGatewayUtil.getRatingCount(dataSetId);
    }

}
