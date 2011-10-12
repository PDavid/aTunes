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

package net.sourceforge.atunes.kernel.modules.instances;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;

/**
 * This class is responsible of create a queue of songs to be added. When
 * opening multiple files, OS launch a "slave" aTunes for every file, so
 * this queue adds songs in the order connections are made, and when no more
 * connections are received, then add to playlist
 */
class SongsQueue extends Thread {

    /** The songs queue. */
    private List<IAudioObject> songsQueue;

    /** The last song added. */
    private long lastSongAdded = 0;
    
    /**
     * Instantiates a new songs queue.
     */
    SongsQueue() {
        songsQueue = new ArrayList<IAudioObject>();
    }

    /**
     * Adds the song.
     * 
     * @param song
     *            the song
     */
    public void addSong(IAudioObject song) {
        songsQueue.add(song);
        lastSongAdded = System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        try {
            while (true) {
                if (!songsQueue.isEmpty() && lastSongAdded < System.currentTimeMillis() - 1000) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // Get an auxiliar list with songs
                            ArrayList<IAudioObject> auxList = new ArrayList<IAudioObject>(songsQueue);
                            // Clear songs queue
                            songsQueue.clear();
                            // Add songs
                            Context.getBean(IPlayListHandler.class).addToPlayListAndPlay(auxList);
                        }
                    });
                }
                // Wait one second always, even if songsQueue was not empty, to avoid entering again until songsQueue is cleared
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}