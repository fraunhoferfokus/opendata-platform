/**
 * 
 */
package de.fhg.fokus.odp.middleware.ckan;

import java.util.HashMap;
import java.util.List;

/**
 * Data class to represent a dataset
 * 
 * @author Thomas Scheel, Fraunhofer Fokus
 * 
 */
public class CKANDataset {

    // non extra CKAN fields

    /** human readable name */
    private String titel = null;

    /** unique name */
    private String name = null;

    /** Description of dataset */
    private String notes = null;

    /** List of Groups */
    private List<String> groups = null;

    /** List of tags */
    private List<String> tags = null;

    /** author of dataset */
    private String author = null;

    /** authors email */
    private String authorEmail = null;

    /** maintainer of dataset */
    private String maintainer = null;

    /** maintainers email */
    private String maintainerEmail = null;

    /** version of dataset */
    private String version = null;

    /** licence of dataset */
    private String license = null;

    /** link to origin */
    private String url = null;

    /** state of dataset (active or deleted) */
    private String state = null;

    // extra fields
    /** release date of dataset */
    private String dateReleased = null;

    /** date of dataset update */
    private String dateUpdated = null;

    /** geographical coverage of dataset */
    private String geographicalCoverage = null;

    /** geographical granularity of dataset */
    private String geographicalGranularity = null;

    /** temporal coverage start date */
    private String temporalCoverageFrom = null;

    /** temporal coverage end date */
    private String temporalCoverageTo = null;

    /** temporal coverage granularity */
    private Integer temporalGranularity = 0;

    /** List of ratings for dataset */
    private List<List<String>> ratings = null;

    /** List of comments for dataset */
    private List<List<String>> comments = null;

    // resources
    /** List of known resources */
    private List<List<String>> resources = null;

    /** standard empty constructor */
    public CKANDataset() {

    }

    /**
     * constructor to initially fill dataset
     * 
     * @param dataSet
     *            a HashMap with all information about the dataset
     */
    public CKANDataset(HashMap<String, Object> dataSet) {
        this.titel = (String) dataSet.get("title");
        this.name = (String) dataSet.get("name");
        this.notes = (String) dataSet.get("notes");
        this.author = (String) dataSet.get("author");
        this.authorEmail = (String) dataSet.get("authorEmail");
        this.maintainer = (String) dataSet.get("maintainer");
        this.maintainerEmail = (String) dataSet.get("maintainerEmail");
        this.version = (String) dataSet.get("version");
        this.license = (String) dataSet.get("license");
        this.url = (String) dataSet.get("url");
        this.state = (String) dataSet.get("state");
        this.dateReleased = (String) dataSet.get("dateReleased");
        this.version = (String) dataSet.get("version");
        this.url = (String) dataSet.get("url");
        this.state = (String) dataSet.get("state");
        this.dateReleased = (String) dataSet.get("dateReleased");
        this.dateUpdated = (String) dataSet.get("dateUpdated");
        this.geographicalGranularity = (String) dataSet
                .get("geographicalGranularity");
        this.geographicalCoverage = (String) dataSet
                .get("geographicalCoverage");
        this.temporalCoverageFrom = (String) dataSet
                .get("temporalCoverageFrom");
        this.temporalCoverageTo = (String) dataSet.get("temporalCoverageTo");
        this.temporalGranularity = (Integer) dataSet.get("temporalGranularity");

        // lists
        this.groups = (List<String>) dataSet.get("groups");
        this.tags = (List<String>) dataSet.get("tags");
        this.ratings = (List<List<String>>) dataSet.get("ratings");
        this.comments = (List<List<String>>) dataSet.get("comments");
        this.resources = (List<List<String>>) dataSet.get("resources");

    }

    /** @return the titel */
    public String getTitel() {
        return titel;
    }

