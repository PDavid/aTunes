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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.INavigationTablePanel;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.ITable;

/**
 * Panel holding navigation table
 * 
 * @author alex
 * 
 */
public final class NavigationTablePanel extends JPanel implements
		INavigationTablePanel {

	private static final long serialVersionUID = -2900418193013495812L;

	private NavigationControlPanel controlPanel;

	private ITable navigationTable;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param controlPanel
	 */
	public void setControlPanel(final NavigationControlPanel controlPanel) {
		this.controlPanel = controlPanel;
	}

	/**
	 * Instantiates a new navigation panel.
	 */
	public NavigationTablePanel() {
		super(new BorderLayout(), true);
	}

	/**
	 * Adds the content.
	 * 
	 * @param lookAndFeelManager
	 */
	public void initialize() {
		this.navigationTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// Disable autoresize, as we will control it
		this.navigationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		add(this.controlPanel, BorderLayout.NORTH);
		add(this.controlsBuilder.createScrollPane(this.navigationTable
				.getSwingComponent()), BorderLayout.CENTER);
	}

	/**
	 * @param navigationTable
	 */
	public void setNavigationTable(final ITable navigationTable) {
		this.navigationTable = navigationTable;
	}

	/**
	 * Gets the navigation table.
	 * 
	 * @return the navigation table
	 */
	public ITable getNavigationTable() {
		return this.navigationTable;
	}

	@Override
	public JPanel getSwingComponent() {
		return this;
	}

	@Override
	public void showNavigationTableFilter(final boolean show) {
		this.controlPanel.showFilter(show);
	}

	@Override
	public void showNavigationTree(final boolean show) {
		// Must show controls if tree not visible
		this.controlPanel.showControls(!show);
		this.controlPanel.enableTreeControls(show);
	}

	@Override
	public void showNavigationView(final INavigationView view) {
		this.controlPanel.setSelectedButton(view.getClass().getName());
	}

}
