package de.fhg.fokus.odp.middleware.unittest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fhg.fokus.odp.middleware.virtuoso.VirtuosoGraphAccess;

/**
 * These are the test cases for the Virtuoso Graph Access.
 * 
 * @author Thomas Scheel, Fraunhofer Fokus
 */
public class xTestVirtuosoGraphAccess extends TestCase {

    /** The local logger instance. */
    private static final Logger log = Logger
            .getLogger(xTestVirtuosoGraphAccess.class.getName());

    /** The CKAN gateway object to use for the test cases. */
    private VirtuosoGraphAccess virtGA = null;

    /** Setup the test case. */
    @Override
    @Before
    protected void setUp() {

        // load property file
        String filename = "testConfig.properties";
        InputStream is = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(filename);
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to load property file: " + filename);
        }

        // prepare the virtuoso instance
        virtGA = new VirtuosoGraphAccess(prop.getProperty("graphAccess.uri"),
                prop.getProperty("graphAccess.user"),
                prop.getProperty("graphAccess.psw"),
                prop.getProperty("graphAccess.graphUri"),
                prop.getProperty("graphAccess.baseUri"),
                prop.getProperty("graphAccess.schemaUri"));
    }

    /** Clean up after the test case. */
    @After
    @Override
    protected void tearDown() {
    }

    /**
     * Test the createMetaDataSet method.
     */
    @Test
    public void testCreateDataSet() {

        HashMap<String, String> hm_MetaData = new HashMap<String, String>();
        hm_MetaData.put("name", "\"tho_test_242\"");
        hm_MetaData.put("title", "\"Tho Test 242\"");
        hm_MetaData.put("maintainer_email", "\"pppp@hahaha.de\"");
        hm_MetaData.put("tags", "[\"test\", \"unittest\"]");
        hm_MetaData.put("groups", "[\"rec\", \"housing\"]");

        hm_MetaData.put("author", "\"the maintainer\"");
        hm_MetaData.put("author_email", "\"me@mymail.de\"");

        hm_MetaData.put("notes", "\"des\ncription\"");

        hm_MetaData.put("maintainer", "\"maintainer\"");
        hm_MetaData.put("version", "\"0.5\"");
        hm_MetaData.put("license_id", "\"cc-by\"");
        hm_MetaData.put("URL", "\"www.test.de\"");

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

        HashMap<String, HashMap> res = virtGA.createDataSet(hm_MetaData);

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
}