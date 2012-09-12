package de.fhg.fokus.odp.portal.startpageboxes;

// TODO: Auto-generated Javadoc
/**
 * The Class DiscussionEntry.
 */
public class DiscussionEntry implements Comparable<DiscussionEntry> {
	
	/** The title. */
	private String title;
	
	/** The rating. */
	private double rating;
	
	/** The id. */
	private String id;
	
	/** The votes. */
	private int votes;
	
	/** The url. */
	private String url;

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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DiscussionEntry o) {

		if (this.rating > o.getRating()) {
			return -1;
		} else if (this.rating < o.getRating()) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the votes.
	 *
	 * @return the votes
	 */
	public int getVotes() {
		return votes;
	}

	/**
	 * Sets the votes.
	 *
	 * @param votes the new votes
	 */
	public void setVotes(int votes) {
		this.votes = votes;
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
}
