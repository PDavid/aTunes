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
