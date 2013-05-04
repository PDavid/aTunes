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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

/**
 * Filters and utils for audio objects
 * 
 * @author alex
 * 
 */
public class LocalAudioObjectFilter implements ILocalAudioObjectFilter {

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * Gets the local audio objects from a list of audio objects
	 * 
	 * @param audioObjects
	 * @return
	 */
	@Override
	public List<ILocalAudioObject> getLocalAudioObjects(
			final List<IAudioObject> audioObjects) {
		if (audioObjects == null || audioObjects.isEmpty()) {
			return Collections.emptyList();
		}
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (IAudioObject audioObject : audioObjects) {
			if (audioObject instanceof ILocalAudioObject) {
				result.add((ILocalAudioObject) audioObject);
			}
		}
		return result;
	}

	@Override
	public List<IAudioObject> getAudioObjects(
			final List<ILocalAudioObject> audioObjects) {
		if (audioObjects == null || audioObjects.isEmpty()) {
			return Collections.emptyList();
		}
		List<IAudioObject> result = new ArrayList<IAudioObject>();
		for (ILocalAudioObject audioObject : audioObjects) {
			result.add(audioObject);
		}
		return result;
	}

	/**
	 * Returns a list where there are no repeated audio objects (same title and
	 * artist)
	 * 
	 * @param list
	 * @return
	 */
	@Override
	public List<ILocalAudioObject> filterRepeatedObjects(
			final List<ILocalAudioObject> list) {
		return filterWithHash(list, new RepeatedObjectsHashCalculator(
				this.unknownObjectChecker));
	}

	/**
	 * Returns a list where there are no repeated audio objects (same title and
	 * album and artist)
	 * 
	 * @param list
	 * @return
	 */
	@Override
	public List<ILocalAudioObject> filterRepeatedObjectsWithAlbums(
			final List<ILocalAudioObject> list) {
		return filterWithHash(list,
				new RepeatedObjectsWithAlbumsHashCalculator(
						this.unknownObjectChecker));
	}

	/**
	 * Returns a list where there are no repeated audio objects (same title and
	 * album and artist)
	 * 
	 * @param list
	 * @return
	 */
	private List<ILocalAudioObject> filterWithHash(
			final List<ILocalAudioObject> list,
			final IHashCalculator hashCalculator) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>(list);
		Set<Integer> artistAndTitles = new HashSet<Integer>();
		for (ILocalAudioObject af : list) {
			// Build a set of strings of type artist_hash * album_hash *
			// title_hash
			Integer hash = hashCalculator.getHash(af);
			if (artistAndTitles.contains(hash)) {
				// Repeated artist + album + title, remove from result list
				result.remove(af);
			} else {
				artistAndTitles.add(hash);
			}
		}
		return result;
	}
}
