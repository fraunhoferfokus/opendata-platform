package org.opencities.berlin.opendata.util;


import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONObject;

import org.codehaus.jettison.json.JSONArray;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class JSONUtil {

	final static String BASE_URI = ""; 	
	final static String API_KEY = ""; 

	public static JSONObject getJsonObject(String restURI) {
		Client client = Client.create();

		WebResource webResource = client.resource(BASE_URI + restURI);

		JSONObject jsonResponse = webResource//.header("Content-Type", "application/json,charset=utf8")
		.header(HttpHeaders.AUTHORIZATION, API_KEY)		
		.accept(MediaType.APPLICATION_JSON).get(JSONObject.class);

		return jsonResponse;
	}
	
	public static JSONArray getJsonArray(String restURI) {
		Client client = Client.create();

		WebResource webResource = client.resource(BASE_URI + restURI);

		JSONArray jsonResponse = webResource//.header("Content-Type", "application/json,charset=utf8")
		.header(HttpHeaders.AUTHORIZATION, API_KEY)		
		.accept(MediaType.APPLICATION_JSON).get(JSONArray.class);

		return jsonResponse;
	}
	
	public static String getStringObject(String restURI) {
		Client client = Client.create();

		WebResource webResource = client.resource(BASE_URI + restURI);

		String stringResponse = webResource
		.header(HttpHeaders.AUTHORIZATION, API_KEY)		
		.accept(MediaType.APPLICATION_XML).get(String.class);

		return stringResponse;
	}
	
	public static ClientResponse postJsonObject(String restURI, JSONObject jsonData) {
		Client client = Client.create();
		
		WebResource webResource = client.resource(BASE_URI + restURI);
		
		ClientResponse response = webResource.header(HttpHeaders.AUTHORIZATION, API_KEY).post(ClientResponse.class, jsonData.toString());
			
		
		return response;
		
	}
}
