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
package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.views.controls.NavigationTable;

public final class NavigationTablePanel extends JPanel {

    private static final long serialVersionUID = -2900418193013495812L;

    /** The navigation table. */
    private NavigationTable navigationTable;

    /** The navigation table button panel. */
    private JPanel navigationTableButtonPanel;

    /**
     * The table filter
     */
    private FilterPanel tableFilterPanel;

    /**
     * Instantiates a new navigation panel.
     */
    public NavigationTablePanel() {
        super(new BorderLayout(), true);
        addContent();
    }

    /**
     * Adds the content.
     */
    private void addContent() {

        navigationTable = new NavigationTable();
        navigationTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Disable autoresize, as we will control it
        navigationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane2 = new JScrollPane(navigationTable);

        navigationTableButtonPanel = new JPanel(new GridBagLayout());

        tableFilterPanel = new FilterPanel();
        tableFilterPanel.setVisible(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        navigationTableButtonPanel.add(tableFilterPanel, c);

        add(scrollPane2, BorderLayout.CENTER);
        add(navigationTableButtonPanel, BorderLayout.SOUTH);
    }

    /**
     * Gets the navigation table.
     * 
     * @return the navigation table
     */
    public JTable getNavigationTable() {
        return navigationTable;
    }

    /**
     * @return the tableFilterPanel
     */
    public FilterPanel getTableFilterPanel() {
        return tableFilterPanel;
    }
}
