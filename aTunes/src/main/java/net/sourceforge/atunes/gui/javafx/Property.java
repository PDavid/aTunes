package net.sourceforge.atunes.gui.javafx;

import javafx.beans.property.SimpleStringProperty;

/**
 * Properties for About Dialog
 * 
 * @author alex
 * 
 */
public class Property {

	private SimpleStringProperty description;

	private SimpleStringProperty value;

	/**
	 * @param description
	 * @param value
	 */
	public Property(String description, String value) {
		this.description = new SimpleStringProperty(description);
		this.value = new SimpleStringProperty(value);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description.get();
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description.set(description);
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value.get();
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value.set(value);
	}
}
