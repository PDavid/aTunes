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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.ParameterizedType;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;

/**
 * Renderer for context table rows
 * 
 * @author alex
 * 
 * @param <T>
 */
public abstract class ContextTableRowPanelRendererCode<T> extends
	AbstractTableCellRendererCode<JComponent, T> implements TableCellEditor {

    private final Class<?> clazz;

    private ContextTable table;

    private final ContextTableRowPanelFactory<T> factory;

    private String cacheKeyControl;

    private final Map<T, ContextTableRowPanel<T>> cachedPanels = new HashMap<T, ContextTableRowPanel<T>>();

    /**
     * @param lookAndFeel
     */
    @SuppressWarnings("unchecked")
    public ContextTableRowPanelRendererCode() {
	this.clazz = (Class<T>) ((ParameterizedType) getClass()
		.getGenericSuperclass()).getActualTypeArguments()[0];
	this.factory = new ContextTableRowPanelFactory<T>();
    }

    @Override
    public JComponent getComponent(final JComponent superComponent,
	    final JTable t, final T value, final boolean isSelected,
	    final boolean hasFocus, final int row, final int column) {
	if (value != null) {
	    if (cacheKeyControl != null
		    && !cacheKeyControl.equals(getCacheKeyControl(value))) {
		// Clean cached panels when similar artist changes
		cachedPanels.clear();
	    }

	    // Create if necessary
	    if (!cachedPanels.containsKey(value)) {
		cachedPanels.put(value,
			createPanel(superComponent, value, hasFocus));
	    }

	    // Get panel
	    ContextTableRowPanel<T> panel = cachedPanels.get(value);

	    // Update panel
	    panel.setColors(superComponent.getBackground(),
		    superComponent.getForeground());
	    panel.setFocus(hasFocus);

	    return panel;
	}
	return superComponent;
    }

    /**
     * Binds as renderer and editor to table
     * 
     * @param table
     */
    public void bind(final ContextTable table) {
	this.table = table;
	this.table.setDefaultRenderer(clazz, getLookAndFeel()
		.getTableCellRenderer(this));
	this.table.setDefaultEditor(clazz, this);
    }

    /**
     * @return
     */
    protected ContextTable getTable() {
	return table;
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table,
	    final Object value, final boolean isSelected, final int row,
	    final int column) {
	return table.getDefaultRenderer(clazz).getTableCellRendererComponent(
		table, value, isSelected, true, row, column);
    }

    @Override
    public void cancelCellEditing() {
    }

    @Override
    public boolean stopCellEditing() {
	return true;
    }

    @Override
    public Object getCellEditorValue() {
	return null;
    }

    @Override
    public boolean isCellEditable(final EventObject anEvent) {
	return true;
    }

    @Override
    public boolean shouldSelectCell(final EventObject anEvent) {
	return true;
    }

    @Override
    public void addCellEditorListener(final CellEditorListener l) {
    }

    @Override
    public void removeCellEditorListener(final CellEditorListener l) {
    }

    protected ContextTableRowPanel<T> getPanelForTableRenderer(
	    final ImageIcon image, final String text,
	    final Color backgroundColor, final Color foregroundColor,
	    final int imageMaxWidth, final int imageMaxHeight,
	    final boolean hasFocus) {

	return factory.getPanelForTableRenderer(this.getActions(), table,
		image, text, backgroundColor, foregroundColor, imageMaxWidth,
		imageMaxHeight, hasFocus);
    }

    /**
     * @return list of actions available in this row
     */
    public abstract List<ContextTableAction<T>> getActions();

    /**
     * @param object
     * @return cache key control value of this object
     */
    public abstract String getCacheKeyControl(T object);

    /**
     * @param superComponent
     * @param value
     * @param hasFocus
     * @return
     */
    public abstract ContextTableRowPanel<T> createPanel(
	    final JComponent superComponent, final T value,
	    final boolean hasFocus);

}
