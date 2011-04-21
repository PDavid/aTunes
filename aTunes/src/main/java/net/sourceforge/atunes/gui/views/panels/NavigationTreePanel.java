/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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
import java.awt.CardLayout;

import javax.swing.JPanel;

import net.sourceforge.atunes.kernel.modules.navigator.AbstractNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.utils.GuiUtils;

public final class NavigationTreePanel extends JPanel {

    private static final long serialVersionUID = -2900418193013495812L;

    private JPanel treePanel;
    
    /**
     * Instantiates a new navigation panel.
     */
    public NavigationTreePanel() {
        super(new BorderLayout(), true);
        addContent();
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        treePanel = new JPanel(new CardLayout());
        addTrees(treePanel);
        add(treePanel);

        // Apply component orientation to all popup menus
        for (AbstractNavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            GuiUtils.applyComponentOrientation(view.getTreePopupMenu());
        }
    }

    /**
     * Updates panel to show all trees
     */
    private void addTrees(JPanel treesContainer) {
        treesContainer.removeAll();

        for (AbstractNavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
        	treesContainer.add(view.getClass().getName(), view.getTreeScrollPane());
        }
    }

    /**
     * Updates trees
     */
    public void updateTrees() {
        addTrees(treePanel);
    }

	/**
	 * Shows tree view
	 * @param name
	 */
	public void showNavigationView(String name) {
		((CardLayout)treePanel.getLayout()).show(treePanel, name);
		
	}
}
