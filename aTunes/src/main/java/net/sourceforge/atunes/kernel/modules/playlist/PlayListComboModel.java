/*
 * aTunes 3.1.0
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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
import java.util.Collections;
import java.util.List;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

final class PlayListComboModel extends ListComboBoxModel<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1833337893582691546L;
	
	private List<String> playLists;
	
	private PlayListComboModel(List<String> list) {
		super(list);
		this.playLists = list;
	}
	
	/**
	 * Creates a new combo model
	 * @return
	 */
	static PlayListComboModel getNewComboModel() {
		return new PlayListComboModel(new ArrayList<String>());
	}

	/**
	 * Removes a play list
	 * @param index
	 */
	void removeItemAt(int index) {
		this.playLists.remove(index);
		// Need this to update combo box list
		fireIntervalRemoved(this, index, index);
	}

	/**
	 * Adds a play list
	 * @param name
	 */
	void addItem(String name) {
		this.playLists.add(name);
		// Need this to update combo box list
		fireIntervalAdded(this, this.playLists.size()-1, this.playLists.size()-1);
	}

	/**
	 * Renames a play list at a given index
	 * @param index
	 * @param newName
	 */
	void rename(int index, String newName) {
		this.playLists.remove(index);
		this.playLists.add(index, newName);		
		fireContentsChanged(this, index, index);
	}

	/**
	 * Returns names of play lists
	 * @return
	 */
	List<String> getItems() {
		return Collections.unmodifiableList(this.playLists);
	}
	

}
