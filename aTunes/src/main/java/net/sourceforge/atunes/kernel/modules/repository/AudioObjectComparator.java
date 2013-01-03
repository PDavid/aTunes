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

package net.sourceforge.atunes.kernel.modules.repository;

import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

/**
 * Default comparator used to sort audio objects
 * 
 * @author fleax
 * 
 */
public class AudioObjectComparator implements IAudioObjectComparator {

	private static final long serialVersionUID = -1297150534262349691L;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	public int compare(final IAudioObject a1, final IAudioObject a2) {
		// Sort by album artist
		int c1 = a1.getAlbumArtist(unknownObjectChecker).compareTo(a2.getAlbumArtist(unknownObjectChecker));
		if (c1 != 0) {
			return c1;
		}

		/*
		 * If album artist is "" in both audio objects (we just need to check
		 * only one audio object since if execution reaches this code both album
		 * artist fields are equal) then sort by artist, album and track If
		 * album artist is not "", then only sort by album and track
		 */
		if (a1.getAlbumArtist(unknownObjectChecker).isEmpty()) {
			int c2 = a1.getArtist(unknownObjectChecker).compareTo(a2.getArtist(unknownObjectChecker));
			if (c2 != 0) {
				return c2;
			}
		}

		// Sort by album
		int c3 = a1.getAlbum(unknownObjectChecker).compareTo(a2.getAlbum(unknownObjectChecker));
		if (c3 != 0) {
			return c3;
		}

		// Sort by disc number
		int c4 = Integer.valueOf(a1.getDiscNumber()).compareTo(a2.getDiscNumber());
		if (c4 != 0) {
			return c4;
		}

		return Integer.valueOf(a1.getTrackNumber()).compareTo(a2.getTrackNumber());
	}

	/**
	 * Sorts given list with this comparator
	 * 
	 * @param audioObjects
	 */
	@Override
	public void sort(final List<? extends IAudioObject> audioObjects) {
		Collections.sort(audioObjects, this);
	}
}
