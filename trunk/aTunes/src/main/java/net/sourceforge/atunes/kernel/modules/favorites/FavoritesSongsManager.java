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

package net.sourceforge.atunes.kernel.modules.favorites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.list.SetUniqueList;

import net.sourceforge.atunes.kernel.modules.webservices.AddLovedSongBackgroundWorker;
import net.sourceforge.atunes.kernel.modules.webservices.RemoveLovedSongBackgroundWorker;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * Manages favorite songs
 * 
 * @author alex
 * 
 */
public class FavoritesSongsManager {

	private IStateContext stateContext;

	private IBeanFactory beanFactory;

	private IRepositoryHandler repositoryHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
	 * Adds favorite songs
	 * 
	 * @param favorites
	 * @param songs
	 * @param updateWebServices
	 * @return true if favorites have changed
	 */
	public boolean addSongs(final IFavorites favorites,
			final Collection<ILocalAudioObject> songs,
			final boolean updateWebServices) {
		if (CollectionUtils.isEmpty(songs)) {
			return false;
		}

		for (ILocalAudioObject song : songs) {
			favorites.addSong(song);
		}

		if (updateWebServices && this.stateContext.isLastFmEnabled()
				&& this.stateContext.isAutoLoveFavoriteSong()) {
			this.beanFactory.getBean(AddLovedSongBackgroundWorker.class).add(
					new ArrayList<IAudioObject>(songs));
		}

		return true;
	}

	/**
	 * Remove favorite songs
	 * 
	 * @param favorites
	 * @param songs
	 * @param updateWebServices
	 * @return true if favorites have changed
	 */
	public boolean removeSongs(final IFavorites favorites,
			final Collection<ILocalAudioObject> songs,
			final boolean updateWebServices) {
		if (CollectionUtils.isEmpty(songs)) {
			return false;
		}

		for (ILocalAudioObject file : songs) {
			favorites.removeSong(file);
		}

		if (updateWebServices && this.stateContext.isLastFmEnabled()
				&& this.stateContext.isAutoLoveFavoriteSong()) {
			this.beanFactory.getBean(RemoveLovedSongBackgroundWorker.class)
					.remove(new ArrayList<IAudioObject>(songs));
		}

		return true;
	}

	/**
	 * Takes songs from a list of audio objects and add or remove songs from
	 * favorites
	 * 
	 * @param favorites
	 * @param songs
	 * @return if favorites have changed
	 */
	public boolean toggleFavoriteSongs(final IFavorites favorites,
			final List<ILocalAudioObject> songs) {
		if (CollectionUtils.isEmpty(songs)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		Set<ILocalAudioObject> set = SetUniqueList.decorate(songs).asSet();

		Set<ILocalAudioObject> toAdd = new HashSet<ILocalAudioObject>();
		Set<ILocalAudioObject> toRemove = new HashSet<ILocalAudioObject>();
		for (ILocalAudioObject song : set) {
			// Toggle favorite songs
			if (favorites.containsSong(song)) {
				toRemove.add(song);
			} else {
				toAdd.add(song);
			}
		}

		addSongs(favorites, toAdd, true);
		removeSongs(favorites, toRemove, true);

		return !toAdd.isEmpty() || !toRemove.isEmpty();
	}

	/**
	 * Checks all songs to see if exists in repository, removing if does not
	 * exist
	 * 
	 * @param favorites
	 * @return true if favorites changed
	 */
	public boolean checkFavoriteSongs(final IFavorites favorites) {
		List<ILocalAudioObject> toRemove = new ArrayList<ILocalAudioObject>();
		for (ILocalAudioObject favorite : favorites.getFavoriteSongs()) {
			if (!this.repositoryHandler.existsFile(favorite)) {
				toRemove.add(favorite);
			}
		}
		return removeSongs(favorites, toRemove, false);
	}

	/**
	 * @param favorites
	 * @param artist
	 * @param title
	 * @return true if a song with given artist and title is favorite
	 */
	public boolean isSongFavorite(final IFavorites favorites,
			final String artist, final String title) {
		// TODO: This method checks all favorite songs to find one matching
		// With favorites stored by metadata this would not be necessary
		for (ILocalAudioObject ao : favorites.getFavoriteSongs()) {
			if (ao.getArtist(this.unknownObjectChecker)
					.equalsIgnoreCase(artist)
					&& title.equalsIgnoreCase(ao.getTitle())) {
				return true;
			}
		}
		return false;
	}
}
