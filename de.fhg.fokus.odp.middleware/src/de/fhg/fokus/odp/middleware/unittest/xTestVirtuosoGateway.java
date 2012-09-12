package de.fhg.fokus.odp.middleware.unittest;

// imports
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.fhg.fokus.odp.middleware.virtuoso.VirtuosoGateway;

/**
 * These are the test cases for the Virtuoso gateway.
 * 
 * @author Nikolay Tcholtchev, nikolay.tcholtchev@fokus.fraunhofer.de
 * 
 */
public class xTestVirtuosoGateway extends TestCase {

    /** The local logger instance. */
    private static final Logger log = Logger
            .getLogger(xTestVirtuosoGateway.class.getName());

    /** The Virtuoso gateway object to use for the test cases. */
    private VirtuosoGateway virtuosoGW = null;

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
        virtuosoGW = new VirtuosoGateway(prop.getProperty("virtuoso.uri"),
                prop.getProperty("virtuoso.user"),
                prop.getProperty("virtuoso.psw"));
    }

    /** Clean up after the test case. */
    // @After
    // @Override
    // protected void tearDown() {
    // }

    /**
     * Test the uploadRDFResource and updateRDFResource method.
     * 
     * @throws FileNotFoundException
     */
    @Test
    public void testUploadRDFResource_UpdateRDFResource_DeleteRDFResource() {
        try {
            FileInputStream in = new FileInputStream(new File(
                    "test_data/PankowFoodOntologyTest1_3_2_rdf-xml.owl"));
            URL url = virtuosoGW.uploadRDFResource(in,
                    "test_data/PankowFoodOntologyTest1_3_2_rdf-xml.owl");

            assertEquals(true, url != null);
            assertEquals(
                    true,
                    url.toString()
                            .equals("http://172.19.4.188:8890/DAV/home/liferay/rdf_sink/PankowFoodOntologyTest1_3_2_rdf-xml.owl"));

            FileInputStream in1 = new FileInputStream(new File(
                    "test_data_1/PankowFoodOntologyTest1_3_2_rdf-xml.owl"));

            URL url1 = virtuosoGW.updateRDFResource(url, in1);

            assertEquals(true, url1 != null);
            assertEquals(
                    true,
                    url1.toString()
                            .equals("http://172.19.4.188:8890/DAV/home/liferay/rdf_sink/PankowFoodOntologyTest1_3_2_rdf-xml.owl"));

            boolean res = virtuosoGW
                    .deleteRDFResource("PankowFoodOntologyTest1_3_2_rdf-xml.owl");
            assertEquals(true, res);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
