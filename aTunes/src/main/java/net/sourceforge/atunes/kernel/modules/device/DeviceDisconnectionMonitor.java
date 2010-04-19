/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

public final class DeviceDisconnectionMonitor extends Thread {

    private static DeviceDisconnectionMonitor instance;
    private static List<DeviceDisconnectionListener> listeners = new ArrayList<DeviceDisconnectionListener>();
    private static int TIME_TO_WAIT = 5000;

    private Logger logger;

    /**
     * Instantiates a new device disconnection monitor.
     */
    private DeviceDisconnectionMonitor() {
        super();
    }

    /**
     * Adds the listener.
     * 
     * @param listener
     *            the listener
     */
    public static void addListener(DeviceDisconnectionListener listener) {
        listeners.add(listener);
    }

    /**
     * Start monitor.
     */
    public static void startMonitor() {
        if (instance == null) {
            instance = new DeviceDisconnectionMonitor();
            instance.start();
        }
    }

    /**
     * Stop monitor.
     */
    public static void stopMonitor() {
        if (instance != null) {
            instance.interrupt();
            instance = null;
        }
    }

    @Override
    public void run() {
        super.run();

        while (!isInterrupted()) {
            if (!DeviceHandler.getInstance().isDeviceConnected()) {
                return;
            }

            final File deviceLocationFile = DeviceHandler.getInstance().getDeviceRepository().getFolders().get(0);
            if (!deviceLocationFile.exists()) {
                getLogger().info(LogCategories.PROCESS, "Device disconnected");
                for (final DeviceDisconnectionListener l : listeners) {
                    SwingUtilities.invokeLater(new DeviceDisconnectedRunnable(l, deviceLocationFile.getAbsolutePath()));
                }
                return;
            }
            try {
                Thread.sleep(TIME_TO_WAIT);
            } catch (InterruptedException e) {
                getLogger().error(LogCategories.PROCESS, e);
            }
        }
    }

    private static class DeviceDisconnectedRunnable implements Runnable {

        private DeviceDisconnectionListener listener;

        private String deviceLocation;

        public DeviceDisconnectedRunnable(DeviceDisconnectionListener listener, String deviceLocation) {
            this.listener = listener;
            this.deviceLocation = deviceLocation;
        }

        @Override
        public void run() {
            listener.deviceDisconnected(deviceLocation);
        }
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
