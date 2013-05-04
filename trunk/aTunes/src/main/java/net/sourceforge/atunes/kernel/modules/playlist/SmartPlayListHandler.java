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
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * Creates play lists based on statistics
 * 
 * @author alex
 * 
 */
public final class SmartPlayListHandler extends AbstractHandler implements
		ISmartPlayListHandler {

	private IStatisticsHandler statisticsHandler;
	private IPlayListHandler playListHandler;

	private IRepositoryHandler repositoryHandler;

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param statisticsHandler
	 */
	public void setStatisticsHandler(final IStatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	@Override
	public void addAlbumsMostPlayed(final int n) {
		// Get n most played albums
		List<IAlbum> albums = this.statisticsHandler.getMostPlayedAlbums(n);

		// Songs selected
		List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

		// Add album songs
		for (IAlbum a : albums) {
			songsSelected.addAll(this.repositoryHandler
					.getAudioFilesForAlbums(Collections.singletonList(a)));
		}

		// Sort
		getBean(IAudioObjectComparator.class).sort(songsSelected);

		// Add to playlist
		this.playListHandler.addToVisiblePlayList(songsSelected);
	}

	@Override
	public void addArtistsMostPlayed(final int n) {
		// Get n most played albums
		List<IArtist> artists = this.statisticsHandler.getMostPlayedArtists(n);

		// Songs selected
		List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

		// Add album songs
		for (IArtist a : artists) {
			songsSelected.addAll(a.getAudioObjects());
		}

		// Sort
		getBean(IAudioObjectComparator.class).sort(songsSelected);

		// Add to playlist
		this.playListHandler.addToVisiblePlayList(songsSelected);
	}

	@Override
	public void addRandomSongs(final int n) {
		// Get reference to Repository songs
		List<ILocalAudioObject> songs = null;
		if (this.repositoryHandler.getAudioFilesList() != null) {
			songs = new ArrayList<ILocalAudioObject>(
					this.repositoryHandler.getAudioFilesList());
		} else {
			songs = new ArrayList<ILocalAudioObject>();
		}

		// Songs selected
		List<IAudioObject> songsSelected = new ArrayList<IAudioObject>();

		if (!CollectionUtils.isEmpty(songs)) {
			// Initialize random generator
			Random r = new Random(System.currentTimeMillis());

			// Get n songs
			for (int i = 0; i < n; i++) {
				// Get song number
				int number = r.nextInt(songs.size());

				// Add selectedSong
				songsSelected.add(songs.get(number));
			}
		}

		// Sort
		getBean(IAudioObjectComparator.class).sort(songsSelected);

		// Add to playlist
		this.playListHandler.addToVisiblePlayList(songsSelected);
	}

	@Override
	public void addSongsMostPlayed(final int n) {
		// Get songs
		List<IAudioObject> songsSelected = this.statisticsHandler
				.getMostPlayedAudioObjects(n);

		// Sort
		getBean(IAudioObjectComparator.class).sort(songsSelected);

		// Add to playlist
		this.playListHandler.addToVisiblePlayList(songsSelected);
	}

	@Override
	public void addUnplayedSongs(final int n) {
		// Get unplayed files
		List<IAudioObject> unplayedSongs = this.statisticsHandler
				.getUnplayedAudioObjects();
		Collections.shuffle(unplayedSongs);

		// Add to playlist
		int count = Math.min(unplayedSongs.size(), n);
		if (count > 0) {
			List<IAudioObject> audioObjects = new ArrayList<IAudioObject>(
					unplayedSongs.subList(0, count));
			getBean(IAudioObjectComparator.class).sort(audioObjects);
			this.playListHandler.addToVisiblePlayList(audioObjects);
		}
	}
}
