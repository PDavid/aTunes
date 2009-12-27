/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.model.CommonColumnModel;
import net.sourceforge.atunes.gui.views.dialogs.ColumnSetSelectorDialog;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class ColumnSetPopupMenu {

	private JMenuItem arrangeColumns;
	
	/**
	 * Adds a right-button popup menu to column set tables
	 * @param table
	 * @param model
	 */
	public ColumnSetPopupMenu(final JTable table, final CommonColumnModel model) {
        final JPopupMenu rightMenu = new JPopupMenu();
        arrangeColumns = new JMenuItem(I18nUtils.getString("ARRANGE_COLUMNS"));
        rightMenu.add(arrangeColumns);
        arrangeColumns.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectColumns(model);
			}
		});
        
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Use right button to arrange columns
                if (e.getButton() == MouseEvent.BUTTON3) {
                	rightMenu.show(table.getTableHeader(), e.getX(), e.getY());
                }
            }
        });
	}
	
	/**
	 * Opens selection dialog and updates model
	 * @param model
	 */
	public static void selectColumns(CommonColumnModel model) {
		// Show column selector
		ColumnSetSelectorDialog selector = GuiHandler.getInstance().getColumnSelector();
		selector.setColumnSet(model.getColumnSet());
		selector.setVisible(true);

		// Apply changes
		model.arrangeColumns(true);
	}
	
	public void enableArrangeColumns(boolean enable) {
		arrangeColumns.setEnabled(enable);
	}
	
	
}
