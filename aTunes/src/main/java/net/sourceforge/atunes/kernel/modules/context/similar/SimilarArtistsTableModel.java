/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.context.similar;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class ContextArtistsTableModel.
 */
public class SimilarArtistsTableModel implements TableModel {

    /** The artists. */
    private List<ArtistInfo> artists;

    /** The listeners. */
    private List<TableModelListener> listeners;

    /**
     * Instantiates a new audio scrobbler artists table model.
     */
    public SimilarArtistsTableModel(List<ArtistInfo> artists) {
        this.artists = artists;
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

    /**
     * Gets the artist.
     * 
     * @param index
     *            the index
     * 
     * @return the artist
     */
    public ArtistInfo getArtist(int index) {
        return artists.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return ArtistInfo.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return I18nUtils.getString("SIMILAR_ARTISTS");
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return artists != null ? artists.size() : 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return artists.get(rowIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; // For context button
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
