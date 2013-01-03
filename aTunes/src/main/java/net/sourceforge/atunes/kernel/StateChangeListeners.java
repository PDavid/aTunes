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

import net.sourceforge.atunes.model.IStateChangeListener;
import net.sourceforge.atunes.utils.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Calls listeners when state changes
 * @author alex
 *
 */
public class StateChangeListeners implements ApplicationContextAware {

	private Collection<IStateChangeListener> listeners;
	
	/**
	 * @param listeners
	 */
	protected void setListeners(Collection<IStateChangeListener> listeners) {
		this.listeners = listeners;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx) {
		listeners = ctx.getBeansOfType(IStateChangeListener.class).values();
	}
	
	/**
	 * Calls all listeners
	 */
	public void notifyApplicationStateChanged() {
		for (IStateChangeListener listener : listeners) {
			Logger.debug("Call to ApplicationStateChangeListener: ", listener.getClass().getName());
			listener.applicationStateChanged();
		}
    }
}

