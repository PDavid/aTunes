/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.Map;

import net.sourceforge.atunes.kernel.ApplicationFinishListener;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler;
import net.sourceforge.atunes.kernel.modules.search.searchableobjects.FavoritesSearchableObject;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.TreeObject;

/**
 * The Class FavoritesHandler.
 */
public final class FavoritesHandler implements ApplicationFinishListener {

    /** The instance. */
    private static FavoritesHandler instance = new FavoritesHandler();

    /** The favorites. */
    private Favorites favorites;

    FavoritesHandler() {
        Kernel.getInstance().addFinishListener(this);
    }

    /**
     * Gets the single instance of FavoritesHandler.
     * 
     * @return single instance of FavoritesHandler
     */
    public static FavoritesHandler getInstance() {
        return instance;
    }

    /**
     * Returns a runnable object to be executed after application start
     */
    public Runnable getAfterStartActionsRunnable() {
        // TODO: create an interface with this method for all classes that need execute code after kernel start
        return new Runnable() {
            @Override
            public void run() {
                SearchHandler.getInstance().registerSearchableObject(FavoritesSearchableObject.getInstance());
            }
        };
    }

    /**
     * Adds the favorite albums.
     * 
     * @param songs
     *            the songs
     */
    public void addFavoriteAlbums(List<AudioFile> songs) {
        Map<String, Artist> structure = RepositoryHandler.getInstance().getRepository().getStructure().getTreeStructure();
        Map<String, Album> favAlbums = favorites.getFavoriteAlbums();
        for (int i = 0; i < songs.size(); i++) {
            AudioFile f = songs.get(i);
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
    public void addFavoriteArtists(List<AudioFile> songs) {
        Map<String, Artist> structure = RepositoryHandler.getInstance().getRepository().getStructure().getTreeStructure();
        Map<String, Artist> favArtists = favorites.getFavoriteArtists();
        for (int i = 0; i < songs.size(); i++) {
            AudioFile f = songs.get(i);
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
    public void addFavoriteSongs(List<AudioFile> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }
        Map<String, AudioFile> favSongs = favorites.getFavoriteAudioFiles();
        for (AudioFile song : songs) {
            favSongs.put(song.getUrl(), song);

            // Add to LastFM if necessary
            if (ApplicationState.getInstance().isLastFmEnabled() && ApplicationState.getInstance().isAutoLoveFavoriteSong()) {
                ContextHandler.getInstance().loveSong(song);
            }
        }
        callActionsAfterFavoritesChange();
    }

    /**
     * Finish.
     */
    public void applicationFinish() {
        ApplicationStateHandler.getInstance().persistFavoritesCache(favorites);
    }

    /**
     * Gets the favorite albums info.
     * 
     * @return the favorite albums info
     */
    public Map<String, Album> getFavoriteAlbumsInfo() {
        return favorites.getFavoriteAlbums();
    }

    /**
     * Gets the favorite artists info.
     * 
     * @return the favorite artists info
     */
    public Map<String, Artist> getFavoriteArtistsInfo() {
        return favorites.getFavoriteArtists();
    }

    /**
     * Gets the favorites.
     * 
     * @return the favorites
     */
    public Favorites getFavorites() {
        // TODO: this method should be removed to manage favorites objects only in this class
        return favorites;
    }

    /**
     * Gets the favorite songs.
     * 
     * @return the favorite songs
     */
    public List<AudioFile> getFavoriteSongs() {
        return favorites.getAllFavoriteSongs();
    }

    /**
     * Gets the favorite songs map
     */
    public Map<String, AudioFile> getFavoriteSongsMap() {
        return favorites.getAllFavoriteSongsMap();
    }

    /**
     * Gets the favorite songs info.
     * 
     * @return the favorite songs info
     */
    public Map<String, AudioFile> getFavoriteSongsInfo() {
        return favorites.getFavoriteAudioFiles();
    }

    /**
     * Read favorites.
     */
    void readFavorites() {
        favorites = ApplicationStateHandler.getInstance().retrieveFavoritesCache();
        if (favorites == null) {
            favorites = new Favorites();
        }
    }

    /**
     * Runnable process to read favorites cache.
     * 
     * @return the read favotires runnable
     */
    public Runnable getReadFavotiresRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                readFavorites();
            }
        };
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
                favorites.getFavoriteArtists().remove(obj.toString());
            } else {
                favorites.getFavoriteAlbums().remove(obj.toString());
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
    public void removeSongsFromFavorites(List<AudioFile> files) {
        for (AudioFile file : files) {
            favorites.getFavoriteAudioFiles().remove(file.getUrl());
        }

        callActionsAfterFavoritesChange();
    }

    /**
     * Actions to do after a favorite change (add, remove)
     */
    private void callActionsAfterFavoritesChange() {
        // Update playlist to remove favorite icon
        ControllerProxy.getInstance().getPlayListController().refreshPlayList();

        // Update all views
        NavigationHandler.getInstance().refreshCurrentView();

        // Update file properties panel
        ControllerProxy.getInstance().getFilePropertiesController().refreshFavoriteIcons();
    }

}
