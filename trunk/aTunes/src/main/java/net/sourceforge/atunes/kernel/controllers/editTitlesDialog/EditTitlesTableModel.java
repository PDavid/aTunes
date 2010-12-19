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

package net.sourceforge.atunes.kernel.controllers.editTitlesDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.utils.I18nUtils;

public final class EditTitlesTableModel extends AbstractTableModel {

    private static final long serialVersionUID = -4440078678648669115L;

    /** The files. */
    private List<AudioFile> files;

    /** The new values. */
    private Map<AudioFile, String> newValues;

    /** The listeners. */
    private List<TableModelListener> listeners;

    /**
     * Instantiates a new edits the titles table model.
     * 
     * @param files
     *            the files
     */
    public EditTitlesTableModel(List<AudioFile> files) {
        this.files = files;
        this.newValues = new HashMap<AudioFile, String>();
        this.listeners = new ArrayList<TableModelListener>();
    }

    /**
     * Adds the listener.
     * 
     * @param listener
     *            the listener
     */
    public void addListener(TableModelListener listener) {
        listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return I18nUtils.getString("FILE");
        }
        return I18nUtils.getString("TITLE");
    }

    /**
     * Gets the new values.
     * 
     * @return the new values
     */
    public Map<AudioFile, String> getNewValues() {
        return newValues;
    }

    @Override
    public int getRowCount() {
        return files.size();
    }

    @Override
    public String getValueAt(int rowIndex, int columnIndex) {
        AudioFile file = files.get(rowIndex);
        if (columnIndex == 0) {
            return file.getFile().getName();
        }
        if (newValues.containsKey(file)) {
            return newValues.get(file);
        }
        return file.getTitle();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }

    /**
     * Sets the titles.
     * 
     * @param titles
     *            the new titles
     */
    public void setTitles(List<String> titles) {
        for (int i = 0; i < files.size(); i++) {
            newValues.put(files.get(i), titles.get(i));
        }

        TableModelEvent event;
        event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).tableChanged(event);
        }
    }

    /**
     * Sets the value at.
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
        newValues.put(files.get(rowIndex), (String) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
