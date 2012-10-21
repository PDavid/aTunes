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

package net.sourceforge.atunes.kernel.actions;

import net.sourceforge.atunes.kernel.DeviceListeners;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Called to disconnect device
 * 
 * @author fleax
 * 
 */
public class DisconnectDeviceAction extends CustomAbstractAction {

	private static final long serialVersionUID = 1782027529649014492L;

	private IDeviceHandler deviceHandler;

	private DeviceListeners deviceListeners;

	/**
	 * @param deviceHandler
	 */
	public void setDeviceHandler(final IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}

	/**
	 * @param deviceListeners
	 */
	public void setDeviceListeners(final DeviceListeners deviceListeners) {
		this.deviceListeners = deviceListeners;
	}

	/**
	 * Default constructor
	 */
	public DisconnectDeviceAction() {
		super(I18nUtils.getString("DISCONNECT"));
		putValue(SHORT_DESCRIPTION, I18nUtils.getString("DISCONNECT"));
		setEnabled(false);
	}

	@Override
	protected void executeAction() {
		// Launch a device disconnected event
		deviceListeners.deviceDisconnected(deviceHandler.getDeviceLocation());
	}
}
