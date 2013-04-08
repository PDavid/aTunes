/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.model.IButtonPanel;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.IContextPanelsContainer;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Contains and manages context panels
 * 
 * @author alex
 * 
 */
public final class ContextPanelContainer extends JPanel implements
		IContextPanelsContainer {

	private static final long serialVersionUID = 707242790413122482L;

	private IButtonPanel contextSelector;

	private PopUpButton options;

	private JPanel container;

	private final List<IContextPanel> panels = new ArrayList<IContextPanel>();

	private final List<IContextPanel> visiblePanels = new ArrayList<IContextPanel>();

	private ILookAndFeelManager lookAndFeelManager;

	private IContextHandler contextHandler;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * Instantiates a new context panel
	 */
	public ContextPanelContainer() {
		super(new GridBagLayout());
	}

	/**
	 * @param contextHandler
	 */
	public void setContextHandler(final IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Sets the content.
	 * 
	 * @param lookAndFeelManager
	 */
	public void initialize() {
		this.contextSelector = this.controlsBuilder.createButtonPanel();
		this.contextSelector.setIconOnly(true);
		this.options = (PopUpButton) this.controlsBuilder
				.createPopUpButton(PopUpButton.BOTTOM_RIGHT);
		this.container = new JPanel(new CardLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 1, 1, 0);
		add(this.options, c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(0, 0, 1, 0);
		add((JComponent) this.contextSelector, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		add(this.container, c);
	}

	@Override
	public JPanel getSwingComponent() {
		return this;
	}

	@Override
	public void updateContextPanels() {
		Logger.debug("Updating context panels");
		IContextPanel selectedPanel = this.contextSelector.getSelectedItem() != null ? (IContextPanel) this.contextSelector
				.getSelectedItem() : null;
		this.container.removeAll();
		this.visiblePanels.clear();
		this.contextSelector.clear();
		for (final IContextPanel panel : this.panels) {
			if (panel.isVisible()) {
				Logger.debug("Context panel ", panel.getContextPanelName(),
						" is visible");
				this.visiblePanels.add(panel);
				this.container.add(panel.getContextPanelName(), panel
						.getUIComponent(this.lookAndFeelManager
								.getCurrentLookAndFeel()));
				panel.getUIComponent(
						this.lookAndFeelManager.getCurrentLookAndFeel())
						.setEnabled(panel.isEnabled());

				this.contextSelector.addButton(panel.getContextPanelName(),
						panel.getTitle(), panel.getIcon(), panel.getAction(),
						panel);
			}
		}
		if (!CollectionUtils.isEmpty(visiblePanels)) {
			IContextPanel newSelectedPanel = selectedPanel != null
					&& selectedPanel.isVisible() ? selectedPanel
					: this.visiblePanels.get(0);
			Logger.debug("Selected context panel: ",
					newSelectedPanel.getContextPanelName());
			this.contextSelector.setSelectedButton(newSelectedPanel
					.getContextPanelName());
			((CardLayout) this.container.getLayout()).show(this.container,
					newSelectedPanel.getContextPanelName());
			if (selectedPanel != null
					&& !newSelectedPanel.equals(selectedPanel)) {
				this.contextHandler.setContextTab(newSelectedPanel
						.getContextPanelName());
			}
		}
		this.invalidate();
		this.revalidate();
		this.repaint();
	}

	@Override
	public void addContextPanel(final IContextPanel panel) {
		Logger.debug("Adding context panel: ", panel.getContextPanelName());
		this.panels.add(panel);
	}

	@Override
	public void removeContextPanel(final IContextPanel panel) {
		this.panels.remove(panel);
	}

	private void showContextPanel(final IContextPanel panel) {
		final IContextPanel source = panel != null ? panel : this.visiblePanels
				.get(0);
		this.options.removeAllItems();
		this.options.setEnabled(source != null
				&& !source.getOptions().isEmpty());
		if (source != null) {
			List<Component> components = source.getOptions();
			if (components != null) {
				for (Component c : components) {
					this.options.add(c);
				}
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					((CardLayout) ContextPanelContainer.this.container
							.getLayout()).show(
							ContextPanelContainer.this.container,
							source.getContextPanelName());
				}
			});
		}
	}

	@Override
	public IContextPanel getSelectedContextPanel() {
		return (IContextPanel) this.contextSelector.getSelectedItem();
	}

	@Override
	public void setSelectedContextPanel(final String selectedContextTab) {
		Logger.debug("Setting context view: ", selectedContextTab);
		IContextPanel panel = null;
		if (selectedContextTab != null) {
			for (IContextPanel p : this.panels) {
				if (p.getContextPanelName().equals(selectedContextTab)) {
					panel = p;
					break;
				}
			}
		}
		this.contextSelector.setSelectedButton(panel != null ? panel
				.getContextPanelName() : this.visiblePanels.get(0)
				.getContextPanelName()); // Show panel or first one
		showContextPanel(panel);
	}
}
