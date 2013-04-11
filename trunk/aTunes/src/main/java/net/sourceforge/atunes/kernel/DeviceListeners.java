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

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDeviceListener;

/**
 * Calls to to DeviceListener instances
 * 
 * @author fleax
 * 
 */
public class DeviceListeners {

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Called when device connected
	 * 
	 * @param path
	 */
	public void deviceConnected(final String path) {
		for (IDeviceListener l : this.beanFactory
				.getBeans(IDeviceListener.class)) {
			l.deviceConnected(path);
		}
	}

	/**
	 * Called when device is ready
	 * 
	 * @param path
	 */
	public void deviceReady(final String path) {
		for (IDeviceListener l : this.beanFactory
				.getBeans(IDeviceListener.class)) {
			l.deviceReady(path);
		}
	}

	/**
	 * Called when device disconnected
	 * 
	 * @param location
	 */
	public void deviceDisconnected(final String location) {
		for (IDeviceListener l : this.beanFactory
				.getBeans(IDeviceListener.class)) {
			l.deviceDisconnected(location);
		}
	}
}
