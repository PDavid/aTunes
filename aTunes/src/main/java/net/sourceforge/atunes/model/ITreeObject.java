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
 * @author alex
 * All objects that can be shown in Navigator tree must implement this
 * interface.
 * @param <T>
 */

public interface ITreeObject<T extends IAudioObject> {

	/**
	 * Returns the audio objects of this tree object
	 * 
	 * @return The audio objects of this object
	 */
	List<T> getAudioObjects();

	/**
	 * Returns number of audio objects
	 * @return
	 */
	int size();
}
