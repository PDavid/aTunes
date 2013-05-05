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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Removes selection from favorites
 * 
 * @author alex
 * 
 */
public class RemoveFromFavoritesAction extends CustomAbstractAction {

	private static final long serialVersionUID = -4288879781314486222L;

	private INavigationHandler navigationHandler;

	private IFavoritesHandler favoritesHandler;

	private INavigationView favoritesNavigationView;

	private IBeanFactory beanFactory;

	private ILocalAudioObjectFilter localAudioObjectFilter;

	/**
	 * @param localAudioObjectFilter
	 */
	public void setLocalAudioObjectFilter(
			final ILocalAudioObjectFilter localAudioObjectFilter) {
		this.localAudioObjectFilter = localAudioObjectFilter;
	}

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
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(final IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}

	/**
	 * Default constructor
	 */
	public RemoveFromFavoritesAction() {
		super(I18nUtils.getString("REMOVE_FROM_FAVORITES"));
	}

	@Override
	protected void executeAction() {
		if (this.navigationHandler.isActionOverTree()
				&& this.navigationHandler.getCurrentView().equals(
						this.favoritesNavigationView)) {
			List<ITreeNode> nodes = this.favoritesNavigationView.getTree()
					.getSelectedNodes();
			if (!CollectionUtils.isEmpty(nodes)) {
				List<IArtist> artists = new ArrayList<IArtist>();
				List<IAlbum> albums = new ArrayList<IAlbum>();
				for (ITreeNode node : nodes) {
					if (node.getUserObject() instanceof IArtist) {
						artists.add((IArtist) node.getUserObject());
					} else if (node.getUserObject() instanceof IAlbum) {
						albums.add((IAlbum) node.getUserObject());
					}
				}
				this.favoritesHandler.removeArtists(artists);
				this.favoritesHandler.removeAlbums(albums);
			}
		} else {
			List<IAudioObject> audioObjects = this.navigationHandler
					.getSelectedAudioObjectsInNavigationTable();
			if (!audioObjects.isEmpty()) {
				this.favoritesHandler
						.removeSongs(this.localAudioObjectFilter
								.getLocalAudioObjects(audioObjects));
			}
		}
	}

	@Override
	public boolean isEnabledForNavigationTreeSelection(
			final boolean rootSelected, final List<ITreeNode> selection) {
		for (ITreeNode node : selection) {
			// Only allow tree objects
			if (!(node.getUserObject() instanceof ITreeObject<?>)) {
				return false;
			}

			// Only allow to remove album if does not belong to a favorite
			// artist
			if (node.getUserObject() instanceof IAlbum) {
				if (this.favoritesHandler.isArtistFavorite(((IAlbum) node
						.getUserObject()).getArtist())) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean isEnabledForNavigationTableSelection(
			final List<IAudioObject> selection) {
		// Enabled if all selected items are favorite songs (not belong to
		// favorite artist nor album)
		return this.favoritesHandler.getFavoriteSongs().containsAll(
				this.beanFactory.getBean(ILocalAudioObjectFilter.class)
						.getLocalAudioObjects(selection));
	}
}
