package org.opencities.berlin.opendata.ckan.gateway;

// imports
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
 * These are the test cases for the CKAN gateway.
 * 
 * @author Nikolay Tcholtchev, nikolay.tcholtchev@fokus.fraunhofer.de
 * 
 */
public class xTestCKANGateway extends TestCase {

	/** The CKAN gateway object to use for the test cases. */
	private CKANGateway ckanGW = null;

	/** Setup the test case. */
	protected void setUp() {

		// prepare the CKAN version
		ckanGW = new CKANGateway("http://localhost",
				"32714abd-3283-4c34-9200-25ff5aac8ed2");
	}

	/** Clean up after the test case. */
	protected void tearDown() {
	}

	/**
	 * Test the getRevisions and getUpdatedSets method.
	 */
	public void testGetRevisionsAndGetUpdatesSets() {
		String revisionsStr = ckanGW.getRevisions("Europe/Berlin", 100000000);
		Vector<String> revisionsDetails = ckanGW
				.getUpdatedDatasets(revisionsStr);
		Vector<Map> dataSetDetails = ckanGW
				.getUpdatedDataSetsDetails(revisionsDetails);
		Vector<Map> categoriesDetails = ckanGW
				.getUpdatedCategoriesDetails(revisionsDetails);
//		for (int i=0; i<categoriesDetails.size(); i++) {
//			System.out.println (categoriesDetails.get(i).toString());
//		}

		// TODO: add some asserts here

	}

	/**
	 * The test for the hasPackageRatingPermission.
	 */
	public void testHasPackageRatingPermission() {
		boolean res = ckanGW.hasPackageRatingPermission("luftgute", "niko");
		assertEquals(false, res);

		res = ckanGW.hasPackageRatingPermission("luftgute", "ben");
		assertEquals(true, res);
	}

	/**
	 * Test the getMostPopularTags method.
	 */
	public void testGetMostPopularTags() {

		// get the most popular tags
		JSONArray arr = ckanGW.getMostPopularTags(10);

		assertEquals(true, arr.toJSONString().contains("pollutants"));
		// assertEquals(true, arr.toJSONString().contains("air_pollution"));
	}

	/**
	 * Test the viewMetaDataSets method.
	 */
	public void testViewMetaDataSets() {

//		// get the meta data sets
//		HashMap<String, HashMap> viewDataResult = ckanGW.viewMetaDataSets();
//
//		// get all the keys
//		Set<String> keys = viewDataResult.keySet();
//
//		// move over all the keys
//		Iterator<String> iter = keys.iterator();
//		while (iter.hasNext()) {
//			String key = iter.next();
//			HashMap hm = viewDataResult.get(key);
//
//			if (key.equals("abschlusspruefungen_hochschulen_berlin_2009")) {
//				assertEquals("info@statistik-berlin-brandenburg.de",
//						hm.get("maintainer_email"));
//
//				// JSONArray resources = (JSONArray) hm.get("resources");
//				// HashMap res = (HashMap) resources.get(0);
//				//
//				// assertEquals(res.get("description"), "XLS | de");
//
//			}
//
//			if (key.equals("luftgute")) {
//				assertEquals("tcholtchev@yahoo.com", hm.get("maintainer_email"));
//			}
//
//			if (key.equals("mz_erwerbsstatus_2006_2010")) {
//				assertEquals("info@statistik-berlin-brandenburg.de",
//						hm.get("maintainer_email"));
//			}
//
//			if (key.equals("umsatz_und_beschaeftigung_im_handel_kraftfahrzeuggewerbe_und_gastgewerbe_mai_2011")) {
//				assertEquals("info@statistik-berlin-brandenburg.de",
//						hm.get("maintainer_email"));
//			}
//
//			if (key.equals("ausfuhr_einfuhr_berlin_maerz_2011")) {
//				assertEquals("info@statistik-berlin-brandenburg.de",
//						hm.get("maintainer_email"));
//			}
//
//			if (key.equals("einbuergerungen_berlin_2010")) {
//				assertEquals("info@statistik-berlin-brandenburg.de",
//						hm.get("maintainer_email"));
//			}
//
//		}

		// get the meta data sets
		HashMap<String, HashMap> filteredDataResult = ckanGW.viewMetaDataSets(
				"test@liferay.com", "RDF");
		System.out.println (filteredDataResult.toString());
		assertEquals(true, filteredDataResult.keySet().size() != 0);
	}

