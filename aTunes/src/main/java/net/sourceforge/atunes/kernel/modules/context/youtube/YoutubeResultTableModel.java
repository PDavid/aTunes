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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Table model used in YouTube panel
 * 
 * @author Tobias Melcher
 * 
 */
public class YoutubeResultTableModel implements TableModel {

    /**
     * List of videos from a search at YouTube
     */
    private List<YoutubeResultEntry> entries;

    /**
     * Listeners of this table model
     */
    private List<TableModelListener> listeners;

    /**
     * Default constructor
     */
    public YoutubeResultTableModel(List<YoutubeResultEntry> entries) {
        this.entries = entries;
        listeners = new ArrayList<TableModelListener>();
    }

    /**
     * Add more results to this model. Fires a refresh
     * 
     * @param entriesToBeAdded
     */
    public void addEntries(List<YoutubeResultEntry> entriesToBeAdded) {
        entries.addAll(entriesToBeAdded);
        // Refresh model
        refresh();
    }

    /**
     * Removes all results from the model. Fires a refresh
     */
    public void clearEntries() {
        entries.clear();
        // Refresh model
        refresh();
    }

    /**
     * Returns an entry at given location
     * 
     * @param row
     * @return
     */
    public YoutubeResultEntry getEntry(int row) {
        return entries.get(row);
    }

    @Override
    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public Class<?> getColumnClass(int arg0) {
        if (arg0 == 0) {
            return YoutubeResultEntry.class;
        }
        return null;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int arg0) {
        return I18nUtils.getString("YOUTUBE_VIDEOS");
    }

    @Override
    public int getRowCount() {
        return entries != null ? entries.size() : 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return entries.get(rowIndex);
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        return true;
    }

    @Override
    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void setValueAt(Object arg0, int arg1, int arg2) {
        // Nothing to do
    }

    /**
     * Fires a refresh by calling all table model listeners
     */
    private void refresh() {
        TableModelEvent event;
        event = new TableModelEvent(this, -1, -1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).tableChanged(event);
        }
    }

}
