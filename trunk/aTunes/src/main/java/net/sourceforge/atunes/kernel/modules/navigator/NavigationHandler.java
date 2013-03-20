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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.draganddrop.TreeNavigationTransferHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

/**
 * Responsible of manage navigator
 * 
 * @author alex
 * 
 */
public final class NavigationHandler extends AbstractHandler implements
		PluginListener, INavigationHandler {

	private List<INavigationView> navigationViews;

	private NavigationController navigationController;

	private IFilterHandler filterHandler;

	private IFilter navigationTreeFilter;

	private IStateNavigation stateNavigation;

	/**
	 * @param filterHandler
	 */
	public void setFilterHandler(final IFilterHandler filterHandler) {
		this.filterHandler = filterHandler;
	}

	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

	/**
	 * @param navigationTreeFilter
	 */
	public void setNavigationTreeFilter(final IFilter navigationTreeFilter) {
		this.navigationTreeFilter = navigationTreeFilter;
	}

	@Override
	public void applicationStarted() {
		showNavigator(this.stateNavigation.isShowNavigator());
		applyNavigationTableVisibility(this.stateNavigation.isShowNavigator()
				&& this.stateNavigation.isShowNavigationTable());

		// Navigation Panel View
		getNavigationController().setNavigationView(
				this.stateNavigation.getNavigationView(), false);

		getNavigationController().getNavigationTreePanel().enableDragAndDrop(
				new TreeNavigationTransferHandler(this));
	}

	@Override
	public List<INavigationView> getNavigationViews() {
		return this.navigationViews;
	}

	/**
	 * Builds a map containing classes of navigation view as keys and references
	 * to instances as values
	 * 
	 * @return
	 */
	private Map<Class<? extends INavigationView>, INavigationView> getNavigationViewsMap() {
		Map<Class<? extends INavigationView>, INavigationView> navigationViewsMap = new HashMap<Class<? extends INavigationView>, INavigationView>();
		for (INavigationView view : getNavigationViews()) {
			navigationViewsMap.put(view.getClass(), view);
		}
		return navigationViewsMap;
	}

	@Override
	public INavigationView getCurrentView() {
		return getView(getViewByName(this.stateNavigation.getNavigationView()));
	}

	@Override
	public ViewMode getCurrentViewMode() {
		return getCurrentView().getCurrentViewMode();
	}

	@Override
	public INavigationView getView(
			final Class<? extends INavigationView> navigationViewClass) {
		return getNavigationViewsMap().get(navigationViewClass);
	}

	@Override
	public void refreshCurrentView() {
		doRefreshView(getCurrentView());
	}

	@Override
	public void refreshView(final INavigationView navigationView) {
		if (navigationView.equals(getCurrentView())) {
			doRefreshView(navigationView);
		}
	}

	private void doRefreshView(final INavigationView navigationView) {
		ViewMode viewMode = this.stateNavigation.getViewMode();
		String filter = this.filterHandler
				.getFilterText(this.navigationTreeFilter);
		navigationView.refreshView(viewMode, filter);
	}

	@Override
	public Class<? extends INavigationView> getViewByName(final String className) {
		if (className == null) {
			return null;
		}
		for (Class<? extends INavigationView> viewFromMap : getNavigationViewsMap()
				.keySet()) {
			if (viewFromMap.getName().equals(className)) {
				return viewFromMap;
			}
		}

		// If class is not found (maybe the view was a plugin and has been
		// removed, return default view)
		return RepositoryNavigationView.class;
	}

	@Override
	public void pluginActivated(final PluginInfo plugin) {
		try {
			getNavigationViews().add(
					(INavigationView) getBean(IPluginsHandler.class)
							.getNewInstance(plugin));
			// Set tress
			getNavigationController().getNavigationTreePanel().updateTrees();
		} catch (PluginSystemException e) {
			Logger.error(e);
		}
	}

	@Override
	public void pluginDeactivated(final PluginInfo plugin,
			final Collection<Plugin> views) {
		// Remove all views
		for (Plugin view : views) {
			getNavigationViews().remove(view);
		}
	}

	@Override
	public void applicationStateChanged() {
		refreshCurrentView();
	}

	/**
	 * Gets the navigation controller.
	 * 
	 * @return the navigation controller
	 */
	private NavigationController getNavigationController() {
		if (this.navigationController == null) {
			this.navigationController = getBean(NavigationController.class);
		}
		return this.navigationController;
	}

	@Override
	public void refreshNavigationTable() {
		getNavigationController().refreshTable();
	}

	@Override
	public List<IAudioObject> getFilesSelectedInNavigator() {
		return getNavigationController().getFilesSelectedInNavigator();
	}

	@Override
	public void setNavigationView(final String name) {
		getNavigationController().setNavigationView(name);
	}

	@Override
	public ISearch openSearchDialog(final ISearchDialog dialog, final boolean b) {
		return getNavigationController().openSearchDialog(dialog, b);
	}

	private void updateTableContent(final INavigationTree tree) {
		getNavigationController().updateTableContent(tree);
	}

	@Override
	public void updateViewTable() {
		updateTableContent(getCurrentView().getTree());
	}

	@Override
	public List<? extends IAudioObject> getAudioObjectsForTreeNode(
			final Class<? extends INavigationView> view, final ITreeNode node) {
		return getNavigationController().getAudioObjectsForTreeNode(view, node);
	}

	@Override
	public IAudioObject getSelectedAudioObjectInNavigationTable() {
		return getNavigationController().getAudioObjectInNavigationTable();
	}

	@Override
	public List<IAudioObject> getSelectedAudioObjectsInNavigationTable() {
		return getNavigationController().getAudioObjectsInNavigationTable();
	}

	@Override
	public IAudioObject getAudioObjectInNavigationTable(final int row) {
		return getNavigationController().getAudioObjectInNavigationTable(row);
	}

	@Override
	public void repositoryReloaded() {
		// Calling inside invokeLater will cause ConcurrentModificationException
		refreshCurrentView();
	}

	@Override
	public void favoritesChanged() {
		// Update all views
		refreshCurrentView();
	}

	@Override
	public void showNavigator(final boolean show) {
		this.stateNavigation.setShowNavigator(show);

		// Disable or enable actions
		for (INavigationView navigationView : getNavigationViews()) {
			navigationView.getActionToShowView().setEnabled(show);
		}

		getFrame().showNavigator(show);

		applyNavigationTableVisibility(show
				&& this.stateNavigation.isShowNavigationTable());
	}

	@Override
	public void showNavigationTable(final boolean show) {
		this.stateNavigation.setShowNavigationTable(show);
		applyNavigationTableVisibility(show);
	}

	/**
	 * Used to show or hide navigation table when changing tree visibility
	 * 
	 * @param show
	 */
	private void applyNavigationTableVisibility(final boolean show) {
		getFrame().showNavigationTable(show);

		updateTableContent(getCurrentView().getTree());
	}

	@Override
	public void selectArtist(final String artist) {
		getCurrentView().selectArtist(getCurrentView().getCurrentViewMode(),
				artist);
	}

	@Override
	public void selectAudioObject(final IAudioObject audioObject) {
		getCurrentView().selectAudioObject(
				getCurrentView().getCurrentViewMode(), audioObject);
	}

	@Override
	public boolean isActionOverTree() {
		return getNavigationController().isActionOverTree();
	}

	@Override
	public void setNavigationViews(final List<INavigationView> navigationViews) {
		this.navigationViews = navigationViews;
	}
}
