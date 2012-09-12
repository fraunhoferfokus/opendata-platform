package de.fhg.fokus.odp.middleware.virtuoso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import virtuoso.jena.driver.VirtGraph;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fhg.fokus.odp.middleware.ckan.IBasicGateway;

/**
 * This class is the gateway towards the Virtuoso API. It provides basic CRUD
 * operations on a graph. It connects to virtuoso using the virtuoso Jena
 * library.
 * 
 * @author Thomas Scheel, Fraunhofer Fokus
 */
public class VirtuosoGraphAccess implements IBasicGateway {

    /** Local field for storing the url of the Virtuoso Instance. */
    private String url = null;

    /** The user name. */
    private String username = null;

    /** The user password. */
    private String password = null;

    /** The user graph. */
    private String graph = null;

    /** The base URI. */
    private String baseUri = null; // "http://www.opendata.engage.eu/dataset#";

    /** The schema URI. */
    private String schemaUri = null; // "http://www.opendata.engage.eu/dataset/schema#";

    /** A singleton instance. */
    private static VirtuosoGraphAccess instance = null;

    /** The local logger instance. */
    private static final Logger log = Logger
            .getLogger(VirtuosoGraphAccess.class.getName());

    /**
     * Function to prepare a singleton instance.
     * 
     * @param url
     *            the url of the Virtuoso Instance.
     * @param username
     *            the username for accessing Virtuoso.
     * @param passwd
     *            the password for accessing Virtuoso.
     * @param graph
     *            the graph to access.
     * 
     * @param baseUri
     *            the URI prefix for elements in this graph
     * 
     * @param schemaUri
     *            the URI prefix for elements defined in your own schema
     */
    public static void prepareInstance(String url, String username,
            String passwd, String graph, String baseUri, String schemaUri) {
        instance = new VirtuosoGraphAccess(url, username, passwd, graph,
                baseUri, schemaUri);
    }

    /**
     * Function to deliver the singleton instance.
     * 
     * @return the (hopefully) pre-configured Virtuoso Gateway.
     */
    public static VirtuosoGraphAccess getInstance() {
        return instance;
    }

    /**
     * Constructor for the Virtuoso Gateway.
     * 
     * @param url
     *            the url of the Virtuoso Instance.
     * @param username
     *            the username for accessing Virtuoso.
     * @param passwd
     *            the password for accessing Virtuoso.
     * @param graph
     *            the graph to access.
     * 
     * @param baseUri
     *            the URI prefix for elements in this graph
     * 
     * @param schemaUri
     *            the URI prefix for elements defined in your own schema
     */
    public VirtuosoGraphAccess(String url, String username, String passwd,
            String graph, String baseUri, String schemaUri) {
        this.url = url;
        this.username = username;
        this.password = passwd;
        this.graph = graph;
        this.baseUri = baseUri;
        this.schemaUri = schemaUri;
    }

