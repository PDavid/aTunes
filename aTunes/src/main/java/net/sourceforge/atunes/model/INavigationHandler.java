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

package net.sourceforge.atunes.model;

import java.util.List;

/**
 * Responsible of navigate through repository, radios, favorites...
 * 
 * @author alex
 * 
 */
public interface INavigationHandler extends IHandler {

	/**
	 * Returns list of all navigation views
	 * 
	 * @return
	 */
	public List<INavigationView> getNavigationViews();

	/**
	 * Returns current view
	 * 
	 * @return
	 */
	public INavigationView getCurrentView();

	/**
	 * Returns current view mode
	 * 
	 * @return
	 */
	public ViewMode getCurrentViewMode();

	/**
	 * Returns view by its class
	 * 
	 * @param navigationViewClass
	 * @return
	 */
	public INavigationView getView(
			Class<? extends INavigationView> navigationViewClass);

	/**
	 * Refreshes current view to update data shown
	 */
	public void refreshCurrentView();

	/**
	 * Refreshes given view. To avoid unnecessary actions, given view is only
	 * refreshed if it's the current view
	 * 
	 * @param navigationView
	 */
	public void refreshView(INavigationView navigationView);

	/**
	 * Returns view class by name
	 * 
	 * @param className
	 * @return
	 */
	public Class<? extends INavigationView> getViewByName(String className);

	/**
	 * Refreshes navigation table
	 */
	public void refreshNavigationTable();

	/**
	 * Returns files selected both from tree and table
	 * 
	 * @return
	 */
	public List<IAudioObject> getFilesSelectedInNavigator();

	/**
	 * Set given navigation name as the current one
	 * 
	 * @param name
	 */
	public void setNavigationView(String name);

	/**
	 * Opens search dialog
	 * 
	 * @param dialog
	 * @param b
	 * @return
	 */
	public ISearch openSearchDialog(ISearchDialog dialog, boolean b);

	/**
	 * Updates view table, usually after apply a filter
	 */
	public void updateViewTable();

	/**
	 * Return audio objects for selected treeNode
	 * 
	 * @param class1
	 * @param objectDragged
	 * @return
	 */
	public List<IAudioObject> getAudioObjectsForTreeNode(
			Class<? extends INavigationView> class1, ITreeNode objectDragged);

	/**
	 * Return audio objects for selected treeNode in current view
	 * 
	 * @param objectDragged
	 * @return
	 */
	public List<IAudioObject> getAudioObjectsForTreeNode(ITreeNode objectDragged);

	/**
	 * Returns selected audio object in navigation table
	 * 
	 * @return
	 */
	public IAudioObject getSelectedAudioObjectInNavigationTable();

	/**
	 * Returns selected audio objects in navigation table
	 * 
	 * @return
	 */
	public List<IAudioObject> getSelectedAudioObjectsInNavigationTable();

	/**
	 * Returns audio object in given table row
	 * 
	 * @param row
	 * @return
	 */
	public IAudioObject getAudioObjectInNavigationTable(int row);

	/**
	 * Called when repository has changed
	 */
	public void repositoryReloaded();

	/**
	 * Show navigator
	 * 
	 * @param show
	 */
	public void showNavigator(boolean show);

	/**
	 * Show navigation tree.
	 * 
	 * @param show
	 */
	public void showNavigationTree(boolean show);

	/**
	 * Show navigation table.
	 * 
	 * @param show
	 */
	public void showNavigationTable(boolean show);

	/**
	 * Show navigation table filter
	 * 
	 * @param show
	 */
	public void showNavigationTableFilter(boolean show);

	/**
	 * Called to select given artist in navigator
	 * 
	 * @param artist
	 */
	public void selectArtist(String artist);

	/**
	 * Called to select given audio object in navigator
	 * 
	 * @param audioObject
	 */
	public void selectAudioObject(IAudioObject audioObject);

	/**
	 * Returns true if last action has been performed in tree
	 * 
	 * @return
	 */
	public boolean isActionOverTree();

	/**
	 * Sets navigation views
	 * 
	 * @param navigationViews
	 */
	public void setNavigationViews(List<INavigationView> navigationViews);
}