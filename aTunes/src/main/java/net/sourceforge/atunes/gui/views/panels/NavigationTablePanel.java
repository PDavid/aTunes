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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.views.controls.NavigationTable;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationTablePanel;
import net.sourceforge.atunes.model.ITable;

public final class NavigationTablePanel extends JPanel implements INavigationTablePanel {

    private static final long serialVersionUID = -2900418193013495812L;

    /** The navigation table. */
    private NavigationTable navigationTable;

    /**
     * Instantiates a new navigation panel.
     * @param lookAndFeelManager
     */
    public NavigationTablePanel(ILookAndFeelManager lookAndFeelManager) {
        super(new BorderLayout(), true);
        addContent(lookAndFeelManager);
    }

    /**
     * Adds the content.
     * @param lookAndFeelManager 
     */
    private void addContent(ILookAndFeelManager lookAndFeelManager) {
        navigationTable = new NavigationTable(lookAndFeelManager);
        navigationTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Disable autoresize, as we will control it
        navigationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollPane2 = lookAndFeelManager.getCurrentLookAndFeel().getTableScrollPane(navigationTable);
        scrollPane2.setOpaque(false); // for some look and feels
        add(scrollPane2, BorderLayout.CENTER);
    }

    /**
     * Gets the navigation table.
     * 
     * @return the navigation table
     */
    public ITable getNavigationTable() {
        return navigationTable;
    }
    
    @Override
    public JPanel getSwingComponent() {
    	return this;
    }
}
