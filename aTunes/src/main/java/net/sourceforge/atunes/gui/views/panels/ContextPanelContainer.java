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

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.IContextPanelsContainer;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.Logger;

public final class ContextPanelContainer extends JPanel implements IContextPanelsContainer {

	private static final long serialVersionUID = 707242790413122482L;

    private ColorMutableIconToggleButtonFlowPanel contextSelector;
    
    private PopUpButton options;
    
    private JPanel container;

    private List<IContextPanel> panels = new ArrayList<IContextPanel>();
    
    private List<IContextPanel> visiblePanels = new ArrayList<IContextPanel>();

    private ILookAndFeelManager lookAndFeelManager;
    
    /**
     * Instantiates a new context panel
     */
    public ContextPanelContainer() {
        super(new GridBagLayout());
    }
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

    /**
     * Sets the content.
     * @param lookAndFeelManager 
     */
    public void initialize() {
    	contextSelector = new ColorMutableIconToggleButtonFlowPanel(lookAndFeelManager); 
    	options = new PopUpButton(PopUpButton.BOTTOM_RIGHT, lookAndFeelManager);
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

    @Override
    public JPanel getSwingComponent() {
    	return this;
    }
    
    @Override
	public void updateContextPanels() {
    	IContextPanel selectedPanel = contextSelector.getSelectedItem() != null ? (IContextPanel) contextSelector.getSelectedItem() : null;
    	container.removeAll();
    	visiblePanels.clear();
    	contextSelector.clear();
    	for (final IContextPanel panel : panels) {
    		if (panel.isVisible()) {
    			visiblePanels.add(panel);
    			container.add(panel.getContextPanelName(), panel.getUIComponent(lookAndFeelManager.getCurrentLookAndFeel()));
    			panel.getUIComponent(lookAndFeelManager.getCurrentLookAndFeel()).setEnabled(panel.isEnabled());

    			contextSelector.addButton(panel.getContextPanelName(), panel.getIcon(), panel.getTitle(), panel.getAction(), panel);
    		}
    	}
    	contextSelector.setSelectedButton(selectedPanel != null ? selectedPanel.getContextPanelName() : visiblePanels.get(0).getContextPanelName());
    	((CardLayout)container.getLayout()).show(container, selectedPanel != null ? selectedPanel.getContextPanelName() : visiblePanels.get(0).getContextPanelName());
    }

    @Override
	public void addContextPanel(IContextPanel panel) {
        panels.add(panel);
        updateContextPanels();
    }

    @Override
	public void removeContextPanel(IContextPanel panel) {
        panels.remove(panel);
        updateContextPanels();
    }

	@Override
	public void showContextPanel(IContextPanel panel) {
		final IContextPanel source = panel != null ? panel : visiblePanels.get(0);
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
	
    @Override
	public IContextPanel getSelectedContextPanel() {
    	return (IContextPanel) contextSelector.getSelectedItem();
    }
    
	@Override
	public void setSelectedContextPanel(String selectedContextTab) {
		Logger.debug("Setting context view: ", selectedContextTab);
		IContextPanel panel = null;
		if (selectedContextTab != null) {
			for (IContextPanel p : panels) {
				if (p.getContextPanelName().equals(selectedContextTab)) {
					panel = p;
					break;
				}
			}
		}
        contextSelector.setSelectedButton(panel != null ? panel.getContextPanelName() : visiblePanels.get(0).getContextPanelName()); // Show panel or first one
		showContextPanel(panel);
	}
}
