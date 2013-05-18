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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.OverlayLayout;
import javax.swing.TransferHandler;

import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTreePanel;
import net.sourceforge.atunes.model.INavigationView;

/**
 * Panel containing navigator
 * 
 * @author alex
 * 
 */
public final class NavigationTreePanel extends JPanel implements
		INavigationTreePanel {

	private static final long serialVersionUID = -2900418193013495812L;

	private JPanel treePanel;

	private INavigationHandler navigationHandler;

	private NavigationControlPanel controlPanel;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlPanel
	 */
	public void setControlPanel(final NavigationControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * Instantiates a new navigation panel.
	 */
	public NavigationTreePanel() {
		super(new GridBagLayout(), true);
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * Adds the content.
	 */
	public void initialize() {
		this.treePanel = new JPanel(new CardLayout());
		addTrees();
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		add(this.controlPanel, c);
		c.gridy = 1;
		c.weighty = 1;
		add(this.treePanel, c);

		// Apply component orientation to all popup menus
		for (INavigationView view : this.navigationHandler.getNavigationViews()) {
			this.controlsBuilder.applyComponentOrientation(view
					.getTreePopupMenu());
		}
	}

	/**
	 * Updates panel to show all trees
	 */
	private void addTrees() {
		this.treePanel.removeAll();

		for (INavigationView view : this.navigationHandler.getNavigationViews()) {
			JPanel panelWithOverlay = new JPanel();
			LayoutManager overlay = new OverlayLayout(panelWithOverlay);
			panelWithOverlay.setLayout(overlay);
			JScrollPane scrollPane = view.getTreeScrollPane();

			JPanel viewOverlayPanel = view.getOverlayPanel();
			viewOverlayPanel.setAlignmentX(0.5f);
			viewOverlayPanel.setAlignmentY(0.5f);
			panelWithOverlay.add(viewOverlayPanel);
			panelWithOverlay.add(scrollPane);
			// Overlay is invisible by default
			viewOverlayPanel.setVisible(false);

			this.treePanel.add(view.getClass().getName(), panelWithOverlay);
		}
	}

	@Override
	public void updateTrees() {
		addTrees();
		this.controlPanel.updateControls();
	}

	@Override
	public void showNavigationView(final INavigationView view) {
		((CardLayout) this.treePanel.getLayout()).show(this.treePanel, view
				.getClass().getName());
		this.controlPanel.setSelectedButton(view.getClass().getName());
	}

	@Override
	public JPanel getSwingComponent() {
		return this;
	}

	@Override
	public void enableDragAndDrop(final TransferHandler transferHandler) {
		setTransferHandler(transferHandler);
	}
}
