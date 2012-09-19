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

package net.sourceforge.atunes.kernel.modules.navigator;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedTreeObjects;
import net.sourceforge.atunes.kernel.actions.CopyToDeviceAction;
import net.sourceforge.atunes.kernel.actions.ExportNavigatorSelectionAction;
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
    
    private AbstractActionOverSelectedObjects<IAudioObject> openFolderFromRepositoryNavigationView;

    private AbstractActionOverSelectedTreeObjects<IFolder> refreshFolderFromNavigatorAction;
    
    private AbstractActionOverSelectedTreeObjects<IFolder> moveFolderFromNavigatorAction;
    
    private Action editTitlesAction;
    
    private IBeanFactory beanFactory;

    private INavigationView repositoryNavigationView;
    
	/**
	 * Initializes menu
	 */
	public void initialize() {
        addToPlayListFromRepositoryNavigationView.setAudioObjectsSource(repositoryNavigationView);
        add(addToPlayListFromRepositoryNavigationView);
        
        setAsPlaylistFromRepositoryNavigationView.setAudioObjectsSource(repositoryNavigationView);
        add(setAsPlaylistFromRepositoryNavigationView);
        
        addArtistTopTracksToPlayListFromRepositoryNavigationView.setTreeObjectsSource(repositoryNavigationView);
        add(addArtistTopTracksToPlayListFromRepositoryNavigationView);
        
        add(new JSeparator());
        
        openFolderFromRepositoryNavigationView.setAudioObjectsSource(repositoryNavigationView);
        add(openFolderFromRepositoryNavigationView);
        
        moveFolderFromNavigatorAction.setTreeObjectsSource(repositoryNavigationView);
        add(moveFolderFromNavigatorAction);

        refreshFolderFromNavigatorAction.setTreeObjectsSource(repositoryNavigationView);
        add(refreshFolderFromNavigatorAction);
                
        add(new JSeparator());
        add(new EditTagMenu(false, repositoryNavigationView));
        add(editTitlesAction);
        add(new JSeparator());
        add(beanFactory.getBean(RemoveFromDiskAction.class));
        add(new JSeparator());
        
        AbstractActionOverSelectedObjects<IAudioObject> exportAction = beanFactory.getBean("exportNavigatorSelectionFromRepositoryViewAction", ExportNavigatorSelectionAction.class);
        exportAction.setAudioObjectsSource(repositoryNavigationView);
        add(exportAction);
        
        AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = beanFactory.getBean("copyToDeviceFromRepositoryNavigationView", CopyToDeviceAction.class);
        copyToDeviceAction.setAudioObjectsSource(repositoryNavigationView);
        add(copyToDeviceAction);
        
        add(new JSeparator());
        
        SetFavoriteAlbumFromNavigatorAction setFavoriteAlbumFromNavigatorAction = beanFactory.getBean(SetFavoriteAlbumFromNavigatorAction.class);
        setFavoriteAlbumFromNavigatorAction.setAudioObjectsSource(repositoryNavigationView);
        add(setFavoriteAlbumFromNavigatorAction);
        
        SetFavoriteArtistFromNavigatorAction setFavoriteArtistFromNavigatorAction = beanFactory.getBean(SetFavoriteArtistFromNavigatorAction.class);
        setFavoriteArtistFromNavigatorAction.setAudioObjectsSource(repositoryNavigationView);
        add(setFavoriteArtistFromNavigatorAction);
        
        add(new JSeparator());
        add(beanFactory.getBean(SearchArtistAction.class));
        add(beanFactory.getBean(SearchArtistAtAction.class));
	}

	/**
	 * @param addToPlayListFromRepositoryNavigationView the addToPlayListFromRepositoryNavigationView to set
	 */
	public void setAddToPlayListFromRepositoryNavigationView(
			AbstractActionOverSelectedObjects<IAudioObject> addToPlayListFromRepositoryNavigationView) {
		this.addToPlayListFromRepositoryNavigationView = addToPlayListFromRepositoryNavigationView;
	}

	/**
	 * @param setAsPlaylistFromRepositoryNavigationView the setAsPlaylistFromRepositoryNavigationView to set
	 */
	public void setSetAsPlaylistFromRepositoryNavigationView(
			AbstractActionOverSelectedObjects<IAudioObject> setAsPlaylistFromRepositoryNavigationView) {
		this.setAsPlaylistFromRepositoryNavigationView = setAsPlaylistFromRepositoryNavigationView;
	}

	/**
	 * @param addArtistTopTracksToPlayListFromRepositoryNavigationView the addArtistTopTracksToPlayListFromRepositoryNavigationView to set
	 */
	public void setAddArtistTopTracksToPlayListFromRepositoryNavigationView(
			AbstractActionOverSelectedTreeObjects<IArtist> addArtistTopTracksToPlayListFromRepositoryNavigationView) {
		this.addArtistTopTracksToPlayListFromRepositoryNavigationView = addArtistTopTracksToPlayListFromRepositoryNavigationView;
	}

	/**
	 * @param openFolderFromRepositoryNavigationView the openFolderFromRepositoryNavigationView to set
	 */
	public void setOpenFolderFromRepositoryNavigationView(
			AbstractActionOverSelectedObjects<IAudioObject> openFolderFromRepositoryNavigationView) {
		this.openFolderFromRepositoryNavigationView = openFolderFromRepositoryNavigationView;
	}

	/**
	 * @param refreshFolderFromNavigatorAction the refreshFolderFromNavigatorAction to set
	 */
	public void setRefreshFolderFromNavigatorAction(
			AbstractActionOverSelectedTreeObjects<IFolder> refreshFolderFromNavigatorAction) {
		this.refreshFolderFromNavigatorAction = refreshFolderFromNavigatorAction;
	}

	/**
	 * @param editTitlesAction the editTitlesAction to set
	 */
	public void setEditTitlesAction(Action editTitlesAction) {
		this.editTitlesAction = editTitlesAction;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param repositoryNavigationView the repositoryNavigationView to set
	 */
	public void setRepositoryNavigationView(INavigationView repositoryNavigationView) {
		this.repositoryNavigationView = repositoryNavigationView;
	}
	
	/**
	 * @param moveFolderFromNavigatorAction
	 */
	public void setMoveFolderFromNavigatorAction(
			AbstractActionOverSelectedTreeObjects<IFolder> moveFolderFromNavigatorAction) {
		this.moveFolderFromNavigatorAction = moveFolderFromNavigatorAction;
	}
}
