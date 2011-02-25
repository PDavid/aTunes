/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

/**
 * Holds references to DeviceListener instances
 * @author fleax
 *
 */
public class DeviceListeners {

	private static List<DeviceListener> listeners = new ArrayList<DeviceListener>();
	
    /**
     * Adds a device listener
     * @param listener
     */
    static void addDeviceListener(DeviceListener listener) {
    	if (listener != null) {
    		listeners.add(listener);
    	}
    }

	/**
	 * Called when device connected
	 * @param path
	 */
	public static void deviceConnected(String path) {
        for (DeviceListener l : listeners) {
        	l.deviceConnected(path);
        }
	}

	/**
	 * Called when device is ready
	 * @param path
	 */
	public static void deviceReady(String path) {
        for (DeviceListener l : listeners) {
        	l.deviceReady(path);
        }
	}

	/**
	 * Called when device disconnected
	 * @param location
	 */
	public static void deviceDisconnected(String location) {
        for (DeviceListener l : listeners) {
        	l.deviceDisconnected(location);
        }
	}
}
