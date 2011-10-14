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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListAudioObject;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.Logger;

public abstract class AbstractHandler implements IHandler {

	private IState state;
	
	private IFrame frame;
	
	private IOSManager osManager;
	
	/**
	 * Returns access to state of application
	 * @return
	 */
	protected IState getState() {
		return state;
	}
	
	public void setState(IState state) {
		this.state = state;
	}
	
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	protected IOSManager getOsManager() {
		return osManager;
	}
	
	protected IFrame getFrame() {
		return frame;
	}
	
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
	public static void setFrameForHandlers(IFrame frame) {
		for (AbstractHandler handler : Context.getBeans(AbstractHandler.class)) {
			handler.setFrame(frame);
		}
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
    public void allHandlersInitialized() {
    	// Does nothing by default
    }

    /**
     * Creates and registers all defined handlers
     * @param state
     */
    static void registerAndInitializeHandlers(IState state) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        // Register handlers
        for (AbstractHandler handler : Context.getBeans(AbstractHandler.class)) {
            Runnable task = handler.getPreviousInitializationTask();
            if (task != null) {
                executorService.submit(task);
            }
        }

        // Initialize handlers
        for (final AbstractHandler handler : Context.getBeans(AbstractHandler.class)) {
            executorService.submit(new Runnable() {
            	@Override
            	public void run() {
            		handler.initHandler();
            	}
            });
        }

        executorService.shutdown();
        
        try {
			executorService.awaitTermination(100, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Logger.error(e);
		}
    }
    
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
    public void audioObjectsAdded(List<PlayListAudioObject> audioObjectsAdded) {}
    
    @Override
    public void audioObjectsRemoved(List<PlayListAudioObject> audioObjectsRemoved) {}
    
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
    public void applicationStateChanged(IState newState) {}

    @Override
    public void applicationStarted() {}
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

    /**
     * Initializes handler 
     */
    protected void initHandler() {
    }

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
