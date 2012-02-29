package org.opencities.berlin.opendata.virtuoso.gateway;

// imports
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import junit.framework.TestCase;

/**
 * These are the test cases for the Virtuoso gateway.
 * 
 * @author Nikolay Tcholtchev, nikolay.tcholtchev@fokus.fraunhofer.de
 * 
 */
public class xTestVirtuosoGateway extends TestCase {

	/** The Virtuoso gateway object to use for the test cases. */
	private VirtuosoGateway virtuosoGW = null;

	/** Setup the test case. */
	protected void setUp() {

		// prepare the CKAN version
		virtuosoGW = new VirtuosoGateway("http://172.19.4.188:8890/",
				"liferay", "liferay");
	}

	/** Clean up after the test case. */
	protected void tearDown() {
	}

	/**
	 * Test the uploadRDFResource and updateRDFResource method.
	 * 
	 * @throws FileNotFoundException
	 */
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
			
			boolean res = virtuosoGW.deleteRDFResource("PankowFoodOntologyTest1_3_2_rdf-xml.owl");
			assertEquals(true, res);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
