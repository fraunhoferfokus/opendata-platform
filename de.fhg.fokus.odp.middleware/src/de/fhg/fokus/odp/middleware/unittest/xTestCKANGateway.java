package de.fhg.fokus.odp.middleware.unittest;

// imports
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import de.fhg.fokus.odp.middleware.ckan.CKANDataset;
import de.fhg.fokus.odp.middleware.ckan.CKANGateway;

/**
 * These are the test cases for the CKAN gateway.
 * 
 * @author Nikolay Tcholtchev, Fraunhofer Fokus
 * 
 * @author Thomas Scheel, Fraunhofer Fokus
 */
public class xTestCKANGateway extends TestCase {

    /** The local logger instance. */
    private static final Logger log = Logger.getLogger(xTestCKANGateway.class
            .getName());

    /** The CKAN gateway object to use for the test cases. */
    private CKANGateway ckanGW = null;

    /** The Name of the CKAN dataset used in this testsuite */
    public static final String DATASET_NAME = "tho_test_243";

    /** Setup the test case. */
    @Override
    @Before
    protected void setUp() {

        // prepare the CKAN version
        String filename = "testConfig.properties";
        InputStream is = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(filename);
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to load property file: " + filename);
        }

        ckanGW = new CKANGateway(prop.getProperty("ckan.uri"),
                prop.getProperty("ckan.apiKey"));
    }

    /** Clean up after the test case. */
    // @Override
    // @After
    // protected void tearDown() {
    // }

    /**
     * Test the viewMetaDataSets method.
     */
    @Test
    public void testViewMetaDataSets() {
        // get the meta data sets
        HashMap<String, HashMap> filteredDataResult = ckanGW.viewDataSets();
        log.fine("########## VIEW DATASET #########");
        log.fine(filteredDataResult.toString());
        assertEquals(true, filteredDataResult.keySet().size() != 0);
    }

    /**
     * Test the createMetaDataSet method.
     */
    @Test
    public void testCreateMetaDataSet() {

        HashMap<String, String> hm_MetaData = new HashMap<String, String>();
        hm_MetaData.put("name", "\"" + DATASET_NAME + "\"");
        hm_MetaData.put("maintainer_email", "\"pppp@hahaha.de\"");
        hm_MetaData.put("tags", "[\"test\", \"unittest\"]");
        hm_MetaData.put("groups", "[\"rec\", \"housing\"]");

        hm_MetaData.put("author", "\"the maintainer\"");
        hm_MetaData.put("author_email", "\"me@mymail.de\"");

        hm_MetaData.put("notes", "\"des\ncription\"");

        hm_MetaData.put("maintainer", "\"maintainer\"");
        hm_MetaData.put("version", "\"0.5\"");
        hm_MetaData.put("license_id", "\"cc-by\"");
        hm_MetaData.put("url", "\"www.test.de\"");
        hm_MetaData.put("ratings_average", "4");
        hm_MetaData.put("ratings_count", "1");

        hm_MetaData
                .put("extras",
                        "{\"date_released\":\"2012-03-02\","
                                + "\"date_updated\":\"2012-03-02\","
                                + "\"geographical_coverage\":\"state\","
                                + "\"geographical_granularity\":\"month\","
                                + "\"temporal_coverage-from\":\"2012-03-02\","
                                + "\"temporal_coverage-to\":\"2013-03-02\","
                                + "\"temporal_granularity\":\"month\","
                                + "\"comments\":[{\"date\":\"Tue Jan 31 12:01:53 GMT 2012\",\"userId\":\"10875\",\"comment\":\"comment 123\"}],"
                                + "\"ratings\":[{\"date\":\"Tue Jan 31 12:01:53 GMT 2012\",\"userId\":\"10875\",\"ratingValue\":\"4\"}]"
                                + "}");
        hm_MetaData
                .put("resources",
                        "[{\"description\":\"Short Abstracts und Provenance information as RDF-N-Quads | de\","
                                + "\"format\":\"HTML\","
                                + "\"language\":\"DE\","
                                + "\"url\":\"http://download.geofabrik.de/osm/europe/germany/berlin.osm.pbf\""
                                + "}]");

        HashMap<String, HashMap> res = ckanGW.createDataSet(hm_MetaData);

        log.fine("########## CREATE #########");
        log.fine("res: " + res);

        assertEquals(true, res != null);
        if (res == null) {
            log.fine("### create dataset failed: maybe the dataset already exists. "
                    + "Please use the other assert statement or change de ID in the name");
        }
        /*
         * use this assert if no dataset is created becausse it alredy exists on
         * CKAN
         */
        // assertEquals(false, res != null);
    }

    /**
     * Test the getDataSetUpdates method.
     */
    @Test
    public void testGetTagsData() {
        JSONArray arr = ckanGW.getTagsData();
        log.fine("########## GET TAGS #########");
        log.fine("arr: " + arr);
        boolean test = false, unittest = false;
        boolean soccer = false, basketball = false;

        for (int i = 0; i < arr.size(); i++) {

            String str = (String) arr.get(i);

            if (str.equals("test")) {
                test = true;
            }
            if (str.equals("unittest")) {
                unittest = true;
            }
        }

        assertEquals(true, test);
        assertEquals(true, unittest);

        assertEquals(false, soccer);
        assertEquals(false, basketball);
    }

    /**
     * Test the updateMetaDataSet method.
     */
    @Test
    public void testUpdateMetaDataSet() {

        HashMap<String, String> hm_MetaData = new HashMap<String, String>();
        hm_MetaData.put("notes", "\"testdescription\"");
        hm_MetaData.put("maintainer_email", "\"info@web.de\"");
        hm_MetaData
                .put("extras",
                        "{\"date_released\":\"2012-03-02\","
                                + "\"date_updated\":\"2012-03-02\","
                                + "\"geographical_coverage\":\"state\","
                                + "\"geographical_granularity\":\"regionx\","
                                + "\"temporal_coverage-from\":\"2012-03-02\","
                                + "\"temporal_coverage-to\":\"2013-03-02\","
                                + "\"temporal_granularity\":\"month\","
                                + "\"comments\":[{\"date\":\"Tue Jan 31 12:01:53 GMT 2012\",\"userId\":\"10875\",\"comment\":\"comment 123\"}],"
                                + "\"ratings\":[{\"date\":\"Tue Jan 31 12:01:53 GMT 2012\",\"userId\":\"10875\",\"ratingValue\":\"4\"}]"
                                + "}");

        hm_MetaData.put("groups", "[\"rec\", \"housing\", \"edu\"]");
        HashMap<String, HashMap> res = ckanGW.updateDataSet(DATASET_NAME,
                hm_MetaData);
        assertEquals(true, res != null);
        log.fine("########## UPDATE #########");
        log.fine("res: " + res);
    }

    /**
     * Test the deleteMetaDataSet method - version for single meta data set.
     */
    @Test
    public void testUndeleteMetaDataSet1() {

        Object obj1 = ckanGW.getDataSetDetails(DATASET_NAME);
        log.fine("obj1 in test:" + ((JSONObject) obj1).toJSONString());
        assertTrue(obj1 != null);

        boolean res1 = ckanGW.deleteDataSet(DATASET_NAME);
        assertEquals(res1, true);

        res1 = ckanGW.undeleteDataSet(DATASET_NAME);
        assertEquals(res1, true);

        Object obj = ckanGW.getDataSetDetails(DATASET_NAME);
        log.fine("obj after active:" + ((JSONObject) obj).toJSONString());

        String state = (String) ((Map) obj).get("state");
        assertEquals(true, state.equals("active"));

    }

    /**
     * Test the GetPackageSearchResults method.
     */
    @Test
    public void testGetDataSetSearchResults() {
        // get the package details
        Object obj = ckanGW
                .getDataSetSearchResults("q=tho_test_243&all_fields=1");
        // testdescription
        // q=tho_test_243&all_fields=1
        // q=regionx

        JSONObject o = (JSONObject) obj;

        log.fine("############ SEARCH DATASET ############");
        log.fine("obj: " + o.toString());
        log.fine("obj: " + o.toJSONString().contains(DATASET_NAME));
        log.fine("obj: " + o.get("count"));

        assertEquals((Long) o.get("count") >= 1, true);
    }

    /**
     * Test the GetPackageSearchResultsWithAuthorization method.
     */
    @Test
    @Deprecated
    public void testGetDataSetSearchResultsWithAuthorization() {
        // get the package details
        Object obj = ckanGW
                .getDataSetSearchResultsAsOwner("q=tho_test_243&all_fields=1");
        // q=tho_test_243&all_fields=1
        // q=regionx

        JSONObject o = (JSONObject) obj;

        assertEquals((Long) o.get("count") >= 1, true);
    }

    /**
     * Test the getDataSetUpdates method.
     */
    @Test
    public void testGetGroupsData() {
        HashMap<String, Object> res = ckanGW.getGroupsData();

        Object bildung = res.get("rec");
        assertEquals(true, bildung != null);

    }

    /**
     * Test the getDataSetUpdates method.
     */
    /*
     * public void testGetPackageSearchResults() { // get the package details
     * Object obj = ckanGW.getPackageSearchResults("q=einbuergerungen");
     * 
     * JSONObject o = (JSONObject) obj;
     * assertEquals(o.toJSONString().indexOf("einbuergerungen") != -1, true);
     * 
     * }
     */

    /**
     * Test the getDataSetUpdates method.
     */
    @Test
    public void testGetDataSetDetails() {
        // get the package details
        log.fine("########### PACKAGE DETAIL ###########");
        Object obj = ckanGW.getDataSetDetails(DATASET_NAME);

        log.fine("Obj: " + obj);
        assertEquals(true, ((JSONObject) obj).toJSONString()
                .contains("regionx"));

        // obj = ckanGW.getMetaDataSetDetails("einbuergerungen_berlin_2010");
        // Map hm = (Map) obj;
        // System.out.println("Obj2: " + obj);
        // System.out.println("Maintainer_Email: " +
        // hm.get("maintainer_email"));
        // assertEquals("info@statistik-berlin-brandenburg.de",
        // hm.get("maintainer_email"));

    }

    /**
     * Test the getGroupDataSetsCount method.
     */
    @Test
    public void testGetGroupDataSetsCount() {
        // get the package details
        Long count = ckanGW.getGroupDataSetsCount("housing");

        log.fine("############ GET GROUP DATASET COUNT ############");
        log.fine("Count: " + count);

        assertEquals(count >= 1, true);
    }

    /**
     * Test the getAllDataSetsCount method.
     */
    @Test
    public void testGetAllDataSetsCount() {
        // get the package details
        Long count = ckanGW.getAllDataSetsCount();

        log.fine("############ GET ALL DATASET COUNT ############");
        log.fine("Count: " + count);

        assertEquals(count >= 1, true);
    }

    /**
     * Test the CKANDataset Constructor and getMap method.
     */
    @Test
    @SuppressWarnings({ "unchecked" })
    public void testCKANDatasetGetMap() {
        CKANDataset set = new CKANDataset();
        log.fine("########### DATASET CLASS TEST ###########");
        // System.out.println("state: " + state);

        set.setAuthor("123");
        set.setAuthorEmail("123@abc.de");
        // set.setComments(comments);
        set.setDateReleased("1.1.1");
        set.setDateUpdated("2.1.1");
        set.setGeographicalCoverage("von bis");
        set.setGeographicalGranularity("fein");

        set.setGroups(new ArrayList(Arrays.asList("group1", "group2", "group3")));

        set.setLicense("CC-BY");
        set.setMaintainer("abc");
        set.setMaintainerEmail("abc@123.de");
        set.setName("123");
        set.setNotes("dsfjlksjdfsd");

        set.setRatings(new ArrayList(Arrays.asList(new ArrayList(Arrays.asList(
                "rating1", "user", "timestamp")))));
        set.setResources((new ArrayList(Arrays.asList(
                new ArrayList(Arrays.asList("res1", "user", "timestamp")),
                new ArrayList(Arrays.asList("res1", "user", "timestamp"))))));

        set.setState("active");

        set.setTags(new ArrayList(Arrays.asList("ich", "du", "er")));

        set.setTemporalCoverageFrom("jetz");
        set.setTemporalCoverageTo("gleich");
        set.setTemporalGranularity(2);
        set.setTitel("123");
        set.setUrl("www.123abc.de");
        set.setVersion("1.2");

        CKANDataset set2 = new CKANDataset(set.getMap());

        assertEquals(true, set2.getAuthor().equals("123"));
        ArrayList<String> tmp = (ArrayList<String>) set2.getResources().get(0);
        assertEquals(true, tmp.contains("res1"));
        log.fine("set2: " + set2.getTags().size());
    }

    // neu

    /**
     * Test the getRevisions and getUpdatedSets method.
     */
    @Test
    public void testGetRevisionsAndGetUpdatesSets() {
        log.fine("########### GET REVISIONS AND UPDATES ###########");
        String revisionsStr = ckanGW.getRevisions("Europe/Berlin", 200000000);
        log.fine(revisionsStr);
        Vector<String> revisionsDetails = ckanGW
                .getUpdatedDatasets(revisionsStr);
        @SuppressWarnings("unchecked")
        Vector<Map> dataSetDetails = ckanGW
                .getUpdatedDataSetsDetails(revisionsDetails);
        Vector<Map> categoriesDetails = ckanGW
                .getUpdatedCategoriesDetails(revisionsDetails);
        for (int i = 0; i < categoriesDetails.size(); i++) {
            log.fine(categoriesDetails.get(i).toString());
        }
        assertTrue(revisionsDetails.size() >= 1);
        assertTrue(dataSetDetails.size() >= 1);
        assertTrue(categoriesDetails.size() >= 1);

    }

    /**
     * The test for the hasPackageRatingPermission.
     */
    @Test
    public void testHasDataSetRatingPermission() {
        // TODO check this test
        log.fine("########### HAS RATING PERMISSION ###########");
        boolean res = ckanGW.hasDataSetRatingPermission(DATASET_NAME, "niko");
        assertEquals(true, res);

        res = ckanGW.hasDataSetRatingPermission(DATASET_NAME, "thomas");
        assertEquals(true, res);
    }

    /**
     * Test the getMostPopularTags method.
     * 
     * NOTE: this Test may fail on an productive system, where other tags were
     * more popular than the "test" tag. "Bundestag" tag is relevant only in OC
     * dev platform.
     */
    @Test
    public void testGetMostPopularTags() {
        log.fine("########### MOST POPULAR TAGS ###########");
        // get the most popular tags
        JSONArray arr = ckanGW.getMostPopularTags(10);
        // assertEquals(true, arr.toJSONString().contains("bundestag"));
        log.fine(arr.toString());
        assertEquals(10, arr.size());
    }

    /**
     * Test the postPackageRating method.
     */
    @Test
    public void testPostDataSetRating() {
        log.fine("########### POST RATING ###########");
        Date d = new Date();
        boolean res = ckanGW.postDataSetRating(DATASET_NAME, "niko2", d, 1);
        assertEquals(true, res);
    }

    /**
     * Test the getPackageRatingsAverage method.
     */
    @Test
    public void testGetDataSetRatingsAverage() {
        log.fine("########### GET AVERAGE RATING ###########");
        Double avRating = ckanGW.getDataSetRatingsAverage(DATASET_NAME);
        assertEquals(true, (avRating <= 5) && (avRating >= 0));
        log.fine(avRating.toString());
    }

    /**
     * Test the postPackageComment method.
     */
    @Test
    public void testPostDataSetComment() {
        log.fine("########### POST COMMENT ###########");
        Date d = new Date();
        boolean res = ckanGW.postDataSetComment(DATASET_NAME, "niko5", d,
                "phillip will mehr datasets");
        assertEquals(true, res);
    }

    /**
     * Test the getPackageComments method.
     */
    @Test
    public void testGetDataSetComments() {
        log.fine("########### GET COMMENTS ###########");
        JSONArray arr = ckanGW.getDataSetComments(DATASET_NAME);

        boolean res = false;
        for (int i = 0; i < arr.size(); i++) {
            Map m = (Map) (arr.get(i));
            log.fine(m.get("userId") + " " + m.get("comment") + " "
                    + m.get("date"));
            String userId = (String) m.get("userId");
            String comment = (String) m.get("comment");
            String date = (String) m.get("date");

            if (userId.equals("niko2") && comment.equals("Bla bla545")) {
                res = true;
            }
        }

        assertEquals(true, res);
    }

    /**
     * Test the postPackageComment method.
     */
    @Test
    public void testGetDataSetCommentsCount() {
        log.fine("########### GET COMMENT COUNT ###########");
        Integer res = ckanGW.getDataSetCommentsCount(DATASET_NAME);
        log.fine(res.toString());
        assertEquals(true, res > 0);
    }

    /**
     * Test the getLatestDatasets method.
     */
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGetLatestDatasets() {
        log.fine("########### GET LATEST DATASETS ###########");
        // HashMap hm = CKANGatewayUtil.getLatestDatasetsOld(1);

        // System.out.println("test1 " + hm.toString());

        HashMap hm = ckanGW.getLatestDatasets(1);

        log.fine(hm.toString());

        Set<String> s = hm.keySet();
        assertEquals(2, s.size());

        // assertEquals(false, hm.toString().indexOf("bildung") != -1);
        // assertEquals(true, hm.toString().indexOf("arun_test") != -1);

    }

    /**
     * Test the getMostPopularDatasets method.
     * 
     * NOTE: This test may fail if there are not enough rated datasets in the
     * registry.
     */
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testGetMostPopularDatasets() {
        log.fine("########### GET POPULAR DATASETS ###########");
        HashMap hm = ckanGW.getMostPopularDatasets(10);

        log.fine(hm.toString());

        Set<String> s = hm.keySet();
        assertEquals(10, s.size());

        // assertEquals(true, hm.toString().indexOf("rating=1.0") != -1);
        // assertEquals(false, hm.toString().indexOf("rating=-11.0") != -1);
        // assertEquals(true, hm.toString().indexOf("rating=-1.0") != -1);
    }

    /**
     * Test get rating count.
     */
    @Test
    public void testGetRatingCount() {
        log.fine("########### GET RATING COUNT ###########");
        String dataSetId = DATASET_NAME;
        // dataSetId = "berlinwahl_2011_vorlaeufig";
        Long count = ckanGW.getRatingCount(dataSetId);
        log.fine("count: " + count);
        assertEquals(count.longValue(), 1L);
    }

    /**
     * Test the Test the deleteDataSet method - version for multiple metadata
     * sets.
     */
    @Test
    public void testDeleteDataSets2() {
        log.fine("########### DELETE DATASETS ###########");
        HashMap<String, String> hm_DataSet = new HashMap<String, String>();
        hm_DataSet.put("name", "\"test_d1\"");
        hm_DataSet.put("maintainer_email", "\"tcholtchev1@test.de\"");

        ckanGW.createDataSet(hm_DataSet);

        HashMap<String, String> hm_DataSet1 = new HashMap<String, String>();
        hm_DataSet1.put("name", "\"test_d2\"");
        hm_DataSet1.put("maintainer_email", "\"tcholtchev2@test.de\"");

        ckanGW.createDataSet(hm_DataSet1);

        Vector<String> t = new Vector<String>();
        t.add("test_d1");
        t.add("test_d2");

        boolean res1 = ckanGW.deleteDataSets(t);
        assertEquals(res1, true);

        Object obj = ckanGW.getDataSetDetails("test_d1");
        String state = (String) ((Map) obj).get("state");
        assertEquals(true, "deleted".equals(state));

        Object obj1 = ckanGW.getDataSetDetails("test_d2");
        String state1 = (String) ((Map) obj1).get("state");
        assertEquals(true, "deleted".equals(state1));
    }

    /**
     * Test the deleteDataSet method - version for single meta data set.
     */
    @Test
    public void testDeleteDataSet1() {
        boolean res1 = ckanGW.deleteDataSet(DATASET_NAME);

        assertEquals(res1, true);

        Object obj = ckanGW.getDataSetDetails(DATASET_NAME);

        String state = (String) ((Map) obj).get("state");
        log.fine("########### DELETE DATASET ###########");
        log.fine("state: " + state);
        assertEquals(true, "deleted".equals(state));

        /*
         * use this to delete a second Ddtaset.
         */
        res1 = ckanGW.deleteDataSet(DATASET_NAME);

        assertEquals(res1, true);
        obj = ckanGW.getDataSetDetails(DATASET_NAME);
        log.fine("state: " + state);
        state = (String) ((Map) obj).get("state");
    }

    /**
     * test pruging of deleted datasets
     */
    @Test
    public void testPurgeAllDataSets() {
        boolean res = ckanGW.purgeAllDataSets();
        log.fine("########### PURGE DATASETS ###########");
        assertEquals(res, true);
        // TODO add useful test for purge. Search for deleted dataset don't
        // work.
    }
}
