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
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.IContextPanelsContainer;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.Logger;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

public final class ContextPanelContainer extends JPanel implements IContextPanelsContainer {

	private static final long serialVersionUID = 707242790413122482L;

    private JComboBox contextSelector;
    
    private PopUpButton options;
    
    private JPanel container;

    private List<IContextPanel> panels = new ArrayList<IContextPanel>();
    
    private List<IContextPanel> visiblePanels = new ArrayList<IContextPanel>();

    private ILookAndFeelManager lookAndFeelManager;
    
    /**
     * Instantiates a new context panel
     * @param lookAndFeelManager
     */
    public ContextPanelContainer(ILookAndFeelManager lookAndFeelManager) {
        super(new GridBagLayout());
        this.lookAndFeelManager = lookAndFeelManager;
        setContent(lookAndFeelManager);
    }

    /**
     * Sets the content.
     * @param lookAndFeelManager 
     */
    private void setContent(final ILookAndFeelManager lookAndFeelManager) {
    	contextSelector = new JComboBox(); 
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
        
		if (lookAndFeelManager.getCurrentLookAndFeel().customComboBoxRenderersSupported()) {
			contextSelector.setRenderer(lookAndFeelManager.getCurrentLookAndFeel().getListCellRenderer(new AbstractListCellRendererCode() {

				@Override
				public JComponent getComponent(JComponent superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					((JLabel)superComponent).setIcon(((IContextPanel)value).getIcon().getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForColorMutableIcon(superComponent, isSelected || cellHasFocus)));
					((JLabel)superComponent).setText(((IContextPanel)value).getTitle());
					return superComponent;
				}
			}));
		}
    }

    @Override
    public JComponent getSwingComponent() {
    	return this;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.panels.IContextPanelsContainer#updateContextTabs()
	 */
    @Override
	public void updateContextPanels() {
    	IContextPanel selectedPanel = contextSelector.getSelectedItem() != null ? (IContextPanel) contextSelector.getSelectedItem() : null;
    	container.removeAll();
    	visiblePanels.clear();
        for (IContextPanel panel : panels) {
        	if (panel.isVisible()) {
        		visiblePanels.add(panel);
        		container.add(panel.getContextPanelName(), panel.getUIComponent(lookAndFeelManager.getCurrentLookAndFeel()));
        		panel.getUIComponent(lookAndFeelManager.getCurrentLookAndFeel()).setEnabled(panel.isEnabled());
        	}
        }
        contextSelector.setModel(new ListComboBoxModel<IContextPanel>(visiblePanels));
        contextSelector.setSelectedIndex(selectedPanel != null ? visiblePanels.indexOf(selectedPanel) : 0);
        ((CardLayout)container.getLayout()).show(container, selectedPanel != null ? selectedPanel.getContextPanelName() : visiblePanels.get(0).getContextPanelName());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.panels.IContextPanelsContainer#addContextPanel(net.sourceforge.atunes.model.IContextPanel)
	 */
    @Override
	public void addContextPanel(IContextPanel panel) {
        panels.add(panel);
        updateContextPanels();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.panels.IContextPanelsContainer#removeContextPanel(net.sourceforge.atunes.model.IContextPanel)
	 */
    @Override
	public void removeContextPanel(IContextPanel panel) {
        panels.remove(panel);
        updateContextPanels();
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.panels.IContextPanelsContainer#showContextPanel(net.sourceforge.atunes.model.IContextPanel)
	 */
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
	
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.panels.IContextPanelsContainer#getSelectedContextTab()
	 */
    @Override
	public IContextPanel getSelectedContextPanel() {
    	return (IContextPanel) contextSelector.getSelectedItem();
    }
    
	protected JComboBox getContextPanelSelector() {
		return contextSelector;
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
		contextSelector.setSelectedIndex(panel != null ? visiblePanels.indexOf(panel) : 0); // Show panel or first one
		showContextPanel(panel);
	}
	
	@Override
	public void enableContextPanelSelection(ItemListener listener) {
		contextSelector.addItemListener(listener);
	}	
}
