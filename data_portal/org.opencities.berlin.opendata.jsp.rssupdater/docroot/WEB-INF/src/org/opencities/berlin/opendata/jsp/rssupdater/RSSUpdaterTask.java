package org.opencities.berlin.opendata.jsp.rssupdater;

// imports
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.opencities.berlin.opendata.ckan.gateway.CKANGateway;

/**
 * This is the task that gets the latest updates from the CKAN repository
 * 
 * @author Nikolay Tcholtchev, Fraunhofer FOKUS
 * 
 */
public class RSSUpdaterTask {

	/** The Hash Map with the group descriptions. */
	private HashMap<String, String> groups = null;

	/** This is the URL to the CKAN instance. */
	private String CKANurl = "";

	/** The maximum number of runs. */
	private final int MAXIMUM_NUMBER_OF_RUNS = 2;

	/** Local field for the number of runs. */
	private int numberOfRuns = 0;

	/**
	 * The variable is meant to contain the period between two subsequent
	 * requests.
	 */
	private long period = 0;

	/**
	 * The variable is meant to contain the period for which CKAN data is
	 * considered for the RSS compilation.
	 */
	private long sincePeriod = 0;

	/** The variable keeps the time zone of the server. */
	private String serverTimeZone = null;

	/** The filename for the package RSS file. */
	private String packageRssFileName = null;

	/** The title for the package RSS. */
	private String packageRssTitle = null;

	/** The description for the package RSS. */
	private String packageRssDescription = null;

	/** The RSS link of the package. */
	private String packageRssLink = null;

	/** The filename for the categories RSS file. */
	private String categoryRssFileName = null;

	/** The filename for the categories RSS file. */
	private String categoriesRssTitle = null;

	/** The description for the categories RSS. */
	private String categoryRssDescription = null;

	/** The RSS link of the category. */
	private String categoryRssLink = null;

	/** Local CKAN gateway reference. */
	private CKANGateway ckanGw = null;

	/** The local field that keeps the authentication key. */
	private String authenticationKey = null;

	/**
	 * The constructor of the RSS updater task.
	 * 
	 * @param cKANurl
	 *            the CKAN url to set.
	 * 
	 * @param period
	 *            the period of the timer task.
	 * @param sincePeriod
	 *            the period since when data within the CKAN instance is
	 *            considered.
	 * @param serverTimeZone
	 *            the time zone of the server.
	 * @param rssFileName
	 *            the name of RSS file.
	 * @param rssTitle
	 *            the title of the RSS feed to updated.
	 * @param rssDescription
	 *            the description of the RSS feed.
	 * @param rssLink
	 *            the link of the RSS feed.
	 */
	public RSSUpdaterTask(String cKANurl, long period, long sincePeriod,
			String serverTimeZone, String packageRssFileName,
			String packageRssTitle, String packageRssDescription,
			String packageRssLink, String categoryRssFileName,
			String categoriesRssTitle, String categoryRssDescription,
			String categoryRssLink, String authKey) {

		// check and store the CKAN url locally
		CKANurl = cKANurl;
		if (!CKANurl.endsWith("/")) {
			CKANurl += "/";
		}

		// store the period value locally
		this.period = period;

		// store the sincePeriod locally
		this.sincePeriod = sincePeriod;

		// store the time zone locally
		this.serverTimeZone = serverTimeZone;

		// store the RSS parameters locally
		this.packageRssFileName = packageRssFileName;
		this.packageRssTitle = packageRssTitle;
		this.packageRssDescription = packageRssDescription;
		this.packageRssLink = packageRssLink;
		this.categoryRssFileName = categoryRssFileName;
		this.categoriesRssTitle = categoriesRssTitle;
		this.categoryRssDescription = categoryRssDescription;
		this.categoryRssLink = categoryRssLink;

		this.authenticationKey = authKey;

		// initialize the local field for the CKAN gateway
		ckanGw = new CKANGateway(CKANurl, authenticationKey);

		// fill the groups array
		// TODO: make this configurable --> at least not in the code
		groups = new HashMap<String, String>();
		groups.put("rec", "Arts and Recreation");
		groups.put("business", "Business Enterprise, Economics, and Trade");
		groups.put("budget", "City Budget: Revenues & Expenditures");
		groups.put("stats", "City Portal Web Statistics");
		groups.put("housing", "Construction, Housing, and Public Works ");
		groups.put("safety", "Crime and Community Safety");
		groups.put("demographics", "Demographics");
		groups.put("edu", "Education");
		groups.put("elections", "Elections");
		groups.put("emergency", "Emergency Services");
		groups.put("energy", "Energy and Utilities");
		groups.put("environment", "Environment, Geography and Meteorological");
		groups.put("health", "Health and Disability");
		groups.put("employment", "Labor Force and Employment Market");
		groups.put("law", "Law Enforcement, Courts, and Prisons");
		groups.put("politics", "Political");
		groups.put("tourism", "Tourism");
		groups.put("transport", "Urban Transport");
		groups.put("misc.", "Others");

	}

