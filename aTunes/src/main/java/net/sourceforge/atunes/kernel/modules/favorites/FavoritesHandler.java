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

import java.util.List;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.FavoritesListeners;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * The Class FavoritesHandler.
 */
public final class FavoritesHandler extends AbstractHandler implements
		IAudioFilesRemovedListener, IFavoritesHandler {

	private IStateService stateService;

	private IRepositoryHandler repositoryHandler;

	/** The favorites. */
	private IFavorites favorites = new Favorites();

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param stateService
	 */
	public void setStateService(final IStateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	protected void initHandler() {
		this.repositoryHandler.addAudioFilesRemovedListener(this);
	}

	@Override
	public void toggleFavoriteAlbums(final List<ILocalAudioObject> songs) {
		if (getBean(FavoritesAlbumsManager.class).toggleFavoriteAlbums(
				this.favorites, songs)) {
			callActionsAfterFavoritesChange();
		}
	}

	@Override
	public void toggleFavoriteArtists(final List<ILocalAudioObject> songs) {
		if (getBean(FavoritesArtistsManager.class).toggleFavoriteArtists(
				this.favorites, songs)) {
			callActionsAfterFavoritesChange();
		}
	}

	@Override
	public void toggleFavoriteSongs(final List<ILocalAudioObject> songs) {
		if (getBean(FavoritesSongsManager.class).toggleFavoriteSongs(
				this.favorites, songs)) {
			callActionsAfterFavoritesChange();
		}
	}

	@Override
	public void importFavoriteSongsFromLastFm(
			final List<ILocalAudioObject> songs) {
		if (getBean(FavoritesSongsManager.class).addSongs(this.favorites,
				songs, false)) {
			callActionsAfterFavoritesChange();
		}
	}

	@Override
	public void removeArtists(final List<IArtist> artists) {
		if (getBean(FavoritesArtistsManager.class).removeArtists(
				this.favorites, artists)) {
			callActionsAfterFavoritesChange();
		}
	}

	@Override
	public void removeAlbums(final List<IAlbum> albums) {
		if (getBean(FavoritesAlbumsManager.class).removeAlbums(this.favorites,
				albums)) {
			callActionsAfterFavoritesChange();
		}
	}

	@Override
	public void removeSongs(final List<ILocalAudioObject> files) {
		if (getBean(FavoritesSongsManager.class).removeSongs(this.favorites,
				files, true)) {
			callActionsAfterFavoritesChange();
		}
	}

	@Override
	public void favoritesChanged() {
		this.stateService.persistFavoritesCache(this.favorites);
	}

	@Override
	public void audioFilesRemoved(final List<ILocalAudioObject> audioFiles) {
		boolean changed = false;

		// Remove from favorite audio files
		changed = getBean(FavoritesSongsManager.class).removeSongs(
				this.favorites, audioFiles, false)
				|| changed;

		for (ILocalAudioObject file : audioFiles) {
			String artistName = file.getArtist(this.unknownObjectChecker);
			String albumName = file.getAlbum(this.unknownObjectChecker);

			IArtist artist = this.repositoryHandler.getArtist(artistName);
			// If artist has been removed then remove it and the album from
			// favorites too
			if (artist == null) {
				changed = getBean(FavoritesArtistsManager.class).removeArtist(
						this.favorites, artistName)
						|| changed;
				changed = getBean(FavoritesAlbumsManager.class).removeAlbum(
						this.favorites, albumName)
						|| changed;
			} else {
				IAlbum album = artist.getAlbum(albumName);
				// If album has been removed then remove it from favorites too
				if (album == null) {
					changed = getBean(FavoritesAlbumsManager.class)
							.removeAlbum(this.favorites, albumName) || changed;
				}
			}
		}
		if (changed) {
			callActionsAfterFavoritesChange();
		}
	}

	@Override
	public void updateFavoritesAfterRepositoryChange(
			final IRepository repository) {
		boolean change = false;
		change = getBean(FavoritesArtistsManager.class).checkFavoriteArtists(
				this.favorites)
				|| change;
		change = getBean(FavoritesAlbumsManager.class).checkFavoriteAlbums(
				this.favorites)
				|| change;
		change = getBean(FavoritesSongsManager.class).checkFavoriteSongs(
				this.favorites)
				|| change;

		if (change) {
			callActionsAfterFavoritesChange();
		}
	}

	/**
	 * @param favorites
	 */
	void setFavorites(final IFavorites favorites) {
		this.favorites = favorites;
	}

	@Override
	public boolean isArtistFavorite(final IArtist artist) {
		return this.favorites.containsArtist(artist);
	}

	@Override
	public boolean isArtistFavorite(final String artist) {
		return this.favorites.containsArtist(artist);
	}

	@Override
	public List<IArtist> getFavoriteArtists() {
		return this.favorites.getFavoriteArtists();
	}

	@Override
	public boolean isAlbumFavorite(final IAlbum album) {
		return this.favorites.containsAlbum(album);
	}

	@Override
	public boolean isAlbumFavorite(final String album) {
		return this.favorites.containsAlbum(album);
	}

	@Override
	public List<IAlbum> getFavoriteAlbums() {
		return this.favorites.getFavoriteAlbums();
	}

	@Override
	public List<ILocalAudioObject> getFavoriteSongs() {
		return this.favorites.getFavoriteSongs();
	}

	@Override
	public boolean isSongFavorite(final ILocalAudioObject audioObject) {
		return this.favorites.containsSong(audioObject);
	}

	@Override
	public List<ILocalAudioObject> getAllFavoriteSongs() {
		return this.favorites.getAllFavoriteSongs();
	}

	/**
	 * Actions to do after a favorite change (add, remove)
	 */
	private void callActionsAfterFavoritesChange() {
		getBean(FavoritesListeners.class).favoritesChanged();
	}

	@Override
	public void checkFavorites(final List<ITrackInfo> tracks) {
		if (!CollectionUtils.isEmpty(tracks)) {
			for (ITrackInfo track : tracks) {
				if (getBean(FavoritesSongsManager.class).isSongFavorite(
						this.favorites, track.getArtist(), track.getTitle())) {
					track.setFavorite(true);
				}
			}
		}
	}
}
