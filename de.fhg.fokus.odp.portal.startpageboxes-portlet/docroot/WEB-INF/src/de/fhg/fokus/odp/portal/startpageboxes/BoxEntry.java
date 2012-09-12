package de.fhg.fokus.odp.portal.startpageboxes;

// TODO: Auto-generated Javadoc
/**
 * The Class BoxEntry.
 */
public class BoxEntry implements Comparable<BoxEntry> {

	/** The title. */
	private String title;
	
	/** The ckan id. */
	private String ckanId;
	
	/** The rating. */
	private double rating;
	
	/** The url. */
	private String url;
	
	/** The votes. */
	private final int votes;

	/**
	 * Instantiates a new box entry.
	 *
	 * @param ckanId the ckan id
	 * @param title the title
	 * @param url the url
	 * @param votes the votes
	 * @param rating the rating
	 */
	public BoxEntry(String ckanId, String title, String url, int votes,
			double rating) {
		super();
		this.title = title;
		this.ckanId = ckanId;
		this.rating = rating;
		this.url = url;
		this.votes = votes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(BoxEntry o) {
		if (this.getRating() > o.getRating()) {
			return -1;
		} else if (this.getRating() < o.getRating()) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the ckan id.
	 *
	 * @return the ckan id
	 */
	public String getCkanId() {
		return ckanId;
	}

	/**
	 * Sets the ckan id.
	 *
	 * @param ckanId the new ckan id
	 */
	public void setCkanId(String ckanId) {
		this.ckanId = ckanId;
	}

	/**
	 * Gets the rating.
	 *
	 * @return the rating
	 */
	public double getRating() {
		return rating;
	}

	/**
	 * Sets the rating.
	 *
	 * @param rating the new rating
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the votes.
	 *
	 * @return the votes
	 */
	public int getVotes() {
		return votes;
	}

}
