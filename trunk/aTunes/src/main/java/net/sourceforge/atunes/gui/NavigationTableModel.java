/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.model.IAudioObject;

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

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
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
    public IAudioObject getAudioObjectAt(int row) {
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
    public List<IAudioObject> getAudioObjectsAt(int[] rows) {
        List<IAudioObject> result = new ArrayList<IAudioObject>();
        for (int element : rows) {
            result.add(getAudioObjectAt(element));
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        IAudioObject audioObject = getAudioObjectAt(rowIndex);
        if (audioObject == null) {
            return null;
        }
        return getColumn(columnIndex).getValueFor(audioObject);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Sets the songs.
     * 
     * @param songs
     *            the new songs
     */
    public void setSongs(List<? extends IAudioObject> songs) {
        this.audioObjects = songs;
        refresh(TableModelEvent.INSERT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // Nothing to do
    }

    @Override
    public void sort(Comparator<IAudioObject> comparator) {
        Collections.sort(this.audioObjects, comparator);
        refresh(TableModelEvent.UPDATE);
    }
}
