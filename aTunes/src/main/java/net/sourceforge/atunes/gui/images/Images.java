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

package net.sourceforge.atunes.gui.images;

import java.net.URL;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class contains the images that are used by aTunes.
 * 
 * @author fleax
 */
public final class Images {

	/**
	 * Logo 16x16
	 */
	public static final String APP_LOGO_16 = "logo16x16.png";
	/**
	 * Logo 24x24
	 */
	public static final String APP_LOGO_24 = "logo24x24.png";
	/**
	 * Logo 32x32
	 */
	public static final String APP_LOGO_32 = "logo32x32.png";
	/**
	 * Logo 48x48
	 */
	public static final String APP_LOGO_48 = "logo48x48.png";
	/**
	 * Logo 90x90
	 */
	public static final String APP_LOGO_90 = "logo90x90.png";
	/**
	 * Logo 150x150
	 */
	public static final String APP_LOGO_150 = "logo150x150.png";
	/**
	 * Logo 300x300
	 */
	public static final String APP_LOGO_300 = "logo300x300.png";
	/**
	 * Logo for tray icon in mac
	 */
	public static final String APP_LOGO_TRAY_ICON_MAC = "trayIconMac.png";
	/**
	 * Last.fm logo
	 */
	public static final String POWERED_BY_LAST_FM = "poweredByLastFm.png";
	/**
	 * Previous icon for tray icon in mac
	 */
	public static final String PREVIOUS_TRAY_ICON_MAC = "previousTrayIconMac.png";
	/**
	 * Next icon for tray icon in mac
	 */
	public static final String NEXT_TRAY_ICON_MAC = "nextTrayIconMac.png";
	/**
	 * Stop icon for tray icon in mac
	 */
	public static final String STOP_TRAY_ICON_MAC = "stopTrayIconMac.png";
	/**
	 * Play icon for tray icon in mac
	 */
	public static final String PLAY_TRAY_ICON_MAC = "playTrayIconMac.png";
	/**
	 * Pause icon for tray icon in mac
	 */
	public static final String PAUSE_TRAY_ICON_MAC = "pauseTrayIconMac.png";
	/**
	 * Previous icon for full screen
	 */
	public static final String PREVIOUS_FULL_SCREEN = "previousFullScreen.png";
	/**
	 * Sourceforge donation button
	 */
	public static final String PROJECT_SUPPORT = "project-support.jpg";
	/**
	 * Next icon for full screen
	 */
	public static final String NEXT_FULL_SCREEN = "nextFullScreen.png";
	/**
	 * Play icon for full screen
	 */
	public static final String PLAY_FULL_SCREEN = "playFullScreen.png";
	/**
	 * Pause icon for full screen
	 */
	public static final String PAUSE_FULL_SCREEN = "pauseFullScreen.png";

	private Images() {
	}

	/**
	 * Returns an image.
	 * 
	 * @param imgName
	 *            the img name
	 * 
	 * @return An ImageIcon
	 */
	public static ImageIcon getImage(final String imgName) {
		URL imgURL = Images.class.getResource(StringUtils.getString("/images/",
				imgName));
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			throw new IllegalArgumentException(StringUtils.getString(
					"Image not found: ", imgName));
		}
	}
}
