package de.fhg.fokus.odp.portal.managedatasets.domain;

// TODO: Auto-generated Javadoc
/**
 * The Class ValueLabelEntry.
 */
public class ValueLabelEntry {
	
	/** The value. */
	private String value;
	
	/** The label. */
	private String label;

	/**
	 * Instantiates a new value label entry.
	 */
	public ValueLabelEntry() {
	}

	/**
	 * Instantiates a new value label entry.
	 *
	 * @param value the value
	 * @param label the label
	 */
	public ValueLabelEntry(String value, String label) {
		super();
		this.value = value;
		this.label = label;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}
