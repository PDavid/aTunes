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

package net.sourceforge.atunes.kernel.modules.context.album;

import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.kernel.modules.context.ITrackTableModel;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Table model to show album tracks
 * @author alex
 *
 */
class ContextTracksTableModel extends DefaultTableModel implements ITrackTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7703905638405573814L;

	private IAlbumInfo album;

	/**
	 * @param album
	 */
	public void setAlbum(final IAlbumInfo album) {
		this.album = album;
		fireTableDataChanged();
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return columnIndex == 0 ? Integer.class : ITrackInfo.class;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(final int columnIndex) {
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
	@Override
	public ITrackInfo getTrack(final int index) {
		return album != null ? album.getTracks().get(index) : null;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		if (columnIndex == 0) {
			return StringUtils.getString(rowIndex + 1, ".");
		}
		return getTrack(rowIndex);
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}
}
