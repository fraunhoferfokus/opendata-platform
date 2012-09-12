package de.fhg.fokus.odp.portal.uploaddata.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONValue;

import de.fhg.fokus.odp.portal.cache.gateway.CKANGateway;

public class Worker {

	private final String ckan;
	private final String key;
	private final String uploadFolder;
	private final CKANGateway gw;
	private final Validator validator;

	public Worker(String ckan, String key, String uploadFolder) {
		this.ckan = ckan;
		this.key = key;
		this.uploadFolder = uploadFolder;
		this.gw = new CKANGateway(ckan, key);
		this.validator = new Validator(this.gw.getGroupsData().keySet());
	}

	/**
	 * loop through all Cells and rows. Firstly, add correct keys to strings.
	 * Secondly, parse corresponding value into correct json and add this
	 * dataset to ckan via middleware.
	 * 
	 * @param args
	 * @throws Exception
	 * 
	 * @return a String of dataset indices, which were not uploaded.
	 */
	public String readXlsx() {
		final StringBuilder errormessage = new StringBuilder("");
		final StringBuilder resourceStringBuilder = new StringBuilder("[{");
		final StringBuilder extrasStringBuilder = new StringBuilder("{");

		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String> strings = new ArrayList<String>();
		XSSFWorkbook workBook = null;
		try {
			workBook = new XSSFWorkbook(uploadFolder + "file.xlsx");
		} catch (IOException e1) {
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
								&& !value.startsWith("extras:")) {
							map.put(value, null);
						}

						strings.add(value);
						break;
					}
					// compute columnIndex for later use
					int columnIndex = cell.getColumnIndex();
					// compute parameter for later use in if-statements
					String parameter = strings.get(columnIndex);
					handleString(resourceStringBuilder, extrasStringBuilder,
							map, value, parameter);
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						// is a date;
						parameter = strings.get(cell.getColumnIndex());
						handleDate(map, parameter, cell, extrasStringBuilder);
					} else {
						// is a number;
						parameter = strings.get(cell.getColumnIndex());
						handleNumber(map, parameter, cell, extrasStringBuilder);
					}
					break;
				default:
					break;
				}
			}
			// finish extras and resources
			finishParseResource(resourceStringBuilder);
			finishParseExtras(extrasStringBuilder);

			Validator.checkTagAndGroupsForEmptyValues(map);
			Validator.setlicenseAndNameToLowerCase(map);

			// add resources and extras to map
			map.put("resources", resourceStringBuilder.toString());
			map.put("extras", extrasStringBuilder.toString());

			createDataSet(errormessage, gw, map, counter);

			++counter;
			// reset resourceStringBuilder and extrasStringBuilder
			resetStringBuilder(resourceStringBuilder, extrasStringBuilder);

			// reset map
			map.clear();
		}

		if (errormessage.toString().equalsIgnoreCase("")) {
			// no errors
			return errormessage.toString();
		} else {
			// return list of dataset indices
			return errormessage.substring(0, errormessage.length() - 1);
		}
	}

	/**
	 * This method resets both StringBuilder for later use.
	 * 
	 * @param resourceStringBuilder
	 *            StringBuilder which should be reset.
	 * @param extrasStringBuilder
	 *            StringBuilder which should be reset.
	 */
	private void resetStringBuilder(StringBuilder resourceStringBuilder,
			StringBuilder extrasStringBuilder) {
		resourceStringBuilder.setLength(0);
		extrasStringBuilder.setLength(0);

		resourceStringBuilder.append("[{");
		extrasStringBuilder.append("{");
	}

	/**
	 * This method calls createMetaDataset of the middleware and tries to add
	 * this dataset.
	 * 
	 * @param errormessage
	 *            String which stores all faulty datasets (the indices)
	 * @param gw
	 *            the CKANGateway
	 * @param map
	 *            (key,value)-pairs
	 * @param counter
	 *            current dataset
	 */
	@SuppressWarnings("rawtypes")
	private void createDataSet(StringBuilder errormessage, CKANGateway gw,
			HashMap<String, String> map, int counter) {
		if (counter >= 1) {
			// add dataset to CKAN via middleware

			HashMap<String, HashMap> out = gw.createDataSet(map);
			if (out == null) {
				errormessage.append(counter);
				errormessage.append(",");
			}
		}
	}

	/**
	 * This method handles all string values, such as tags, groups et cetera.
	 * 
	 * @param resourceStringBuilder
	 * @param extrasStringBuilder
	 * @param map
	 * @param value
	 * @param parameter
	 */
	private void handleString(StringBuilder resourceStringBuilder,
			StringBuilder extrasStringBuilder, HashMap<String, String> map,
			String value, String parameter) {

		if (parameter.equalsIgnoreCase("tags")) {
			String[] tmp = value.split(",");
			List<String> tags = new ArrayList<String>(Arrays.asList(tmp));
			Validator.validateTags(tags);
			String out = buildJSONArray(tags);
			map.put(parameter, out);
		} else if (parameter.equalsIgnoreCase("groups")) {
			String[] tmp = value.split(",");
			List<String> groups = new LinkedList<String>(Arrays.asList(tmp));
			validator.validateGroups(groups);
			String out = buildJSONArray(groups);
			map.put(parameter, out);
		} else if (parameter.startsWith("resources:")) {
			String[] tmp = parameter.split(":");
			parseResource(resourceStringBuilder, tmp[1], value);
		} else if (parameter.startsWith("extras:")) {
			String[] tmp = parameter.split(":");
			parseExtras(extrasStringBuilder, tmp[1], value);
		} else {
			map.put(parameter, "\"" + value + "\"");
		}
	}

	/**
	 * returns JSONArray from List of Strings.
	 * 
	 * @param values
	 *            List of strings
	 * @return JSONArray
	 */
	private String buildJSONArray(List<String> values) {
		return JSONValue.toJSONString(values);
	}

	/**
	 * This method handles integer or double values and adds them as a correct
	 * String to our map.
	 * 
	 * @param map
	 *            HashMap which stores the (key,value)-pairs
	 * @param parameter
	 *            this is the name of this column
	 * @param cell
	 * @param extrasStringBuilder
	 *            intermediate extrasString
	 */
	private void handleNumber(HashMap<String, String> map, String parameter,
			Cell cell, StringBuilder extrasStringBuilder) {
		String val;
		val = String.valueOf(cell.getNumericCellValue());

		if (parameter.startsWith("extras:")) {
			String[] tmp = parameter.split(":");
			parseExtras(extrasStringBuilder, tmp[1], val);
		} else {
			map.put(parameter, "\"" + val + "\"");
		}
	}

	/**
	 * This method handles some dates, parses and adds them as a correct String
	 * to our map.
	 * 
	 * @param map
	 *            HashMap which stores the (key,value)-pairs
	 * @param parameter
	 *            this is the name of this column
	 * @param cell
	 * @param extrasStringBuilder
	 *            intermediate extrasString
	 */
	private void handleDate(HashMap<String, String> map, String parameter,
			Cell cell, StringBuilder extrasStringBuilder) {
		String val;
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
		val = form.format(cell.getDateCellValue());

		if (parameter.startsWith("extras:")) {
			String[] tmp = parameter.split(":");
			parseExtras(extrasStringBuilder, tmp[1], val);
		} else {
			map.put(parameter, "\"" + val + "\"");
		}
	}

	/**
	 * substitutes last ',' by '}' if there's any input.
	 * 
	 * @param extrasStringBuilder
	 */
	private void finishParseExtras(StringBuilder extrasStringBuilder) {
		int length = extrasStringBuilder.length();
		if (length > 1) {
			extrasStringBuilder.replace(length - 1, length, "}");
		} else {
			extrasStringBuilder.append("}");
		}
	}

	/**
	 * adds newKey:value in extras.
	 * 
	 * @param extrasStringBuilder
	 * 
	 * @param newKey
	 *            key in extras
	 * @param value
	 *            value in extras
	 */
	private void parseExtras(StringBuilder extrasStringBuilder, String newKey,
			String value) {
		extrasStringBuilder.append("\"");
		extrasStringBuilder.append(newKey);
		extrasStringBuilder.append("\":\"");
		extrasStringBuilder.append(value);
		extrasStringBuilder.append("\",");
	}

	/**
	 * adds newKey:value in resources.
	 * 
	 * @param resourceString2
	 * 
	 * @param newKey
	 *            key in resources
	 * @param value
	 *            value in resources
	 */
	private void parseResource(StringBuilder resourceStringBuilder,
			String newKey, String value) {
		resourceStringBuilder.append("\"");
		resourceStringBuilder.append(newKey);
		resourceStringBuilder.append("\":\"");
		resourceStringBuilder.append(value);
		resourceStringBuilder.append("\",");
	}

	/**
	 * substitutes last ',' by "}]" if there's any input.
	 * 
	 * @param resourceStringBuilder
	 */
	private void finishParseResource(StringBuilder resourceStringBuilder) {
		int length = resourceStringBuilder.length();
		if (length > 2) {
			(resourceStringBuilder.replace(length - 1, length, "}"))
					.append("]");
		} else {
			resourceStringBuilder.append("}]");
		}
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