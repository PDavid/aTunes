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
import java.awt.Image;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.IPlayerTrayIconsHandler;

/**
 * Handler for tray icons in OS X tray
 * @author alex
 *
 */
public class MacPlayerTrayIconsHandler implements IPlayerTrayIconsHandler {
	
	@Override
	public Image getNextIcon(Dimension iconSize) {
    	return Images.getImage(Images.NEXT_TRAY_ICON_MAC).getImage();
	}
	
	@Override
	public Image getPauseIcon(Dimension iconSize) {
    	return Images.getImage(Images.PAUSE_TRAY_ICON_MAC).getImage();
	}
	
	@Override
	public Image getPlayIcon(Dimension iconSize) {
    	return Images.getImage(Images.PLAY_TRAY_ICON_MAC).getImage();
	}
	
	@Override
	public Image getPreviousIcon(Dimension iconSize) {
    	return Images.getImage(Images.PREVIOUS_TRAY_ICON_MAC).getImage();
	}
	
	@Override
	public Image getStopIcon(Dimension iconSize) {
    	return Images.getImage(Images.STOP_TRAY_ICON_MAC).getImage();
	}
}
