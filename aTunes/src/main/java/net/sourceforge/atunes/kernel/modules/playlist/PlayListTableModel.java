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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.Comparator;

import net.sourceforge.atunes.gui.model.AbstractColumnSetTableModel;
import net.sourceforge.atunes.kernel.modules.columns.PlayListColumnSet;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.model.AudioObject;

/**
 * The playlist table model.
 * 
 * @author fleax
 */
public class PlayListTableModel extends AbstractColumnSetTableModel {

    /**
     * Reference to the visible play list
     */
    private PlayList visiblePlayList = null;

    /**
     * Constructor.
     * 
     * @param table
     *            the table
     */
    public PlayListTableModel() {
        super(PlayListColumnSet.getInstance());
    }

    /**
     * Return row count.
     * 
     * @return the row count
     */
    @Override
    public int getRowCount() {
        if (visiblePlayList != null) {
            return visiblePlayList.size();
        }
        return 0;
    }

    /**
     * Returns value of a row and column.
     * 
     * @param rowIndex
     *            the row index
     * @param colIndex
     *            the col index
     * 
     * @return the value at
     */
    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        // Call Column method to get value from AudioFile
        if (visiblePlayList != null) {
            return getColumn(colIndex).getValueFor(visiblePlayList.get(rowIndex));
        }
        return null;
    }

    /**
     * Returns if a cell is editable.
     * 
     * @param rowIndex
     *            the row index
     * @param columnIndex
     *            the column index
     * 
     * @return true, if checks if is cell editable
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return getColumn(columnIndex).isEditable();
    }

    /**
     * Sets value for a cell
     * 
     * @param aValue
     *            the a value
     * @param rowIndex
     *            the row index
     * @param columnIndex
     *            the column index
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // AudioFile
        AudioObject audioObject = visiblePlayList.get(rowIndex);

        // Call column set value
        getColumn(columnIndex).setValueFor(audioObject, aValue);

        // After changing audio object refresh playlist, as the same object can be duplicated
        PlayListHandler.getInstance().refreshPlayList();

        // Mark repository as dirty
        RepositoryHandler.getInstance().getRepository().setDirty(true);
    }

    /**
     * @param visiblePlayList
     *            the visiblePlayList to set
     */
    public void setVisiblePlayList(PlayList visiblePlayList) {
        this.visiblePlayList = visiblePlayList;
    }

    @Override
    public void sort(Comparator<AudioObject> comparator) {
        // If comparator is null, do nothing
        if (comparator == null) {
            return;
        }

        // If current play list is empty, don't sort
        if (visiblePlayList == null || visiblePlayList.isEmpty()) {
            return;
        }

        visiblePlayList.sort(comparator);
    }
}
