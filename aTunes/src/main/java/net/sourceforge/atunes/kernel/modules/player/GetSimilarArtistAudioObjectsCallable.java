/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * Retrieve audio objects from similar artists
 * @author Laurent Cathala
 *
 */
public class GetSimilarArtistAudioObjectsCallable implements Callable<List<IAudioObject>> {
	
	// to be parameterized
	private static final int NUMBER_OF_TRACKS = 1;
	
	private static final int MAX_DEEP_ARTIST_SEARCH = 10;
	
	private String artistName;
	
	private Map<String, List<ILocalAudioObject>> notAlreadySelectedSongsForArtist; 
	
	private IWebServicesHandler webServicesHandler;
	
	private IRepositoryHandler repositoryHandler;
	
	private Random randomGenerator = new Random(System.currentTimeMillis());
	
	/**
	 * @param artistName
	 */
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	
	/**
	 * @param notAlreadySelectedSongsForArtist
	 */
	public void setNotAlreadySelectedSongsForArtist(Map<String, List<ILocalAudioObject>> notAlreadySelectedSongsForArtist) {
		this.notAlreadySelectedSongsForArtist = notAlreadySelectedSongsForArtist;
	}
	
	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
	
	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
	
	@Override
	public List<IAudioObject> call() {
		List<IAudioObject> newTracks = new ArrayList<IAudioObject>(); 
		// First get artist from which select songs
		List<IArtist> artistsList = getSimilarArtistList(0, artistName);
		while (newTracks.size() < NUMBER_OF_TRACKS && !artistsList.isEmpty()) {
			// Get a random similar artist
			IArtist currentArtist = artistsList.get(randomGenerator.nextInt(artistsList.size()));
			// Then select a song
			IAudioObject artistSong = getRandomTrackFromArtist(currentArtist, artistsList);
			if (artistSong != null){
				newTracks.add(artistSong);
			}
		}
		return newTracks;
	}
	
	/**
	 * Returns list of similar artists with available songs
	 * @param currentDeep
	 * @param artist
	 * @return
	 */
	private List<IArtist> getSimilarArtistList(int currentDeep, String artist) {
		ISimilarArtistsInfo artists = webServicesHandler.getSimilarArtists(artist);
		List<IArtistInfo> similarArtistsInfo = artists.getArtists();

		//filter match artists
		List<IArtist> availlableArtistsList = getSimilarArtistsWithNotPlayedSongs(similarArtistsInfo);

		// search similar of similar if necessary
		if (availlableArtistsList.isEmpty() && currentDeep < MAX_DEEP_ARTIST_SEARCH){
			// get similar artists of similar
			return getSimilarArtistList(currentDeep + 1, similarArtistsInfo.get(currentDeep).getName());
		}

		return availlableArtistsList;
	}

	/**
	 * @param similarArtistsInfo
	 * @return
	 */
	private List<IArtist> getSimilarArtistsWithNotPlayedSongs(List<IArtistInfo> similarArtistsInfo) {
		List<IArtist> availlableArtistsList = new ArrayList<IArtist>();
		for (IArtistInfo artistInfo : similarArtistsInfo) {
			IArtist currentArtist = repositoryHandler.getArtist(artistInfo.getName());
			if (currentArtist != null) {
				List<ILocalAudioObject> alreadyArtistPlayedSong = notAlreadySelectedSongsForArtist.get(artistInfo.getName());
				if (alreadyArtistPlayedSong == null || !alreadyArtistPlayedSong.isEmpty()){
					availlableArtistsList.add(currentArtist);
				}
			}
		}
		return availlableArtistsList;
	}
	
	/**
	 * Get a random track from an Artist
	 * @param currentArtist
	 * @param artistsList
	 * @return
	 */
	private IAudioObject getRandomTrackFromArtist(IArtist currentArtist, List<IArtist> artistsList){
		// Get not already selected songs (or all artist songs if none selected before)
		List<ILocalAudioObject> artistSongs = notAlreadySelectedSongsForArtist.get(currentArtist.getName());
		if (artistSongs == null) {
			artistSongs = currentArtist.getAudioObjects();
			notAlreadySelectedSongsForArtist.put(currentArtist.getName(), artistSongs);
		}

		IAudioObject randomTrack = null;
		if (!artistSongs.isEmpty()) {
			int randomIndex = randomGenerator.nextInt(artistSongs.size());
			randomTrack = artistSongs.get(randomIndex);
			artistSongs.remove(randomIndex);
			if (artistSongs.isEmpty()) {
				artistsList.remove(currentArtist);
			}
		}

		return randomTrack;
	}
}