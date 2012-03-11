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
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.model.ILookAndFeel;

public abstract class ContextTableRowPanel<T> extends AbstractTableCellRendererCode<JComponent, T> implements TableCellEditor {
	
	private Class<?> clazz;
	
	protected ContextTable table;
	
	@SuppressWarnings("unchecked")
	public ContextTableRowPanel(ILookAndFeel lookAndFeel) {
		super(lookAndFeel);
		this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	/**
	 * Binds as renderer and editor to table
	 * @param table
	 */
	public void bind(ContextTable table) {
		this.table = table;
        this.table.setDefaultRenderer(clazz, getLookAndFeel().getTableCellRenderer(this));
        this.table.setDefaultEditor(clazz, this);
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		return table.getDefaultRenderer(clazz).getTableCellRendererComponent(table, value, isSelected, true, row, column);
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
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
	}
	    
	protected JPanel getPanelForTableRenderer(ImageIcon image, 
		     String text, 
		     Color backgroundColor, 
		     Color foregroundColor, 
		     int imageMaxWidth, 
		     int imageMaxHeight,
		     boolean hasFocus) {
		
		return new ContextTableRowPanelFactory<T>().getPanelForTableRenderer(this.getActions(), table, image, text, backgroundColor, foregroundColor, imageMaxWidth, imageMaxHeight, hasFocus);
	}
	
    @Override
    public abstract JComponent getComponent(JComponent superComponent, JTable t, T value, boolean isSelected, boolean hasFocus, int row, int column);
    
    public abstract List<ContextTableAction<T>> getActions();
}
