/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.context.album;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.TrackInfo;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class ContextTracksTableModel implements TableModel {

    private AlbumInfo album;
    private List<TableModelListener> listeners;

    /**
     * Instantiates a new audio scrobbler tracks table model.
     * 
     * @param album
     *            the album
     */
    public ContextTracksTableModel(AlbumInfo album) {
        this.album = album;
        listeners = new ArrayList<TableModelListener>();
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? Integer.class : String.class;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnIndex != 0 ? I18nUtils.getString("SONGS") : "";
    }

    @Override
    public int getRowCount() {
        return album != null ? album.getTracks().size() : 0;
    }

    /**
     * Gets the track.
     * 
     * @param index
     *            the index
     * 
     * @return the track
     */
    public TrackInfo getTrack(int index) {
        return album != null ? album.getTracks().get(index) : null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return StringUtils.getString(rowIndex + 1, ".");
        }
        return album != null ? album.getTracks().get(rowIndex).getTitle() : "";
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // Nothing to do
    }

}
