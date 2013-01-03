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

import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedTreeObjects;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAtAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteAlbumFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteArtistFromNavigatorAction;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.INavigationView;

/**
 * Popup menu for repository tree
 * 
 * @author alex
 * 
 */
public class RepositoryNavigationViewTreePopupMenu extends JPopupMenu {

    /**
	 * 
	 */
    private static final long serialVersionUID = -5391228599224832484L;

    private AbstractActionOverSelectedObjects<IAudioObject> addToPlayListFromRepositoryNavigationView;

    private AbstractActionOverSelectedObjects<IAudioObject> setAsPlaylistFromRepositoryNavigationView;

    private AbstractActionOverSelectedTreeObjects<IArtist> addArtistTopTracksToPlayListFromRepositoryNavigationView;

    private AbstractActionOverSelectedTreeObjects<IFolder> openFolderFromRepositoryNavigationTree;

    private AbstractActionOverSelectedTreeObjects<IFolder> refreshFolderFromNavigatorAction;

    private AbstractActionOverSelectedTreeObjects<IFolder> moveFolderFromNavigatorAction;

    private AbstractActionOverSelectedObjects<IAudioObject> exportNavigatorSelectionFromRepositoryViewAction;

    private AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceFromRepositoryNavigationView;

    private IBeanFactory beanFactory;

    private INavigationView repositoryNavigationView;

    /**
     * sets audio objects source and adds to popup
     * 
     * @param action
     */
    private void addAudioObjectsSourceAndAdd(
	    final AbstractActionOverSelectedObjects<?> action) {
	action.setAudioObjectsSource(repositoryNavigationView);
	add(action);
    }

    /**
     * sets tree objects source and adds to popup
     * 
     * @param action
     */
    private void addTreeObjectsSourceAndAdd(
	    final AbstractActionOverSelectedTreeObjects<?> action) {
	action.setTreeObjectsSource(repositoryNavigationView);
	add(action);
    }

    /**
     * Initializes menu
     */
    public void initialize() {
	addAudioObjectsSourceAndAdd(addToPlayListFromRepositoryNavigationView);
	addAudioObjectsSourceAndAdd(setAsPlaylistFromRepositoryNavigationView);
	addTreeObjectsSourceAndAdd(addArtistTopTracksToPlayListFromRepositoryNavigationView);
	add(new JSeparator());
	addTreeObjectsSourceAndAdd(openFolderFromRepositoryNavigationTree);
	addTreeObjectsSourceAndAdd(moveFolderFromNavigatorAction);
	addTreeObjectsSourceAndAdd(refreshFolderFromNavigatorAction);
	add(new JSeparator());
	add(new EditTagMenu(false, repositoryNavigationView, beanFactory));
	addTreeObjectsSourceAndAdd(beanFactory.getBean(
		"editTitlesFromRepositoryViewAction", EditTitlesAction.class));
	add(new JSeparator());
	add(beanFactory.getBean(RemoveFromDiskAction.class));
	add(new JSeparator());
	addAudioObjectsSourceAndAdd(exportNavigatorSelectionFromRepositoryViewAction);
	addAudioObjectsSourceAndAdd(copyToDeviceFromRepositoryNavigationView);
	add(new JSeparator());
	addAudioObjectsSourceAndAdd(beanFactory
		.getBean(SetFavoriteAlbumFromNavigatorAction.class));
	addAudioObjectsSourceAndAdd(beanFactory
		.getBean(SetFavoriteArtistFromNavigatorAction.class));
	add(new JSeparator());
	add(beanFactory.getBean(SearchArtistAction.class));
	add(beanFactory.getBean(SearchArtistAtAction.class));
    }

    /**
     * @param copyToDeviceFromRepositoryNavigationView
     */
    public void setCopyToDeviceFromRepositoryNavigationView(
	    final AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceFromRepositoryNavigationView) {
	this.copyToDeviceFromRepositoryNavigationView = copyToDeviceFromRepositoryNavigationView;
    }

    /**
     * @param exportNavigatorSelectionFromRepositoryViewAction
     */
    public void setExportNavigatorSelectionFromRepositoryViewAction(
	    final AbstractActionOverSelectedObjects<IAudioObject> exportNavigatorSelectionFromRepositoryViewAction) {
	this.exportNavigatorSelectionFromRepositoryViewAction = exportNavigatorSelectionFromRepositoryViewAction;
    }

    /**
     * @param addToPlayListFromRepositoryNavigationView
     *            the addToPlayListFromRepositoryNavigationView to set
     */
    public void setAddToPlayListFromRepositoryNavigationView(
	    final AbstractActionOverSelectedObjects<IAudioObject> addToPlayListFromRepositoryNavigationView) {
	this.addToPlayListFromRepositoryNavigationView = addToPlayListFromRepositoryNavigationView;
    }

    /**
     * @param setAsPlaylistFromRepositoryNavigationView
     *            the setAsPlaylistFromRepositoryNavigationView to set
     */
    public void setSetAsPlaylistFromRepositoryNavigationView(
	    final AbstractActionOverSelectedObjects<IAudioObject> setAsPlaylistFromRepositoryNavigationView) {
	this.setAsPlaylistFromRepositoryNavigationView = setAsPlaylistFromRepositoryNavigationView;
    }

    /**
     * @param addArtistTopTracksToPlayListFromRepositoryNavigationView
     *            the addArtistTopTracksToPlayListFromRepositoryNavigationView
     *            to set
     */
    public void setAddArtistTopTracksToPlayListFromRepositoryNavigationView(
	    final AbstractActionOverSelectedTreeObjects<IArtist> addArtistTopTracksToPlayListFromRepositoryNavigationView) {
	this.addArtistTopTracksToPlayListFromRepositoryNavigationView = addArtistTopTracksToPlayListFromRepositoryNavigationView;
    }

    /**
     * @param openFolderFromRepositoryNavigationTree
     */
    public void setOpenFolderFromRepositoryNavigationTree(
	    final AbstractActionOverSelectedTreeObjects<IFolder> openFolderFromRepositoryNavigationTree) {
	this.openFolderFromRepositoryNavigationTree = openFolderFromRepositoryNavigationTree;
    }

    /**
     * @param refreshFolderFromNavigatorAction
     *            the refreshFolderFromNavigatorAction to set
     */
    public void setRefreshFolderFromNavigatorAction(
	    final AbstractActionOverSelectedTreeObjects<IFolder> refreshFolderFromNavigatorAction) {
	this.refreshFolderFromNavigatorAction = refreshFolderFromNavigatorAction;
    }

    /**
     * @param beanFactory
     *            the beanFactory to set
     */
    public void setBeanFactory(final IBeanFactory beanFactory) {
	this.beanFactory = beanFactory;
    }

    /**
     * @param repositoryNavigationView
     *            the repositoryNavigationView to set
     */
    public void setRepositoryNavigationView(
	    final INavigationView repositoryNavigationView) {
	this.repositoryNavigationView = repositoryNavigationView;
    }

    /**
     * @param moveFolderFromNavigatorAction
     */
    public void setMoveFolderFromNavigatorAction(
	    final AbstractActionOverSelectedTreeObjects<IFolder> moveFolderFromNavigatorAction) {
	this.moveFolderFromNavigatorAction = moveFolderFromNavigatorAction;
    }
}