    /**
     * Gets the data set details.
     * 
     * @param dataSetId
     *            the data set id
     * @return the data set details
     * @see de.fhg.fokus.odp.middleware.ckan.IBasicGateway#getDataSetDetails(java.lang.String)
     */
    @Override
    public Object getDataSetDetails(String dataSetId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns a HashMap with all datasets in the graph.
     * 
     * @return the hash map
     * @see de.fhg.fokus.odp.middleware.ckan.IBasicGateway#viewDataSets()
     */
    @Override
    public HashMap<String, HashMap> viewDataSets() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Creates the data set.
     * 
     * @param hm_dataSet
     *            the hm_ data set
     * @return the hash map
     * @see de.fhg.fokus.odp.middleware.ckan.IBasicGateway#createDataSet(java.util.HashMap)
     */
    @SuppressWarnings("unchecked")
    @Override
    public HashMap<String, HashMap> createDataSet(
            HashMap<String, String> hm_dataSet) {

        // check instantiation
        if (url == null || username == null || password == null
                || graph == null || baseUri == null) {
            return null;
        }

        // check the submitted parameters
        if (hm_dataSet == null) {
            return null;
        }

        // get the set of keys
        Set<String> set = hm_dataSet.keySet();
        Iterator<String> it = set.iterator();

        try {

            VirtGraph graph = new VirtGraph(this.graph, url, username, password);

            // get instance name
            String name = hm_dataSet.get("name");
            String encodedName = URLEncoder.encode(name, "UTF-8");

            // create Nodes
            Node s = Node.createURI(baseUri + encodedName);
            Node p = null;
            Node o = null;

            // create dataset node
            p = Node.createURI(RDFPrefixes.RDF + "type");
            o = Node.createURI(schemaUri + "dataset");
            log.fine("add instance triple: " + new Triple(s, p, o).toString());
            graph.add(new Triple(s, p, o));

            // iterate over the set of keys
            while (it.hasNext()) {
                String key = it.next();

                // reset the variables to null, so they could be reused in new
                // iteration
                p = null;
                o = null;

                // create additional label
                if ("title".equals(key)) {
                    Node p2 = Node.createURI(RDFPrefixes.RDFS + "label");
                    Node o2 = Node.createLiteral(hm_dataSet.get(key));

                    // add label triple
                    log.fine("add triple: " + new Triple(s, p2, o2).toString());
                    graph.add(new Triple(s, p2, o2));

                    p = Node.createURI(schemaUri
                            + URLEncoder.encode(key, "UTF-8"));
                    o = Node.createLiteral(
                            hm_dataSet.get(key),
                            null,
                            TypeMapper.getInstance().getSafeTypeByName(
                                    RDFPrefixes.XSD + "string"));
                }

                // add triple to store
                if (o != null && p != null) {
                    log.fine("add triple: " + new Triple(s, p, o).toString());
                    graph.add(new Triple(s, p, o));
                }

                // all complex properties
                // if ("group".equals(key)) {
                // // TODO insert magic here
                // }

                // if ("resources".equals(key)) {
                // // TODO insert magic here
                // }

                if ("extras".equals(key)) {
                    // get the groups string
                    JSONObject groupArr = null;
                    // JSONArray groupArr = null;
                    String groupsStr = hm_dataSet.get("extras");
                    if (groupsStr != null) {
                        groupArr = (JSONObject) (JSONValue.parse(groupsStr));
                    }
                    Set<String> extraSet = groupArr.keySet();
                    Iterator<String> it2 = extraSet.iterator();

                    while (it2.hasNext()) {
                        String key2 = it2.next();

                        p = Node.createURI(schemaUri
                                + URLEncoder.encode(key2, "UTF-8"));

                        o = Node.createLiteral(
                                groupArr.get(key2).toString(),
                                null,
                                TypeMapper.getInstance().getSafeTypeByName(
                                        RDFPrefixes.XSD + "string"));

                        // add triple
                        log.fine("add triple: "
                                + new Triple(s, p, o).toString());
                        graph.add(new Triple(s, p, o));
                    }
                }

            }

            // RDFDatatype dt = TypeMapper.getInstance().getSafeTypeByName(
            // "http://www.w3.org/2001/XMLSchema#float");
            // Node o2 = Node.createLiteral(
            // new Float(rating.getLatitude()).toString(), null, dt);

            // SimpleDateFormat sdf = new SimpleDateFormat(); //
            // 2002-10-10T12:00:00-05:00
            // sdf.applyPattern( "yyyy'-'MM'-'dd'T'HH:mm:ss" );
            // uhrzeit als erstes wegen parser abrecken

            // Node p8 = Node.createURI(schemaUri + "time");
            // Node o8 = Node.createLiteral(
            // rating.getTime(),
            // null,
            // TypeMapper.getInstance().getSafeTypeByName(
            // "http://www.w3.org/2001/XMLSchema#dateTime"));

            // if (!graph.contains(s2_1, p2_1, o2_1)) {
            // System.out.println("Adding Aspect: "
            // + new Triple(s2_1, p2_1, o2_1).toString());
            // graph.add(new Triple(s2_1, p2_1, o2_1));
            // graph.add(new Triple(s2_1, p2_2, o2_2));
            // }

            // graph.clear();
            log.fine("graph.isEmpty() = " + graph.isEmpty());

            // System.out.println("graph.isEmpty() = " + graph.isEmpty());
            // System.out.println("graph.getCount() = " + graph.getCount());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Update data set.
     * 
     * @param dataSetID
     *            the data set id
     * @param hm_dataSet
     *            the hm_ data set
     * @return the hash map
     * @see de.fhg.fokus.odp.middleware.ckan.IBasicGateway#updateDataSet(java.lang.String,
     *      java.util.HashMap)
     */
    @Override
    public HashMap<String, HashMap> updateDataSet(String dataSetID,
            HashMap<String, String> hm_dataSet) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Delete data set.
     * 
     * @param dataSetID
     *            the data set id
     * @return true, if successful
     * @see de.fhg.fokus.odp.middleware.ckan.IBasicGateway#deleteDataSet(java.lang.String)
     */
    @Override
    public boolean deleteDataSet(String dataSetID) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Delete data sets.
     * 
     * @param dataSetIDs
     *            the data set i ds
     * @return true, if successful
     * @see de.fhg.fokus.odp.middleware.ckan.IBasicGateway#deleteDataSets(java.util.Vector)
     */
    @Override
    public boolean deleteDataSets(Vector<String> dataSetIDs) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Undelete data set.
     * 
     * @param dataSetID
     *            the data set id
     * @return true, if successful
     * @see de.fhg.fokus.odp.middleware.ckan.IBasicGateway#undeleteDataSet(java.lang.String)
     */
    @Override
    public boolean undeleteDataSet(String dataSetID) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Gets the groups data.
     * 
     * @return the groups data
     * @see de.fhg.fokus.odp.middleware.ckan.IBasicGateway#getGroupsData()
     */
    @Override
    public HashMap<String, Object> getGroupsData() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Gets the tags data.
     * 
     * @return the tags data
     * @see de.fhg.fokus.odp.middleware.ckan.IBasicGateway#getTagsData()
     */
    @Override
    public JSONArray getTagsData() {
        // TODO Auto-generated method stub
        return null;
    }

}
