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

package net.sourceforge.atunes.kernel;

import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListEventListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Holds references to PlayListEventListener instances
 * @author fleax
 *
 */
public class PlayListEventListeners implements ApplicationContextAware {

	private Collection<IPlayListEventListener> listeners;
	
	protected void setListeners(Collection<IPlayListEventListener> listeners) {
		this.listeners = listeners;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		listeners = applicationContext.getBeansOfType(IPlayListEventListener.class).values();
	}
	
	/**
	 * Called when added objects to play list
	 * @param playListAudioObjects
	 */
	public void audioObjectsAdded(List<IPlayListAudioObject> playListAudioObjects) {
		for (IPlayListEventListener listener : listeners) {
			listener.audioObjectsAdded(playListAudioObjects);
		}
	}

	/**
	 * Called when audio objects are removed from play list
	 * @param audioObjectList
	 */
	public void audioObjectsRemoved(List<IPlayListAudioObject> audioObjectList) {
		for (IPlayListEventListener listener : listeners) {
			listener.audioObjectsRemoved(audioObjectList);
		}
	}

	/**
	 * Play list has been cleared
	 */
	public void playListCleared() {
		for (IPlayListEventListener listener : listeners) {
			listener.playListCleared();
		}
	}
	
    /**
     * Called when current audio object changes
     * 
     * @param audioObject
     *            the audio object
     */
    public void selectedAudioObjectHasChanged(final IAudioObject audioObject) {
        if (audioObject == null) {
            return;
        }
    	for (IPlayListEventListener listener : listeners) {
    		listener.selectedAudioObjectChanged(audioObject);
    	}
    }

}