	/**
	 * Starts the task.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void run() {
		// check weather the portlet should be terminated
		if (MAXIMUM_NUMBER_OF_RUNS != -1
				&& numberOfRuns >= MAXIMUM_NUMBER_OF_RUNS) {
			System.exit(0);
		}
		if (MAXIMUM_NUMBER_OF_RUNS != -1) {
			numberOfRuns++;
		}
		
		
		//// HERE WE GO
		System.out.println("Fetching last CKAN contents  ...");

		// get all revisions
		String revisionsStr = ckanGw.getRevisions(serverTimeZone, sincePeriod);

		if (revisionsStr == null) {
			// in case reading the revisions failed
			return;
		}

		// get the updated data sets and correspondingly the details for the
		// revisions
		Vector<String> revisionsDetails = ckanGw
				.getUpdatedDatasets(revisionsStr);
		if (revisionsDetails == null) {
			System.err
					.println("Failed to obtain information about the revisions !!!");
			return;
		}

		// get the details for the data sets in the revisions
		Vector<Map> dataSetDetails = ckanGw
				.getUpdatedDataSetsDetails(revisionsDetails);
		if (dataSetDetails == null) {
			System.err
					.println("Failed to obtain detailed information about the revisions !!!");
			return;
		}

		// generate the XML text for the RSS feed for packages
		String RSStext = generatePackageRSSFeed(dataSetDetails);

		// write the RSS feed to a server directory
		writeRSSFeed(RSStext, packageRssFileName);

		// get the details for the categories in the revisions
		Vector<Map> categoriesDetails = ckanGw
				.getUpdatedCategoriesDetails(revisionsDetails);
		if (categoriesDetails == null) {
			System.err
					.println("Failed to obtain detailed information about the categories !!!");
			return;
		}

		// generate the XML text for the RSS feed for categories
		RSStext = generateCroupsRSSFeed(categoriesDetails, dataSetDetails);

		// write the RSS feed to a server directory
		writeRSSFeed(RSStext, categoryRssFileName);

		System.out.println("RSS feeds creation process completed.");
	}

	/**
	 * The method writes the RSS feed.
	 * 
	 * @param rSStext
	 *            the text to write to the RSS file.
	 */
	private void writeRSSFeed(String rSStext, String filename) {

		// check the input.
		if (rSStext == null || filename == null) {
			return;
		}

		try {
			FileWriter fw = new FileWriter(filename);
			fw.write(rSStext);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * This is a help function used to escape HTML characters in a string.
	 * 
	 * @param string
	 *            the string to escape the characters.
	 * @return the string with the escaped characters.
	 */
	private String stringToHTMLString(String string) {
		StringBuffer sb = new StringBuffer(string.length());
		// true if last char was blank
		boolean lastWasBlankChar = false;
		int len = string.length();
		char c;

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);
			if (c == ' ') {
				// blank gets extra work,
				// this solves the problem you get if you replace all
				// blanks with &nbsp;, if you do that you loss
				// word breaking
				if (lastWasBlankChar) {
					lastWasBlankChar = false;
					sb.append("&nbsp;");
				} else {
					lastWasBlankChar = true;
					sb.append(' ');
				}
			} else {
				lastWasBlankChar = false;
				//
				// HTML Special Chars
				if (c == '"')
					sb.append("&quot;");
				else if (c == '&')
					sb.append("&amp;");
				else if (c == '<')
					sb.append("&lt;");
				else if (c == '>')
					sb.append("&gt;");
				// else if (c == '\n')
				// // Handle Newline
				// sb.append("&lt;br/&gt;");
				else {
					int ci = 0xffff & c;
					if (ci < 160)
						// nothing special only 7 Bit
						sb.append(c);
					else {
						// Not 7 Bit use the unicode system
						sb.append("&#");
						sb.append(new Integer(ci).toString());
						sb.append(';');
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * The function generates the RSS XML feed text for packages.
	 * 
	 * @param updatedDatasets
	 *            the vector containing the information about the updated data
	 *            sets.
	 * 
	 * @return a string containing the complete XML RSS feed.
	 */
	@SuppressWarnings("rawtypes")
	private String generatePackageRSSFeed(Vector<Map> updatedDatasets) {

		// the variable to return
		String toreturn = "";

		// check the input parameters
		if (updatedDatasets == null) {
			return null;
		}

		// prepare initially the text
		toreturn += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\n";
		toreturn += "<rss version=\"2.0\">\n\n";

		toreturn += "\t<channel>\n";
		toreturn += "\t\t<title>" + packageRssTitle + "</title>\n";
		toreturn += "\t\t<description>" + packageRssDescription
				+ "</description>\n";
		toreturn += "\t\t<link>" + packageRssLink + "</link>\n";

		// iterate over the updated data sets
		for (int i = 0; i < updatedDatasets.size(); i++) {

			// the description
			String description = "";

			// get the map
			Map helpMap = (Map) (updatedDatasets.get(i));

			// prepare the description of the item

			// get the timestamp
			String timestamp = (String) helpMap.get("metadata_modified");

			// pick the email for the contact field
			String contact = (String) helpMap.get("maintainer_email");
			if (contact == null) {
				contact = "";
			}
			// pick the name of the maintainer
			String maintainer = (String) helpMap.get("maintainer");
			if (maintainer == null) {
				maintainer = "";
			}

			// add maintainer and contact
			description += "<b>Maintainer:</b>" + maintainer + "<br/>\n";
			description += "<b>Contact:</b>" + contact + "<br/><br/>\n";

			// add the tags
			ArrayList tags = (ArrayList) helpMap.get("tags");
			description += "<b>Tags:</b>";
			for (int j = 0; j < tags.size(); j++) {
				description += (String) tags.get(j);
				if ((j + 1) < tags.size()) {
					description += ",";
				}
			}
			description += "<br/><br/>\n";

			// add the categories
			ArrayList categories = (ArrayList) helpMap.get("groups");
			description += "<b>Categories:</b><br/>";
			for (int j = 0; j < categories.size(); j++) {
				String ctgory = groups.get((String) categories.get(j));
				if (ctgory == null) {
					ctgory = "";
				} else {
					ctgory += "<br/>";
				}
				description += ctgory;
			}
			description += "<br/><br/>\n";

			// add the website
			String website = (String) helpMap.get("url");
			if (website == null) {
				website = "";
			}
			description += "<b>Website:</b> <a href=\"" + website + "\">"
					+ website + "</a><br/><br/>\n";

			// add the license
			String license = (String) helpMap.get("license");
			if (license == null) {
				license = "";
			}
			description += "<b>License:</b> " + license + "<br/><br/>\n";

			// add the notes
			String notes = (String) helpMap.get("notes");
			if (notes == null) {
				notes = "";
			}
			description += "<b>Notes:</b> " + notes + "<br/><br/>\n";

			// get the extras and the belonging fields
			Map extras = (Map) helpMap.get("extras");
			String geographical_coverage = (String) extras
					.get("geographical_coverage");
			if (geographical_coverage == null) {
				geographical_coverage = "";
			}

			String temporal_coverage_from = (String) extras
					.get("temporal_coverage_from");
			if (temporal_coverage_from == null) {
				temporal_coverage_from = "";
			}

			String temporal_coverage_to = (String) extras
					.get("temporal_coverage_to");
			if (temporal_coverage_to == null) {
				temporal_coverage_to = "";
			}

			// add the georgraphical coverage
			description += "<b>Geographical coverage:</b>"
					+ geographical_coverage + "<br/><br/>\n";

			// add the temporal coverage
			description += "<b>Temporal coverage:</b> <br/>\n";
			description += "<b><i>From:</i></b>" + temporal_coverage_from
					+ "<br/>\n";
			description += "<b><i>To:</i></b>" + temporal_coverage_to
					+ "<br/><br/>\n";

			// compile the string for the resources
			description += "<b>Resources:</b><br/>";
			ArrayList resources = (ArrayList) (helpMap.get("resources"));
			for (int j = 0; j < resources.size(); j++) {
				Map resMap = (Map) resources.get(j);
				String resourceDescription = (String) (resMap
						.get("description"));
				if (resourceDescription == null) {
					resourceDescription = "";
				}

				String language = (String) (resMap.get("language"));
				if (language == null) {
					language = "";
				}

				String url = (String) (resMap.get("url"));
				if (url == null) {
					url = "";
				} else {
					url = "<a href=\"" + url + "\">" + url + "</a>";
				}

				String format = (String) (resMap.get("format"));
				if (format == null) {
					format = "";
				}

				description += "<table <table style=\"border:1px solid #9E8DE3;\" border=\"1\">";
				description += "<tr><td> Resource Description:</td><td>"
						+ resourceDescription + "</td></tr>";
				description += "<tr><td> Format:</td><td>" + format
						+ "</td></tr>";
				description += "<tr><td> Language:</td><td>" + language
						+ "</td></tr>";
				description += "<tr><td> Resource URL:</td><td>" + url
						+ "</td></tr>";
				description += "</table><br/><br/>";
			}

			// add a <hr/>
			description += "<hr/>";

			// escape HTML characters
			description = stringToHTMLString(description);

			// add the item
			toreturn += "\t\t<item>\n";
			toreturn += "\t\t\t<title>" + helpMap.get("title") + "</title>\n";
			toreturn += "\t\t\t<description>" + description
					+ "</description>\n";
			toreturn += "\t\t\t<guid isPermaLink=\"false\"></guid>\n";
			toreturn += "\t\t\t<pubDate>" + timestamp + "</pubDate>\n";
			toreturn += "\t\t</item>\n";

		}
		toreturn += "\t</channel>\n";
		toreturn += "</rss>\n";

		return toreturn;
	}

	/**
	 * Help function to pick a data set from the belonging array.
	 * 
	 * @param updatedDatasets
	 *            the array.
	 * @param packageName
	 *            the name of the package.
	 * @return the map for the package.
	 */
	@SuppressWarnings("rawtypes")
	private Map getDataSet(Vector<Map> updatedDatasets, String packageName) {
		Map toreturn = null;

		for (int i = 0; i < updatedDatasets.size(); i++) {
			Map m = updatedDatasets.get(i);
			String name = (String) m.get("name");
			if (name != null && name.equals(packageName)) {
				toreturn = m;
				break;
			}
		}

		return toreturn;
	}

	/**
	 * The function generates the RSS XML feed text for packages.
	 * 
	 * @param updatedCategories
	 *            the vector containing the information about the updated
	 *            categories.
	 * @param updatedDatasetsthe
	 *            vector containing the information about the updated data sets.
	 * 
	 * @return a string containing the complete XML RSS feed.
	 */
	@SuppressWarnings({ "rawtypes" })
	private String generateCroupsRSSFeed(Vector<Map> updatedCategories,
			Vector<Map> updatedDatasets) {

		// the variable to return
		String toreturn = "";

		// check the input parameters
		if (updatedCategories == null) {
			return null;
		}

		// prepare initially the text
		toreturn += "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\n";
		toreturn += "<rss version=\"2.0\">\n\n";

		toreturn += "\t<channel>\n";
		toreturn += "\t\t<title>" + categoriesRssTitle + "</title>\n";
		toreturn += "\t\t<description>" + categoryRssDescription
				+ "</description>\n";
		toreturn += "\t\t<link>" + categoryRssLink + "</link>\n";

		// iterate over the categories
		for (int i = 0; i < updatedCategories.size(); i++) {
			// get the map containing the updated categories data
			Map helpMap = (Map) updatedCategories.get(i);

			// get the title
			String title = (String) helpMap.get("title");

			// the variable to store the description
			String description = "";

			// get the packages and iterate over the packages
			ArrayList arr = (ArrayList) helpMap.get("packages");
			if (arr != null && arr.size()>0) {
			
				description = "<b>Data sets:</b><br/><br/>";
				for (int j = 0; j < arr.size(); j++) {
					String packageName = (String) arr.get(j);
					Map pMap = getDataSet(updatedDatasets, packageName);
					if (pMap == null) {
						continue;
					}

					String packageTitle = (String) pMap.get("title");
					if (packageTitle == null) {
						packageTitle = "";
					}

					String license = (String) pMap.get("license");
					if (license == null) {
						license = "";
					}

					// get the timestamp
					String timestamp = (String) pMap.get("metadata_modified");

					// pick the email for the contact field
					String contact = (String) pMap.get("maintainer_email");
					if (contact == null) {
						contact = "";
					}

					// pick the name of the maintainer
					String maintainer = (String) pMap.get("maintainer");
					if (maintainer == null) {
						maintainer = "";
					}

					// add the website
					String website = (String) pMap.get("url");
					if (website == null) {
						website = "";
					}

					// add the notes
					String notes = (String) pMap.get("notes");
					if (notes == null) {
						notes = "";
					}

					description += "<table style=\"border:5px solid #9E8DE3;\" border=\"1\">";
					description += "<tr><td><b>Title:</b></td><td>" + packageTitle
						+ "</td></tr>";
					description += "<tr><td><b>Contact:</b></td><td>" + contact
						+ "</td></tr>";
					description += "<tr><td><b>Maintainer:</b></td><td>" + contact
						+ "</td></tr>";
					description += "<tr><td><b>Website:</b></td><td>" + website
						+ "</td></tr>";
					description += "<tr><td><b>License:</b></td><td>" + license
						+ "</td></tr>";
					description += "<tr><td><b>Notes:</b></td><td>" + notes
						+ "</td></tr>";
					description += "</tr></td></table><br/>";

				}
			}
			
			if (description.equals ("")) {
				// if no packages found in the category --> just continue
				continue;
			}

			// add a <hr/>
			description += "<hr/>";

			// escape HTML characters
			description = stringToHTMLString(description);

			// add the item
			toreturn += "\t\t<item>\n";
			toreturn += "\t\t\t<title>" + title + "</title>\n";
			toreturn += "\t\t\t<description>" + description
					+ "</description>\n";
			toreturn += "\t\t\t<guid isPermaLink=\"false\"></guid>\n";
			toreturn += "\t\t\t<pubDate/>\n";
			toreturn += "\t\t</item>\n";

		}

		toreturn += "\t</channel>\n";
		toreturn += "</rss>\n";

		return toreturn;
	}

}
