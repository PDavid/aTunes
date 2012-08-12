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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IApplicationLifeCycleListener;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.Timer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Calls to ApplicationLifeCycleListener instances
 * 
 * @author fleax
 *
 */
public class ApplicationLifeCycleListeners implements ApplicationContextAware {

	private static final class DoUserInteractionRunnable implements Runnable {
		
		private final Map<Integer, IApplicationLifeCycleListener> requests;
		private final Integer req;

		private DoUserInteractionRunnable(
				Map<Integer, IApplicationLifeCycleListener> requests,
				Integer req) {
			this.requests = requests;
			this.req = req;
		}

		@Override
		public void run() {
			requests.get(req).doUserInteraction();
		}
	}

	private Collection<IApplicationLifeCycleListener> listeners;

	protected void setListeners(Collection<IApplicationLifeCycleListener> listeners) {
		this.listeners = listeners;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context) {
		listeners = context.getBeansOfType(IApplicationLifeCycleListener.class).values();
	}

    /**
     * Call after application started
     */
    void applicationStarted() {
        for (IApplicationLifeCycleListener listener : listeners) {
        	Timer t = new Timer();
        	t.start();
       		listener.applicationStarted();
       		Logger.debug(listener.getClass().getName(), ".applicationStarted: ", t.stop(), " seconds");
        }
    }
    
    /**
     * Called after all handlers initialized
     */
    void allHandlersInitialized() {
        for (IApplicationLifeCycleListener listener : listeners) {
        	Timer t = new Timer();
        	t.start();
       		listener.allHandlersInitialized();
       		Logger.debug(listener.getClass().getName(), ".allHandlersInitialized: ", t.stop(), " seconds");
        }
    }
    
    /**
     * Called of deferred initialization
     */
    void deferredInitialization() {
    	for (IApplicationLifeCycleListener listener : listeners) {
        	Timer t = new Timer();
        	t.start();
    		listener.deferredInitialization();
       		Logger.debug(listener.getClass().getName(), ".deferredInitialization: ", t.stop(), " seconds");
    	}
    }
    
    /**
     * Executes actions needed before closing application, finished all
     * necessary modules and writes configuration.
     */
    void applicationFinish() {
        for (IApplicationLifeCycleListener listener : listeners) {
       		listener.applicationFinish();
        }
    }
    
    /**
     * Calculates components that need user interaction
     * @return
     */
    Map<Integer, IApplicationLifeCycleListener> getUserInteractionRequests() {
    	Map<Integer, IApplicationLifeCycleListener> requests = new HashMap<Integer, IApplicationLifeCycleListener>();
    	for (IApplicationLifeCycleListener listener : listeners) {
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
    void doUserInteraction(final Map<Integer, IApplicationLifeCycleListener> requests) {
    	List<Integer> order = new ArrayList<Integer>(requests.keySet());
    	Collections.sort(order);
    	for (final Integer req: order) {
    		if (req != -1) {
    			try {
    				SwingUtilities.invokeAndWait(new DoUserInteractionRunnable(requests, req));
    			} catch (InterruptedException e) {
    				Logger.error(e);
    			} catch (InvocationTargetException e) {
    				Logger.error(e);
    			}
    		}
    	}
    }
}
