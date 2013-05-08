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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnModel;
import net.sourceforge.atunes.model.IColumnSetPopupAction;
import net.sourceforge.atunes.model.IColumnSetPopupMenu;
import net.sourceforge.atunes.model.IColumnSetTableModel;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Popup menu for column sets
 * 
 * @author alex
 * 
 */
public class ColumnSetPopupMenu implements IColumnSetPopupMenu {

	private JMenuItem arrangeColumns;

	private JPopupMenu menu;

	private JTable table;

	private IColumnModel model;

	private IColumn<?> columnSelected;

	private IColumnSetTableModel tableModel;

	private IOSManager osManager;

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * Adds a right-button popup menu to column set tables
	 * 
	 * @param table
	 * @param model
	 * @param tableModel
	 */
	void bindTo(final JTable table, final IColumnModel model,
			final IColumnSetTableModel tableModel) {
		this.table = table;
		this.model = model;
		this.tableModel = tableModel;
		this.menu = new JPopupMenu();
		this.arrangeColumns = new JMenuItem(
				I18nUtils.getString("ARRANGE_COLUMNS"));
		menu.add(this.arrangeColumns);
		this.arrangeColumns.addActionListener(new SelectColumnsActionListener(
				model, dialogFactory));
		table.getTableHeader().addMouseListener(
				new ColumnSetTableHeaderMouseAdapter(this, osManager));
	}

	@Override
	public void enableArrangeColumns(final boolean enable) {
		this.arrangeColumns.setEnabled(enable);
	}

	@Override
	public void show(int x, int y) {
		int columnIndex = this.table.getColumnModel().getColumnIndexAtX(x);
		if (columnIndex != -1) {
			this.columnSelected = this.model.getColumnSet().getColumn(
					this.model.getColumnSet().getColumnId(columnIndex));
			this.menu.show(this.table.getTableHeader(), x, y);
		}
	}

	@Override
	public void addAction(final IColumnSetPopupAction action) {
		JMenuItem item = new JMenuItem(action.getText());
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (ColumnSetPopupMenu.this.columnSelected != null) {
					action.executeAction(columnSelected, model, tableModel);
				}
			}
		});
		menu.add(item);
	}
}
