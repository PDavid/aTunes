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

package net.sourceforge.atunes.kernel.modules.notify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.notify.classic.DefaultNotifications;
import net.sourceforge.atunes.kernel.modules.notify.growl.GrowlNotificationEngine;
import net.sourceforge.atunes.kernel.modules.notify.libnotify.LibnotifyNotificationEngine;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.INotificationEngine;
import net.sourceforge.atunes.model.IPlaybackStateListener;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.PlaybackState;

public final class NotificationsHandler extends AbstractHandler implements IPlaybackStateListener {

    private static NotificationsHandler instance;

    private static Map<String, INotificationEngine> engines = new HashMap<String, INotificationEngine>();
    
    private INotificationEngine defaultEngine;
    
    /**
     * Adds a new notification engine
     * @param engine
     */
    private void addNotificationEngine(INotificationEngine engine) {
   		engines.put(engine.getName(), engine);
    }

    private static Set<String> availableEngines = new HashSet<String>();
    
    private NotificationsHandler() {
    }

    public static NotificationsHandler getInstance() {
        if (instance == null) {
            instance = new NotificationsHandler();
        }
        return instance;
    }

    /**
     * @return notification engine to use
     */
    private INotificationEngine getNotificationEngine() {
    	INotificationEngine engine = engines.get(getState().getNotificationEngine());
    	if (engine == null) {
    		engine = defaultEngine;
    	}
    	return engine;
    }
    
    @Override
    public void applicationFinish() {
    	getNotificationEngine().disposeNotifications();
    }

    @Override
    public void applicationStateChanged(IState newState) {
    	getNotificationEngine().updateNotification(newState);
    }
    
    @Override
    protected void initHandler() {
    	defaultEngine = new DefaultNotifications(getState(), getOsManager());
    	// Add here any new notification engine
    	addNotificationEngine(defaultEngine);
    	addNotificationEngine(new LibnotifyNotificationEngine(getOsManager()));
    	addNotificationEngine(new GrowlNotificationEngine(getOsManager()));

    	// Load available engines
    	for (INotificationEngine engine : engines.values()) {
    		addAvailableNoticationEngine(engine);
    	}
    };

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
    }

    /**
     * Adds a new notification engine available
     * @param engine
     */
    private void addAvailableNoticationEngine(INotificationEngine engine) {
    	if (engine.isEngineAvailable()) {
    		availableEngines.add(engine.getName());
    	}
    }
    
    
    /**
     * Show notification
     */
    public void showNotification(IAudioObject audioObject) {
    	// only show notification if not in full screen
    	if (!getBean(IFullScreenHandler.class).isVisible()) {
    		getNotificationEngine().showNotification(audioObject);
    	}
    }

    @Override
    public void playbackStateChanged(PlaybackState newState, IAudioObject currentAudioObject) {
        if (getState().isShowOSD() && newState == PlaybackState.PLAYING) {
            // Playing
            showNotification(currentAudioObject);
        }
    }
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}
	
	/**
	 * @return set of names of notification engines, default is the first one
	 */
	public List<String> getNotificationEngines() {
		List<String> names = new ArrayList<String>(engines.keySet());
		Collections.sort(names, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1.equals(defaultEngine.getName())) {
					return -1;
				} else if (o2.equals(defaultEngine.getName())) {
					return 1;
				}
				return o1.compareTo(o2);
			}
		}); 
		return names;
	}

	/**
	 * @param name
	 * @return notification engine by name
	 */
	public INotificationEngine getNotificationEngine(String name) {
		return engines.get(name);
	}
	
	public INotificationEngine getDefaultEngine() {
		return defaultEngine;
	}
}
