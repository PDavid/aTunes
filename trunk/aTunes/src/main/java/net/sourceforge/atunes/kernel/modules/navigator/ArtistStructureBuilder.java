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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.Album;
import net.sourceforge.atunes.kernel.modules.repository.Artist;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

class ArtistStructureBuilder {

	private final IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	ArtistStructureBuilder(final IUnknownObjectChecker unknownObjectChecker) {
		super();
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * Returns an structure of artists and albums containing songs from a list
	 * 
	 * @param audioFiles
	 * @return
	 */
	Map<String, IArtist> getArtistObjects(final List<ILocalAudioObject> audioFiles) {
		Map<String, IArtist> structure = new HashMap<String, IArtist>();
		for (ILocalAudioObject song : audioFiles) {
			String artist = !song.getAlbumArtist(unknownObjectChecker).equals("") ? song.getAlbumArtist(unknownObjectChecker) : song.getArtist(unknownObjectChecker);
			if (!structure.containsKey(artist)) {
				structure.put(artist, new Artist(artist));
			}
			if (!structure.get(artist).getAlbums().containsKey(song.getAlbum(unknownObjectChecker))) {
				IAlbum album = new Album(structure.get(artist), song.getAlbum(unknownObjectChecker));
				structure.get(artist).addAlbum(album);
			}
			structure.get(artist).getAlbum(song.getAlbum(unknownObjectChecker)).addAudioFile(song);
		}
		return structure;
	}

	/**
	 * Returns all artists from a list of audio object
	 * @param audioFiles
	 * @return
	 */
	List<String> getArtistList(final List<ILocalAudioObject> audioFiles) {
		List<String> artists = new ArrayList<String>();
		for (ILocalAudioObject song : audioFiles) {
			if (!song.getAlbumArtist(unknownObjectChecker).equals("")) {
				artists.add(song.getAlbumArtist(unknownObjectChecker));
			} else {
				artists.add(song.getArtist(unknownObjectChecker));
			}
		}
		return artists;
	}
}
