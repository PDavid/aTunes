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

import java.awt.Image;
import java.awt.TrayIcon;

import javax.swing.JPopupMenu;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.controls.JTrayIcon;
import net.sourceforge.atunes.gui.views.controls.JTrayIconPopupMenu;
import net.sourceforge.atunes.kernel.actions.ToggleWindowVisibilityAction;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITrayIcon;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Icon builder for windows and linux
 * 
 * @author alex
 * 
 */
public class CommonTrayIcon implements ITrayIcon, ApplicationContextAware {

	private IOSManager osManager;

	private ApplicationContext context;

	private ITrayIconFiller trayIconFiller;

	/**
	 * @param trayIconFiller
	 */
	public void setTrayIconFiller(final ITrayIconFiller trayIconFiller) {
		this.trayIconFiller = trayIconFiller;
	}

	@Override
	public void setApplicationContext(
			final ApplicationContext applicationContext) {
		this.context = applicationContext;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public TrayIcon getTrayIcon(final Image iconImage, final int iconSize) {
		Image icon = ImageUtils
				.scaleImageBicubic(iconImage, iconSize, iconSize).getImage();
		JTrayIcon trayIcon = new JTrayIcon(icon, this.osManager.isLinux(),
				this.context.getBean(ToggleWindowVisibilityAction.class));
		trayIcon.setToolTip(StringUtils.getString(Constants.APP_NAME, " ",
				Constants.VERSION.toShortString()));
		JPopupMenu popupmenu = new JTrayIconPopupMenu(trayIcon);
		trayIcon.setJTrayIconJPopupMenu(popupmenu);
		this.trayIconFiller.fillTrayIcon(trayIcon);
		return trayIcon;
	}

	@Override
	public void setPlayMenuItemText(final String text) {
		this.trayIconFiller.setPlayMenuItemText(text);
	}
}
