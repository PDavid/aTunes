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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.FavoritesListeners;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.modules.webservices.RemoveLovedSongBackgroundWorker;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchableObject;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

import org.apache.commons.collections.list.SetUniqueList;

/**
 * The Class FavoritesHandler.
 */
public final class FavoritesHandler extends AbstractHandler implements
		IAudioFilesRemovedListener, IFavoritesHandler {

	private IStateService stateService;

	private IRepositoryHandler repositoryHandler;

	/** The favorites. */
	private IFavorites favorites = new Favorites();

	private IStateContext stateContext;

	private ISearchableObject favoritesSearchableObject;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param favoritesSearchableObject
	 */
	public void setFavoritesSearchableObject(
			final ISearchableObject favoritesSearchableObject) {
		this.favoritesSearchableObject = favoritesSearchableObject;
	}

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
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
	public void deferredInitialization() {
		getBean(ISearchHandler.class).registerSearchableObject(
				this.favoritesSearchableObject);
	}

	@Override
	public void toggleFavoriteAlbums(final List<ILocalAudioObject> songs) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		@SuppressWarnings("unchecked")
		Set<ILocalAudioObject> set = SetUniqueList.decorate(songs).asSet();

		List<ITreeObject<?>> toRemove = new ArrayList<ITreeObject<?>>();
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
					if (this.favorites.containsAlbum(album)) {
						toRemove.add(album);
					} else {
						this.favorites.addAlbum(album);
					}
				}
			}
		}

		removeFromFavorites(toRemove);
		callActionsAfterFavoritesChange();
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
		if (songs == null || songs.isEmpty()) {
			return;
		}

		@SuppressWarnings("unchecked")
		Set<ILocalAudioObject> set = SetUniqueList.decorate(songs).asSet();

		List<ILocalAudioObject> toRemove = new LinkedList<ILocalAudioObject>();
		for (ILocalAudioObject song : set) {
			// Toggle favorite songs
			if (this.favorites.containsSong(song)) {
				toRemove.add(song);
			} else {
				this.favorites.addSong(song);

				// Add to LastFM if necessary
				if (this.stateContext.isLastFmEnabled()
						&& this.stateContext.isAutoLoveFavoriteSong()) {
					// TODO: do this with a listener interface
					getBean(AddLovedSongInLastFMAction.class).loveSong(song);
				}
			}
		}
		removeSongsFromFavorites(toRemove);
		callActionsAfterFavoritesChange();
	}

	@Override
	public void addFavoriteSongs(final List<ILocalAudioObject> songs,
			final boolean automcaticallyLove) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		for (ILocalAudioObject song : songs) {
			this.favorites.addSong(song);

			// Add to web service if necessary
			if (automcaticallyLove && this.stateContext.isLastFmEnabled()
					&& this.stateContext.isAutoLoveFavoriteSong()) {
				// TODO: do this with a listener interface
				getBean(AddLovedSongInLastFMAction.class).loveSong(song);
			}
		}
		callActionsAfterFavoritesChange();
	}

	@Override
	public void removeFromFavorites(final List<ITreeObject<?>> objects) {
		for (ITreeObject<? extends IAudioObject> obj : objects) {
			if (obj instanceof IArtist) {
				this.favorites.removeArtist((IArtist) obj);
			} else {
				this.favorites.removeAlbum((IAlbum) obj);
			}
		}

		callActionsAfterFavoritesChange();
	}

	@Override
	public void removeSongsFromFavorites(final List<ILocalAudioObject> files) {
		for (ILocalAudioObject file : files) {
			this.favorites.removeSong(file);
			// Unlove on LastFM if necessary
			if (this.stateContext.isLastFmEnabled()
					&& this.stateContext.isAutoLoveFavoriteSong()) {
				// TODO: do this with a listener interface
				getBean(RemoveLovedSongBackgroundWorker.class).remove(file);
			}
		}
		callActionsAfterFavoritesChange();
	}

	/**
	 * Actions to do after a favorite change (add, remove)
	 */
	private void callActionsAfterFavoritesChange() {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				getBeanFactory().getBean(FavoritesListeners.class)
						.favoritesChanged();
			}
		});
	}

	@Override
	public void favoritesChanged() {
		this.stateService.persistFavoritesCache(this.favorites);
	}

	@Override
	public void audioFilesRemoved(final List<ILocalAudioObject> audioFiles) {
		for (ILocalAudioObject file : audioFiles) {
			// Remove from favorite audio files
			this.favorites.removeSong(file);

			// If artist has been removed then remove it from favorites too
			if (this.repositoryHandler.getArtist(file
					.getArtist(this.unknownObjectChecker)) == null) {
				this.favorites.removeArtistByName(file
						.getArtist(this.unknownObjectChecker));
			} else {
				// If album has been removed then remove it from favorites too
				if (this.repositoryHandler.getArtist(
						file.getArtist(this.unknownObjectChecker)).getAlbum(
						file.getAlbum(this.unknownObjectChecker)) == null) {
					this.favorites.removeAlbumByName(file
							.getAlbum(this.unknownObjectChecker));
				}
			}
		}
		callActionsAfterFavoritesChange();
	}

	@Override
	public void updateFavorites(final IRepository repository) {
		List<ILocalAudioObject> toRemove = new ArrayList<ILocalAudioObject>();
		for (ILocalAudioObject favorite : this.favorites.getFavoriteSongs()) {
			if (!repository.getFiles().contains(favorite)) {
				toRemove.add(favorite);
			}
		}
		if (!toRemove.isEmpty()) {
			removeSongsFromFavorites(toRemove);
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
}
