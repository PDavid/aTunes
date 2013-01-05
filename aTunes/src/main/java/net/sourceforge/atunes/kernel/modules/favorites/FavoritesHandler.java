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
import java.util.Map;
import java.util.Set;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.FavoritesListeners;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.RemoveLovedSongInLastFmAction;
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
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.Logger;

import org.apache.commons.collections.list.SetUniqueList;

/**
 * The Class FavoritesHandler.
 */
public final class FavoritesHandler extends AbstractHandler implements
		IAudioFilesRemovedListener, IFavoritesHandler {

	private IStateHandler stateHandler;

	private IRepositoryHandler repositoryHandler;

	private FavoritesListeners favoritesListeners;

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
	 * @param favoritesListeners
	 */
	public void setFavoritesListeners(
			final FavoritesListeners favoritesListeners) {
		this.favoritesListeners = favoritesListeners;
	}

	/**
	 * @param stateHandler
	 */
	public void setStateHandler(final IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
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
		Map<String, IAlbum> favAlbums = this.favorites.getFavoriteAlbums();
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
					if (favAlbums.containsValue(album)) {
						toRemove.add(album);
					} else {
						favAlbums.put(album.getName(), album);
					}
				}
			}
		}

		removeFromFavorites(toRemove);
		callActionsAfterFavoritesChange();
	}

	@Override
	public void toggleFavoriteArtists(final List<ILocalAudioObject> songs) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		@SuppressWarnings("unchecked")
		Set<ILocalAudioObject> set = SetUniqueList.decorate(songs).asSet();

		List<ITreeObject<?>> toRemove = new ArrayList<ITreeObject<?>>();
		Map<String, IArtist> favArtists = this.favorites.getFavoriteArtists();
		for (ILocalAudioObject f : set) {
			IArtist artist = this.repositoryHandler.getArtist(f
					.getArtist(this.unknownObjectChecker));
			if (artist == null) {
				artist = this.repositoryHandler.getArtist(f
						.getAlbumArtist(this.unknownObjectChecker));
			}
			if (artist != null) {
				if (favArtists.containsValue(artist)) {
					toRemove.add(artist);
				} else {
					favArtists.put(artist.getName(), artist);
				}
			}
		}
		removeFromFavorites(toRemove);
		callActionsAfterFavoritesChange();
	}

	@Override
	public void toggleFavoriteSongs(final List<ILocalAudioObject> songs) {
		if (songs == null || songs.isEmpty()) {
			return;
		}

		@SuppressWarnings("unchecked")
		Set<ILocalAudioObject> set = SetUniqueList.decorate(songs).asSet();

		Map<String, ILocalAudioObject> favSongs = this.favorites
				.getFavoriteAudioFiles();
		List<IAudioObject> toRemove = new LinkedList<IAudioObject>();
		for (ILocalAudioObject song : set) {
			// Toggle favorite songs
			if (favSongs.containsKey(song.getUrl())) {
				toRemove.add(song);
			} else {
				favSongs.put(song.getUrl(), song);

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

		Map<String, ILocalAudioObject> favSongs = this.favorites
				.getFavoriteAudioFiles();
		for (ILocalAudioObject song : songs) {
			favSongs.put(song.getUrl(), song);

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
	public void applicationFinish() {
		// Only store repository if it's dirty
		if (((Favorites) this.favorites).isDirty()) {
			this.stateHandler.persistFavoritesCache(this.favorites);
		} else {
			Logger.info("Favorites are clean");
		}
	}

	@Override
	public Map<String, IAlbum> getFavoriteAlbumsInfo() {
		return this.favorites.getFavoriteAlbums();
	}

	@Override
	public Map<String, IArtist> getFavoriteArtistsInfo() {
		return this.favorites.getFavoriteArtists();
	}

	@Override
	public List<ILocalAudioObject> getFavoriteSongs() {
		return this.favorites.getAllFavoriteSongs();
	}

	@Override
	public Map<String, ILocalAudioObject> getFavoriteSongsMap() {
		return this.favorites.getAllFavoriteSongsMap();
	}

	@Override
	public Map<String, ILocalAudioObject> getFavoriteSongsInfo() {
		return this.favorites.getFavoriteAudioFiles();
	}

	@Override
	public void removeFromFavorites(final List<ITreeObject<?>> objects) {
		for (ITreeObject<? extends IAudioObject> obj : objects) {
			if (obj instanceof IArtist) {
				this.favorites.getFavoriteArtists().remove(obj.toString());
			} else {
				this.favorites.getFavoriteAlbums().remove(obj.toString());
			}
		}

		callActionsAfterFavoritesChange();
	}

	@Override
	public void removeSongsFromFavorites(final List<IAudioObject> files) {
		for (IAudioObject file : files) {
			this.favorites.getFavoriteAudioFiles().remove(file.getUrl());
			// Unlove on LastFM if necessary
			if (this.stateContext.isLastFmEnabled()
					&& this.stateContext.isAutoLoveFavoriteSong()) {
				// TODO: do this with a listener interface
				getBean(RemoveLovedSongInLastFmAction.class)
						.removeFromLovedSongs(file);
			}
		}
		callActionsAfterFavoritesChange();
	}

	/**
	 * Actions to do after a favorite change (add, remove)
	 */
	private void callActionsAfterFavoritesChange() {
		if (this.favoritesListeners != null) {
			GuiUtils.callInEventDispatchThread(new Runnable() {
				@Override
				public void run() {
					FavoritesHandler.this.favoritesListeners.favoritesChanged();
				}
			});
		}
	}

	@Override
	public void favoritesChanged() {
		// Mark favorites information as dirty
		((Favorites) this.favorites).setDirty(true);
	}

	@Override
	public void audioFilesRemoved(final List<ILocalAudioObject> audioFiles) {
		for (ILocalAudioObject file : audioFiles) {
			// Remove from favorite audio files
			this.favorites.getFavoriteAudioFiles().remove(file.getUrl());

			// If artist has been removed then remove it from favorites too
			if (this.repositoryHandler.getArtist(file
					.getArtist(this.unknownObjectChecker)) == null) {
				this.favorites.getFavoriteArtists().remove(
						file.getArtist(this.unknownObjectChecker));
			} else {
				// If album has been removed then remove it from favorites too
				if (this.repositoryHandler.getArtist(
						file.getArtist(this.unknownObjectChecker)).getAlbum(
						file.getAlbum(this.unknownObjectChecker)) == null) {
					this.favorites.getFavoriteAlbums().remove(
							file.getAlbum(this.unknownObjectChecker));
				}
			}
		}
		callActionsAfterFavoritesChange();
	}

	@Override
	public void updateFavorites(final IRepository repository) {
		List<IAudioObject> toRemove = new ArrayList<IAudioObject>();
		for (ILocalAudioObject favorite : this.favorites
				.getFavoriteAudioFiles().values()) {
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
}
