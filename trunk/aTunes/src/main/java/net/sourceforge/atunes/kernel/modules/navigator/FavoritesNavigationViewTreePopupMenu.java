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
import net.sourceforge.atunes.kernel.actions.AddArtistTopTracksToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.CopyToDeviceAction;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.ExportNavigatorSelectionAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorTreeAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromFavoritesAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAtAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.INavigationView;

/**
 * Favorites view tree popup
 * 
 * @author alex
 * 
 */
public class FavoritesNavigationViewTreePopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7973271882891777085L;

	private IBeanFactory beanFactory;

	private INavigationView favoritesNavigationView;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param favoritesNavigationView
	 */
	public void setFavoritesNavigationView(
			final INavigationView favoritesNavigationView) {
		this.favoritesNavigationView = favoritesNavigationView;
	}

	/**
	 * Initializes menu
	 */
	public void initialize() {
		AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = this.beanFactory
				.getBean("addToPlayListFromFavoritesNavigationView",
						AddToPlayListAction.class);
		addToPlayListAction.setAudioObjectsSource(this.favoritesNavigationView);
		add(addToPlayListAction);

		SetAsPlayListAction setAsPlayListAction = this.beanFactory.getBean(
				"setAsPlaylistFromFavoritesNavigationView",
				SetAsPlayListAction.class);
		setAsPlayListAction.setAudioObjectsSource(this.favoritesNavigationView);
		add(setAsPlayListAction);

		AddArtistTopTracksToPlayListAction createTopTracksAction = this.beanFactory
				.getBean(
						"addArtistTopTracksToPlayListFromFavoritesNavigationView",
						AddArtistTopTracksToPlayListAction.class);
		createTopTracksAction
				.setTreeObjectsSource(this.favoritesNavigationView);
		add(createTopTracksAction);

		add(new JSeparator());

		AbstractActionOverSelectedTreeObjects<IFolder> openFolder = this.beanFactory
				.getBean("openFolderFromDeviceNavigationTree",
						OpenFolderFromNavigatorTreeAction.class);
		openFolder.setTreeObjectsSource(this.favoritesNavigationView);
		add(openFolder);

		add(new JSeparator());
		add(new EditTagMenu(false, this.favoritesNavigationView,
				this.beanFactory));
		AbstractActionOverSelectedTreeObjects<IAlbum> editTitles = this.beanFactory
				.getBean("editTitlesFromFavoritesViewAction",
						EditTitlesAction.class);
		editTitles.setTreeObjectsSource(this.favoritesNavigationView);
		add(editTitles);
		add(new JSeparator());
		add(this.beanFactory.getBean(RemoveFromDiskAction.class));
		add(new JSeparator());

		AbstractActionOverSelectedObjects<IAudioObject> exportAction = this.beanFactory
				.getBean("exportNavigatorSelectionFromFavoritesViewAction",
						ExportNavigatorSelectionAction.class);
		exportAction.setAudioObjectsSource(this.favoritesNavigationView);
		add(exportAction);

		AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = this.beanFactory
				.getBean("copyToDeviceFromFavoritesNavigationView",
						CopyToDeviceAction.class);
		copyToDeviceAction.setAudioObjectsSource(this.favoritesNavigationView);
		add(copyToDeviceAction);

		add(new JSeparator());
		add(this.beanFactory.getBean(RemoveFromFavoritesAction.class));
		add(new JSeparator());
		add(this.beanFactory.getBean(SearchArtistAction.class));
		add(this.beanFactory.getBean(SearchArtistAtAction.class));
	}
}
