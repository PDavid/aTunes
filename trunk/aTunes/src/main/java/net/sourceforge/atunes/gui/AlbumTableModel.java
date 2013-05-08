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
import java.util.List;

import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IColumn;

/**
 * The Class AlbumTableModel.
 * 
 * @author encestre
 */
public final class AlbumTableModel extends AbstractColumnSetTableModel {

	/** The albums. */
	private List<IAlbum> albums;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return albums != null ? albums.size() : 0;
	}

	/**
	 * Gets the album at.
	 * 
	 * @param row
	 *            the row
	 * 
	 * @return the album at
	 */
	public IAlbum getAlbumAt(final int row) {
		return albums != null ? albums.get(row) : null;
	}

	/**
	 * Gets the albums.
	 * 
	 * @return the albums
	 */
	public List<IAlbum> getAlbums() {
		return albums;
	}

	/**
	 * Gets the album at.
	 * 
	 * @param rows
	 *            the rows
	 * 
	 * @return the album at
	 */
	public List<IAlbum> getAlbumsAt(final int[] rows) {
		List<IAlbum> result = new ArrayList<IAlbum>();
		for (int element : rows) {
			result.add(albums.get(element));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		IAlbum album = getAlbumAt(rowIndex);
		if (album == null) {
			return null;
		}
		IColumn<?> c = getColumn(columnIndex);
		return c != null ? c.getValueFor(album.getAudioObjects().get(0),
				rowIndex) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}

	/**
	 * Sets the albums.
	 * 
	 * @param albums
	 * 
	 */
	public void setAlbums(final List<IAlbum> albums) {
		this.albums = albums;
		refresh(TableModelEvent.INSERT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(final Object aValue, final int rowIndex,
			final int columnIndex) {
	}

	@Override
	public void sortByColumn(IColumn<?> column) {
	}
}
