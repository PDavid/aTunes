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

package net.sourceforge.atunes.model;

import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;

import javax.swing.Action;

/**
 * A panel holding buttons
 * 
 * @author alex
 * 
 */
public interface IButtonPanel {

	/**
	 * @param iconOnly
	 */
	public void setIconOnly(final boolean iconOnly);

	/**
	 * Adds a new button to this panel
	 * 
	 * @param name
	 * @param tooltip
	 * @param icon
	 * @param action
	 * @param userObject
	 */
	public void addButton(final String name, final String tooltip,
			final IColorMutableImageIcon icon, final Action action,
			final Object userObject);

	/**
	 * Removes all buttons
	 */
	public void clear();

	/**
	 * Removes a button
	 * 
	 * @param index
	 */
	public void removeButton(final int index);

	/**
	 * Sets button identified by name as selected
	 * 
	 * @param buttonName
	 */
	public void setSelectedButton(final String buttonName);

	/**
	 * Sets button identified by index
	 * 
	 * @param index
	 */
	public void setSelectedButton(final int index);

	/**
	 * Returns user object of selected toggle
	 * 
	 * @return
	 */
	public Object getSelectedItem();

	/**
	 * Adds an item listener
	 * 
	 * @param l
	 */
	public void addItemListener(final ItemListener l);

	/**
	 * Renames button
	 * 
	 * @param index
	 * @param newName
	 */
	public void renameButton(final int index, final String newName);

	/**
	 * @param event
	 * @return index of button selected in an action event
	 */
	public int getIndexOfButtonSelected(final ActionEvent event);

	/**
	 * Shows or hides this panel
	 * 
	 * @param show
	 */
	public void setVisible(boolean show);

}