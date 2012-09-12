/**
 * 
 */
package de.fhg.fokus.odp.middleware.ckan;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.simple.JSONArray;

/**
 * @author Thomas Scheel, Fraunhofer Fokus
 * 
 */
public interface ICKANGateway extends IBasicGateway {
    /** {@inheritDoc} */
    @Override
    Object getDataSetDetails(String dataSetId);

    /** {@inheritDoc} */
    @Override
    HashMap<String, HashMap> viewDataSets();

    /** {@inheritDoc} */
    @Override
    HashMap<String, HashMap> createDataSet(HashMap<String, String> hm_DataSet);

    /** {@inheritDoc} */
    @Override
    HashMap<String, HashMap> updateDataSet(String dataSetID,
            HashMap<String, String> hm_DataSet);

    /** {@inheritDoc} */
    @Override
    boolean deleteDataSet(String dataSetID);

    /** {@inheritDoc} */
    @Override
    boolean deleteDataSets(Vector<String> dataSetIDs);

    /** {@inheritDoc} */
    @Override
    boolean undeleteDataSet(String dataSetID);

    /** {@inheritDoc} */
    @Override
    HashMap<String, Object> getGroupsData();

    /** {@inheritDoc} */
    @Override
    JSONArray getTagsData();

    /**
     * Get dataset search results.
     * 
     * @param params
     *            string of parameters for the dataset search.
     * 
     * @return JSON object containing the search results or "null" if something
     *         has gone wrong.
     */
    Object getDataSetSearchResults(String params);

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
    Object getDataSetSearchResultsAsOwner(String params);

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
    boolean hasDataSetRatingPermission(String dataSetId, String userId);

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
    String getRevisions(String serverTimeZone, long offset);

    /**
     * The function receives the revisions as a JSON string and obtains the
     * details for the revisions in a list of strings.
     * 
     * @param revisionStr
     *            the string containing the revisions.
     * 
     * @return a vector with JSON strings describing the updated data sets.
     */
    Vector<String> getUpdatedDatasets(String revisionStr);

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
    HashMap<String, HashMap> viewDataSets(String maintainerEmail,
            String fileFormat);

    /**
     * Returns a list of the most popular tags.
     * 
     * @param numberOfTags
     *            the number of popular tags to return.
     * @return the most popular tags or null if an error occurred.
     */
    JSONArray getMostPopularTags(int numberOfTags);

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
    boolean postDataSetRating(String dataSetId, String userId, Date date,
            int rating);

    /**
     * Get the average of a dataset’s ratings [rounded to 0.5 steps]
     * 
     * @param dataSetId
     *            the ID of the dataset.
     * @return the averaged rating or -1 if smth. has gone wrong.
     */
    double getDataSetRatingsAverage(String dataSetId);

    /**
     * The function gets a JSON array with the ratings and returns an average
     * rating value, which is rounded to 0.5.
     * 
     * @param arr
     *            the array with the ratings.
     * 
     * @return the averaged rating value rounded to 0.5-
     */
    double calculateAverageRating(JSONArray arr);

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
    boolean postDataSetComment(String dataSetId, String userId, Date date,
            String comment);

    /**
     * Get the number of existing comments for a dataset.
     * 
     * @param dataSetId
     *            the dataset ID.
     * 
     * @return the number of existing comments or -1 in case of an error.
     */
    int getDataSetCommentsCount(String dataSetId);

    /**
     * Get the comments for a dataset.
     * 
     * @param dataSetId
     *            the dataset ID.
     * @return a JSON array with the comments or null in case of an error.
     */
    JSONArray getDataSetComments(String dataSetId);

    /**
     * The function receives a vector with details for a set of revisions and
     * returns the details for the datasets affected by these revisions.
     * 
     * @param revisionsDetails
     *            a vector of strings containing the JSON details for the
     *            revisions.
     * @return a vector of maps with the details for each affected dataset.
     */
    Vector getUpdatedDataSetsDetails(Vector<String> revisionsDetails);

    /**
     * The function receives a vector with details for a set of revisions and
     * returns the details for the datasets affected by these revisions.
     * 
     * @param revisionsDetails
     *            a vector of strings containing the JSON details for the
     *            revisions.
     * @return a vector of maps with the details for each affected dataset.
     */
    Vector<Map> getUpdatedCategoriesDetails(Vector<String> revisionsDetails);

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
    HashMap getLatestDatasets(int numberOfDatasets);

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
    HashMap getMostPopularDatasets(int numberOfDatasets);

    /**
     * Returns the count of all active data sets in the registry.
     * 
     * @return null in case of an error, or a long value with the data set
     *         count.
     */
    Long getAllDataSetsCount();

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
    Long getGroupDataSetsCount(String groupName);

    /**
     * @see de.fhg.fokus.odp.middleware.ckan.ICKANGateway#getRatingCount(java.lang.String)
     *      Returns the count of all ratings associated with this dataSetId.
     * 
     * @param dataSetId
     *            the dataset ID
     * 
     * @return null in case of an error, or a long value with the the rating
     *         count.
     * 
     */
    Long getRatingCount(String dataSetId);

    /**
     * Finally remove all deleted datasets from the database
     * 
     * @return success of purge
     */
    boolean purgeAllDataSets();

}
