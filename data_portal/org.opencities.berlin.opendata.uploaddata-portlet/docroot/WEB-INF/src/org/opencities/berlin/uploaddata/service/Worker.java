package org.opencities.berlin.uploaddata.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.opencities.berlin.opendata.ckan.gateway.CKANGateway;

public class Worker {

	private String resourceString = "[{";
	private String extrasString = "{";
	private String ckan;
	private String key;
	private String uploadFolder;

	public Worker(String ckan, String key, String uploadFolder) {
		this.ckan = ckan;
		this.key = key;
		this.uploadFolder = uploadFolder;
	}

	/**
	 * loop through all Cells and rows. Firstly, add correct keys to strings.
	 * Secondly, parse corresponding value into correct json and add this
	 * dataset to ckan via middleware.
	 * 
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String readXlsx() {
		String errormessage = "";

		CKANGateway gw = new CKANGateway(ckan, key);
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String> strings = new ArrayList<String>();
		XSSFWorkbook workBook = null;
		try {
			workBook = new XSSFWorkbook(uploadFolder + "file.xlsx");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int counter = 0;
		XSSFSheet sheet = workBook.getSheetAt(0);
		for (Row row : sheet) {
			for (Cell cell : row) {
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					String value = cell.getRichStringCellValue().getString();
					// first row, add value to strings
					if (counter == 0) {
						if (!value.startsWith("resources:")
								&& !value.startsWith("extras:"))
							map.put(value, null);

						strings.add(value);
						break;
					}
					if (strings.get(cell.getColumnIndex()).equalsIgnoreCase(
							"tags")
							|| strings.get(cell.getColumnIndex())
									.equalsIgnoreCase("groups")) {
						String[] tmp = value.split(",");
						String out = buildString(tmp);
						map.put(strings.get(cell.getColumnIndex()), out);
					} else if (strings.get(cell.getColumnIndex()).startsWith(
							"resources:")) {
						String[] tmp = strings.get(cell.getColumnIndex())
								.split(":");
						parseResource(tmp[1], value);
					} else if (strings.get(cell.getColumnIndex()).startsWith(
							"extras:")) {
						String[] tmp = strings.get(cell.getColumnIndex())
								.split(":");
						parseExtras(tmp[1], value);
					} else {
						map.put(strings.get(cell.getColumnIndex()), "\""
								+ value + "\"");
					}
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						// is a date;
						map = handleDate(map, strings, cell);
					} else {
						// is a number;
						map = handleNumber(map, strings, cell);
					}
					break;
				default:
					break;
				}
			}
			// finish extras and resources
			finishParseResource();
			finishParseExtras();

			map = checkEmptyValues(map);
			map = toLowerCase(map);
			// add resources and extras to map
			map.put("resources", resourceString);
			map.put("extras", extrasString);
			if (counter >= 1) {
				// add dataset to CKAN via middleware
				HashMap<String, HashMap> out = gw.createMetaDataSet(map);
				if (out == null)
					errormessage += String.valueOf(counter) + ",";
			}
			++counter;
			resourceString = resetResourceString();
			extrasString = resetExtrasString();
		}

		if (errormessage.equalsIgnoreCase(""))
			return errormessage;
		else
			return errormessage.substring(0, errormessage.length() - 1);
	}

	private HashMap<String, String> handleNumber(HashMap<String, String> map,
			ArrayList<String> strings, Cell cell) {
		String val;
		val = String.valueOf(cell.getNumericCellValue());

		if (strings.get(cell.getColumnIndex()).startsWith("extras:")) {
			String[] tmp = strings.get(cell.getColumnIndex()).split(":");
			parseExtras(tmp[1], val);
		} else
			map.put(strings.get(cell.getColumnIndex()), "\"" + val + "\"");

		return map;
	}

	private HashMap<String, String> handleDate(HashMap<String, String> map,
			ArrayList<String> strings, Cell cell) {
		String val;
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
		val = form.format(cell.getDateCellValue());

		if (strings.get(cell.getColumnIndex()).startsWith("extras:")) {
			String[] tmp = strings.get(cell.getColumnIndex()).split(":");
			parseExtras(tmp[1], val);
		} else
			map.put(strings.get(cell.getColumnIndex()), "\"" + val + "\"");

		return map;
	}

	private HashMap<String, String> toLowerCase(HashMap<String, String> map) {

		String name = map.get("name");
		if (name != null)
			name = name.toLowerCase();
		map.put("name", name);

		String licenseID = map.get("license-id");
		if (licenseID != null)
			licenseID = licenseID.toLowerCase();
		map.put("license-id", licenseID);

		return map;
	}

	/**
	 * checks if tags or groups are empty. if true, substitute null by []
	 * 
	 * @param map
	 *            hashmap with all key,value pairs
	 * @return updated hashmap
	 */
	private HashMap<String, String> checkEmptyValues(HashMap<String, String> map) {
		if (map.get("tags") == null)
			map.put("tags", "[]");

		if (map.get("groups") == null)
			map.put("groups", "[]");

		return map;
	}

	/**
	 * builds a string according to the required format.
	 * out=["value1","value2",...]
	 * 
	 * @param array
	 *            contains values
	 * @return
	 */
	private String buildString(String[] array) {
		String out = "[";
		for (String x : array) {
			if (x.startsWith(" "))
				x = x.substring(1);
			out += "\"" + x + "\"" + ",";
		}
		out = out.substring(0, out.length() - 1) + "]";

		return out;
	}

	/**
	 * sets resourceString to initial value
	 * 
	 * @return resourceString
	 */
	private String resetResourceString() {
		return "[{";
	}

	/**
	 * sets extrasString to initial value
	 * 
	 * @return extrasString
	 */
	private String resetExtrasString() {
		return "{";
	}

	/**
	 * substitutes last ',' by '}' if there's any input
	 */
	private void finishParseExtras() {
		if (extrasString.length() > 1)
			extrasString = extrasString.substring(0, extrasString.length() - 1)
					+ "}";
		else
			extrasString += "}";
	}

	/**
	 * adds newKey:value in extras
	 * 
	 * @param newKey
	 *            key in extras
	 * @param value
	 *            value in extras
	 */
	private void parseExtras(String newKey, String value) {
		extrasString += "\"" + newKey + "\"" + ":" + "\"" + value + "\"" + ",";
	}

	/**
	 * adds newKey:value in resources
	 * 
	 * @param newKey
	 *            key in resources
	 * @param value
	 *            value in resources
	 */
	private void parseResource(String newKey, String value) {
		resourceString += "\"" + newKey + "\"" + ":" + "\"" + value + "\""
				+ ",";
	}

	/**
	 * substitutes last ',' by "}]" if there's any input
	 */
	private void finishParseResource() {
		if (resourceString.length() > 2)
			resourceString = resourceString.substring(0,
					resourceString.length() - 1)
					+ "}]";
		else
			resourceString += "}]";
	}

	public String getCkan() {
		return ckan;
	}

	public String getKey() {
		return key;
	}

	public String getULFolder() {
		return uploadFolder;
	}

}