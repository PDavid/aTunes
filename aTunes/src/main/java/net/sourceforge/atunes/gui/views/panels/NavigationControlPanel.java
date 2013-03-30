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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.model.IButtonPanel;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFilterPanel;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;

/**
 * Panel with basic controls of navigator, can be associated to tree or table
 * 
 * @author alex
 * 
 */
public class NavigationControlPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -663892614694849970L;

	private IButtonPanel viewButtonsPanel;
	private PopUpButton popupButton;
	private Action showArtistsInNavigatorAction;
	private Action showAlbumsInNavigatorAction;
	private Action showGenresInNavigatorAction;
	private Action showYearsInNavigatorAction;
	private Action showFoldersInNavigatorAction;
	private Action expandTreesAction;
	private Action collapseTreesAction;
	private Action showNavigationTableAction;
	private Action showNavigationTableFilterAction;
	private Action showNavigationTreeAction;

	private IControlsBuilder controlsBuilder;

	private INavigationHandler navigationHandler;

	private IFilterPanel filter;

	/**
	 * @param filter
	 */
	public void setFilter(final IFilterPanel filter) {
		this.filter = filter;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
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
	public NavigationControlPanel() {
		super(new GridBagLayout(), true);
	}

	/**
	 * @param showNavigationTreeAction
	 */
	public void setShowNavigationTreeAction(
			final Action showNavigationTreeAction) {
		this.showNavigationTreeAction = showNavigationTreeAction;
	}

	/**
	 * @param showNavigationTableFilterAction
	 */
	public void setShowNavigationTableFilterAction(
			final Action showNavigationTableFilterAction) {
		this.showNavigationTableFilterAction = showNavigationTableFilterAction;
	}

	/**
	 * @param showArtistsInNavigatorAction
	 */
	public void setShowArtistsInNavigatorAction(
			final Action showArtistsInNavigatorAction) {
		this.showArtistsInNavigatorAction = showArtistsInNavigatorAction;
	}

	/**
	 * @param showAlbumsInNavigatorAction
	 */
	public void setShowAlbumsInNavigatorAction(
			final Action showAlbumsInNavigatorAction) {
		this.showAlbumsInNavigatorAction = showAlbumsInNavigatorAction;
	}

	/**
	 * @param showGenresInNavigatorAction
	 */
	public void setShowGenresInNavigatorAction(
			final Action showGenresInNavigatorAction) {
		this.showGenresInNavigatorAction = showGenresInNavigatorAction;
	}

	/**
	 * @param showYearsInNavigatorAction
	 */
	public void setShowYearsInNavigatorAction(
			final Action showYearsInNavigatorAction) {
		this.showYearsInNavigatorAction = showYearsInNavigatorAction;
	}

	/**
	 * @param showFoldersInNavigatorAction
	 */
	public void setShowFoldersInNavigatorAction(
			final Action showFoldersInNavigatorAction) {
		this.showFoldersInNavigatorAction = showFoldersInNavigatorAction;
	}

	/**
	 * @param expandTreesAction
	 */
	public void setExpandTreesAction(final Action expandTreesAction) {
		this.expandTreesAction = expandTreesAction;
	}

	/**
	 * @param collapseTreesAction
	 */
	public void setCollapseTreesAction(final Action collapseTreesAction) {
		this.collapseTreesAction = collapseTreesAction;
	}

	/**
	 * @param showNavigationTableAction
	 */
	public void setShowNavigationTableAction(
			final Action showNavigationTableAction) {
		this.showNavigationTableAction = showNavigationTableAction;
	}

	/**
	 * Adds the content.
	 */
	public void initialize() {
		this.popupButton = getOptionsPopUpButton();
		this.viewButtonsPanel = this.controlsBuilder.createButtonPanel();
		this.viewButtonsPanel.setIconOnly(true);
		JPanel buttonsContainer = new JPanel(new BorderLayout());
		buttonsContainer.add(this.popupButton, BorderLayout.WEST);
		buttonsContainer.add((JComponent) this.viewButtonsPanel,
				BorderLayout.CENTER);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.insets = new Insets(1, 1, 1, 0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		add(buttonsContainer, c);

		c.gridx = 2;
		c.anchor = GridBagConstraints.EAST;
		c.weightx = 0;
		add(this.filter.getSwingComponent(), c);

		// Apply component orientation to all popup menus
		for (INavigationView view : this.navigationHandler.getNavigationViews()) {
			this.controlsBuilder.applyComponentOrientation(view
					.getTreePopupMenu());
		}
		updateControls();
	}

	/**
	 * @param lookAndFeelManager
	 */
	private PopUpButton getOptionsPopUpButton() {
		PopUpButton options = (PopUpButton) this.controlsBuilder
				.createPopUpButton(PopUpButton.BOTTOM_RIGHT);
		ButtonGroup group = new ButtonGroup();
		addRadioButtonMenuItem(this.showArtistsInNavigatorAction, group,
				options);
		addRadioButtonMenuItem(this.showAlbumsInNavigatorAction, group, options);
		addRadioButtonMenuItem(this.showGenresInNavigatorAction, group, options);
		addRadioButtonMenuItem(this.showYearsInNavigatorAction, group, options);
		addRadioButtonMenuItem(this.showFoldersInNavigatorAction, group,
				options);
		options.add(new JSeparator());
		options.add(this.expandTreesAction);
		options.add(this.collapseTreesAction);
		options.addSeparator();
		options.add(new JCheckBoxMenuItem(this.showNavigationTreeAction));
		options.add(new JCheckBoxMenuItem(this.showNavigationTableAction));
		options.add(new JCheckBoxMenuItem(this.showNavigationTableFilterAction));
		return options;
	}

	/**
	 * Adds a radio button menu item
	 * 
	 * @param action
	 * @param group
	 * @param options
	 */
	private void addRadioButtonMenuItem(final Action action,
			final ButtonGroup group, final PopUpButton options) {
		JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(action);
		group.add(menuItem);
		options.add(menuItem);
	}

	/**
	 * Updates controls
	 */
	public void updateControls() {
		this.viewButtonsPanel.clear();
		for (INavigationView view : this.navigationHandler.getNavigationViews()) {
			this.viewButtonsPanel.addButton(view.getClass().getName(),
					view.getTitle(), view.getIcon(),
					view.getActionToShowView(), view);
		}
	}

	/**
	 * Delegate method for buttons
	 * 
	 * @param name
	 */
	public void setSelectedButton(final String name) {
		this.viewButtonsPanel.setSelectedButton(name);
	}

	/**
	 * Shows or hides controls
	 * 
	 * @param show
	 */
	public void showControls(final boolean show) {
		this.viewButtonsPanel.setVisible(show);
		this.popupButton.setVisible(show);
	}

	/**
	 * Shows or hides filter
	 * 
	 * @param show
	 */
	public void showFilter(final boolean show) {
		this.filter.setVisible(show);
	}

	/**
	 * Enables actions of tree
	 * 
	 * @param show
	 */
	public void enableTreeControls(final boolean show) {
		this.showArtistsInNavigatorAction.setEnabled(show);
		this.showAlbumsInNavigatorAction.setEnabled(show);
		this.showGenresInNavigatorAction.setEnabled(show);
		this.showYearsInNavigatorAction.setEnabled(show);
		this.showFoldersInNavigatorAction.setEnabled(show);
		this.expandTreesAction.setEnabled(show);
		this.collapseTreesAction.setEnabled(show);
	}
}
