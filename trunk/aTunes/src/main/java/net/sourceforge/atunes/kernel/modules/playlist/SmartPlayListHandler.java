/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISmartPlayListHandler;
import net.sourceforge.atunes.model.IStatisticsHandler;

public final class SmartPlayListHandler extends AbstractHandler implements ISmartPlayListHandler {

    private IStatisticsHandler statisticsHandler;
    private IPlayListHandler playListHandler;
    
    private IAudioObjectComparator audioObjectComparator;
    
    /**
     * @param audioObjectComparator
     */
    public void setaudioObjectComparator(IAudioObjectComparator audioObjectComparator) {
		this.audioObjectComparator = audioObjectComparator;
	}
    
    /**
     * @param statisticsHandler
     */
    public void setStatisticsHandler(IStatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    @Override
	public void addAlbumsMostPlayed(int n) {
        // Get n most played albums
        List<IAlbum> albums = statisticsHandler.getMostPlayedAlbums(n);

        // Songs selected
        List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

        // Add album songs
        for (IAlbum a : albums) {
            songsSelected.addAll(getBean(IRepositoryHandler.class).getAudioFilesForAlbums(Collections.singletonMap(a.getName(), a)));
        }

        // Sort
        audioObjectComparator.sort(songsSelected);

        // Add to playlist
        playListHandler.addToPlayList(songsSelected);
    }

    @Override
	public void addArtistsMostPlayed(int n) {
        // Get n most played albums
        List<IArtist> artists = statisticsHandler.getMostPlayedArtists(n);

        // Songs selected
        List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

        // Add album songs
        for (IArtist a : artists) {
            songsSelected.addAll(a.getAudioObjects());
        }

        // Sort
        audioObjectComparator.sort(songsSelected);

        // Add to playlist
        playListHandler.addToPlayList(songsSelected);
    }

    @Override
	public void addRandomSongs(int n) {
        // Get reference to Repository songs
        List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>(getBean(IRepositoryHandler.class).getAudioFilesList());

        // Songs selected
        List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

        // Initialize random generator
        Random r = new Random(System.currentTimeMillis());

        // Get n songs
        for (int i = 0; i < n; i++) {
            // Get song number
            int number = r.nextInt(songs.size());

            // Add selectedSong
            songsSelected.add(songs.get(number));
        }

        // Sort
        audioObjectComparator.sort(songsSelected);

        // Add to playlist
        playListHandler.addToPlayList(songsSelected);
    }

    @Override
	public void addSongsMostPlayed(int n) {
        // Get songs
        List<IAudioObject> songsSelected = statisticsHandler.getMostPlayedAudioObjects(n);

        // Sort
        audioObjectComparator.sort(songsSelected);

        // Add to playlist
        playListHandler.addToPlayList(songsSelected);
    }

    @Override
	public void addUnplayedSongs(int n) {
        // Get unplayed files
        List<IAudioObject> unplayedSongs = statisticsHandler.getUnplayedAudioObjects();
        Collections.shuffle(unplayedSongs);

        // Add to playlist
        int count = Math.min(unplayedSongs.size(), n);
        if (count > 0) {
            List<IAudioObject> audioObjects = new ArrayList<IAudioObject>(unplayedSongs.subList(0, count));
            audioObjectComparator.sort(audioObjects);
            playListHandler.addToPlayList(audioObjects);
        }
    }
}
