/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.repository.favorites;

import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.FavoritesListeners;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.modules.repository.AudioFilesRemovedListener;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler;
import net.sourceforge.atunes.kernel.modules.search.searchableobjects.FavoritesSearchableObject;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.model.TreeObject;

/**
 * The Class FavoritesHandler.
 */
public final class FavoritesHandler extends AbstractHandler implements AudioFilesRemovedListener {

    /** The instance. */
    private static FavoritesHandler instance = new FavoritesHandler();

    /** The favorites. */
    private Favorites favorites;

    /**
     * Gets the single instance of FavoritesHandler.
     * 
     * @return single instance of FavoritesHandler
     */
    public static FavoritesHandler getInstance() {
        return instance;
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        // TODO Auto-generated method stub	
    }

    @Override
    protected void initHandler() {
        RepositoryHandler.getInstance().addAudioFilesRemovedListener(this);
    }

    @Override
    public void applicationStarted(List<AudioObject> playList) {
        SearchHandler.getInstance().registerSearchableObject(FavoritesSearchableObject.getInstance());
    }

    /**
     * Adds the favorite albums.
     * 
     * @param songs
     *            the songs
     */
    public void addFavoriteAlbums(List<LocalAudioObject> songs) {
        Map<String, Artist> structure = RepositoryHandler.getInstance().getArtistStructure();
        Map<String, Album> favAlbums = getFavorites().getFavoriteAlbums();
        for (int i = 0; i < songs.size(); i++) {
        	LocalAudioObject f = songs.get(i);
            Artist artist = structure.get(f.getArtist());
            if (artist == null) {
                artist = structure.get(f.getAlbumArtist());
            }
            Album album = artist.getAlbum(f.getAlbum());
            favAlbums.put(album.getName(), album);
        }
        callActionsAfterFavoritesChange();
    }

    /**
     * Adds the favorite artists.
     * 
     * @param songs
     *            the songs
     */
    public void addFavoriteArtists(List<LocalAudioObject> songs) {
        Map<String, Artist> structure = RepositoryHandler.getInstance().getArtistStructure();
        Map<String, Artist> favArtists = getFavorites().getFavoriteArtists();
        for (int i = 0; i < songs.size(); i++) {
        	LocalAudioObject f = songs.get(i);
            Artist artist = structure.get(f.getArtist());
            favArtists.put(artist.getName(), artist);
        }
        callActionsAfterFavoritesChange();
    }

    /**
     * Adds the favorite songs.
     * 
     * @param songs
     *            the songs
     */
    public void addFavoriteSongs(List<LocalAudioObject> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }
        Map<String, LocalAudioObject> favSongs = getFavorites().getFavoriteAudioFiles();
        for (LocalAudioObject song : songs) {
            favSongs.put(song.getUrl(), song);

            // Add to LastFM if necessary
            if (ApplicationState.getInstance().isLastFmEnabled() && ApplicationState.getInstance().isAutoLoveFavoriteSong()) {
            	// TODO: do this with a listener interface            	
           		((AddLovedSongInLastFMAction)Actions.getAction(AddLovedSongInLastFMAction.class)).loveSong(song);
            }
        }
        callActionsAfterFavoritesChange();
    }

    /**
     * Finish.
     */
    public void applicationFinish() {
        // Only store repository if it's dirty
        if (getFavorites().isDirty()) {
            ApplicationStateHandler.getInstance().persistFavoritesCache(getFavorites());
        } else {
            getLogger().info(LogCategories.FAVORITES, "Favorites are clean");
        }
    }

    /**
     * Gets the favorite albums info.
     * 
     * @return the favorite albums info
     */
    public Map<String, Album> getFavoriteAlbumsInfo() {
        return getFavorites().getFavoriteAlbums();
    }

    /**
     * Gets the favorite artists info.
     * 
     * @return the favorite artists info
     */
    public Map<String, Artist> getFavoriteArtistsInfo() {
        return getFavorites().getFavoriteArtists();
    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new Runnable() {
            @Override
            public void run() {
                favorites = ApplicationStateHandler.getInstance().retrieveFavoritesCache();
            }
        };
    }

    /**
     * Gets the favorites.
     * 
     * @return the favorites
     */
    private Favorites getFavorites() {
        return favorites;
    }

    /**
     * Gets the favorite songs.
     * 
     * @return the favorite songs
     */
    public List<LocalAudioObject> getFavoriteSongs() {
        return getFavorites().getAllFavoriteSongs();
    }

    /**
     * Gets the favorite songs map
     */
    public Map<String, LocalAudioObject> getFavoriteSongsMap() {
        return getFavorites().getAllFavoriteSongsMap();
    }

    /**
     * Gets the favorite songs info.
     * 
     * @return the favorite songs info
     */
    public Map<String, LocalAudioObject> getFavoriteSongsInfo() {
        return getFavorites().getFavoriteAudioFiles();
    }

    /**
     * Removes the objects from favorites.
     * 
     * @param objects
     *            list of objects of type TreeObject
     */
    public void removeFromFavorites(List<TreeObject> objects) {
        for (TreeObject obj : objects) {
            if (obj instanceof Artist) {
                getFavorites().getFavoriteArtists().remove(obj.toString());
            } else {
                getFavorites().getFavoriteAlbums().remove(obj.toString());
            }
        }

        callActionsAfterFavoritesChange();
    }

    /**
     * Removes the songs from favorites.
     * 
     * @param files
     *            the files
     */
    public void removeSongsFromFavorites(List<AudioObject> files) {
        for (AudioObject file : files) {
            getFavorites().getFavoriteAudioFiles().remove(file.getUrl());
        }

        callActionsAfterFavoritesChange();
    }

    /**
     * Actions to do after a favorite change (add, remove)
     */
    private void callActionsAfterFavoritesChange() {
    	FavoritesListeners.favoritesChanged();
    }
    
    @Override
    public void favoritesChanged() {
        // Mark favorites information as dirty
        getFavorites().setDirty(true);
    }

    @Override
    public void audioFilesRemoved(List<LocalAudioObject> audioFiles) {
        for (LocalAudioObject file : audioFiles) {
            // Remove from favorite audio files
            getFavorites().getFavoriteAudioFiles().remove(file.getUrl());

            // If artist has been removed then remove it from favorites too
            if (!RepositoryHandler.getInstance().getArtistStructure().containsKey(file.getArtist())) {
                getFavorites().getFavoriteArtists().remove(file.getArtist());
            } else {
                // If album has been removed then remove it from favorites too
                if (RepositoryHandler.getInstance().getArtistStructure().get(file.getArtist()).getAlbum(file.getAlbum()) == null) {
                    getFavorites().getFavoriteAlbums().remove(file.getAlbum());
                }
            }
        }
        callActionsAfterFavoritesChange();
    }

	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(AudioObject audioObject) {}
}
