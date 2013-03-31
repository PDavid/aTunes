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
 * Decides what object to return when moving through play list
 * 
 * @author alex
 * 
 */
public interface IPlayListMode {

	/**
	 * @return moves to previous audio object
	 */
	public IAudioObject moveToPreviousAudioObject();

	/**
	 * @return moves to next audio object
	 */
	public IAudioObject moveToNextAudioObject();

	/**
	 * @param index
	 * @return previous audio object by given index
	 */
	public IAudioObject getPreviousAudioObject(final int index);

	/**
	 * @param index
	 * @return next audio object by given index
	 */
	public IAudioObject getNextAudioObject(final int index);

	/**
	 * Resets mode to initial state
	 */
	void reset();

	/**
	 * Adds audio object to history
	 * 
	 * @param object
	 */
	void addToPlaybackHistory(final IAudioObject object);

	/**
	 * Notify of all objects being removed
	 */
	void audioObjectsRemovedAll();

	/**
	 * Notify of given object being removed
	 * 
	 * @param audioObjectsRemoved
	 */
	void audioObjectsRemoved(
			final List<IPlayListAudioObject> audioObjectsRemoved);

	/**
	 * Notify of given audio objects being added
	 * 
	 * @param audioObjectsAdded
	 */
	void audioObjectsAdded(final List<IPlayListAudioObject> audioObjectsAdded);

}