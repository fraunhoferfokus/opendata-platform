package de.fhg.fokus.odp.portal.uploaddata.test.service;

public class WorkerTest{
}
/*import java.util.HashMap;

import junit.framework.TestCase;

import org.junit.Test;
import org.opencities.berlin.uploaddata.service.Worker;

public class WorkerTest extends TestCase{
	Worker worker = null;
	protected void setUp(){
		String ckan = "hallo";
		String authKey = "key";
		String ulFolder = "ulFolder";
		
		worker = new Worker(ckan,authKey,ulFolder);
	}

	@Test
	public void testWorker() {

		assertEquals("wrong init of ckan","hallo", worker.getCkan());
		assertEquals("wrong init of key","key", worker.getKey());
		assertEquals("wrong init of ulFolder","ulFolder", worker.getULFolder());
	}
	
	@Test
	public void testBuildString(){
		String[] array = new String[]{"a","b","c"};
		String result = worker.buildString(array);
		
		assertEquals("wrong buildString","[\"a\",\"b\",\"c\"]",result);
	}
	
	@Test
	public void testToLower(){
		HashMap< String, String> map = new HashMap<String, String>();
		map.put("name", null);
		map.put("license-id", "Cc-by2");
		map = worker.toLowerCase(map);
		
		assertNull(map.get("name"));
		assertEquals("tolowercase failed", "cc-by2",map.get("license-id"));
	}
	
	@Test
	public void testemptyValues(){
		HashMap< String, String> map = new HashMap<String, String>();
		map.put("tags", null);
		map.put("groups", "[\"edu\"]");
		map = worker.checkEmptyValues(map);
		
		assertNotNull(map.get("tags"));
		assertEquals("checkEmpty fail", "[]", map.get("tags"));
		
		assertNotNull(map.get("groups"));
		assertEquals("checkEmpty fail", "[\"edu\"]", map.get("groups"));
		
	}

}
*/