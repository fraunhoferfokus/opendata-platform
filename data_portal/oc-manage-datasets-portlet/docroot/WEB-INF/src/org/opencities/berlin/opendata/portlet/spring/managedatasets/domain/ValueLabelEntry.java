package org.opencities.berlin.opendata.portlet.spring.managedatasets.domain;

public class ValueLabelEntry {
	private String value;
	private String label;

	public ValueLabelEntry() {
	}

	public ValueLabelEntry(String value, String label) {
		super();
		this.value = value;
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
