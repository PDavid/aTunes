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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.AbstractCommonColumnModel;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Popup menu for column sets
 * 
 * @author alex
 * 
 */
public class ColumnSetPopupMenu {

	private final JMenuItem arrangeColumns;

	/**
	 * Adds a right-button popup menu to column set tables
	 * 
	 * @param table
	 * @param model
	 * @param dialogFactory
	 * @param osManager
	 */
	ColumnSetPopupMenu(final JTable table,
			final AbstractCommonColumnModel model,
			final IDialogFactory dialogFactory, final IOSManager osManager) {
		final JPopupMenu rightMenu = new JPopupMenu();
		this.arrangeColumns = new JMenuItem(
				I18nUtils.getString("ARRANGE_COLUMNS"));
		rightMenu.add(this.arrangeColumns);
		this.arrangeColumns.addActionListener(new SelectColumnsActionListener(
				model, dialogFactory));
		table.getTableHeader().addMouseListener(
				new ColumnSetTableHeaderMouseAdapter(rightMenu, table,
						osManager));
	}

	/**
	 * Allows columns to be arranged
	 * 
	 * @param enable
	 */
	public void enableArrangeColumns(final boolean enable) {
		this.arrangeColumns.setEnabled(enable);
	}
}