	/**
	 * Test the createMetaDataSet method.
	 */
	public void testCreateMetaDataSet() {

		HashMap<String, String> hm_MetaData = new HashMap<String, String>();
		hm_MetaData.put("name", "\"niko_test_204\"");
		hm_MetaData.put("maintainer_email", "\"pppp@hahaha.de\"");
		hm_MetaData.put("tags", "[\"hahahah\", \"hahahah1\"]");
		hm_MetaData.put("groups", "[\"rec\", \"housing\"]");

		HashMap<String, HashMap> res = ckanGW.createMetaDataSet(hm_MetaData);
		
		assertEquals(false, res != null);

	}

	/**
	 * Test the updateMetaDataSet method.
	 */
	public void testUpdateMetaDataSet() {
		HashMap<String, String> hm_MetaData = new HashMap<String, String>();
		hm_MetaData.put("maintainer_email", "\"tcholtchev@yahoo.com\"");
		hm_MetaData.put("groups", "[\"rec\", \"housing\", \"edu\"]");
		HashMap<String, HashMap> res = ckanGW.updateMetaDataSet("arun_test_1111", hm_MetaData);
		
		hm_MetaData.put("maintainer_email", "\"tcholtchev@yahoo.com\"");
		hm_MetaData.put("groups", "[\"rec\", \"housing\"]");
		res = ckanGW.updateMetaDataSet("arun_test_8888", hm_MetaData);
		assertEquals(true, res  != null);
		
		
		Object res1  = ckanGW.getPackageDetails("arun_test_8888");
		JSONArray arr = (JSONArray) ((Map)res1).get("groups");
		
		System.out.println (((JSONObject)res1).toJSONString());
		
		
		hm_MetaData.put("maintainer_email", "\"tcholtchev@yahoo.com\"");
		hm_MetaData.put("groups", "[\"rec\", \"housing\"]");
		res = ckanGW.updateMetaDataSet("niko_test_119", hm_MetaData);
		
	}

	/**
	 * Test the deleteMetaDataSet method - version for single meta data set.
	 */
	public void testDeleteMetaDataSet1() {
		HashMap<String, String> hm_MetaData = new HashMap<String, String>();
		hm_MetaData.put("name", "\"test_d1\"");
		hm_MetaData.put("maintainer_email",
				"\"nikolay.tcholtchev@fokus.fraunhofer.de\"");

		ckanGW.createMetaDataSet(hm_MetaData);
		Object obj1 = ckanGW.getPackageDetails("luftgute");

		boolean res1 = ckanGW.deleteMetaDataSet("luftgute");
		assertEquals(res1, true);

		Object obj = ckanGW.getPackageDetails("luftgute");

		String state = (String) ((Map) obj).get("state");
		assertEquals(true, state.equals("deleted"));

	}

	/**
	 * Test the deleteMetaDataSet method - version for single meta data set.
	 */
	public void testUndeleteMetaDataSet1() {
		Object obj1 = ckanGW.getPackageDetails("luftgute");
		System.out.println ("obj1 in test:"+((JSONObject)obj1).toJSONString());

		boolean res1 = ckanGW.undeleteMetaDataSet("luftgute");
		assertEquals(res1, true);

		Object obj = ckanGW.getPackageDetails("luftgute");
		System.out.println ("obj after active:"+((JSONObject)obj).toJSONString());

		String state = (String) ((Map) obj).get("state");
		assertEquals(true, state.equals("active"));

	}
	/**
	 * Test the Test the deleteMetaDataSet method - version for multiple
	 * metadata sets.
	 */
	public void testDeleteMetaDataSets2() {
		HashMap<String, String> hm_MetaData = new HashMap<String, String>();
		hm_MetaData.put("name", "\"test_d1\"");
		hm_MetaData.put("maintainer_email",
				"\"nikolay.tcholtchev@fokus.fraunhofer.de\"");

		ckanGW.createMetaDataSet(hm_MetaData);

		HashMap<String, String> hm_MetaData1 = new HashMap<String, String>();
		hm_MetaData1.put("name", "\"test_d2\"");
		hm_MetaData1.put("maintainer_email",
				"\"nikolay.tcholtchev@fokus.fraunhofer.de\"");

		ckanGW.createMetaDataSet(hm_MetaData1);

		Vector<String> t = new Vector<String>();
		t.add("test_d1");
		t.add("test_d2");

		boolean res1 = ckanGW.deleteMetaDataSets(t);
		assertEquals(res1, true);

		Object obj = ckanGW.getPackageDetails("test_d1");
		String state = (String) ((Map) obj).get("state");
		assertEquals(true, state.equals("deleted"));

		Object obj1 = ckanGW.getPackageDetails("test_d2");
		String state1 = (String) ((Map) obj).get("state");
		assertEquals(true, state1.equals("deleted"));
	}

	/**
	 * Test the getDataSetUpdates method.
	 */
	public void testGetGroupsData() {
		HashMap<String, Object> res = ckanGW.getGroupsData();

		Object bildung = res.get("law");
		assertEquals(true, bildung != null);

	}

