/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.gui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.kernel.modules.columns.NavigatorColumnSet;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationView;
import net.sourceforge.atunes.model.AudioObject;

/**
 * The Class NavigationTableModel.
 * 
 * @author fleax
 */
public final class NavigationTableModel extends ColumnSetTableModel {

    /**
     * Enum for properties
     */
    public enum Property {
        /**
         * No properties
         */
        NO_PROPERTIES,
        /**
         * Favorite
         */
        FAVORITE,
        /**
         * Not listened
         */
        NOT_LISTENED_ENTRY,
        /**
         * Downloaded
         */
        DOWNLOADED_ENTRY,
        /**
         * Old entry
         */
        OLD_ENTRY;
    }

    /** The songs. */
    private List<AudioObject> songs;

    /**
     * Instantiates a new navigation table model.
     * 
     * @param controller
     *            the controller
     */
    public NavigationTableModel() {
    	super(NavigatorColumnSet.getInstance());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
    	NavigationView view = NavigationHandler.getInstance().getCurrentView();
    	
    	// Use default navigator columns or custom defined by view
    	if (view.isUseDefaultNavigatorColumns()) {
    		return super.getColumnClass(columnIndex);
    	} else {
    		return view.getNavigatorTableColumnClass(columnIndex);
    	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
    	NavigationView view = NavigationHandler.getInstance().getCurrentView();
    	
    	// Use default navigator columns or custom defined by view
    	if (view.isUseDefaultNavigatorColumns()) {
    		return super.getColumnCount();
    	} else {
    		return view.getNavigatorTableColumnCount();
    	}    	
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int columnIndex) {
    	NavigationView view = NavigationHandler.getInstance().getCurrentView();
    	
    	// Use default navigator columns or custom defined by view
    	if (view.isUseDefaultNavigatorColumns()) {
    		return super.getColumnName(columnIndex);
    	} else {
    		return view.getNavigatorTableColumnName(columnIndex);
    	}    	    	
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return songs != null ? songs.size() : 0;
    }

    /**
     * Gets the song at.
     * 
     * @param row
     *            the row
     * 
     * @return the song at
     */
    public AudioObject getSongAt(int row) {
        return songs != null ? songs.get(row) : null;
    }

    /**
     * Gets the songs.
     * 
     * @return the songs
     */
    public List<AudioObject> getSongs() {
        return songs;
    }

    /**
     * Gets the songs at.
     * 
     * @param rows
     *            the rows
     * 
     * @return the songs at
     */
    public List<AudioObject> getAudioObjectsAt(int[] rows) {
        List<AudioObject> result = new ArrayList<AudioObject>();
        for (int element : rows) {
            result.add(getSongAt(element));
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
        AudioObject audioObject = getSongAt(rowIndex);

        if (audioObject == null) {
        	return null;
        }
        
    	NavigationView view = NavigationHandler.getInstance().getCurrentView();
    	
    	// Use default navigator columns or custom defined by view
    	if (view.isUseDefaultNavigatorColumns()) {
    		return getColumn(columnIndex).getValueFor(audioObject);
    	} else {
    		return view.getNavigatorTableValueAt(audioObject, columnIndex);
    	}
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
    public void setSongs(List<AudioObject> songs) {
        this.songs = songs;
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
    public void sort(Comparator<AudioObject> comparator) {
    	Collections.sort(this.songs, comparator);
    	refresh(TableModelEvent.UPDATE);
    }
}
