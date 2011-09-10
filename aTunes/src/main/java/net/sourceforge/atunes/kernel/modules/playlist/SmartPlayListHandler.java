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
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.sourceforge.atunes.kernel.modules.repository.AudioObjectComparator;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.statistics.StatisticsHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;

public final class SmartPlayListHandler {

    /**
     * Singleton instance
     */
    private static SmartPlayListHandler instance;

    /**
     * Default constructor
     */
    private SmartPlayListHandler() {
        // Nothing to do
    }

    /**
     * Returns singleton instance of this class
     * 
     * @return
     */
    public static SmartPlayListHandler getInstance() {
        if (instance == null) {
            instance = new SmartPlayListHandler();
        }
        return instance;
    }

    /**
     * Gets n albums most played and adds to play list.
     * 
     * @param n
     *            the n
     */
    public void addAlbumsMostPlayed(int n) {
        // Get n most played albums
        List<Album> albums = StatisticsHandler.getInstance().getMostPlayedAlbums(n);

        // Songs selected
        List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

        // Add album songs
        for (Album a : albums) {
            songsSelected.addAll(RepositoryHandler.getInstance().getAudioFilesForAlbums(Collections.singletonMap(a.getName(), a)));
        }

        // Sort
        AudioObjectComparator.sort(songsSelected);

        // Add to playlist
        PlayListHandler.getInstance().addToPlayList(songsSelected);
    }

    /**
     * Gets n artists most played and adds to play list.
     * 
     * @param n
     *            the n
     */
    public void addArtistsMostPlayed(int n) {
        // Get n most played albums
        List<Artist> artists = StatisticsHandler.getInstance().getMostPlayedArtists(n);

        // Songs selected
        List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

        // Add album songs
        for (Artist a : artists) {
            songsSelected.addAll(a.getAudioObjects());
        }

        // Sort
        AudioObjectComparator.sort(songsSelected);

        // Add to playlist
        PlayListHandler.getInstance().addToPlayList(songsSelected);
    }

    /**
     * Gets a number of random songs and adds to play list.
     * 
     * @param n
     *            the n
     */
    public void addRandomSongs(int n) {
        // Get reference to Repository songs
        List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>(RepositoryHandler.getInstance().getAudioFilesList());

        // Songs selected
        List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

        // Initialize random generator
        Random r = new Random(new Date().getTime());

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
        PlayListHandler.getInstance().addToPlayList(songsSelected);
    }

    /**
     * Gets n songs most played and adds to play list.
     * 
     * @param n
     *            the n
     */
    public void addSongsMostPlayed(int n) {
        // Get songs
        List<ILocalAudioObject> songsSelected = StatisticsHandler.getInstance().getMostPlayedAudioFiles(n);

        // Sort
        AudioObjectComparator.sort(songsSelected);

        // Add to playlist
        PlayListHandler.getInstance().addToPlayList(songsSelected);
    }

    /**
     * Adds n unplayed songs to playlist.
     * 
     * @param n
     *            the n
     */
    public void addUnplayedSongs(int n) {
        // Get unplayed files
        List<ILocalAudioObject> unplayedSongs = StatisticsHandler.getInstance().getUnplayedAudioFiles();
        Collections.shuffle(unplayedSongs);

        // Add to playlist
        int count = Math.min(unplayedSongs.size(), n);
        if (count > 0) {
            List<IAudioObject> audioObjects = new ArrayList<IAudioObject>(unplayedSongs.subList(0, count));
            AudioObjectComparator.sort(audioObjects);
            PlayListHandler.getInstance().addToPlayList(audioObjects);
        }
    }
}