    /**
     * @param titel
     *            the titel to set
     */
    public void setTitel(String titel) {
        this.titel = titel;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes
     *            the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return the groups
     */
    public List<String> getGroups() {
        return groups;
    }

    /**
     * @param groups
     *            the groups to set
     */
    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    /**
     * @return the tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * @param tags
     *            the tags to set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author
     *            the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the authorEmail
     */
    public String getAuthorEmail() {
        return authorEmail;
    }

    /**
     * @param authorEmail
     *            the authorEmail to set
     */
    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    /**
     * @return the maintainer
     */
    public String getMaintainer() {
        return maintainer;
    }

    /**
     * @param maintainer
     *            the maintainer to set
     */
    public void setMaintainer(String maintainer) {
        this.maintainer = maintainer;
    }

    /**
     * @return the maintainerEmail
     */
    public String getMaintainerEmail() {
        return maintainerEmail;
    }

    /**
     * @param maintainerEmail
     *            the maintainerEmail to set
     */
    public void setMaintainerEmail(String maintainerEmail) {
        this.maintainerEmail = maintainerEmail;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the license
     */
    public String getLicense() {
        return license;
    }

    /**
     * @param license
     *            the license to set
     */
    public void setLicense(String license) {
        this.license = license;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the dateReleased
     */
    public String getDateReleased() {
        return dateReleased;
    }

    /**
     * @param dateReleased
     *            the dateReleased to set
     */
    public void setDateReleased(String dateReleased) {
        this.dateReleased = dateReleased;
    }

    /**
     * @return the dateUpdated
     */
    public String getDateUpdated() {
        return dateUpdated;
    }

    /**
     * @param dateUpdated
     *            the dateUpdated to set
     */
    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    /**
     * @return the geographicalCoverage
     */
    public String getGeographicalCoverage() {
        return geographicalCoverage;
    }

    /**
     * @param geographicalCoverage
     *            the geographicalCoverage to set
     */
    public void setGeographicalCoverage(String geographicalCoverage) {
        this.geographicalCoverage = geographicalCoverage;
    }

    /**
     * @return the geographicalGranularity
     */
    public String getGeographicalGranularity() {
        return geographicalGranularity;
    }

    /**
     * @param geographicalGranularity
     *            the geographicalGranularity to set
     */
    public void setGeographicalGranularity(String geographicalGranularity) {
        this.geographicalGranularity = geographicalGranularity;
    }

    /**
     * @return the temporalCoverageFrom
     */
    public String getTemporalCoverageFrom() {
        return temporalCoverageFrom;
    }

    /**
     * @param temporalCoverageFrom
     *            the temporalCoverageFrom to set
     */
    public void setTemporalCoverageFrom(String temporalCoverageFrom) {
        this.temporalCoverageFrom = temporalCoverageFrom;
    }

    /**
     * @return the temporalCoverageTo
     */
    public String getTemporalCoverageTo() {
        return temporalCoverageTo;
    }

    /**
     * @param temporalCoverageTo
     *            the temporalCoverageTo to set
     */
    public void setTemporalCoverageTo(String temporalCoverageTo) {
        this.temporalCoverageTo = temporalCoverageTo;
    }

    /**
     * @return the temporalGranularity
     */
    public int getTemporalGranularity() {
        return temporalGranularity;
    }

    /**
     * @param temporalGranularity
     *            the temporalGranularity to set
     */
    public void setTemporalGranularity(int temporalGranularity) {
        this.temporalGranularity = temporalGranularity;
    }

    /**
     * @return the ratings
     */
    public List<List<String>> getRatings() {
        return ratings;
    }

    /**
     * @param ratings
     *            the ratings to set
     */
    public void setRatings(List<List<String>> ratings) {
        this.ratings = ratings;
    }

    /**
     * @return the comments
     */
    public List<List<String>> getComments() {
        return comments;
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments(List<List<String>> comments) {
        this.comments = comments;
    }

    /**
     * @return the resources
     */
    public List<List<String>> getResources() {
        return resources;
    }

    /**
     * @param resources
     *            the resources to set
     */
    public void setResources(List<List<String>> resources) {
        this.resources = resources;
    }

    /**
     * @param temporalGranularity
     *            the temporalGranularity to set
     */
    public void setTemporalGranularity(Integer temporalGranularity) {
        this.temporalGranularity = temporalGranularity;
    }

    /**
     * @return the HashMap representation of the @class CKANDataset
     */
    public HashMap<String, Object> getMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("titel", this.titel);
        map.put("name", this.name);
        map.put("notes", this.notes);
        map.put("author", this.author);
        map.put("authorEmail", this.authorEmail);
        map.put("maintainer", this.maintainer);
        map.put("maintainerEmail", this.maintainerEmail);
        map.put("version", this.version);
        map.put("license", this.license);
        map.put("url", this.url);
        map.put("state", this.state);
        map.put("dateReleased", this.dateReleased);
        map.put("dateUpdated", this.dateUpdated);
        map.put("geographicalCoverage", this.geographicalCoverage);
        map.put("geographicalGranularity", this.geographicalGranularity);
        map.put("temporalCoverageFrom", this.temporalCoverageFrom);
        map.put("temporalCoverageTo", this.temporalCoverageTo);
        map.put("temporalGranularity", this.temporalGranularity);
        map.put("ratings", this.ratings);
        map.put("comments", this.comments);
        map.put("resources", this.resources);
        map.put("groups", this.groups);
        map.put("tags", this.tags);

        return map;
    }
}
