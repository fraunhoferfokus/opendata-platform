package de.fhg.fokus.odp.middleware.ckan;

import java.util.HashMap;
import java.util.Vector;

import org.json.simple.JSONArray;

/**
 * This class is the gateway towards any metadata store instance. The util class
 * is for non simple request like metrics and special
 * 
 * 
 * @author Nikolay Tcholtchev, Fraunhofer Fokus
 * 
 * @author Thomas Scheel, Fraunhofer Fokus
 */
public interface IBasicGateway {
    /**
     * Get Dataset details.
     * 
     * @param dataSetId
     *            the id of the dataset.
     * @return a json object containing the dataset details or "null" in case of
     *         an error.
     */
    Object getDataSetDetails(String dataSetId);

    /**
     * View all MetaData entries. The function returns a HashMap with
     * key=metadataSetId and value=HashMap(with Key = MetaDataEntryKey and value
     * = MetaDataEntryValue).
     * 
     * @return the hash map as described in the general comments of the method
     *         or null if anything has gone wrong.
     */
    HashMap<String, HashMap> viewDataSets();

    /**
     * Create a new MetaDataSet in CKAN. In the HashMap<String, String>, the key
     * = metadataentry, and value = value corresponding to the meta dataentry.
     * 
     * @param hm_DataSet
     *            hash map as described in the general comments of the method.
     * @return null in case of an error, otherwise a hash map of arrays and maps
     *         reflecting the structure of the submitted meta data set.
     */
    HashMap<String, HashMap> createDataSet(HashMap<String, String> hm_DataSet);

    /**
     * Update a selected MetaDataSet in CKAN. In order to select which
     * MetaDataSet is to be updated, the id of the MetaDataSet in CKAN would be
     * provided.
     * 
     * @param dataSetID
     *            the id of the MetaDataSet in CKAN.
     * @param hm_DataSet
     *            hash map as described in the general comments of the method.
     * @return null in case of an error, otherwise a hash map of arrays and maps
     *         reflecting the structure of the submitted meta data set.
     */
    HashMap<String, HashMap> updateDataSet(String dataSetID,
            HashMap<String, String> hm_DataSet);

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
    boolean deleteDataSet(String dataSetID);

    /**
     * Delete a MetaDataEntry. In order to select which MetaDataSet is to be
     * deleted, the id of the MetaDataSet in CKAN would be provided. A boolean
     * needs to be returned as ack to indicate whether the MetaDataSet deletion
     * has been successful or not.
     * 
     * @param dataSetIDs
     *            the id's of the MetaDataSets in CKAN which will be deleted.
     * @return a boolean indicating the success or failure of the operation.
     */
    boolean deleteDataSets(Vector<String> dataSetIDs);

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
    boolean undeleteDataSet(String dataSetID);

    /**
     * Get groups list including details.
     * 
     * @return a hash map of JSON objects containing the data for each group.
     */
    HashMap<String, Object> getGroupsData();

    /**
     * Get tags list.
     * 
     * @return a JSON array containing the list of all tags.
     */
    JSONArray getTagsData();
}
