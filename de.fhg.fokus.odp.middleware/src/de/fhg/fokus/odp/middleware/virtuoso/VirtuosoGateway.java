package de.fhg.fokus.odp.middleware.virtuoso;

// imports 
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * This class provides an API to communicate with the corresponding Virtuoso
 * Instance.
 * 
 * @author Nikolay Tcholtchev, Fraunhofer FOKUS
 * @author Thomas Scheel, Fraunhofer FOKUS
 */
public class VirtuosoGateway {

    /** Local field for storing the url of the Virtuoso Instance. */
    private String url = null;

    /** The user name. */
    private String username = null;

    /** The password. */
    private String password = null;

    /** A singleton instance. */
    private static VirtuosoGateway instance = null;

    /**
     * Function to prepare a singleton instance.
     * 
     * @param VirtuosoUrl
     *            a string with the url of the Virtuoso instance.
     * @param uname
     *            the username for accessing Virtuoso.
     * @param passwd
     *            the password for accessing Virtuoso.
     * 
     */
    public static void prepareInstance(String VirtuosoUrl, String uname,
            String passwd) {
        instance = new VirtuosoGateway(VirtuosoUrl, uname, passwd);
    }

    /**
     * Function to deliver the singleton instance.
     * 
     * @return the (hopefully) pre-configured Virtuoso Gateway.
     */
    public static VirtuosoGateway getInstance() {
        return instance;
    }

    /**
     * Constructor for the Virtuoso Gateway.
     * 
     * @param url
     *            the url of the Virtuoso Instance.
     * @param username
     *            the username for virtuoso access.
     * 
     * @param passwd
     *            the password for virtuoso access.
     */
    public VirtuosoGateway(String url, String username, String passwd) {
        this.url = url;
        this.username = username;
        this.password = passwd;
    }

    /**
     * Upload a new RDF resource. An authentication key corresponding to the
     * user who wants to upload a new RDF resource is provided. An OutputStream
     * of the rdf resource is provided. A String RDF Resource ID is returned on
     * successful upload else null.
     * 
     * @param rdfResourceStream
     *            the RDF resource.
     * @param filename
     *            the name of the file.
     * @return null in case of problems, RDF Resource ID in case of success.
     */
    public URL uploadRDFResource(InputStream rdfResourceStream, String filename) {

        // check the input
        if (rdfResourceStream == null || filename == null) {
            return null;
        }

        // this is the variable to return
        URL toreturn = null;

        // prepare the URI string for the request
        String uriStr = "DAV/home/" + username + "/rdf_sink/"
                + new File(filename).getName();

        try {
            // create an url
            toreturn = new URL(this.url + uriStr);
            HttpURLConnection httpCon = (HttpURLConnection) toreturn
                    .openConnection();

            // configure the connection
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");

            // prepare the authorization
            String credentials = username + ":" + password;
            byte[] encodedCrBytes = Base64.encodeBase64(credentials.getBytes());
            String encodedCrStr = new String(encodedCrBytes);
            httpCon.setRequestProperty("Authorization", "Basic " + encodedCrStr);

            // write to the stream
            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            int token = -1;
            while ((token = rdfResourceStream.read()) != -1) {
                out.write(token);
            }

            // close the stream
            out.close();

            // read the output from the API
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    httpCon.getInputStream()));

            String inputLine, resStr = "";
            while ((inputLine = in.readLine()) != null) {
                resStr += inputLine;
            }
            in.close();

            // check the output
            if (!resStr.contains("Created")) {
                toreturn = null;
            }
        }
        // catch exceptions
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return toreturn;
    }

    /**
     * Update an existing RDF resource. An authentication key corresponding to
     * the user who wants to update an existing RDF resource. The String ID of
     * the RDF Resource that is updated is also provided. An OutputStream of the
     * rdf resource is provided. A boolean needs to be returned as ack to
     * indicate whether the update was successful or not.
     * 
     * @param rdfResourceID
     *            the rdfResourceID in question.
     * @param rdfResourceStream
     *            the RDF resource.
     * @return null in case of problems, URL to RDF Resource ID in case of
     *         success.
     */
    public URL updateRDFResource(URL rdfResourceID,
            InputStream rdfResourceStream) {
        // check the input parameters
        if (rdfResourceID == null || rdfResourceStream == null) {
            return null;
        }

        // get the file name from the resource ID
        String filename = rdfResourceID.getFile().toString();
        int index = filename.lastIndexOf("/");
        if (index == -1) {
            return null;
        }
        filename = filename.substring(index);

        return uploadRDFResource(rdfResourceStream, filename);
    }

    /**
     * Delete an existing RDF resource. The String ID of the RDF Resource is
     * provided. A boolean needs to be returned as ack to indicate whether the
     * deletion was successful or not.
     * 
     * @param rdfResourceID
     *            the ID of the rdf ressource to delete.
     * 
     * @return true in case of success, false in case of an error.
     */
    public boolean deleteRDFResource(String rdfResourceID) {

        // check the input
        if (rdfResourceID == null) {
            return false;
        }

        // this is the variable to return
        URL toreturn = null;

        // prepare the URI string for the request
        String uriStr = "DAV/home/" + username + "/rdf_sink/"
                + new File(rdfResourceID).getName();

        try {
            // create an url
            toreturn = new URL(this.url + uriStr);
            HttpURLConnection httpCon = (HttpURLConnection) toreturn
                    .openConnection();

            // configure the connection
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");

            // prepare the authorization
            String credentials = username + ":" + password;
            byte[] encodedCrBytes = Base64.encodeBase64(credentials.getBytes());
            String encodedCrStr = new String(encodedCrBytes);
            httpCon.setRequestProperty("Authorization", "Basic " + encodedCrStr);

            // write to the stream
            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <rdf/>\n";
            out.write(str);

            // close the stream
            out.close();

            // read the output from the API
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    httpCon.getInputStream()));

            String inputLine, resStr = "";
            while ((inputLine = in.readLine()) != null) {
                resStr += inputLine;
            }
            in.close();

            // check the output
            if (!resStr.contains("Created")) {
                toreturn = null;
            }
        }
        // catch exceptions
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Delete an existing RDF resources. The String ID of the RDF Resource is
     * provided. A boolean needs to be returned as ack to indicate whether the
     * deletion was successful or not.
     * 
     * @param rdfResourceIDs
     *            the IDs of the rdf ressources to delete.
     * 
     * @return true in case of success, false in case of an error.
     */
    public boolean deleteRDFResource(Vector<String> rdfResourceIDs) {

        // check the input parameter
        if (rdfResourceIDs == null) {
            return false;
        }

        // invoke the function to delete for each entry
        boolean toreturn = true;
        for (int i = 0; i < rdfResourceIDs.size(); i++) {
            boolean res = deleteRDFResource(rdfResourceIDs.get(i));
            if (!res) {
                toreturn = false;
            }
        }

        return toreturn;
    }
}
