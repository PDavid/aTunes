/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.cdripper.RipperHandler;
import net.sourceforge.atunes.kernel.modules.command.CommandHandler;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.filter.FilterHandler;
import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeyHandler;
import net.sourceforge.atunes.kernel.modules.instances.MultipleInstancesHandler;
import net.sourceforge.atunes.kernel.modules.notify.NotificationsHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListAudioObject;
import net.sourceforge.atunes.kernel.modules.plugins.PluginsHandler;
import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.IGeneralPurposePluginsHandler;
import net.sourceforge.atunes.model.IHandler;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISmartPlayListHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ISystemTrayHandler;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.model.IUpdateHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.Logger;

public abstract class AbstractHandler implements IHandler {

	private IState state;
	
	private IFrame frame;
	
	private IOSManager osManager;
	
	private static List<AbstractHandler> handlers;
	
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
		for (AbstractHandler handler : getHandlers()) {
			handler.frame = frame;
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
     * Registers handler
     * 
     * @param handler
     */
    private static final void registerHandler(AbstractHandler handler) {
        ApplicationLifeCycleListeners.addApplicationLifeCycleListener(handler);
        FavoritesListeners.addFavoritesListener(handler);
        Context.getBean(IStateHandler.class).addStateChangeListener(handler);
        DeviceListeners.addDeviceListener(handler);
        PlaybackStateListeners.addPlaybackStateListener(handler);
        PlayListEventListeners.addPlayListEventListener(handler);
    }

    private static synchronized List<AbstractHandler> getHandlers() {
    	if (handlers == null) {
            // Instance handlers
        	// TODO: Add here every new Handler
        	handlers = new ArrayList<AbstractHandler>();
        	handlers.add((AbstractHandler) Context.getBean(IStateHandler.class));
        	handlers.add((AbstractHandler) Context.getBean(IPodcastFeedHandler.class));
        	handlers.add((AbstractHandler) Context.getBean(IContextHandler.class));
        	handlers.add(RipperHandler.getInstance());
        	handlers.add(CommandHandler.getInstance());
        	handlers.add(DeviceHandler.getInstance());
        	handlers.add((AbstractHandler) Context.getBean(IFavoritesHandler.class));
        	handlers.add(HotkeyHandler.getInstance());
        	handlers.add(MultipleInstancesHandler.getInstance());
        	handlers.add((AbstractHandler) Context.getBean(INavigationHandler.class));
            handlers.add(NotificationsHandler.getInstance());
            handlers.add(PlayerHandler.getInstance());
            handlers.add(FilterHandler.getInstance());
            handlers.add((AbstractHandler) Context.getBean(IPlayListHandler.class));
            handlers.add(PluginsHandler.getInstance());
            handlers.add(RadioHandler.getInstance());
            handlers.add((AbstractHandler) Context.getBean(IRepositoryHandler.class));
            handlers.add((AbstractHandler) Context.getBean(ISearchHandler.class));
            handlers.add((AbstractHandler) Context.getBean(IUpdateHandler.class));
            handlers.add((AbstractHandler) Context.getBean(IUIHandler.class));
            handlers.add((AbstractHandler) Context.getBean(ISmartPlayListHandler.class));
            handlers.add((AbstractHandler) Context.getBean(IStatisticsHandler.class));
            handlers.add((AbstractHandler) Context.getBean(ISystemTrayHandler.class));
            handlers.add((AbstractHandler) Context.getBean(IGeneralPurposePluginsHandler.class));
            handlers.add((AbstractHandler) Context.getBean(IWebServicesHandler.class));
            handlers.add((AbstractHandler) Context.getBean(ITagHandler.class));
            handlers.add((AbstractHandler) Context.getBean(IFullScreenHandler.class));
    	}
    	return handlers;
    }
    
    /**
     * Creates and registers all defined handlers
     * @param state
     */
    static void registerAndInitializeHandlers(IState state) {

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        // Register handlers
        for (AbstractHandler handler : getHandlers()) {
        	handler.setState(state);
        	handler.setOsManager(Context.getBean(IOSManager.class));
            registerHandler(handler);
            Runnable task = handler.getPreviousInitializationTask();
            if (task != null) {
                executorService.submit(task);
            }
        }

        // Initialize handlers
        for (final AbstractHandler handler : getHandlers()) {
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
    public void applicationStarted(List<IAudioObject> playList) {}
    
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
