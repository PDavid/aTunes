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

package net.sourceforge.atunes.kernel.modules.notify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectGenericImageFactory;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INotificationEngine;
import net.sourceforge.atunes.model.INotificationsHandler;
import net.sourceforge.atunes.model.IPlaybackStateListener;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITemporalDiskStorage;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.Logger;

public final class NotificationsHandler extends AbstractHandler implements IPlaybackStateListener, INotificationsHandler {

    private Map<String, INotificationEngine> engines;
    
    private INotificationEngine defaultEngine;
    
    private IAudioObjectGenericImageFactory audioObjectGenericImageFactory;
    
    private ITemporalDiskStorage temporalDiskStorage;
    
    /**
     * @param temporalDiskStorage
     */
    public void setTemporalDiskStorage(ITemporalDiskStorage temporalDiskStorage) {
		this.temporalDiskStorage = temporalDiskStorage;
	}
    
    /**
     * @param audioObjectGenericImageFactory
     */
    public void setAudioObjectGenericImageFactory(IAudioObjectGenericImageFactory audioObjectGenericImageFactory) {
		this.audioObjectGenericImageFactory = audioObjectGenericImageFactory;
	}
    
    private Map<String, INotificationEngine> getEngines() {
    	if (engines == null) {
    		Logger.debug("Initializing notification engines");
    		engines = new HashMap<String, INotificationEngine>();
        	// Add here any new notification engine
        	addNotificationEngine(engines, getDefaultEngine());
        	addNotificationEngine(engines, new LibnotifyNotificationEngine(getOsManager(), audioObjectGenericImageFactory, temporalDiskStorage));
        	addNotificationEngine(engines, new GrowlNotificationEngine(getOsManager(), audioObjectGenericImageFactory, temporalDiskStorage));
    	}
    	return engines;
    }
    
    /**
     * Adds a new notification engine to map of notifications
     * @param engine
     */
    private void addNotificationEngine(Map<String, INotificationEngine> engines, INotificationEngine engine) {
    	engines.put(engine.getName(), engine);
    }

    /**
     * @return notification engine to use
     */
    private INotificationEngine getNotificationEngine() {
    	INotificationEngine engine = getEngines().get(getState().getNotificationEngine());
    	if (engine == null) {
    		engine = getDefaultEngine();
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
	public List<String> getNotificationEngines() {
		List<String> names = new ArrayList<String>(getEngines().keySet());
		Collections.sort(names, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1.equals(getDefaultEngine().getName())) {
					return -1;
				} else if (o2.equals(getDefaultEngine().getName())) {
					return 1;
				}
				return o1.compareTo(o2);
			}
		}); 
		return names;
	}

	@Override
	public INotificationEngine getNotificationEngine(String name) {
		return getEngines().get(name);
	}
	
	@Override
	public INotificationEngine getDefaultEngine() {
		if (defaultEngine == null) {
	    	defaultEngine = new DefaultNotifications(getState(), getOsManager(), getBean(ILookAndFeelManager.class), audioObjectGenericImageFactory, temporalDiskStorage);
		}
		return defaultEngine;
	}
}
