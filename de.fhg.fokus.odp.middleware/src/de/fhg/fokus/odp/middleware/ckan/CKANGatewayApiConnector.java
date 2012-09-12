/**
 * 
 */
package de.fhg.fokus.odp.middleware.ckan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is the gateway towards the CKAN API. It is responsible for sending
 * REST calls to the CKAN API.
 * 
 * 
 * @author Nikolay Tcholtchev, Fraunhofer Fokus
 * 
 * @author Thomas Scheel, Fraunhofer Fokus
 */
public class CKANGatewayApiConnector {
    /** The url to the CKAN gateway instance. */
    private String url = null;

    /** The local authentication key. */
    private String authenticationKey = null;

    /** A singleton instance. */
    private static CKANGatewayApiConnector instance = null;

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
        instance = new CKANGatewayApiConnector(CKANurl, authKey);
    }

    /**
     * Function to deliver the singleton instance.
     * 
     * @return the (hopefully) pre-configured CKAN Gateway.
     */
    public static CKANGatewayApiConnector getInstance() {
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
    private CKANGatewayApiConnector(String url, String authenticationKey) {

        this.url = url;
        if (!this.url.endsWith("/")) {
            this.url += "/";
        }

        this.authenticationKey = authenticationKey;
    }

    /**
     * The function executes a REST API call on the belonging CKAN instance.
     * Thereby the API key is used.
     * 
     * @param restCall
     *            the api string to use for the call.
     * @param jsonString
     *            a json string to be put in the request part - if null it is
     *            just ignored.
     * 
     * @return the corresponding result.
     * @throws MalformedURLException
     *             the exceptions of the REST call.
     * @throws IOException
     *             the exceptions of the REST call.
     */
    @Deprecated
    protected String restCallWithAuthorization(String restCall,
            String jsonString) throws IOException, MalformedURLException {

        // read from the CKAN API
        URL CKANurlInstance;
        String returnStr = "";

        // open a connection to the CKAN API
        CKANurlInstance = new URL(url + restCall);
        URLConnection CKANconnection = CKANurlInstance.openConnection();
        CKANconnection.setRequestProperty("authorization", authenticationKey);
        if (jsonString != null) {
            CKANconnection.setRequestProperty("Content-Type",
                    "application/json; charset=utf-8");
            CKANconnection.setDoOutput(true);
            CKANconnection.setDoInput(true);

            OutputStreamWriter wr = new OutputStreamWriter(
                    CKANconnection.getOutputStream());
            wr.write(jsonString);
            wr.flush();
        }

        // read the output from the API
        BufferedReader in = new BufferedReader(new InputStreamReader(
                CKANconnection.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            returnStr += inputLine;
        }
        in.close();

        return returnStr;
    }

    /**
     * The function executes a REST API call on the belonging CKAN instance.
     * 
     * @param restCall
     *            the api string to use for the call.
     * 
     * @return the corresponding result.
     * 
     * @throws MalformedURLException
     *             the exceptions of the REST call.
     * 
     * @throws IOException
     *             the exceptions of the REST call.
     */
    protected String restCall(String restCall) throws IOException,
            MalformedURLException {

        // read from the CKAN API
        URL CKANurlInstance;
        String returnStr = "";

        // open a connection to the CKAN API
        CKANurlInstance = new URL(url + restCall);
        URLConnection CKANconnection = CKANurlInstance.openConnection();

        // read the output from the API
        BufferedReader in = new BufferedReader(new InputStreamReader(
                CKANconnection.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            returnStr += inputLine;
        }
        in.close();

        return returnStr;
    }

}
