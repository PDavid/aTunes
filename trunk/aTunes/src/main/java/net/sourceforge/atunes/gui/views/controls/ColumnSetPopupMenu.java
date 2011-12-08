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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.AbstractCommonColumnModel;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IColumnSelectorDialog;
import net.sourceforge.atunes.utils.I18nUtils;

public class ColumnSetPopupMenu {

    private static final class ColumnSetTableHeaderMouseAdapter extends MouseAdapter {
        private final JPopupMenu rightMenu;
        private final JTable table;

        private ColumnSetTableHeaderMouseAdapter(JPopupMenu rightMenu, JTable table) {
            this.rightMenu = rightMenu;
            this.table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // Use right button to arrange columns
            if (GuiUtils.isSecondaryMouseButton(e)) {
                rightMenu.show(table.getTableHeader(), e.getX(), e.getY());
            }
        }
    }

    private static final class SelectColumnsActionListener implements ActionListener {
        private final AbstractCommonColumnModel model;

        private SelectColumnsActionListener(AbstractCommonColumnModel model) {
            this.model = model;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Show column selector
        	IColumnSelectorDialog selector = Context.getBean(IColumnSelectorDialog.class);
            selector.setColumnSetToSelect(model.getColumnSet());
            selector.showDialog();

            // Apply changes
            model.arrangeColumns(true);
        }
    }

    private JMenuItem arrangeColumns;

    /**
     * Adds a right-button popup menu to column set tables
     * 
     * @param table
     * @param model
     */
    public ColumnSetPopupMenu(final JTable table, final AbstractCommonColumnModel model) {
        final JPopupMenu rightMenu = new JPopupMenu();
        arrangeColumns = new JMenuItem(I18nUtils.getString("ARRANGE_COLUMNS"));
        rightMenu.add(arrangeColumns);
        arrangeColumns.addActionListener(new SelectColumnsActionListener(model));

        table.getTableHeader().addMouseListener(new ColumnSetTableHeaderMouseAdapter(rightMenu, table));
    }

    public void enableArrangeColumns(boolean enable) {
        arrangeColumns.setEnabled(enable);
    }
}
