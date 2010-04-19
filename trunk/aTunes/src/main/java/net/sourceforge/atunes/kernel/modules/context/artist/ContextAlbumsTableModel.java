/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.utils.I18nUtils;

public class ContextAlbumsTableModel implements TableModel {

    private List<AlbumInfo> albums;
    private List<TableModelListener> listeners;

    /**
     * Instantiates a new audio scrobbler albums table model.
     */
    public ContextAlbumsTableModel(List<AlbumInfo> albums) {
        this.albums = albums;
        listeners = new ArrayList<TableModelListener>();
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    /**
     * Gets the album.
     * 
     * @param index
     *            the index
     * 
     * @return the album
     */
    public AlbumInfo getAlbum(int index) {
        return albums.get(index);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return AlbumInfo.class;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return I18nUtils.getString("ALBUMS");
    }

    @Override
    public int getRowCount() {
        return albums != null ? albums.size() : 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return albums.get(rowIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // Nothing to do
    }
}
