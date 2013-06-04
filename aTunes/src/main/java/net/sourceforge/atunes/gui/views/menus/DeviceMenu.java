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

package net.sourceforge.atunes.gui.views.menus;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Device menu
 * 
 * @author alex
 * 
 */
public class DeviceMenu extends JMenu {

	private static final long serialVersionUID = -3624790857729577320L;

	private Action connectDeviceAction;
	private Action refreshDeviceAction;
	private Action disconnectDeviceAction;
	private Action copyPlayListToDeviceAction;
	private Action syncDeviceWithPlayListAction;

	/**
	 * @param i18nKey
	 */
	public DeviceMenu(final String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}

	/**
	 * @param copyPlayListToDeviceAction
	 */
	public void setCopyPlayListToDeviceAction(Action copyPlayListToDeviceAction) {
		this.copyPlayListToDeviceAction = copyPlayListToDeviceAction;
	}

	/**
	 * @param syncDeviceWithPlayListAction
	 */
	public void setSyncDeviceWithPlayListAction(
			Action syncDeviceWithPlayListAction) {
		this.syncDeviceWithPlayListAction = syncDeviceWithPlayListAction;
	}

	/**
	 * @param connectDeviceAction
	 */
	public void setConnectDeviceAction(final Action connectDeviceAction) {
		this.connectDeviceAction = connectDeviceAction;
	}

	/**
	 * @param refreshDeviceAction
	 */
	public void setRefreshDeviceAction(final Action refreshDeviceAction) {
		this.refreshDeviceAction = refreshDeviceAction;
	}

	/**
	 * @param disconnectDeviceAction
	 */
	public void setDisconnectDeviceAction(final Action disconnectDeviceAction) {
		this.disconnectDeviceAction = disconnectDeviceAction;
	}

	/**
	 * Initializes menu
	 */
	public void initialize() {
		add(connectDeviceAction);
		add(refreshDeviceAction);
		add(disconnectDeviceAction);
		add(new JSeparator());
		add(copyPlayListToDeviceAction);
		add(syncDeviceWithPlayListAction);
	}
}
