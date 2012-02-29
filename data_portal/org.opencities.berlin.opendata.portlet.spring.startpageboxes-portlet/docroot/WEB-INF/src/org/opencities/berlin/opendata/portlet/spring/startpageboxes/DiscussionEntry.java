package org.opencities.berlin.opendata.portlet.spring.startpageboxes;

public class DiscussionEntry implements Comparable<DiscussionEntry> {
	private String title;
	private double rating;
	private String id;
	private int votes;
	private String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
