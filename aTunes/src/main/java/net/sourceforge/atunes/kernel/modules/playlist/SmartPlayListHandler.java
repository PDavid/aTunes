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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.repository.AudioObjectComparator;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ISmartPlayListHandler;
import net.sourceforge.atunes.model.IStatisticsHandler;

public final class SmartPlayListHandler extends AbstractHandler implements ISmartPlayListHandler {

    private IStatisticsHandler statisticsHandler;
    private IPlayListHandler playListHandler;
    
    @Override
    protected void initHandler() {
    	statisticsHandler = getBean(IStatisticsHandler.class);
    	playListHandler = getBean(IPlayListHandler.class);
    }
    
    @Override
	public void addAlbumsMostPlayed(int n) {
        // Get n most played albums
        List<Album> albums = statisticsHandler.getMostPlayedAlbums(n);

        // Songs selected
        List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

        // Add album songs
        for (Album a : albums) {
            songsSelected.addAll(RepositoryHandler.getInstance().getAudioFilesForAlbums(Collections.singletonMap(a.getName(), a)));
        }

        // Sort
        AudioObjectComparator.sort(songsSelected);

        // Add to playlist
        playListHandler.addToPlayList(songsSelected);
    }

    @Override
	public void addArtistsMostPlayed(int n) {
        // Get n most played albums
        List<Artist> artists = statisticsHandler.getMostPlayedArtists(n);

        // Songs selected
        List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

        // Add album songs
        for (Artist a : artists) {
            songsSelected.addAll(a.getAudioObjects());
        }

        // Sort
        AudioObjectComparator.sort(songsSelected);

        // Add to playlist
        playListHandler.addToPlayList(songsSelected);
    }

    @Override
	public void addRandomSongs(int n) {
        // Get reference to Repository songs
        List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>(RepositoryHandler.getInstance().getAudioFilesList());

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
        AudioObjectComparator.sort(songsSelected);

        // Add to playlist
        playListHandler.addToPlayList(songsSelected);
    }

    @Override
	public void addSongsMostPlayed(int n) {
        // Get songs
        List<IAudioObject> songsSelected = statisticsHandler.getMostPlayedAudioObjects(n);

        // Sort
        AudioObjectComparator.sort(songsSelected);

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
            AudioObjectComparator.sort(audioObjects);
            playListHandler.addToPlayList(audioObjects);
        }
    }
}
