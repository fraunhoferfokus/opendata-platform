package de.fhg.fokus.odp.portal.uploaddata.service;

import java.util.Map;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * This class provides all validation methods.
 * 
 * @author pla
 * 
 */
public class Validator {
	private static final String INVALID_TAG_CHARACTERS_REG_EXP = "[^a-z0-9-._]";
	private final Set<String> groupsInCKAN;

	/**
	 * Constructor which gets a Set of all valid groups from CKAN.
	 * 
	 * @param groups
	 *            Set of valid groups of CKAN.
	 */
	public Validator(Set<String> groups) {
		this.groupsInCKAN = groups;
	}

	/**
	 * This method validates all tags in the provided List of Strings. It
	 * changes the value of every String to lower case and removes all invalid
	 * characters.
	 * 
	 * @param tags
	 *            List of Strings which contains all tags
	 */
	public static void validateTags(List<String> tags) {
		final ListIterator<String> listIterator = tags.listIterator();
		while (listIterator.hasNext()) {
			String item = listIterator.next();
			item = (item.toLowerCase()).replaceAll(INVALID_TAG_CHARACTERS_REG_EXP,
					"");
			listIterator.set(item);
		}
	}

	/**
	 * This method validates all groups in the provided List of Strings. It
	 * checks if the groupname is a valid one (comparison to groupsInCKAN). If
	 * groupname is invalid, remove it from List.
	 * 
	 * @param groups
	 *            List of Strings which contains all group names.
	 */
	public void validateGroups(List<String> groups) {
		final ListIterator<String> listIterator = groups.listIterator();
		boolean validgroup = false;

		while (listIterator.hasNext()) {
			String group = listIterator.next();
			String groupCompare = group.replaceAll("\\s", "");

			for (String groupInCKAN : groupsInCKAN) {
				if (groupCompare.equalsIgnoreCase(groupInCKAN)) {
					// found a valid group in ckan
					validgroup = true;
					break;
				}
			}
			if (validgroup) { // reset validgroup
				listIterator.set(groupCompare);
				validgroup = false;
			} else {
				listIterator.remove();
			}
		}
	}

	/**
	 * checks if tags or groups are empty. if true, substitute null by [].
	 * 
	 * @param map
	 *            hashmap with all key,value pairs
	 * @return updated hashmap
	 */
	public static void checkTagAndGroupsForEmptyValues(Map<String, String> map) {
		if (map.get("tags") == null) {
			map.put("tags", "[]");
		}

		if (map.get("groups") == null) {
			map.put("groups", "[]");
		}
	}

	/**
	 * This method stores the name and license_id in lower case.
	 * 
	 * @param map
	 *            HashMap which stores the (key,value)-üairs
	 */
	public static void setlicenseAndNameToLowerCase(Map<String, String> map) {

		final String name = map.get("name");
		if (name != null) {
			map.put("name", name.toLowerCase());
		}

		final String licenseID = map.get("license_id");
		if (licenseID != null) {
			map.put("license_id", licenseID.toLowerCase());
		}
	}
}