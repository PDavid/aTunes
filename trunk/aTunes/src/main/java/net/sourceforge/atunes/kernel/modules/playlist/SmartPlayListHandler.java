/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;

public final class SmartPlayListHandler {

    /**
     * Logger
     */
    private Logger logger = new Logger();

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
        logger.debug(LogCategories.HANDLER, new String[] { Integer.toString(n) });

        // Get n most played albums
        List<Album> albums = RepositoryHandler.getInstance().getMostPlayedAlbums(n);

        // Songs selected
        List<AudioObject> songsSelected = new ArrayList<AudioObject>();

        // Add album songs
        for (Album a : albums) {
            songsSelected.addAll(a.getAudioObjects());
        }

        // Sort
        songsSelected = RepositoryHandler.getInstance().sort(songsSelected);

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
        logger.debug(LogCategories.HANDLER, new String[] { Integer.toString(n) });

        // Get n most played albums
        List<Artist> artists = RepositoryHandler.getInstance().getMostPlayedArtists(n);

        // Songs selected
        List<AudioFile> songsSelected = new ArrayList<AudioFile>();

        // Add album songs
        for (Artist a : artists) {
            songsSelected.addAll(a.getAudioFiles());
        }

        // Sort
        List<AudioObject> songsSorted = RepositoryHandler.getInstance().sort(songsSelected);

        // Add to playlist
        PlayListHandler.getInstance().addToPlayList(songsSorted);
    }

    /**
     * Gets a number of random songs and adds to play list.
     * 
     * @param n
     *            the n
     */
    public void addRandomSongs(int n) {
        logger.debug(LogCategories.HANDLER, new String[] { Integer.toString(n) });

        // Get reference to Repository songs
        List<AudioFile> songs = RepositoryHandler.getInstance().getAudioFiles();

        // Songs selected
        List<AudioObject> songsSelected = new ArrayList<AudioObject>();

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
        songsSelected = RepositoryHandler.getInstance().sort(songsSelected);

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
        logger.debug(LogCategories.HANDLER, new String[] { Integer.toString(n) });

        // Get songs
        List<AudioFile> songsSelected = RepositoryHandler.getInstance().getMostPlayedAudioFiles(n);

        // Sort
        List<AudioObject> songsSorted = RepositoryHandler.getInstance().sort(songsSelected);

        // Add to playlist
        PlayListHandler.getInstance().addToPlayList(songsSorted);
    }

    /**
     * Adds n unplayed songs to playlist.
     * 
     * @param n
     *            the n
     */
    public void addUnplayedSongs(int n) {
        logger.debug(LogCategories.HANDLER, Integer.toString(n));

        // Get unplayed files
        List<AudioFile> unplayedSongs = RepositoryHandler.getInstance().getUnplayedAudioFiles();
        Collections.shuffle(unplayedSongs);

        // Add to playlist
        int count = Math.min(unplayedSongs.size(), n);
        if (count > 0) {
            PlayListHandler.getInstance().addToPlayList(RepositoryHandler.getInstance().sort(new ArrayList<AudioObject>(unplayedSongs.subList(0, count))));
        }
    }

}
