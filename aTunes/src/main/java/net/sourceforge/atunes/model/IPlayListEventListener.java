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
 * The listener interface for receiving playListEvent events.
 */
/**
 * @author alex
 *
 */
public interface IPlayListEventListener {

    /**
     * Called when play list is cleared.
     */
    public void playListCleared();

    /**
     * Called when audio object to being played changes.
     * 
     * @param audioObject
     *            the audio object
     */
    public void selectedAudioObjectChanged(IAudioObject audioObject);

	/**
	 * Called when audio objects are added to play list
	 * @param playListAudioObjects
	 */
	public void audioObjectsAdded(List<IPlayListAudioObject> playListAudioObjects);

	/**
	 * Called when audio objects are removed from play list
	 * @param audioObjectList
	 */
	public void audioObjectsRemoved(List<IPlayListAudioObject> audioObjectList);
}
