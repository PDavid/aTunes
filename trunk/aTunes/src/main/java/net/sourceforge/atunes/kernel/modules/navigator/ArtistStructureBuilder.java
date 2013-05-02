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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.Album;
import net.sourceforge.atunes.kernel.modules.repository.Artist;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

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
		Map<String, IArtist> structure = new HashMap<String, IArtist>();
		for (ILocalAudioObject song : audioFiles) {
			String artist = song
					.getAlbumArtistOrArtist(this.unknownObjectChecker);
			if (!structure.containsKey(artist)) {
				structure.put(artist, new Artist(artist));
			}
			String albumName = song.getAlbum(this.unknownObjectChecker);
			if (!structure.get(artist).getAlbums().containsKey(albumName)) {
				structure.get(artist).addAlbum(
						new Album(structure.get(artist), albumName));
			}
			structure.get(artist).getAlbum(albumName).addAudioFile(song);
		}
		return structure;
	}
}
