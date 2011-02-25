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
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;

public final class ContextPanel extends JPanel {

    private static final long serialVersionUID = 707242790413122482L;

    public static final Dimension PREF_SIZE = new Dimension(230, 0);
    public static final Dimension MINIMUM_SIZE = new Dimension(170, 0);

    private JTabbedPane tabbedPane;

    private Map<net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel, Integer> panelsIndexes = new HashMap<net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel, Integer>();

    /**
     * Instantiates a new audio scrobbler panel.
     */
    public ContextPanel() {
        super(new BorderLayout());
        setPreferredSize(PREF_SIZE);
        setMinimumSize(MINIMUM_SIZE);
        setContent();
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Sets text of tabs
     * 
     * @param set
     */
    public void updateContextTabsText(List<AbstractContextPanel> panels) {
        int i = 0;
        for (net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel panel : panels) {
            tabbedPane.setTitleAt(i, panel.getTitle());
            i++;
        }
    }

    /**
     * Sets text of tabs
     * 
     * @param panels
     */
    public void updateContextTabsIcons(List<AbstractContextPanel> panels) {
        int i = 0;
        for (net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel panel : panels) {
            tabbedPane.setIconAt(i, panel.getIcon());
            i++;
        }
    }

    public void enableContextTabs(List<AbstractContextPanel> panels) {
        for (net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel panel : panels) {
            enableContextTab(panel);
        }
    }

    /**
     * Enables or disables the given panel
     * 
     * @param panel
     */
    private void enableContextTab(net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel panel) {
        tabbedPane.setEnabledAt(panelsIndexes.get(panel), panel.isEnabled());
        tabbedPane.getComponentAt(panelsIndexes.get(panel)).setEnabled(panel.isEnabled());
    }

    /**
     * Selects given tab index
     * 
     * @param index
     */
    public void setSelectedIndex(int index) {
        tabbedPane.setSelectedIndex(index);
    }

    /**
     * Returns selected tab index
     * 
     * @return
     */
    public int getSelectedIndex() {
        return tabbedPane.getSelectedIndex();
    }

    /**
     * Adds a new context panel
     * 
     * @param panel
     */
    public void addContextPanel(net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel panel) {
        tabbedPane.addTab(panel.getTitle(), panel.getIcon(), panel.getUIComponent());
        panelsIndexes.put(panel, tabbedPane.getTabCount() - 1);
        enableContextTab(panel);
    }

    /**
     * Removes a context panel
     * 
     * @param panel
     */
    public void removeContextPanel(net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel panel) {
        tabbedPane.remove(panelsIndexes.get(panel));
        panelsIndexes.remove(panel);
    }

    /**
     * @return the tabbedPane
     */
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}
