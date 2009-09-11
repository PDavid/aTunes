/*
 * aTunes 1.14.0
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
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

/**
 * The Class ContextPanel.
 */
public class ContextPanel extends JPanel {

    private static final long serialVersionUID = 707242790413122482L;

    /** The Constant PREF_SIZE. */
    public static final Dimension PREF_SIZE = new Dimension(230, 0);

    /** The Constant MINIMUM_SIZE. */
    public static final Dimension MINIMUM_SIZE = new Dimension(170, 0);

    /** The tabbed pane. */
    private JTabbedPane tabbedPane;

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
        
        // Add tabs
        for (net.sourceforge.atunes.kernel.modules.context.ContextPanel panel : ContextHandler.getInstance().getContextPanels()) {
        	tabbedPane.addTab(panel.getTitle(), panel.getIcon(), panel.getUIComponent());
        }
        
        // Set previous selected tab
        // IMPORTANT: this method must be called before adding change listener to avoid firing events when
        // UI is being created
        setSelectedIndex(ApplicationState.getInstance().getSelectedContextTab());

        // Add listener for tab changes
        tabbedPane.addChangeListener(new ChangeListener() {
        	@Override
        	public void stateChanged(ChangeEvent e) {
        		ContextHandler.getInstance().contextPanelChanged();
        	}
        });
    }
    
    /**
     * Sets text of tabs
     * @param set
     */
    public void updateContextTabsText() {
    	int i = 0;
    	for (net.sourceforge.atunes.kernel.modules.context.ContextPanel panel : ContextHandler.getInstance().getContextPanels()) {
    		tabbedPane.setTitleAt(i, panel.getTitle());
    		i++;
    	}
    }

    /**
     * Sets text of tabs
     * @param set
     */
    public void updateContextTabsIcons() {
    	int i = 0;
    	for (net.sourceforge.atunes.kernel.modules.context.ContextPanel panel : ContextHandler.getInstance().getContextPanels()) {
    		tabbedPane.setIconAt(i, panel.getIcon());
    		i++;
    	}
    }

    public void enableContextTabs() {
    	int i = 0;
    	for (net.sourceforge.atunes.kernel.modules.context.ContextPanel panel : ContextHandler.getInstance().getContextPanels()) {
    		tabbedPane.setEnabledAt(i, panel.isEnabled());
    		tabbedPane.getComponentAt(i).setEnabled(panel.isEnabled());
    		i++;
    	}
    }
	
	/**
	 * Selects given tab index
	 * @param index
	 */
	public void setSelectedIndex(int index) {
		tabbedPane.setSelectedIndex(index);
	}
	
	/**
	 * Returns selected tab index
	 * @return
	 */
	public int getSelectedIndex() {
		return tabbedPane.getSelectedIndex();
		
	}
}
