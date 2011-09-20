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

package net.sourceforge.atunes.kernel.modules.device;

import java.io.File;

import net.sourceforge.atunes.kernel.DeviceListeners;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITaskService;

final class DeviceMonitor {

    private static int DELAY = 5;

    /**
     * Start monitor.
     */
    static void startMonitor(final IState state, ITaskService taskService) {
    	taskService.submitPeriodically("Device Monitor", new Runnable() {
    		@Override
    		public void run() {
    			checkConnection(state);
    			checkDisconnection();
    		}
    	}, DELAY);
    }

    /**
     * Checks if device has been disconnected, returning true if so, false otherwise
     * @return
     */
    protected static boolean checkDisconnection() {
        if (!DeviceHandler.getInstance().isDeviceConnected()) {
            return false;
        }

        File deviceLocationFile = new File(DeviceHandler.getInstance().getDeviceLocation());
        if (!deviceLocationFile.exists()) {
            Logger.info("Device disconnected");
            DeviceListeners.deviceDisconnected(deviceLocationFile.getAbsolutePath());
            return true;
        }
        return false;
	}

	/**
	 * Checks if there is a device connected, returning true if so, false otherwise
     * @param deviceLocation
     * @return
     */
    private static boolean checkConnection(IState state) {
    	String deviceLocation = state.getDefaultDeviceLocation();
        if (deviceLocation != null && !deviceLocation.equals("")) {
            File deviceLocationFile = new File(deviceLocation);
            if (!DeviceHandler.getInstance().isDeviceConnected() && deviceLocationFile.exists()) {
            	Logger.info("Device connected");
            	DeviceListeners.deviceConnected(deviceLocationFile.getAbsolutePath());
            	return true;
            }
        }
        return false;
    }
}
