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

import net.sourceforge.atunes.model.IApplicationLifeCycleListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Calls to ApplicationLifeCycleListener instances
 * 
 * @author fleax
 *
 */
public class ApplicationLifeCycleListeners implements ApplicationContextAware {

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
       		listener.applicationStarted();
        }
    }
    
    /**
     * Called after all handlers initialized
     */
    void allHandlersInitialized() {
        for (IApplicationLifeCycleListener listener : listeners) {
       		listener.allHandlersInitialized();
        }
    }
    
    /**
     * Called of deferred initialization
     */
    void deferredInitialization() {
    	for (IApplicationLifeCycleListener listener : listeners) {
    		listener.deferredInitialization();
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
}
