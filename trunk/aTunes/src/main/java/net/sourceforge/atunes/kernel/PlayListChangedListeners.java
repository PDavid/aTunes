/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds references to instances
 * @author fleax
 *
 */
public class PlayListChangedListeners {

	private static List<PlayListChangedListener> listeners = new ArrayList<PlayListChangedListener>();
	
    /**
     * Adds a new play list change listener
     * @param listener
     */
    static void addPlayListChangedListener(PlayListChangedListener listener) {
    	if (listener != null) {
    		listeners.add(listener);
    	}
    }

	/**
	 * Called when added objects to play list
	 * @param playListAudioObjects
	 */
	public static void audioObjectsAdded(List<PlayListAudioObject> playListAudioObjects) {
		for (PlayListChangedListener listener : listeners) {
			listener.audioObjectsAdded(playListAudioObjects);
		}
	}

	/**
	 * Called when audio objects are removed from play list
	 * @param audioObjectList
	 */
	public static void audioObjectsRemoved(List<PlayListAudioObject> audioObjectList) {
		for (PlayListChangedListener listener : listeners) {
			listener.audioObjectsRemoved(audioObjectList);
		}
	}

	/**
	 * Called when all audio objects of a play list are removed
	 */
	public static void audioObjectsRemovedAll() {
		for (PlayListChangedListener listener : listeners) {
			listener.audioObjectsRemovedAll();
		}
	}

	/**
	 * Called when audio object changes
	 */
	public static void currentAudioObjectChanged() {
        for (PlayListChangedListener listener : listeners) {
            listener.currentAudioObjectChanged();
        }
	}
}
