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

package net.sourceforge.atunes.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.utils.Logger;

/**
 * The Class NavigationTableModel.
 * 
 * @author fleax
 */
public final class NavigationTableModel extends AbstractColumnSetTableModel {

	/** The songs. */
	private List<? extends IAudioObject> audioObjects;

	/**
	 * Instantiates a new navigation table model.
	 * 
	 * @param controller
	 *            the controller
	 */
	public NavigationTableModel() {
		super();
	}

	@Override
	public int getRowCount() {
		return audioObjects != null ? audioObjects.size() : 0;
	}

	/**
	 * Gets the audio object at.
	 * 
	 * @param row
	 *            the row
	 * 
	 * @return the song at
	 */
	public IAudioObject getAudioObjectAt(final int row) {
		return audioObjects != null ? audioObjects.get(row) : null;
	}

	/**
	 * Gets the audio objects.
	 * 
	 * @return the audio objects
	 */
	public List<? extends IAudioObject> getAudioObjects() {
		return audioObjects;
	}

	/**
	 * Gets the songs at.
	 * 
	 * @param rows
	 *            the rows
	 * 
	 * @return the songs at
	 */
	public List<IAudioObject> getAudioObjectsAt(final int[] rows) {
		List<IAudioObject> result = new ArrayList<IAudioObject>();
		for (int element : rows) {
			result.add(getAudioObjectAt(element));
		}
		return result;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		IAudioObject audioObject = getAudioObjectAt(rowIndex);
		if (audioObject == null) {
			return null;
		}
		IColumn<?> c = getColumn(columnIndex);
		return c != null ? c.getValueFor(audioObject, rowIndex) : null;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}

	/**
	 * Sets the songs.
	 * 
	 * @param songs
	 *            the new songs
	 */
	public void setSongs(final List<? extends IAudioObject> songs) {
		this.audioObjects = songs;
		refresh(TableModelEvent.INSERT);
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex,
			final int columnIndex) {
		// Nothing to do
	}

	@Override
	public void sortByColumn(IColumn<?> column) {
		net.sourceforge.atunes.utils.Timer t = new net.sourceforge.atunes.utils.Timer();
		t.start();
		Collections.sort(this.audioObjects, column.getComparator());
		Logger.debug("Navigation table sort: ", t.stop(), " seconds");
	}
}
