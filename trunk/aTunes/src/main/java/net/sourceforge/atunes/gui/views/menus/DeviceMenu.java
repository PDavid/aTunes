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

package net.sourceforge.atunes.gui.views.menus;

import javax.swing.Action;
import javax.swing.JMenu;

import net.sourceforge.atunes.utils.I18nUtils;

public class DeviceMenu extends JMenu {

	private static final long serialVersionUID = -3624790857729577320L;

	private Action connectDeviceAction;
	private Action refreshDeviceAction;
	private Action disconnectDeviceAction;
	
	/**
	 * @param i18nKey
	 */
	public DeviceMenu(String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}
	
	/**
	 * @param connectDeviceAction
	 */
	public void setConnectDeviceAction(Action connectDeviceAction) {
		this.connectDeviceAction = connectDeviceAction;
	}
	
	/**
	 * @param refreshDeviceAction
	 */
	public void setRefreshDeviceAction(Action refreshDeviceAction) {
		this.refreshDeviceAction = refreshDeviceAction;
	}
	
	/**
	 * @param disconnectDeviceAction
	 */
	public void setDisconnectDeviceAction(Action disconnectDeviceAction) {
		this.disconnectDeviceAction = disconnectDeviceAction;
	}
	
	/**
	 * Initializes menu
	 */
	public void initialize() {
        add(connectDeviceAction);
        add(refreshDeviceAction);
        add(disconnectDeviceAction);
	}
}
