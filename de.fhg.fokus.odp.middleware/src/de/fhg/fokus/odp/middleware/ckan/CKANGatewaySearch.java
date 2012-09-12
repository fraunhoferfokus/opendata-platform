/**
 * 
 */
package de.fhg.fokus.odp.middleware.ckan;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.json.simple.JSONValue;

/**
 * @author Thomas Scheel, Fraunhofer Fokus
 * 
 */
public class CKANGatewaySearch {

    /** The url to the CKAN gateway instance. */
    private static String url = null;

    /** The local authentication key. */
    private static String authenticationKey = null;

    /** The relative path to the CKAN package API */
    private static final String API_URI = "api/rest/package";

    /** The relative path to the CKAN search API */
    private static final String SEARCH_API_URI = "api/search/dataset?";

    /** The local logger instance. */
    private static final Logger log = Logger.getLogger(CKANGatewaySearch.class
            .getName());

    /** A singleton instance. */
    private static CKANGatewaySearch instance = null;

    /** A singleton instance. */
    private static CKANGatewayApiConnector connectorInstance = null;

    /**
     * Function to prepare a singleton instance.
     * 
     * @param CKANurl
     *            a string with the url of the CKAN instance.
     * @param authKey
     *            the authentication key for CKAN communication.
     * 
     */
    public static void prepareInstance(String CKANurl, String authKey) {
        instance = new CKANGatewaySearch(CKANurl, authKey);
    }

    /**
     * Function to deliver the singleton instance.
     * 
     * @return the (hopefully) pre-configured CKAN Gateway.
     */
    public static CKANGatewaySearch getInstance() {
        return instance;
    }

    /**
     * Constructor.
     * 
     * @param url
     *            a string with the url of the CKAN instance.
     * @param authenticationKey
     *            the authentication key for CKAN communication.
     * 
     */
    private CKANGatewaySearch(String url, String authenticationKey) {

        CKANGatewaySearch.url = url;
        if (!CKANGatewaySearch.url.endsWith("/")) {
            CKANGatewaySearch.url += "/";
        }

        CKANGatewaySearch.authenticationKey = authenticationKey;

        if (CKANGatewayApiConnector.getInstance() == null) {
            CKANGatewayApiConnector.prepareInstance(CKANGatewaySearch.url,
                    CKANGatewaySearch.authenticationKey);
        }

        connectorInstance = CKANGatewayApiConnector.getInstance();
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
    public static Object getDataSetSearchResults(String params) {

        // check the submitted parameters
        if (params == null) {
            return null;
        }

        // prepare the REST call
        String RESTcall = SEARCH_API_URI + params;

        // the JSON object to return
        Object toreturn = null;

        try {
            // get the search results
            String searchResultsStr = connectorInstance.restCall(RESTcall);

            // check the API results
            if (searchResultsStr == null) {
                return null;
            }

            // parse the string for the search results
            toreturn = JSONValue.parse(searchResultsStr);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return toreturn;
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
    @Deprecated
    public static Object getDataSetSearchResultsAsOwner(String params) {

        // check the submitted parameters
        if (params == null) {
            return null;
        }

        // prepare the REST call
        String RESTcall = SEARCH_API_URI + params;

        // the JSON object to return
        Object toreturn = null;

        try {
            // get the search results
            String searchResultsStr = connectorInstance
                    .restCallWithAuthorization(RESTcall, null);

            // check the API results
            if (searchResultsStr == null) {
                return null;
            }

            // parse the string for the search results
            toreturn = JSONValue.parse(searchResultsStr);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return toreturn;
    }
}
