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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.atunes.kernel.modules.repository.Album;
import net.sourceforge.atunes.kernel.modules.repository.Artist;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Organizes audio objects in structures of artists for trees
 * 
 * @author alex
 * 
 */
public class ArtistStructureBuilder {

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * Returns an structure of artists and albums containing songs from a list
	 * 
	 * @param audioFiles
	 * @return
	 */
	Map<String, IArtist> getArtistObjects(
			final List<ILocalAudioObject> audioFiles) {
		return getArtistObjects(audioFiles, null, null);
	}

	/**
	 * Returns an structure of artists and albums containing songs from a list
	 * and with given filter for artist and album
	 * 
	 * @param audioFiles
	 * @param artistFilter
	 * @param albumFilter
	 * @return
	 */
	Map<String, IArtist> getArtistObjects(
			final List<ILocalAudioObject> audioFiles,
			final String artistFilter, final String albumFilter) {
		Map<String, IArtist> structure = new HashMap<String, IArtist>();
		for (ILocalAudioObject song : audioFiles) {
			String artist = song
					.getAlbumArtistOrArtist(this.unknownObjectChecker);
			if (StringUtils.isEmpty(artistFilter)
					|| artist.toUpperCase()
							.contains(artistFilter.toUpperCase())) {
				if (!structure.containsKey(artist)) {
					structure.put(artist, new Artist(artist));
				}
				String albumName = song.getAlbum(this.unknownObjectChecker);
				if (StringUtils.isEmpty(albumFilter)
						|| albumName.toUpperCase().contains(
								albumFilter.toUpperCase())) {
					if (!structure.get(artist).getAlbums()
							.containsKey(albumName)) {
						structure.get(artist).addAlbum(
								new Album(structure.get(artist), albumName));
					}
					structure.get(artist).getAlbum(albumName)
							.addAudioFile(song);
				}
			}
		}
		return structure;
	}

	/**
	 * Returns an structure of artists and albums containing songs from a list
	 * and with given filter for artist OR album
	 * 
	 * @param audioFiles
	 * @param artistFilter
	 * @param albumFilter
	 * @return
	 */
	Map<String, IArtist> getArtistObjectsMatchingFilterArtistOrAlbum(
			final List<ILocalAudioObject> audioFiles,
			final String artistFilter, final String albumFilter) {
		Map<String, IArtist> structure = new HashMap<String, IArtist>();
		for (ILocalAudioObject song : audioFiles) {
			String artist = song
					.getAlbumArtistOrArtist(this.unknownObjectChecker);
			String albumName = song.getAlbum(this.unknownObjectChecker);
			if ((StringUtils.isEmpty(artistFilter) || artist.toUpperCase()
					.contains(artistFilter.toUpperCase()))
					|| (StringUtils.isEmpty(albumFilter) || albumName
							.toUpperCase().contains(albumFilter.toUpperCase()))) {

				if (!structure.containsKey(artist)) {
					structure.put(artist, new Artist(artist));
				}
				if (!structure.get(artist).getAlbums().containsKey(albumName)) {
					structure.get(artist).addAlbum(
							new Album(structure.get(artist), albumName));
				}
				structure.get(artist).getAlbum(albumName).addAudioFile(song);
			}
		}
		return structure;
	}

	/**
	 * Returns all artists from a list of audio object
	 * 
	 * @param audioFiles
	 * @return
	 */
	List<String> getArtistList(final List<ILocalAudioObject> audioFiles) {
		return getArtistList(audioFiles, null);
	}

	/**
	 * Returns artists from a list of audio object filtered
	 * 
	 * @param audioFiles
	 * @param filter
	 * @return
	 */
	List<String> getArtistList(final List<ILocalAudioObject> audioFiles,
			final String filter) {
		Set<String> artists = new HashSet<String>();
		for (ILocalAudioObject song : audioFiles) {
			String artist = song
					.getAlbumArtistOrArtist(this.unknownObjectChecker);
			if (StringUtils.isEmpty(filter)
					|| artist.toUpperCase().contains(filter.toUpperCase())) {
				artists.add(artist);
			}
		}
		return new ArrayList<String>(artists);
	}

	/**
	 * Returns albums from a list of audio object filtered
	 * 
	 * @param audioFiles
	 * @param filter
	 * @return
	 */
	List<String> getAlbumsList(final List<ILocalAudioObject> audioFiles,
			final String filter) {
		Set<String> albums = new HashSet<String>();
		for (ILocalAudioObject song : audioFiles) {
			String album = song.getAlbum(this.unknownObjectChecker);
			if (StringUtils.isEmpty(filter)
					|| album.toUpperCase().contains(filter.toUpperCase())) {
				albums.add(album);
			}
		}
		return new ArrayList<String>(albums);
	}

}
