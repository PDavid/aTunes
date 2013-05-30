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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.list.SetUniqueList;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * Manages favorite artists
 * 
 * @author alex
 * 
 */
public class FavoritesArtistsManager {

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
	 * Takes artists from a list of audio objects and add or remove artists from
	 * favorites
	 * 
	 * @param favorites
	 * @param songs
	 * @return if favorites have changed
	 */
	public boolean toggleFavoriteArtists(final IFavorites favorites,
			final List<ILocalAudioObject> songs) {
		if (CollectionUtils.isEmpty(songs)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		Set<ILocalAudioObject> set = SetUniqueList.decorate(songs).asSet();

		Set<IArtist> toRemove = new HashSet<IArtist>();
		Set<IArtist> toAdd = new HashSet<IArtist>();
		for (ILocalAudioObject f : set) {
			IArtist artist = this.repositoryHandler.getArtist(f
					.getArtist(this.unknownObjectChecker));
			if (artist == null) {
				artist = this.repositoryHandler.getArtist(f
						.getAlbumArtist(this.unknownObjectChecker));
			}
			if (artist != null) {
				if (favorites.containsArtist(artist)) {
					toRemove.add(artist);
				} else {
					toAdd.add(artist);
				}
			}
		}

		addArtists(favorites, toAdd);
		removeArtists(favorites, toRemove);

		return !toRemove.isEmpty() || !toAdd.isEmpty();
	}

	/**
	 * Adds artists
	 * 
	 * @param favorites
	 * @param artists
	 * @return if favorites have changed
	 */
	public boolean addArtists(final IFavorites favorites,
			final Collection<IArtist> artists) {
		if (CollectionUtils.isEmpty(artists)) {
			return false;
		}

		for (IArtist artistToAdd : artists) {
			favorites.addArtist(artistToAdd);
		}

		return true;
	}

	/**
	 * Removes artists
	 * 
	 * @param favorites
	 * @param artists
	 * @return if favorites have changed
	 */
	public boolean removeArtists(final IFavorites favorites,
			final Collection<IArtist> artists) {
		if (CollectionUtils.isEmpty(artists)) {
			return false;
		}

		for (IArtist artistToRemove : artists) {
			favorites.removeArtist(artistToRemove);
		}

		return true;
	}

	/**
	 * Removes artist from favorites
	 * 
	 * @param favorites
	 * @param artistName
	 * @return true if favorites changed
	 */
	public boolean removeArtist(final IFavorites favorites,
			final String artistName) {
		if (favorites.containsArtist(artistName)) {
			favorites.removeArtist(artistName);
			return true;
		}
		return false;
	}

	/**
	 * Checks all favorite artists exist in repository, removing them if
	 * necessary
	 * 
	 * @param favorites
	 * @return true if favorites changed
	 */
	public boolean checkFavoriteArtists(final IFavorites favorites) {
		Set<IArtist> toRemove = new HashSet<IArtist>();
		for (IArtist artist : favorites.getFavoriteArtists()) {
			if (!this.repositoryHandler.existsArtist(artist)) {
				toRemove.add(artist);
			}
		}
		return removeArtists(favorites, toRemove);
	}
}
