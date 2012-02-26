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

    // Every time a image is added, an attribute must be added
    public static final String APP_LOGO_16 = "logo16x16.png";
    public static final String APP_LOGO_24 = "logo24x24.png";
    public static final String APP_LOGO_32 = "logo32x32.png";
    public static final String APP_LOGO_48 = "logo48x48.png";
    public static final String APP_LOGO_90 = "logo90x90.png";
    public static final String APP_LOGO_150 = "logo150x150.png";
    public static final String APP_LOGO_300 = "logo300x300.png";
    public static final String APP_LOGO_TRAY_ICON_MAC = "trayIconMac.png";
    public static final String POWERED_BY_LAST_FM = "poweredByLastFm.png";
    
    private Images() {}

    /**
     * Returns an image.
     * 
     * @param imgName
     *            the img name
     * 
     * @return An ImageIcon
     */
    public static ImageIcon getImage(String imgName) {
    	URL imgURL = Images.class.getResource(StringUtils.getString("/images/", imgName));
    	if (imgURL != null) {
    		return new ImageIcon(imgURL);
    	} else {
    		throw new IllegalArgumentException(StringUtils.getString("Image not found: ", imgName));
    	}
    }
}
