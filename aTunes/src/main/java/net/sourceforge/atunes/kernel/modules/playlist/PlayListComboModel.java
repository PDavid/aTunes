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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;


import org.jdesktop.swingx.combobox.ListComboBoxModel;

final class PlayListComboModel extends
		ListComboBoxModel<PlayListComboModelObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1833337893582691546L;

	private final List<PlayListComboModelObject> objects;

	private PlayListComboModel(final List<PlayListComboModelObject> objects) {
		super(objects);
		this.objects = objects;
	}

	/**
	 * Creates a new combo model
	 * 
	 * @return
	 */
	static PlayListComboModel getNewComboModel() {
		return new PlayListComboModel(new ArrayList<PlayListComboModelObject>());
	}

	/**
	 * Removes a play list
	 * 
	 * @param index
	 */
	void removeItemAt(final int index) {
		this.objects.remove(index);
		// Need this to update combo box list
		fireIntervalRemoved(this, index, index);
	}

	/**
	 * Adds a play list
	 * 
	 * @param name
	 */
	void addItem(final String name, final ImageIcon icon) {
		this.objects.add(new PlayListComboModelObject(name, icon));
		// Need this to update combo box list
		fireIntervalAdded(this, this.objects.size() - 1,
				this.objects.size() - 1);
	}

	/**
	 * Renames a play list at a given index
	 * 
	 * @param index
	 * @param newName
	 */
	void rename(final int index, final String newName) {
		PlayListComboModelObject o = this.objects.get(index);
		this.objects.remove(index);
		this.objects.add(index,
				new PlayListComboModelObject(newName, o.getIcon()));
		fireContentsChanged(this, index, index);
	}

	/**
	 * Returns names of play lists
	 * 
	 * @return
	 */
	List<String> getItems() {
		List<String> names = new ArrayList<String>();
		for (PlayListComboModelObject o : this.objects) {
			names.add(o.getName());
		}
		return names;
	}
}
