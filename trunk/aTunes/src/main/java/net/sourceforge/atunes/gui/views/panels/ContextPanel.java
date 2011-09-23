/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.utils.Logger;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

public final class ContextPanel extends JPanel {

	private static final long serialVersionUID = 707242790413122482L;

    private JComboBox contextSelector;
    
    private PopUpButton options;
    
    private JPanel container;

    private List<AbstractContextPanel> panels = new ArrayList<AbstractContextPanel>();
    
    private List<AbstractContextPanel> visiblePanels = new ArrayList<AbstractContextPanel>();

    /**
     * Instantiates a new context panel
     */
    public ContextPanel() {
        super(new GridBagLayout());
        setContent();
    }

    /**
     * Sets the content.
     */
    private void setContent() {
    	contextSelector = new JComboBox(); 
    	options = new PopUpButton(PopUpButton.BOTTOM_RIGHT);
    	container = new JPanel(new CardLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 0;
    	c.fill = GridBagConstraints.BOTH;
    	add(options, c);
    	c.gridx = 1;
    	c.weightx = 1;
        add(contextSelector, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        add(container, c);
    }

    public void updateContextTabs() {
    	AbstractContextPanel selectedPanel = contextSelector.getSelectedItem() != null ? (AbstractContextPanel) contextSelector.getSelectedItem() : null;
    	container.removeAll();
    	visiblePanels.clear();
        for (AbstractContextPanel panel : panels) {
        	if (panel.isVisible()) {
        		visiblePanels.add(panel);
        		container.add(panel.getContextPanelName(), panel.getUIComponent());
        		panel.getUIComponent().setEnabled(panel.isEnabled());
        	}
        }
        contextSelector.setModel(new ListComboBoxModel<AbstractContextPanel>(visiblePanels));
        contextSelector.setSelectedIndex(selectedPanel != null ? visiblePanels.indexOf(selectedPanel) : 0);
        ((CardLayout)container.getLayout()).show(container, selectedPanel != null ? selectedPanel.getContextPanelName() : visiblePanels.get(0).getContextPanelName());
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
	 * @param panel
	 */
	public void showContextPanel(AbstractContextPanel panel) {
		final AbstractContextPanel source = panel != null ? panel : visiblePanels.get(0);
		options.removeAllItems();
		options.setEnabled(source != null && !source.getOptions().isEmpty());
		if (source != null) {
			List<Component> components = source.getOptions();
			if (components != null) {
				for (Component c : components) {
					options.add(c);
				}
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					((CardLayout)container.getLayout()).show(container, source.getContextPanelName());		
				}
			});
		}
	}
	
    /**
     * Returns selected tab index
     * 
     * @return
     */
    public AbstractContextPanel getSelectedContextTab() {
    	return (AbstractContextPanel) contextSelector.getSelectedItem();
    }
    
    /**
	 * @return the contextSelector
	 */
	public JComboBox getContextSelector() {
		return contextSelector;
	}

	/**
	 * @param selectedContextTab
	 */
	public void setSelectedContextTab(String selectedContextTab) {
		Logger.debug("Setting context view: ", selectedContextTab);
		AbstractContextPanel panel = null;
		if (selectedContextTab != null) {
			for (AbstractContextPanel p : panels) {
				if (p.getContextPanelName().equals(selectedContextTab)) {
					panel = p;
					break;
				}
			}
		}
		contextSelector.setSelectedIndex(panel != null ? visiblePanels.indexOf(panel) : 0); // Show panel or first one
		showContextPanel(panel);
	}
}
