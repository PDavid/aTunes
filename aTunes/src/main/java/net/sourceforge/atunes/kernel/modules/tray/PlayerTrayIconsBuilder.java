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

package net.sourceforge.atunes.kernel.modules.tray;

import java.awt.Dimension;
import java.awt.TrayIcon;

import net.sourceforge.atunes.gui.views.controls.ActionTrayIcon;
import net.sourceforge.atunes.kernel.actions.PlayAction;
import net.sourceforge.atunes.kernel.actions.PlayNextAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.StopCurrentAudioObjectAction;
import net.sourceforge.atunes.model.IOSManager;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Returns tray icon components
 * @author alex
 *
 */
public class PlayerTrayIconsBuilder implements ApplicationContextAware {

	private ApplicationContext context;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.context = applicationContext;
	}

	/**
	 * Getter for previousIcon
	 * 
	 * @param trayIconSize
	 * @return
	 */
	public TrayIcon getPreviousTrayIcon(final Dimension trayIconSize) {
		return new ActionTrayIcon(osManager.getPlayerTrayIcons().getPreviousIcon(trayIconSize), context.getBean(PlayPreviousAudioObjectAction.class));
	}

	/**
	 * Returns play tray icon
	 * @param trayIconSize
	 * @return
	 */
	public TrayIcon getPlayTrayIcon(final Dimension trayIconSize) {
		return new ActionTrayIcon(osManager.getPlayerTrayIcons().getPlayIcon(trayIconSize), context.getBean(PlayAction.class));
	}

	/**
	 * Returns stop tray icon
	 * @param trayIconSize
	 * @return
	 */
	public TrayIcon getStopTrayIcon(final Dimension trayIconSize) {
		return new ActionTrayIcon(osManager.getPlayerTrayIcons().getStopIcon(trayIconSize), context.getBean(StopCurrentAudioObjectAction.class));
	}

	/**
	 * Returns next tray icon
	 * @param trayIconSize
	 * @return
	 */
	public TrayIcon getNextTrayIcon(final Dimension trayIconSize) {
		return new ActionTrayIcon(osManager.getPlayerTrayIcons().getNextIcon(trayIconSize), context.getBean(PlayNextAudioObjectAction.class));
	}
}
