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

import java.util.List;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.PlaybackState;

public abstract class AbstractHandler implements IHandler {

	private IFrame frame;
	
	private IOSManager osManager;
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	/**
	 * @return os manager
	 */
	protected IOSManager getOsManager() {
		return osManager;
	}
	
	/**
	 * @return the frame
	 */
	protected IFrame getFrame() {
		return frame;
	}
	
	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
    /**
     * Returns a task to be executed before initialize handler By default
     * handlers do not define any task
     * 
     * @return
     */
    protected Runnable getPreviousInitializationTask() {
        return null;
    }
    
    /**
     * Code to be executed when all handlers have been initialized
     */
    public void allHandlersInitialized() {}

    @Override
    public void favoritesChanged() {}
    
    @Override
    public void deviceConnected(String location) {}

    @Override
    public void deviceReady(String location) {}
    
    @Override
    public void deviceDisconnected(String location) {}
    
    @Override
    public void playbackStateChanged(PlaybackState newState, IAudioObject currentAudioObject) {}
    
    @Override
    public void audioObjectsAdded(List<IPlayListAudioObject> audioObjectsAdded) {}
    
    @Override
    public void audioObjectsRemoved(List<IPlayListAudioObject> audioObjectsRemoved) {}
    
    @Override
    public int requestUserInteraction() {
    	// By default no user interaction is requested
    	return -1;
    }
    
    @Override
    public void doUserInteraction() {}
    
    @Override
    public void applicationFinish() {}

    @Override
    public void applicationStateChanged() {}

    @Override
    public void applicationStarted() {}
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

    /**
     * Initializes handler 
     */
    protected void initHandler() {}
    
    @Override
    public void windowIconified() {}
    
    @Override
    public void windowDeiconified() {}

	/**
	 * Delegate method to get beans
	 * @param <T>
	 * @param beanType
	 * @return
	 */
	protected <T> T getBean(Class<T> beanType) {
		return Context.getBean(beanType);
	}
	
	/**
	 * Delegate method to get beans
	 * @param name
	 * @return
	 */
	protected Object getBean(String name) {
		return Context.getBean(name);
	}
}
