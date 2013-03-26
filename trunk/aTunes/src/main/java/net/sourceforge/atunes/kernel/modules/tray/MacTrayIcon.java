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
import java.awt.PopupMenu;
import java.awt.TrayIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.ITrayIcon;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Icon builder for windows and linux
 * 
 * @author alex
 * 
 */
public class MacTrayIcon implements ITrayIcon {

	private ITrayIconFiller trayIconFiller;

	/**
	 * @param trayIconFiller
	 */
	public void setTrayIconFiller(ITrayIconFiller trayIconFiller) {
		this.trayIconFiller = trayIconFiller;
	}

	@Override
	public TrayIcon getTrayIcon(Image iconImage, int iconSize) {
		TrayIcon trayIcon = new java.awt.TrayIcon(Images.getImage(
				Images.APP_LOGO_TRAY_ICON_MAC).getImage());
		trayIcon.setToolTip(StringUtils.getString(Constants.APP_NAME, " ",
				Constants.VERSION.toShortString()));
		PopupMenu popupmenu = new PopupMenu();
		trayIcon.setPopupMenu(popupmenu);
		trayIconFiller.fillTrayIcon(trayIcon);
		return trayIcon;
	}

	@Override
	public void setPlayMenuItemText(String text) {
		trayIconFiller.setPlayMenuItemText(text);
	}
}
