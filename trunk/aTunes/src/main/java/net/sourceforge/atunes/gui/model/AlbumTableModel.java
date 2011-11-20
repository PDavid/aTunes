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

package net.sourceforge.atunes.gui.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.AlbumImageSmallIcon;
import net.sourceforge.atunes.kernel.modules.columns.TypeColumn;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILookAndFeel;

/**
 * The Class AlbumTableModel.
 * 
 * @author encestre
 */
public final class AlbumTableModel extends AbstractColumnSetTableModel {

   
    /** The albums. */
    private List<Album> albums;
	private ILookAndFeel lookAndFeel;
    

    /**
     * Instantiates a new navigation table model.
     * 
     * @param lookAndFeel
     */
    public AlbumTableModel(ILookAndFeel lookAndFeel) {
        super();
        this.lookAndFeel = lookAndFeel;
    }

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
    public Album getAlbumAt(int row) {
        return albums != null ? albums.get(row) : null;
    }

    /**
     * Gets the albums.
     * 
     * @return the albums
     */
    public List<Album> getAlbums() {
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
    public List<Album> getAlbumsAt(int[] rows) {
        List<Album> result = new ArrayList<Album>();
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
    public Object getValueAt(int rowIndex, int columnIndex) {
    	Album album = getAlbumAt(rowIndex);
    	if (album == null) {
    		return null;
    	} else if (getColumn(columnIndex) instanceof TypeColumn) {
    		return Context.getBean(AlbumImageSmallIcon.class).getColorMutableIcon();
    	}
    	return getColumn(columnIndex).getValueFor(album.getAudioObjects().get(0));
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
     * Sets the albums.
     * 
     * @param albums
     *            
     */
    public void setAlbums(List<Album> albums) {
        this.albums = albums;
        refresh(TableModelEvent.INSERT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

	@Override
	public void sort(Comparator<IAudioObject> comparator) {
	}
}
