/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.FavoritesListeners;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.UnlovesongInLastFmAction;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler;
import net.sourceforge.atunes.kernel.modules.search.searchableobjects.FavoritesSearchableObject;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.ITreeObject;

/**
 * The Class FavoritesHandler.
 */
public final class FavoritesHandler extends AbstractHandler implements IAudioFilesRemovedListener {

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
    public void applicationStateChanged(IState newState) {
    }

    @Override
    protected void initHandler() {
        RepositoryHandler.getInstance().addAudioFilesRemovedListener(this);
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
    }
    
    @Override
    public void allHandlersInitialized() {
        SearchHandler.getInstance().registerSearchableObject(FavoritesSearchableObject.getInstance());
    }

    /**
     * Adds or removes the favorite albums.
     * 
     * @param songs
     *            the songs
     */
    public void toggleFavoriteAlbums(List<ILocalAudioObject> songs) {
    	if (songs == null || songs.isEmpty()) {
    		return;
    	}
    	
    	List<ITreeObject<?>> toRemove = new ArrayList<ITreeObject<?>>();
        Map<String, Album> favAlbums = getFavorites().getFavoriteAlbums();
        for (int i = 0; i < songs.size(); i++) {
        	ILocalAudioObject f = songs.get(i);
            Artist artist = RepositoryHandler.getInstance().getArtist(f.getArtist());
            if (artist == null) {
                artist = RepositoryHandler.getInstance().getArtist(f.getAlbumArtist());
            }
            Album album = artist.getAlbum(f.getAlbum());
            if (favAlbums.containsValue(album)) {
            	toRemove.add(album);
            } else {
            	favAlbums.put(album.getName(), album);
            }
        }
        
        removeFromFavorites(toRemove);
        callActionsAfterFavoritesChange();
    }

    /**
     * Adds or removes the favorite artists.
     * 
     * @param songs
     *            the songs
     */
    public void toggleFavoriteArtists(List<ILocalAudioObject> songs) {
    	if (songs == null || songs.isEmpty()) {
    		return;
    	}

    	List<ITreeObject<?>> toRemove = new ArrayList<ITreeObject<?>>();    	
        Map<String, Artist> favArtists = getFavorites().getFavoriteArtists();
        for (int i = 0; i < songs.size(); i++) {
        	ILocalAudioObject f = songs.get(i);
            Artist artist = RepositoryHandler.getInstance().getArtist(f.getArtist());
            if (favArtists.containsValue(artist)) {
            	toRemove.add(artist);
            } else {
            	favArtists.put(artist.getName(), artist);
            }
        }
        removeFromFavorites(toRemove);
        callActionsAfterFavoritesChange();
    }

    /**
     * Adds or remvoves the favorite songs.
     * 
     * @param songs
     *            the songs
     */
    public void toggleFavoriteSongs(List<ILocalAudioObject> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }
        Map<String, ILocalAudioObject> favSongs = getFavorites().getFavoriteAudioFiles();
        List<IAudioObject> toRemove = new LinkedList<IAudioObject>();
        for (ILocalAudioObject song : songs) {
            //Toggle favorite songs
            if (favSongs.containsKey(song.getUrl())) {
                toRemove.add(song);
            } else {
            	favSongs.put(song.getUrl(), song);

            	// Add to LastFM if necessary
            	if (getState().isLastFmEnabled() && getState().isAutoLoveFavoriteSong()) {
            		// TODO: do this with a listener interface            	
            		((AddLovedSongInLastFMAction) Actions.getAction(AddLovedSongInLastFMAction.class)).loveSong(song);
            	}
            }
        }
        removeSongsFromFavorites(toRemove);
        callActionsAfterFavoritesChange();
    }

    /**
     * Finish.
     */
    @Override
    public void applicationFinish() {
        // Only store repository if it's dirty
        if (getFavorites().isDirty()) {
        	getBean(IStateHandler.class).persistFavoritesCache(getFavorites());
        } else {
            Logger.info("Favorites are clean");
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
                favorites = getBean(IStateHandler.class).retrieveFavoritesCache();
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
    public List<ILocalAudioObject> getFavoriteSongs() {
        return getFavorites().getAllFavoriteSongs();
    }

    /**
     * Gets the favorite songs map
     */
    public Map<String, ILocalAudioObject> getFavoriteSongsMap() {
        return getFavorites().getAllFavoriteSongsMap();
    }

    /**
     * Gets the favorite songs info.
     * 
     * @return the favorite songs info
     */
    public Map<String, ILocalAudioObject> getFavoriteSongsInfo() {
        return getFavorites().getFavoriteAudioFiles();
    }

    /**
     * Removes the objects from favorites.
     * 
     * @param objects
     *            list of objects of type TreeObject
     */
    public void removeFromFavorites(List<ITreeObject<?>> objects) {
        for (ITreeObject<? extends IAudioObject> obj : objects) {
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
    public void removeSongsFromFavorites(List<IAudioObject> files) {
    	for (IAudioObject file : files) {
    		getFavorites().getFavoriteAudioFiles().remove(file.getUrl());
    		// Unlove on LastFM if necessary
    		if (getState().isLastFmEnabled() && getState().isAutoLoveFavoriteSong()) {
    			// TODO: do this with a listener interface            	
    			((UnlovesongInLastFmAction) Actions.getAction(UnlovesongInLastFmAction.class)).unloveSong(file);
    		}
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
    public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
        for (ILocalAudioObject file : audioFiles) {
            // Remove from favorite audio files
            getFavorites().getFavoriteAudioFiles().remove(file.getUrl());

            // If artist has been removed then remove it from favorites too
            if (RepositoryHandler.getInstance().getArtist(file.getArtist()) == null) {
                getFavorites().getFavoriteArtists().remove(file.getArtist());
            } else {
                // If album has been removed then remove it from favorites too
                if (RepositoryHandler.getInstance().getArtist(file.getArtist()).getAlbum(file.getAlbum()) == null) {
                    getFavorites().getFavoriteAlbums().remove(file.getAlbum());
                }
            }
        }
        callActionsAfterFavoritesChange();
    }

	@Override
    public void playListCleared() {
    }

	@Override
    public void selectedAudioObjectChanged(IAudioObject audioObject) {
}
}
