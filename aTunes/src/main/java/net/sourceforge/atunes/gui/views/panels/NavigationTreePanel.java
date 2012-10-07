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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.TransferHandler;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.gui.views.controls.ToggleButtonFlowPanel;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFilterPanel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTreePanel;
import net.sourceforge.atunes.model.INavigationView;

/**
 * Panel containing navigator
 * @author alex
 *
 */
public final class NavigationTreePanel extends JPanel implements INavigationTreePanel {

	private static final long serialVersionUID = -2900418193013495812L;

	private ToggleButtonFlowPanel viewButtonsPanel;

	private JPanel treePanel;

	private ILookAndFeelManager lookAndFeelManager;

	private INavigationHandler navigationHandler;

	private Action showArtistsInNavigatorAction;
	private Action showAlbumsInNavigatorAction;
	private Action showGenresInNavigatorAction;
	private Action showYearsInNavigatorAction;
	private Action showFoldersInNavigatorAction;
	private Action expandTreesAction;
	private Action collapseTreesAction;
	private Action showNavigationTableAction;

	private IFilterPanel navigatorFilterPanel;

	private IBeanFactory beanFactory;


	/**
	 * Instantiates a new navigation panel.
	 */
	public NavigationTreePanel()  {
		super(new GridBagLayout(), true);
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param navigatorFilterPanel
	 */
	public void setNavigatorFilterPanel(final IFilterPanel navigatorFilterPanel) {
		this.navigatorFilterPanel = navigatorFilterPanel;
	}

	/**
	 * @param showArtistsInNavigatorAction
	 */
	public void setShowArtistsInNavigatorAction(final Action showArtistsInNavigatorAction) {
		this.showArtistsInNavigatorAction = showArtistsInNavigatorAction;
	}

	/**
	 * @param showAlbumsInNavigatorAction
	 */
	public void setShowAlbumsInNavigatorAction(final Action showAlbumsInNavigatorAction) {
		this.showAlbumsInNavigatorAction = showAlbumsInNavigatorAction;
	}

	/**
	 * @param showGenresInNavigatorAction
	 */
	public void setShowGenresInNavigatorAction(final Action showGenresInNavigatorAction) {
		this.showGenresInNavigatorAction = showGenresInNavigatorAction;
	}

	/**
	 * @param showYearsInNavigatorAction
	 */
	public void setShowYearsInNavigatorAction(final Action showYearsInNavigatorAction) {
		this.showYearsInNavigatorAction = showYearsInNavigatorAction;
	}

	/**
	 * @param showFoldersInNavigatorAction
	 */
	public void setShowFoldersInNavigatorAction(final Action showFoldersInNavigatorAction) {
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
	public void setShowNavigationTableAction(final Action showNavigationTableAction) {
		this.showNavigationTableAction = showNavigationTableAction;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
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
		viewButtonsPanel = new ToggleButtonFlowPanel(true, beanFactory);
		treePanel = new JPanel(new CardLayout());
		addTrees();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(1, 1, 0, 0);
		add(getOptionsPopUpButton(lookAndFeelManager), c);
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(1, 0, 0, 0);
		add(viewButtonsPanel, c);

		c.gridx = 2;
		c.weightx = 0;
		add(navigatorFilterPanel.getSwingComponent(), c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.weighty = 1;
		add(treePanel, c);

		// Apply component orientation to all popup menus
		for (INavigationView view : navigationHandler.getNavigationViews()) {
			GuiUtils.applyComponentOrientation(view.getTreePopupMenu());
		}
	}

	/**
	 * @param lookAndFeelManager
	 */
	private PopUpButton getOptionsPopUpButton(final ILookAndFeelManager lookAndFeelManager) {
		PopUpButton options = new PopUpButton(PopUpButton.BOTTOM_RIGHT, lookAndFeelManager);
		ButtonGroup group = new ButtonGroup();
		addRadioButtonMenuItem(showArtistsInNavigatorAction, group, options);
		addRadioButtonMenuItem(showAlbumsInNavigatorAction, group, options);
		addRadioButtonMenuItem(showGenresInNavigatorAction, group, options);
		addRadioButtonMenuItem(showYearsInNavigatorAction, group, options);
		addRadioButtonMenuItem(showFoldersInNavigatorAction, group, options);
		options.add(new JSeparator());
		options.add(expandTreesAction);
		options.add(collapseTreesAction);
		options.addSeparator();
		options.add(new JCheckBoxMenuItem(showNavigationTableAction));
		return options;
	}

	/**
	 * Adds a radio button menu item
	 * @param action
	 * @param group
	 * @param options
	 */
	private void addRadioButtonMenuItem(final Action action, final ButtonGroup group, final PopUpButton options) {
		JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(action);
		group.add(menuItem);
		options.add(menuItem);
	}

	/**
	 * Updates panel to show all trees
	 */
	private void addTrees() {
		viewButtonsPanel.clear();
		treePanel.removeAll();

		for (INavigationView view : navigationHandler.getNavigationViews()) {
			viewButtonsPanel.addButton(view.getClass().getName(), view.getTitle(), view.getIcon(), view.getActionToShowView(), view);
			treePanel.add(view.getClass().getName(), view.getTreeScrollPane());
		}
	}

	@Override
	public void updateTrees() {
		addTrees();
	}

	@Override
	public void showNavigationView(final INavigationView view) {
		((CardLayout)treePanel.getLayout()).show(treePanel, view.getClass().getName());
		viewButtonsPanel.setSelectedButton(view.getClass().getName());
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
