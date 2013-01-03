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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Table model for artist albums
 * @author alex
 *
 */
class ContextAlbumsTableModel extends DefaultTableModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2224777412499576679L;
	
	private List<IAlbumInfo> albums;

    /**
     * @param albums
     */
    public void setAlbums(List<IAlbumInfo> albums) {
		this.albums = albums;
		fireTableDataChanged();
	}
    
    /**
     * Gets the album.
     * 
     * @param index
     *            the index
     * 
     * @return the album
     */
    public IAlbumInfo getAlbum(int index) {
        return albums.get(index);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return IAlbumInfo.class;
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
        return true;
    }
}