	/**
	 * Test the getDataSetUpdates method.
	 */
	public void testGetTagsData() {
		JSONArray arr = ckanGW.getTagsData();

		boolean schule = false, sensor = false, umwelt = false, haushalt = false, lebensform = false, luft = false;
		boolean soccer = false, basketball = false;

		for (int i = 0; i < arr.size(); i++) {

			String str = (String) arr.get(i);

			if (str.equals("schule")) {
				schule = true;
			}
			if (str.equals("sensor")) {
				sensor = true;
			}
			if (str.equals("umwelt")) {
				umwelt = true;
			}
			if (str.equals("haushalt")) {
				haushalt = true;
			}
			if (str.equals("lebensform")) {
				lebensform = true;
			}
			if (str.equals("luft")) {
				luft = true;
			}
		}

		assertEquals(true, schule);
		assertEquals(true, sensor);
		assertEquals(true, umwelt);
		assertEquals(true, haushalt);
		assertEquals(true, lebensform);
		assertEquals(true, luft);

		assertEquals(false, soccer);
		assertEquals(false, basketball);
	}

	/**
	 * Test the getDataSetUpdates method.
	 */
	public void testGetPackageSearchResults() {
		// get the package details
		Object obj = ckanGW.getPackageSearchResults("q=einbuergerungen");

		JSONObject o = (JSONObject) obj;
		assertEquals(o.toJSONString().indexOf("einbuergerungen") != -1, true);

	}

	/**
	 * Test the getDataSetUpdates method.
	 */
	public void testGetPackageDetails() {
		// get the package details
		
		Object obj = ckanGW.getPackageDetails("arun_test_4021");
	
		System.out.println (obj);
		assertEquals(true, ((JSONObject)obj).toJSONString().contains("tcholtchev"));
	
		obj = ckanGW.getPackageDetails("einbuergerungen_berlin_2010");
		Map hm = (Map) obj;
		
		assertEquals("info@statistik-berlin-brandenburg.de",
				hm.get("maintainer_email"));

	}

	/**
	 * Test the postPackageRating method.
	 */
	public void testPostPackageRating() {
		Date d = new Date();
		boolean res = ckanGW.postPackageRating("luftgute", "niko2", d, 1);
		assertEquals(true, res);
	}

	/**
	 * Test the getPackageRatingsAverage method.
	 */
	public void testGetPackageRatingsAverage() {
		double avRating = ckanGW.getPackageRatingsAverage("osm_berlin");
		assertEquals(true, (avRating <= 5) && (avRating >= 0));
	}

	/**
	 * Test the postPackageComment method.
	 */
	public void testPostPackageComment() {
		Date d = new Date();
		boolean res = ckanGW.postPackageComment("luftgute", "niko2", d,
				"Bla bla545");
		assertEquals(true, res);
	}

	/**
	 * Test the getPackageComments method.
	 */
	public void testGetPackageComments() {
		JSONArray arr = ckanGW.getPackageComments("luftgute");

		boolean res = false;
		for (int i = 0; i < arr.size(); i++) {
			Map m = (Map) (arr.get(i));

			String userId = (String) m.get("userId");
			String comment = (String) m.get("comment");
			String date = (String) m.get("date");

			if (userId.equals("niko") && comment.equals("Bla bla")
					&& date.equals("Fri Oct 07 15:57:56 CEST 2011")) {
				res = true;
			}
		}

		assertEquals(true, res);
	}

	/**
	 * Test the postPackageComment method.
	 */
	public void testGetPackageCommentsCount() {
		Date d = new Date();
		int res = ckanGW.getPackageCommentsCount("luftgute");
		assertEquals(true, res > 0);
	}

	/**
	 * Test the getLatestDatasets method.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetLatestDatasets() {

		HashMap hm = ckanGW.getLatestDatasets(10);
		
		System.out.println (hm.toString());
		
		Set<String> s = (Set<String>) hm.keySet();
		assertEquals(10, s.size());
		

//		assertEquals(false, hm.toString().indexOf("bildung") != -1);
//		assertEquals(true, hm.toString().indexOf("arun_test") != -1);

	}

	/**
	 * Test the getMostPopularDatasets method.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetMostPopularDatasets() {
		HashMap hm = ckanGW.getMostPopularDatasets(10);
		
		System.out.println (hm.toString());

		Set<String> s = (Set<String>) hm.keySet();
		assertEquals(10, s.size());
//		assertEquals(true, hm.toString().indexOf("rating=1.0") != -1);
		assertEquals(false, hm.toString().indexOf("rating=-11.0") != -1);
		assertEquals(true, hm.toString().indexOf("rating=-1.0") != -1);
	}
}
