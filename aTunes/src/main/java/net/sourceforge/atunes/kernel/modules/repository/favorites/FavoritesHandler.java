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
import java.util.Set;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.FavoritesListeners;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.UnlovesongInLastFmAction;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.search.searchableobjects.FavoritesSearchableObject;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.Logger;

import org.apache.commons.collections.list.SetUniqueList;

/**
 * The Class FavoritesHandler.
 */
public final class FavoritesHandler extends AbstractHandler implements IAudioFilesRemovedListener, IFavoritesHandler {

	private IStateHandler stateHandler;
	
    /** The favorites. */
    private IFavorites favorites;

    public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}
    
    @Override
    protected void initHandler() {
        RepositoryHandler.getInstance().addAudioFilesRemovedListener(this);
    }

    @Override
    public void allHandlersInitialized() {
    	getBean(ISearchHandler.class).registerSearchableObject(FavoritesSearchableObject.getInstance());
    }

    @Override
	public void toggleFavoriteAlbums(List<ILocalAudioObject> songs) {
    	if (songs == null || songs.isEmpty()) {
    		return;
    	}
    	
        @SuppressWarnings("unchecked")
		Set<ILocalAudioObject> set = SetUniqueList.decorate(songs).asSet();

    	List<ITreeObject<?>> toRemove = new ArrayList<ITreeObject<?>>();
        Map<String, Album> favAlbums = favorites.getFavoriteAlbums();
        for (ILocalAudioObject f : set) {
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

    @Override
	public void toggleFavoriteArtists(List<ILocalAudioObject> songs) {
    	if (songs == null || songs.isEmpty()) {
    		return;
    	}
    	
        @SuppressWarnings("unchecked")
		Set<ILocalAudioObject> set = SetUniqueList.decorate(songs).asSet();

    	List<ITreeObject<?>> toRemove = new ArrayList<ITreeObject<?>>();    	
        Map<String, Artist> favArtists = favorites.getFavoriteArtists();
        for (ILocalAudioObject f : set) {
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

    @Override
	public void toggleFavoriteSongs(List<ILocalAudioObject> songs) {
        if (songs == null || songs.isEmpty()) {
            return;
        }
        
        @SuppressWarnings("unchecked")
		Set<ILocalAudioObject> set = SetUniqueList.decorate(songs).asSet();
        
        Map<String, ILocalAudioObject> favSongs = favorites.getFavoriteAudioFiles();
        List<IAudioObject> toRemove = new LinkedList<IAudioObject>();
        for (ILocalAudioObject song : set) {
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

    @Override
	public void addFavoriteSongs(List<ILocalAudioObject> songs, boolean automcaticallyLove) {
        if (songs == null || songs.isEmpty()) {
            return;
        }
        
        Map<String, ILocalAudioObject> favSongs = favorites.getFavoriteAudioFiles();
        for (ILocalAudioObject song : songs) {
        	favSongs.put(song.getUrl(), song);

        	// Add to web service if necessary
        	if (automcaticallyLove && getState().isLastFmEnabled() && getState().isAutoLoveFavoriteSong()) {
        		// TODO: do this with a listener interface            	
        		((AddLovedSongInLastFMAction) Actions.getAction(AddLovedSongInLastFMAction.class)).loveSong(song);
        	}
        }
        callActionsAfterFavoritesChange();
    }

    @Override
    public void applicationFinish() {
        // Only store repository if it's dirty
        if (((Favorites)favorites).isDirty()) {
        	stateHandler.persistFavoritesCache(favorites);
        } else {
            Logger.info("Favorites are clean");
        }
    }

    @Override
	public Map<String, Album> getFavoriteAlbumsInfo() {
        return favorites.getFavoriteAlbums();
    }

    @Override
	public Map<String, Artist> getFavoriteArtistsInfo() {
        return favorites.getFavoriteArtists();
    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new Runnable() {

            @Override
            public void run() {
                favorites = stateHandler.retrieveFavoritesCache();
                if (favorites == null) {
                	favorites = new Favorites();
                }
            }
        };
    }

    @Override
	public List<ILocalAudioObject> getFavoriteSongs() {
        return favorites.getAllFavoriteSongs();
    }

    @Override
	public Map<String, ILocalAudioObject> getFavoriteSongsMap() {
        return favorites.getAllFavoriteSongsMap();
    }

    @Override
	public Map<String, ILocalAudioObject> getFavoriteSongsInfo() {
        return favorites.getFavoriteAudioFiles();
    }

    @Override
	public void removeFromFavorites(List<ITreeObject<?>> objects) {
        for (ITreeObject<? extends IAudioObject> obj : objects) {
            if (obj instanceof Artist) {
                favorites.getFavoriteArtists().remove(obj.toString());
            } else {
                favorites.getFavoriteAlbums().remove(obj.toString());
            }
        }

        callActionsAfterFavoritesChange();
    }

    @Override
	public void removeSongsFromFavorites(List<IAudioObject> files) {
    	for (IAudioObject file : files) {
    		favorites.getFavoriteAudioFiles().remove(file.getUrl());
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
    	if (SwingUtilities.isEventDispatchThread()) {
    		FavoritesListeners.favoritesChanged();
    	} else {
    		SwingUtilities.invokeLater(new Runnable() {
    			@Override
    			public void run() {
    	    		FavoritesListeners.favoritesChanged();
    			}
    		});
    	}
    }
    
    @Override
    public void favoritesChanged() {
        // Mark favorites information as dirty
        ((Favorites)favorites).setDirty(true);
    }

    @Override
    public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
        for (ILocalAudioObject file : audioFiles) {
            // Remove from favorite audio files
            favorites.getFavoriteAudioFiles().remove(file.getUrl());

            // If artist has been removed then remove it from favorites too
            if (RepositoryHandler.getInstance().getArtist(file.getArtist()) == null) {
                favorites.getFavoriteArtists().remove(file.getArtist());
            } else {
                // If album has been removed then remove it from favorites too
                if (RepositoryHandler.getInstance().getArtist(file.getArtist()).getAlbum(file.getAlbum()) == null) {
                    favorites.getFavoriteAlbums().remove(file.getAlbum());
                }
            }
        }
        callActionsAfterFavoritesChange();
    }

    @Override
    public void updateFavorites(IRepository repository) {
    	List<IAudioObject> toRemove = new ArrayList<IAudioObject>();
    	for (ILocalAudioObject favorite : favorites.getFavoriteAudioFiles().values()) {
    		if (!repository.getFiles().contains(favorite)) {
    			toRemove.add(favorite);
    		}
    	}
    	if (!toRemove.isEmpty()) {
    		removeSongsFromFavorites(toRemove);
    	}
    }
}
