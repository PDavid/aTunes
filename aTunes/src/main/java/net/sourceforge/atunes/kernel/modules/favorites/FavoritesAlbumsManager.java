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

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * Manages favorite albums
 * 
 * @author alex
 * 
 */
public class FavoritesAlbumsManager {

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
	 * Takes albums from a list of audio objects and add or remove albums from
	 * favorites
	 * 
	 * @param favorites
	 * @param songs
	 * @return if favorites have changed
	 */
	public boolean toggleFavoriteAlbums(final IFavorites favorites,
			final List<ILocalAudioObject> songs) {
		if (CollectionUtils.isEmpty(songs)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		Set<ILocalAudioObject> set = SetUniqueList.decorate(songs).asSet();

		Set<IAlbum> toAdd = new HashSet<IAlbum>();
		Set<IAlbum> toRemove = new HashSet<IAlbum>();
		for (ILocalAudioObject f : set) {
			IArtist artist = this.repositoryHandler.getArtist(f
					.getArtist(this.unknownObjectChecker));
			if (artist == null) {
				artist = this.repositoryHandler.getArtist(f
						.getAlbumArtist(this.unknownObjectChecker));
			}
			if (artist != null) {
				IAlbum album = artist.getAlbum(f
						.getAlbum(this.unknownObjectChecker));
				if (album != null) {
					if (favorites.containsAlbum(album)) {
						toRemove.add(album);
					} else {
						toAdd.add(album);
					}
				}
			}
		}

		addAlbums(favorites, toAdd);
		removeAlbums(favorites, toRemove);

		return !toRemove.isEmpty() || !toAdd.isEmpty();
	}

	/**
	 * Adds albums
	 * 
	 * @param favorites
	 * @param albums
	 * @return if favorites have changed
	 */
	public boolean addAlbums(final IFavorites favorites,
			final Collection<IAlbum> albums) {
		if (CollectionUtils.isEmpty(albums)) {
			return false;
		}

		for (IAlbum albumToAdd : albums) {
			favorites.addAlbum(albumToAdd);
		}

		return true;
	}

	/**
	 * Removes albums
	 * 
	 * @param favorites
	 * @param albums
	 * @return if favorites have changed
	 */
	public boolean removeAlbums(final IFavorites favorites,
			final Collection<IAlbum> albums) {
		if (CollectionUtils.isEmpty(albums)) {
			return false;
		}

		for (IAlbum albumToRemove : albums) {
			favorites.removeAlbum(albumToRemove);
		}

		return true;
	}

	/**
	 * Removes album
	 * 
	 * @param favorites
	 * @param albumName
	 * @return true if favorites changed
	 */
	public boolean removeAlbum(final IFavorites favorites,
			final String albumName) {
		if (favorites.containsAlbum(albumName)) {
			favorites.removeAlbum(albumName);
			return true;
		}
		return false;
	}

	/**
	 * Checks all favorite albums exist in repository, removing them if
	 * necessary
	 * 
	 * @param favorites
	 * @return true if favorites changed
	 */
	public boolean checkFavoriteAlbums(final IFavorites favorites) {
		Set<IAlbum> toRemove = new HashSet<IAlbum>();
		for (IAlbum album : favorites.getFavoriteAlbums()) {
			if (!this.repositoryHandler.existsAlbum(album)) {
				toRemove.add(album);
			}
		}
		return removeAlbums(favorites, toRemove);
	}

}
