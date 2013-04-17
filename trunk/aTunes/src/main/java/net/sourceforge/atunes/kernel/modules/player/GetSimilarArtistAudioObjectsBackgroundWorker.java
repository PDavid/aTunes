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

package net.sourceforge.atunes.kernel.modules.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * Retrieve audio objects from similar artists
 * 
 * @author Laurent Cathala
 * 
 */
public class GetSimilarArtistAudioObjectsBackgroundWorker extends
		BackgroundWorker<List<IAudioObject>, Void> {

	// to be parameterized
	private static final int NUMBER_OF_TRACKS = 1;

	private static final int MAX_DEEP_ARTIST_SEARCH = 10;

	private String artistName;

	private Map<String, List<ILocalAudioObject>> notAlreadySelectedSongsForArtist;

	private IWebServicesHandler webServicesHandler;

	private IRepositoryHandler repositoryHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	private IPlayListHandler playListHandler;

	private final Random randomGenerator = new Random(
			System.currentTimeMillis());

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param currentAudioObject
	 * @param notAlreadySelectedSongsForArtist
	 */
	public void getSimilarArtists(
			final IAudioObject currentAudioObject,
			final Map<String, List<ILocalAudioObject>> notAlreadySelectedSongsForArtist) {
		this.artistName = currentAudioObject
				.getArtist(this.unknownObjectChecker);
		this.notAlreadySelectedSongsForArtist = notAlreadySelectedSongsForArtist;
		execute();
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(final List<Void> chunks) {
	}

	@Override
	protected List<IAudioObject> doInBackground() {
		List<IAudioObject> newTracks = new ArrayList<IAudioObject>();
		// First get artist from which select songs
		List<IArtist> artistsList = getSimilarArtistList(0, this.artistName);
		while (newTracks.size() < NUMBER_OF_TRACKS && !artistsList.isEmpty()) {
			// Get a random similar artist
			IArtist currentArtist = artistsList.get(this.randomGenerator
					.nextInt(artistsList.size()));
			// Then select a song
			IAudioObject artistSong = getRandomTrackFromArtist(currentArtist,
					artistsList);
			if (artistSong != null) {
				newTracks.add(artistSong);
			}
		}
		return newTracks;
	}

	@Override
	protected void done(final List<IAudioObject> result) {
		this.playListHandler.addToVisiblePlayList(result);
	}

	/**
	 * Returns list of similar artists with available songs
	 * 
	 * @param currentDeep
	 * @param artist
	 * @return
	 */
	private List<IArtist> getSimilarArtistList(final int currentDeep,
			final String artist) {
		ISimilarArtistsInfo artists = this.webServicesHandler
				.getSimilarArtists(artist);
		List<IArtistInfo> similarArtistsInfo = artists.getArtists();

		// filter match artists
		List<IArtist> availlableArtistsList = getSimilarArtistsWithNotPlayedSongs(similarArtistsInfo);

		// search similar of similar if necessary
		if (availlableArtistsList.isEmpty()
				&& currentDeep < MAX_DEEP_ARTIST_SEARCH) {
			// get similar artists of similar
			return getSimilarArtistList(currentDeep + 1, similarArtistsInfo
					.get(currentDeep).getName());
		}

		return availlableArtistsList;
	}

	/**
	 * @param similarArtistsInfo
	 * @return
	 */
	private List<IArtist> getSimilarArtistsWithNotPlayedSongs(
			final List<IArtistInfo> similarArtistsInfo) {
		List<IArtist> availlableArtistsList = new ArrayList<IArtist>();
		for (IArtistInfo artistInfo : similarArtistsInfo) {
			IArtist currentArtist = this.repositoryHandler.getArtist(artistInfo
					.getName());
			if (currentArtist != null) {
				List<ILocalAudioObject> alreadyArtistPlayedSong = this.notAlreadySelectedSongsForArtist
						.get(artistInfo.getName());
				if (alreadyArtistPlayedSong == null
						|| !alreadyArtistPlayedSong.isEmpty()) {
					availlableArtistsList.add(currentArtist);
				}
			}
		}
		return availlableArtistsList;
	}

	/**
	 * Get a random track from an Artist
	 * 
	 * @param currentArtist
	 * @param artistsList
	 * @return
	 */
	private IAudioObject getRandomTrackFromArtist(final IArtist currentArtist,
			final List<IArtist> artistsList) {
		// Get not already selected songs (or all artist songs if none selected
		// before)
		List<ILocalAudioObject> artistSongs = this.notAlreadySelectedSongsForArtist
				.get(currentArtist.getName());
		if (artistSongs == null) {
			artistSongs = currentArtist.getAudioObjects();
			this.notAlreadySelectedSongsForArtist.put(currentArtist.getName(),
					artistSongs);
		}

		IAudioObject randomTrack = null;
		if (!artistSongs.isEmpty()) {
			int randomIndex = this.randomGenerator.nextInt(artistSongs.size());
			randomTrack = artistSongs.get(randomIndex);
			artistSongs.remove(randomIndex);
			if (artistSongs.isEmpty()) {
				artistsList.remove(currentArtist);
			}
		}

		return randomTrack;
	}
}