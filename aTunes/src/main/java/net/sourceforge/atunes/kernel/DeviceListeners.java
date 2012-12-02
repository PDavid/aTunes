/*
 * aTunes 3.0.0
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

import java.util.Collection;

import net.sourceforge.atunes.model.IDeviceListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Calls to to DeviceListener instances
 * @author fleax
 *
 */
public class DeviceListeners implements ApplicationContextAware {

	private Collection<IDeviceListener> listeners;
	
	protected void setListeners(Collection<IDeviceListener> listeners) {
		this.listeners = listeners;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx) {
		listeners = ctx.getBeansOfType(IDeviceListener.class).values();
	}
	
	/**
	 * Called when device connected
	 * @param path
	 */
	public void deviceConnected(String path) {
        for (IDeviceListener l : listeners) {
        	l.deviceConnected(path);
        }
	}

	/**
	 * Called when device is ready
	 * @param path
	 */
	public void deviceReady(String path) {
        for (IDeviceListener l : listeners) {
        	l.deviceReady(path);
        }
	}

	/**
	 * Called when device disconnected
	 * @param location
	 */
	public void deviceDisconnected(String location) {
        for (IDeviceListener l : listeners) {
        	l.deviceDisconnected(location);
        }
	}
}
