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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;

/**
 * Holds references to ApplicationLifeCycleListener instances
 * @author fleax
 *
 */
public class ApplicationLifeCycleListeners {

	private static List<ApplicationLifeCycleListener> listeners = new ArrayList<ApplicationLifeCycleListener>();
	
    /**
     * Adds a life cycle listener to list of listeners. All classes that implements
     * ApplicationLifeCycleListener must call this method in order to be notified by
     * Kernel when application phase changes
     * 
     * @param listener
     */
    static void addApplicationLifeCycleListener(ApplicationLifeCycleListener listener) {
    	if (listener != null) {
    		listeners.add(listener);
    	}
    }
    
    /**
     * Call after application started
     * @param playList
     */
    static void applicationStarted(List<AudioObject> playList) {
        for (ApplicationLifeCycleListener listener : listeners) {
       		listener.applicationStarted(playList);
        }
    }
    
    /**
     * Called after all handlers initialized
     */
    static void allHandlersInitialized() {
        for (ApplicationLifeCycleListener listener : listeners) {
       		listener.allHandlersInitialized();
        }
    }
    
    /**
     * Executes actions needed before closing application, finished all
     * necessary modules and writes configuration.
     */
    static void applicationFinish() {
        for (ApplicationLifeCycleListener listener : listeners) {
       		listener.applicationFinish();
        }
    }
    
    /**
     * Calculates components that need user interaction
     * @return
     */
    static Map<Integer, ApplicationLifeCycleListener> getUserInteractionRequests() {
    	Map<Integer, ApplicationLifeCycleListener> requests = new HashMap<Integer, ApplicationLifeCycleListener>();
    	for (ApplicationLifeCycleListener listener : listeners) {
    		int request = listener.requestUserInteraction();
    		if (request != -1) {
    			if (requests.containsKey(request)) {
    				throw new IllegalStateException("Duplicate user interaction request order");
    			} else {
    				requests.put(request, listener);
    			}
    		}
    	}
    	return requests;
    }
    
    /**
     * Calls user interaction in requested order
     * @param requests
     */
    static void doUserInteraction(final Map<Integer, ApplicationLifeCycleListener> requests) {
    	List<Integer> order = new ArrayList<Integer>(requests.keySet());
    	Collections.sort(order);
    	for (final Integer req: order) {
    		try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						requests.get(req).doUserInteraction();
					}
				});
			} catch (InterruptedException e) {
				Logger.error(e);
			} catch (InvocationTargetException e) {
				Logger.error(e);
			}
    	}
    }
}
