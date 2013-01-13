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

package net.sourceforge.atunes.kernel.modules.instances;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;

/**
 * This class is responsible of create a queue of songs to be added. When
 * opening multiple files, OS launch a "slave" aTunes for every file, so this
 * queue adds songs in the order connections are made, and when no more
 * connections are received, then add to playlist
 */
class SongsQueue implements ActionListener {

	/** The songs queue. */
	private final List<IAudioObject> songsQueue;

	private Timer timer;

	private final IPlayListHandler playListHandler;

	/**
	 * Instantiates a new songs queue.
	 * 
	 * @param playListHandler
	 */
	SongsQueue(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
		this.songsQueue = new ArrayList<IAudioObject>();
	}

	/**
	 * Adds the song.
	 * 
	 * @param song
	 *            the song
	 */
	public void addSong(final IAudioObject song) {
		this.songsQueue.add(song);
		if (this.timer != null) {
			this.timer.restart();
		} else {
			this.timer = new Timer(1000, this);
			this.timer.start();
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (!this.songsQueue.isEmpty()) {
			// Get an auxiliar list with songs
			ArrayList<IAudioObject> auxList = new ArrayList<IAudioObject>(
					this.songsQueue);
			// Clear songs queue
			this.songsQueue.clear();
			// Add songs
			this.playListHandler.addToPlayListAndPlay(auxList);
		}
	}
}