/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public abstract class CommonTableModel implements TableModel {

    private List<TableModelListener> listeners;

    protected CommonTableModel() {
        listeners = new ArrayList<TableModelListener>();
    }

    /**
     * Adds a listener.
     * 
     * @param l
     *            the l
     */
    @Override
    public final void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    /**
     * Removes a listener.
     * 
     * @param l
     *            the l
     */
    @Override
    public final void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    /**
     * Refresh table.
     */
    public final void refresh(int eventType) {
        TableModelEvent event;
        event = new TableModelEvent(this, -1, -1, TableModelEvent.ALL_COLUMNS, eventType);

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).tableChanged(event);
        }
    }
}
