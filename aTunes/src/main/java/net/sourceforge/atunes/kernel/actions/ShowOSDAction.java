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

package net.sourceforge.atunes.kernel.actions;

import net.sourceforge.atunes.model.INotificationsHandler;
import net.sourceforge.atunes.model.IPlayListHandler;

/**
 * Activates OSD
 * @author alex
 *
 */
public class ShowOSDAction extends CustomAbstractAction {

	private static final long serialVersionUID = 646318992035897920L;

	private INotificationsHandler notificationsHandler;

	private IPlayListHandler playListHandler;

	/**
	 * @param notificationsHandler
	 */
	public void setNotificationsHandler(final INotificationsHandler notificationsHandler) {
		this.notificationsHandler = notificationsHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	@Override
	protected void executeAction() {
		notificationsHandler.showNotification(playListHandler.getCurrentAudioObjectFromCurrentPlayList());
	}
}
