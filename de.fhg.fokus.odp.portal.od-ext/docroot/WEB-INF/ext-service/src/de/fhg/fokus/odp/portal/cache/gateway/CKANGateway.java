package de.fhg.fokus.odp.portal.cache.gateway;

// imports
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.simple.JSONArray;

import de.fhg.fokus.odp.middleware.caching.Caching;

/**
 * This class is the gateway towards the CKAN instance.
 * 
 * @author Nikolay Tcholtchev, nikolay.tcholtchev@fraunhofer.fokus.de
 */
public class CKANGateway implements
		de.fhg.fokus.odp.middleware.ckan.ICKANGateway {

	/** The ckan gateway. */
	private de.fhg.fokus.odp.middleware.ckan.CKANGateway ckanGateway = null;

	/** The constants holds the cashing interval. */
	private final int CASHING_INTERVAL = 60;

	private final String GET_MOST_POPULAR_DATASETS_KEY = "GET_MOST_POPULAR_DATASETS";

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

		ckanGateway = new de.fhg.fokus.odp.middleware.ckan.CKANGateway(url,
				authenticationKey);

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
	public boolean hasPackageRatingPermission(String packageId, String userId) {

		return ckanGateway.hasDataSetRatingPermission(packageId, userId);
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
	@Override
	public String getRevisions(String serverTimeZone, long offset) {

		return ckanGateway.getRevisions(serverTimeZone, offset);
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
	@Override
	public Vector<String> getUpdatedDatasets(String revisionStr) {

		return ckanGateway.getUpdatedDatasets(revisionStr);
	}

	/**
	 * The function returns all meta data sets filtered according to the
	 * maintainer email. The function returns a HashMap with key=metadataSetId
	 * and value=HashMap(with Key = MetaDataEntryKey and value =
	 * MetaDataEntryValue).
	 * 
	 * @param maintainerEmail
	 *            the maintainer email
	 * @param fileFormat
	 *            the file format to filter for.
	 * @return the the hash map as described in the general comments of the
	 *         method or null if anything has gone wrong.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public HashMap<String, HashMap> viewDataSets(String maintainerEmail,
			String fileFormat) {

		return ckanGateway.viewDataSets(maintainerEmail, fileFormat);
	}

	/**
	 * Returns a list of the most popular tags.
	 * 
	 * @param numberOfTags
	 *            the number of popular tags to return.
	 * @return the most popular tags or null if an error occurred.
	 */
	@Override
	public JSONArray getMostPopularTags(int numberOfTags) {

		return ckanGateway.getMostPopularTags(numberOfTags);
	}

	/**
	 * View all MetaData entries. The function returns a HashMap with
	 * key=metadataSetId and value=HashMap(with Key = MetaDataEntryKey and value
	 * = MetaDataEntryValue).
	 * 
	 * @return the hash map as described in the general comments of the method
	 *         or null if anything has gone wrong.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public HashMap<String, HashMap> viewDataSets() {

		return ckanGateway.viewDataSets();
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
	@Override
	@SuppressWarnings("rawtypes")
	public HashMap<String, HashMap> createDataSet(
			HashMap<String, String> hm_MetaData) {

		return ckanGateway.createDataSet(hm_MetaData);
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
	@Override
	@SuppressWarnings("rawtypes")
	public HashMap<String, HashMap> updateDataSet(String metaDataSetID,
			HashMap<String, String> hm_MetaData) {

		return ckanGateway.updateDataSet(metaDataSetID, hm_MetaData);
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
	@Override
	public boolean deleteDataSet(String metaDataSetID) {

		return ckanGateway.deleteDataSet(metaDataSetID);
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
	@Override
	public boolean undeleteDataSet(String metaDataSetID) {

		return ckanGateway.undeleteDataSet(metaDataSetID);
	}

	/**
	 * Delete a MetaDataEntry. In order to select which MetaDataSet is to be
	 * deleted, the id of the MetaDataSet in CKAN would be provided. A boolean
	 * needs to be returned as ack to indicate whether the MetaDataSet deletion
	 * has been successful or not.
	 * 
	 * @param metaDataSetIDs
	 *            the meta data set i ds
	 * @return a boolean indicating the success or failure of the operation.
	 */
	@Override
	public boolean deleteDataSets(Vector<String> metaDataSetIDs) {

		return ckanGateway.deleteDataSets(metaDataSetIDs);
	}

	/**
	 * Get groups list including details.
	 * 
	 * @return a hash map of JSON objects containing the data for each group.
	 */
	@Override
	public HashMap<String, Object> getGroupsData() {

		return ckanGateway.getGroupsData();
	}

	/**
	 * Get tags list.
	 * 
	 * @return a JSON array containing the list of all tags.
	 */
	@Override
	public JSONArray getTagsData() {

		return ckanGateway.getTagsData();
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
	@Override
	public Object getDataSetSearchResults(String params) {

		return ckanGateway.getDataSetSearchResults(params);
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
	@Override
	@Deprecated
	public Object getDataSetSearchResultsAsOwner(String params) {

		return ckanGateway.getDataSetSearchResultsAsOwner(params);
	}

	/**
	 * Get package details.
	 * 
	 * @param packageId
	 *            the id of the package.
	 * @return a json object containing the package details or "null" in case of
	 *         an error.
	 */
	@Override
	public Object getDataSetDetails(String packageId) {

		return ckanGateway.getDataSetDetails(packageId);
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
	@Override
	public boolean postDataSetRating(String packageId, String userId,
			Date date, int rating) {

		return ckanGateway.postDataSetRating(packageId, userId, date, rating);
	}

	/**
	 * Get the average of a package’s ratings [rounded to 0.5 steps]
	 * 
	 * @param packageId
	 *            the ID of the package.
	 * @return the averaged rating or -1 if smth. has gone wrong.
	 */
	@Override
	public double getDataSetRatingsAverage(String packageId) {

		return ckanGateway.getDataSetRatingsAverage(packageId);
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
	@Override
	public double calculateAverageRating(JSONArray arr) {

		return ckanGateway.calculateAverageRating(arr);
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
	@Override
	public boolean postDataSetComment(String packageId, String userId,
			Date date, String comment) {

		return ckanGateway.postDataSetComment(packageId, userId, date, comment);
	}

	/**
	 * Get the number of existing comments for a package.
	 * 
	 * @param packageId
	 *            the package ID.
	 * 
	 * @return the number of existing comments or -1 in case of an error.
	 */
	@Override
	public int getDataSetCommentsCount(String packageId) {

		return ckanGateway.getDataSetCommentsCount(packageId);
	}

	/**
	 * Get the comments for a package.
	 * 
	 * @param packageId
	 *            the package ID.
	 * @return a JSON array with the comments or null in case of an error.
	 */
	@Override
	public JSONArray getDataSetComments(String packageId) {

		return ckanGateway.getDataSetComments(packageId);
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
	@Override
	@SuppressWarnings({ "rawtypes" })
	public Vector getUpdatedDataSetsDetails(Vector<String> revisionsDetails) {

		return ckanGateway.getUpdatedDataSetsDetails(revisionsDetails);
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
	@Override
	@SuppressWarnings("rawtypes")
	public Vector<Map> getUpdatedCategoriesDetails(
			Vector<String> revisionsDetails) {

		return ckanGateway.getUpdatedCategoriesDetails(revisionsDetails);
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
	@Override
	@SuppressWarnings({ "rawtypes" })
	public HashMap getLatestDatasets(int numberOfDatasets) {

		return ckanGateway.getLatestDatasets(numberOfDatasets);

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
	@Override
	@SuppressWarnings({ "rawtypes" })
	public HashMap getMostPopularDatasets(int numberOfDatasets) {

		HashMap result;

		String cachingKey = GET_MOST_POPULAR_DATASETS_KEY + "_"
				+ numberOfDatasets;
		// get a cashing instance and check whether the data is up-to-date
		Caching cInstance = Caching.getInstance();
		HashMap cResult = (HashMap) cInstance.areValuesStillUpToDate(
				cachingKey, new Date(), CASHING_INTERVAL);
		if (cResult != null) {
			// if the data is up-to-date --> return it
			result = cResult;
		} else {
			result = ckanGateway.getMostPopularDatasets(numberOfDatasets);
			cInstance.insertObject(cachingKey, new Date(), result);
		}

		return result;

	}

	@Override
	public Long getAllDataSetsCount() {
		return ckanGateway.getAllDataSetsCount();
	}

	@Override
	public Long getGroupDataSetsCount(String groupName) {
		return ckanGateway.getGroupDataSetsCount(groupName);
	}

	@Override
	public Long getRatingCount(String dataSetId) {
		return ckanGateway.getRatingCount(dataSetId);
	}

	@Override
	public boolean hasDataSetRatingPermission(String dataSetId, String userId) {
		return ckanGateway.hasDataSetRatingPermission(dataSetId, userId);
	}

	@Override
	public boolean purgeAllDataSets() {
		return ckanGateway.purgeAllDataSets();
	}

}
