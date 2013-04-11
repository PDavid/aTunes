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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.util.concurrent.ScheduledFuture;

import net.sourceforge.atunes.kernel.DeviceListeners;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IStateDevice;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Monitors when device is connected
 * 
 * @author alex
 * 
 */
public final class DeviceMonitor implements Runnable {

	private int delayInSeconds;

	private ScheduledFuture<?> future;

	private IDeviceHandler deviceHandler;

	private ITaskService taskService;

	private IStateDevice stateDevice;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param stateDevice
	 */
	public void setStateDevice(final IStateDevice stateDevice) {
		this.stateDevice = stateDevice;
	}

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param deviceHandler
	 */
	public void setDeviceHandler(final IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}

	/**
	 * @param delay
	 */
	public void setDelayInSeconds(final int delay) {
		this.delayInSeconds = delay;
	}

	/**
	 * Start monitor.
	 */
	void startMonitor() {
		this.future = this.taskService.submitPeriodically("Device Monitor",
				this.delayInSeconds, this.delayInSeconds, this);
	}

	/**
	 * Stops monitor
	 */
	void stopMonitor() {
		this.future.cancel(true);
	}

	/**
	 * Returns if monitor is running
	 * 
	 * @return
	 */
	boolean isMonitorRunning() {
		return this.future != null;
	}

	@Override
	public void run() {
		checkConnection();
		checkDisconnection();
	}

	/**
	 * Checks if device has been disconnected, returning true if so, false
	 * otherwise
	 * 
	 * @return
	 */
	private boolean checkDisconnection() {
		if (!this.deviceHandler.isDeviceConnected()) {
			return false;
		}

		File deviceLocationFile = new File(
				this.deviceHandler.getDeviceLocation());
		if (!deviceLocationFile.exists()) {
			Logger.info("Device disconnected");
			this.beanFactory.getBean(DeviceListeners.class).deviceDisconnected(
					net.sourceforge.atunes.utils.FileUtils
							.getPath(deviceLocationFile));
			return true;
		}
		return false;
	}

	/**
	 * Checks if there is a device connected, returning true if so, false
	 * otherwise
	 * 
	 * @param deviceLocation
	 * @return
	 */
	private boolean checkConnection() {
		String deviceLocation = this.stateDevice.getDefaultDeviceLocation();
		if (!StringUtils.isEmpty(deviceLocation)) {
			File deviceLocationFile = new File(deviceLocation);
			if (!this.deviceHandler.isDeviceConnected()
					&& deviceLocationFile.exists()) {
				Logger.info("Device connected");
				this.beanFactory.getBean(DeviceListeners.class)
						.deviceConnected(
								net.sourceforge.atunes.utils.FileUtils
										.getPath(deviceLocationFile));
				return true;
			}
		}
		return false;
	}
}
