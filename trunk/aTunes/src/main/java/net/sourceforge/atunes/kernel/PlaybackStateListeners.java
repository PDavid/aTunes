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

package net.sourceforge.atunes.kernel;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlaybackStateListener;
import net.sourceforge.atunes.model.PlaybackState;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Holds references to PlaybackStateListener instances
 * @author fleax
 *
 */
public class PlaybackStateListeners implements ApplicationContextAware {

	private Collection<IPlaybackStateListener> listeners;
	
	protected void setListeners(Collection<IPlaybackStateListener> listeners) {
		this.listeners = listeners;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		// Clone listeners as list can be modified when adding plugins
		listeners = new ArrayList<IPlaybackStateListener>(applicationContext.getBeansOfType(IPlaybackStateListener.class).values());
	}
	
    /**
     * Adds a new listener
     * @param listener
     */
    public void addPlaybackStateListener(IPlaybackStateListener listener) {
    	if (listener != null) {
    		getListeners().add(listener);
    	}
    }

    /**
     * Called when play back state changes
     * @param newState
     * @param audioObject
     */
    public void playbackStateChanged(PlaybackState newState, IAudioObject audioObject) {
		for (IPlaybackStateListener listener : getListeners()) {
			listener.playbackStateChanged(newState, audioObject);
		}
    }

	/**
	 * Removes a listener
	 * @param createdInstance
	 */
	public void removePlaybackStateListener(IPlaybackStateListener createdInstance) {
		if (createdInstance != null) {
			getListeners().remove(createdInstance);
		}
	}
	
	private Collection<IPlaybackStateListener> getListeners() {
		if (listeners == null) {
			listeners = new ArrayList<IPlaybackStateListener>();
		}
		return listeners;
	}
}
