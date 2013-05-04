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

package net.sourceforge.atunes.model;

import java.util.List;

/**
 * Filters and utils for local audio objects
 * 
 * @author alex
 * 
 */
public interface ILocalAudioObjectFilter {

	/**
	 * Gets the local audio objects from a list of audio objects
	 * 
	 * @param audioObjects
	 * @return
	 */
	List<ILocalAudioObject> getLocalAudioObjects(List<IAudioObject> audioObjects);

	/**
	 * Gets an audio objects list from a list of local audio objects
	 * 
	 * @param audioObjects
	 * @return
	 */
	List<IAudioObject> getAudioObjects(List<ILocalAudioObject> audioObjects);

	/**
	 * Returns a list where there are no repeated audio objects (same title and
	 * artist)
	 * 
	 * @param list
	 * @return
	 */
	List<ILocalAudioObject> filterRepeatedObjects(List<ILocalAudioObject> list);

	/**
	 * Returns a list where there are no repeated audio objects (same title and
	 * album and artist)
	 * 
	 * @param list
	 * @return
	 */
	List<ILocalAudioObject> filterRepeatedObjectsWithAlbums(
			List<ILocalAudioObject> list);
}