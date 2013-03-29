/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

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
