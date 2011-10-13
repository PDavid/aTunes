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

package net.sourceforge.atunes.kernel.modules.device;

import java.io.File;
import java.util.concurrent.ScheduledFuture;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.DeviceListeners;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.Logger;

final class DeviceMonitor {

    private static int DELAY = 5;
    
    private static ScheduledFuture<?> future;
    
    private static IDeviceHandler deviceHandler;
    
    /**
     * Start monitor.
     * @param state
     * @param taskService
     * @param handler
     */
    static void startMonitor(final IState state, ITaskService taskService, IDeviceHandler handler) {
    	deviceHandler = handler;
    	future = taskService.submitPeriodically("Device Monitor", DELAY, DELAY, new Runnable() {
    		@Override
    		public void run() {
    			checkConnection(state);
    			checkDisconnection();
    		}
    	});
    }
    
    /**
     * Stops monitor
     */
    static void stopMonitor() {
    	future.cancel(true);
    }
    
    /**
     * Returns if monitor is running
     * @return
     */
    static boolean isMonitorRunning() {
    	return future != null;
    }

    /**
     * Checks if device has been disconnected, returning true if so, false otherwise
     * @return
     */
    protected static boolean checkDisconnection() {
        if (!deviceHandler.isDeviceConnected()) {
            return false;
        }

        File deviceLocationFile = new File(deviceHandler.getDeviceLocation());
        if (!deviceLocationFile.exists()) {
            Logger.info("Device disconnected");
            Context.getBean(DeviceListeners.class).deviceDisconnected(deviceLocationFile.getAbsolutePath());
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
            if (!deviceHandler.isDeviceConnected() && deviceLocationFile.exists()) {
            	Logger.info("Device connected");
            	Context.getBean(DeviceListeners.class).deviceConnected(deviceLocationFile.getAbsolutePath());
            	return true;
            }
        }
        return false;
    }
}
