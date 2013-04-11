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

import net.sourceforge.atunes.model.IApplicationLifeCycleListener;
import net.sourceforge.atunes.model.IBeanFactory;

/**
 * Calls to ApplicationLifeCycleListener instances
 * 
 * @author fleax
 * 
 */
public class ApplicationLifeCycleListeners {

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Call after application started
	 */
	void applicationStarted() {
		for (IApplicationLifeCycleListener listener : this.beanFactory
				.getBeans(IApplicationLifeCycleListener.class)) {
			listener.applicationStarted();
		}
	}

	/**
	 * Called after all handlers initialized
	 */
	void allHandlersInitialized() {
		for (IApplicationLifeCycleListener listener : this.beanFactory
				.getBeans(IApplicationLifeCycleListener.class)) {
			listener.allHandlersInitialized();
		}
	}

	/**
	 * Called of deferred initialization
	 */
	void deferredInitialization() {
		for (IApplicationLifeCycleListener listener : this.beanFactory
				.getBeans(IApplicationLifeCycleListener.class)) {
			listener.deferredInitialization();
		}
	}

	/**
	 * Executes actions needed before closing application, finished all
	 * necessary modules and writes configuration.
	 */
	void applicationFinish() {
		for (IApplicationLifeCycleListener listener : this.beanFactory
				.getBeans(IApplicationLifeCycleListener.class)) {
			listener.applicationFinish();
		}
	}
}
