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
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.filter.FilterHandler;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeyHandler;
import net.sourceforge.atunes.kernel.modules.instances.MultipleInstancesHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.notify.NotificationsHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.plugins.PluginsHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.IGeneralPurposePluginsHandler;
import net.sourceforge.atunes.model.IHandler;
import net.sourceforge.atunes.model.ISmartPlayListHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ISystemTrayHandler;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.IUpdateHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;

public abstract class AbstractHandler implements IHandler {

	private IState state;
	
	private IFrame frame;
	
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
	
	protected IFrame getFrame() {
		return frame;
	}
	
	public static void setFrame(IFrame frame) {
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
        ApplicationStateHandler.getInstance().addStateChangeListener(handler);
        DeviceListeners.addDeviceListener(handler);
        PlaybackStateListeners.addPlaybackStateListener(handler);
        PlayListEventListeners.addPlayListEventListener(handler);
    }

    private static List<AbstractHandler> getHandlers() {
    	if (handlers == null) {
            // Instance handlers
        	// TODO: Add here every new Handler
        	handlers = new ArrayList<AbstractHandler>();
        	handlers.add(ApplicationStateHandler.getInstance());
        	handlers.add(PodcastFeedHandler.getInstance());
        	handlers.add(ContextHandler.getInstance());
        	handlers.add(RipperHandler.getInstance());
        	handlers.add(CommandHandler.getInstance());
        	handlers.add(DeviceHandler.getInstance());
        	handlers.add(FavoritesHandler.getInstance());
        	handlers.add(HotkeyHandler.getInstance());
        	handlers.add(MultipleInstancesHandler.getInstance());
        	handlers.add(NavigationHandler.getInstance());
            handlers.add(NotificationsHandler.getInstance());
            handlers.add(PlayerHandler.getInstance());
            handlers.add(FilterHandler.getInstance());
            handlers.add(PlayListHandler.getInstance());
            handlers.add(PluginsHandler.getInstance());
            handlers.add(RadioHandler.getInstance());
            handlers.add(RepositoryHandler.getInstance());
            handlers.add(SearchHandler.getInstance());
            handlers.add((AbstractHandler)Context.getBean(IUpdateHandler.class));
            handlers.add(GuiHandler.getInstance());
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
}
