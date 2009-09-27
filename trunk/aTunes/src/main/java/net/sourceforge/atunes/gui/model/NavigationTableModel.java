/*
 * aTunes 2.0.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

/**
 * The Class NavigationTableModel.
 * 
 * @author fleax
 */
public class NavigationTableModel implements TableModel {

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

    /** The listeners. */
    private List<TableModelListener> listeners;

    /**
     * Instantiates a new navigation table model.
     * 
     * @param controller
     *            the controller
     */
    public NavigationTableModel() {
        songs = new ArrayList<AudioObject>();
        listeners = new ArrayList<TableModelListener>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.table.TableModel#addTableModelListener(javax.swing.event.
     * TableModelListener)
     */
    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return NavigationHandler.getInstance().getView(NavigationHandler.getInstance().getViewByName(ApplicationState.getInstance().getNavigationView()))
                .getNavigatorTableColumnClass(columnIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return NavigationHandler.getInstance().getView(NavigationHandler.getInstance().getViewByName(ApplicationState.getInstance().getNavigationView()))
                .getNavigatorTableColumnCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int columnIndex) {
        return NavigationHandler.getInstance().getView(NavigationHandler.getInstance().getViewByName(ApplicationState.getInstance().getNavigationView()))
                .getNavigatorTableColumnName(columnIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return songs.size();
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
        return songs.get(row);
    }

    /**
     * Gets the songs.
     * 
     * @return the songs
     */
    public List<AudioObject> getSongs() {
        return new ArrayList<AudioObject>(songs);
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

    /**
     * Gets the audio files at.
     * 
     * @param rows
     *            the rows
     * 
     * @return the songs at
     */
    public List<AudioFile> getAudioFilesAt(int[] rows) {
        List<AudioFile> result = new ArrayList<AudioFile>();
        for (int element : rows) {
            if (getSongAt(element) instanceof AudioFile) {
                result.add((AudioFile) getSongAt(element));
            }
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
        AudioObject audioObject = songs.get(rowIndex);

        return NavigationHandler.getInstance().getView(NavigationHandler.getInstance().getViewByName(ApplicationState.getInstance().getNavigationView())).getNavigatorTableValueAt(
                audioObject, columnIndex);

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
     * Refresh.
     */
    public void refresh() {
        TableModelEvent event = new TableModelEvent(this, 0, this.getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).tableChanged(event);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.table.TableModel#removeTableModelListener(javax.swing.event
     * .TableModelListener)
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    /**
     * Sets the songs.
     * 
     * @param songs
     *            the new songs
     */
    public void setSongs(List<AudioObject> songs) {
        this.songs = songs;
        TableModelEvent event = new TableModelEvent(this, -1, this.getRowCount() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).tableChanged(event);
        }
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

}
