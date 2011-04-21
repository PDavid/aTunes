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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

public final class ContextPanel extends JPanel {

	private static final long serialVersionUID = 707242790413122482L;

    private JComboBox contextSelector;
    
    private JPanel container;

    private List<AbstractContextPanel> panels = new ArrayList<AbstractContextPanel>();

    /**
     * Instantiates a new context panel
     */
    public ContextPanel() {
        super(new BorderLayout());
        setContent();
    }

    /**
     * Sets the content.
     */
    private void setContent() {
    	JPanel panel = new JPanel(new BorderLayout());
    	contextSelector = new JComboBox(); 
    	container = new JPanel(new CardLayout());
        panel.add(contextSelector, BorderLayout.NORTH);
        panel.add(container, BorderLayout.CENTER);
        add(panel);
    }

    public void updateContextTabs() {
    	container.removeAll();
    	List<AbstractContextPanel> visiblePanels = new ArrayList<AbstractContextPanel>();
        for (AbstractContextPanel panel : panels) {
        	if (panel.isVisible()) {
        		visiblePanels.add(panel);
        		container.add(panel.getContextPanelName(), panel.getUIComponent());
        		panel.getUIComponent().setEnabled(panel.isEnabled());
        	}
        }
        contextSelector.setModel(new ListComboBoxModel<AbstractContextPanel>(visiblePanels));
    }

    /**
     * Adds a new context panel
     * 
     * @param panel
     */
    public void addContextPanel(AbstractContextPanel panel) {
        panels.add(panel);
        updateContextTabs();
    }

    /**
     * Removes a context panel
     * 
     * @param panel
     */
    public void removeContextPanel(AbstractContextPanel panel) {
        panels.remove(panel);
        updateContextTabs();
    }

	/**
	 * Shows context panel
	 * @param source
	 */
	public void showContextPanel(AbstractContextPanel source) {
		((CardLayout)container.getLayout()).show(container, source.getContextPanelName());		
	}
	
    /**
     * Selects given tab index
     * 
     * @param index
     */
    public void setSelectedIndex(int index) {
    	contextSelector.setSelectedIndex(index);
    }

    /**
     * Returns selected tab index
     * 
     * @return
     */
    public int getSelectedIndex() {
    	return contextSelector.getSelectedIndex();
    }
    
    /**
	 * @return the contextSelector
	 */
	public JComboBox getContextSelector() {
		return contextSelector;
	}
}
