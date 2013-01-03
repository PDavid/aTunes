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

package net.sourceforge.atunes.kernel.modules.context.similar;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class ContextArtistsTableModel.
 */
public class SimilarArtistsTableModel extends DefaultTableModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1310120443913287609L;
	
	/** The artists. */
    private List<IArtistInfo> artists;

    /**
     * @param artists
     */
    public void setArtists(List<IArtistInfo> artists) {
		this.artists = artists;
		fireTableDataChanged();
	}
    
    /**
     * Gets the artist.
     * 
     * @param index
     *            the index
     * 
     * @return the artist
     */
    public IArtistInfo getArtist(int index) {
        return artists.get(index);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return IArtistInfo.class;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return I18nUtils.getString("SIMILAR_ARTISTS");
        }
        return "";
    }

    @Override
    public int getRowCount() {
        return artists != null ? artists.size() : 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return artists.get(rowIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; // For context button
    }
}
