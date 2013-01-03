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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.model.ILyricsEngineInfo;

class LyricsEnginesTableModel implements TableModel {

    private final JTable enginesTable;

    /** The lyrics engines info. */
    private List<ILyricsEngineInfo> lyricsEnginesInfo;

    /** The listeners. */
    private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();

    /**
     * Instantiates a new lyrics engines info model.
     * 
     * @param enginesTable
     */
    LyricsEnginesTableModel(final JTable enginesTable) {
        this.enginesTable = enginesTable;
    }

    @Override
    public void addTableModelListener(final TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return columnIndex == 0 ? Boolean.class : String.class;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(final int column) {
        return "";
    }

    @Override
    public int getRowCount() {
        if (this.lyricsEnginesInfo != null) {
    	return this.lyricsEnginesInfo.size();
        }
        return 0;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (columnIndex == 0) {
    	return lyricsEnginesInfo.get(rowIndex).isEnabled();
        }
        return lyricsEnginesInfo.get(rowIndex).getName();
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return columnIndex == 0;
    }

    public void moveDown(final int columnPos) {

        Collections.swap(lyricsEnginesInfo, columnPos, columnPos + 1);

        TableModelEvent event;
        event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS,
    	    TableModelEvent.UPDATE);
        for (int i = 0; i < listeners.size(); i++) {
    	listeners.get(i).tableChanged(event);
        }
        enginesTable.getColumnModel().getColumn(0).setMaxWidth(20);
        enginesTable.getSelectionModel().setSelectionInterval(
    	    columnPos + 1, columnPos + 1);

    }

    public void moveUp(final int columnPos) {

        Collections.swap(lyricsEnginesInfo, columnPos, columnPos - 1);

        TableModelEvent event;
        event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS,
    	    TableModelEvent.UPDATE);
        for (int i = 0; i < listeners.size(); i++) {
    	listeners.get(i).tableChanged(event);
        }
        enginesTable.getColumnModel().getColumn(0).setMaxWidth(20);
        enginesTable.getSelectionModel().setSelectionInterval(
    	    columnPos - 1, columnPos - 1);
    }

    @Override
    public void removeTableModelListener(final TableModelListener l) {
        listeners.remove(l);
    }

    public void setLyricsEnginesInfo(
    	final List<ILyricsEngineInfo> lyricsEnginesInfo) {
        this.lyricsEnginesInfo = new ArrayList<ILyricsEngineInfo>(
    	    lyricsEnginesInfo);
    }

    public List<ILyricsEngineInfo> getLyricsEnginesInfo() {
        return lyricsEnginesInfo;
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex,
    	final int columnIndex) {
        if (columnIndex == 0) {
    	lyricsEnginesInfo.get(rowIndex).setEnabled((Boolean) aValue);
        }
    }

}