/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.AbstractNavigationView;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.GuiUtils;

public final class NavigationTreePanel extends JPanel {

    private static final long serialVersionUID = -2900418193013495812L;

    /** The tabbed pane. */
    private JTabbedPane tabbedPane;

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
        JPanel treePanel = new JPanel(new BorderLayout());

        // Create tabbed pane. Set tab position from configuration
        tabbedPane = new JTabbedPane(ApplicationState.getInstance().isShowNavigatorTabsAtLeft() ? SwingConstants.LEFT : SwingConstants.TOP);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());

        // Add tabs
        addTabs();

        // Set navigator tabs text based on configuration 
        setNavigatorTabsText(ApplicationState.getInstance().isShowNavigatorTabsText());

        treePanel.add(tabbedPane, BorderLayout.CENTER);

        add(treePanel);

        // Apply component orientation to all popup menus
        for (AbstractNavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            GuiUtils.applyComponentOrientation(view.getTreePopupMenu());
        }
    }

    /**
     * Gets the tabbed pane.
     * 
     * @return the tabbed pane
     */
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     * Updates tabbed pane to show all tabs
     */
    private void addTabs() {
        tabbedPane.removeAll();

        for (AbstractNavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            tabbedPane.addTab(view.getTitle(), view.getIcon(), view.getTreeScrollPane(), view.getTooltip());
        }
    }

    /**
     * Shows or hides tabbed pane text
     * 
     * @param set
     */
    public void setNavigatorTabsText(boolean set) {
        int i = 0;
        for (AbstractNavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            tabbedPane.setTitleAt(i++, set ? view.getTitle() : null);
        }
    }

    /**
     * Updates tabs keeping selected tab view
     */
    public void updateTabs() {
        int selectedTab = tabbedPane.getSelectedIndex();
        addTabs();
        setNavigatorTabsText(ApplicationState.getInstance().isShowNavigatorTabsText());
        if (tabbedPane.getTabCount() > selectedTab) {
            tabbedPane.setSelectedIndex(selectedTab);
        } else {
            tabbedPane.setSelectedIndex(0);
        }
    }
}
