package de.fhg.fokus.odp.portal.cache.gateway;

// imports 
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

/**
 * 
 * This class provides an API to communicate with the corresponding Virtuoso
 * Instance.
 * 
 * @author Nikolay Tcholtchev, nikolay.tcholtchev@fraunhofer.fokus.de
 * 
 */
public class VirtuosoGateway {

	/** A singleton instance. */
	private de.fhg.fokus.odp.middleware.virtuoso.VirtuosoGateway instance = null;

	/**
	 * Constructor for the Virtuoso Gateway.
	 * 
	 * @param url
	 *            the url of the Virtuoso Instance.
	 * @param username
	 *            the username
	 * @param passwd
	 *            the passwd
	 */
	public VirtuosoGateway(String url, String username, String passwd) {
		this.instance = new de.fhg.fokus.odp.middleware.virtuoso.VirtuosoGateway(
				url, username, passwd);
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

		return instance.uploadRDFResource(rdfResourceStream, filename);
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

		return instance.updateRDFResource(rdfResourceID, rdfResourceStream);
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

		return instance.deleteRDFResource(rdfResourceID);
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

		return instance.deleteRDFResource(rdfResourceIDs);
	}
}
