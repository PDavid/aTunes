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

package net.sourceforge.atunes.model;

import java.awt.Dimension;
import java.awt.Image;

/**
 * Returns icons for player tray icons 
 * @author alex
 *
 */
public interface IPlayerTrayIconsHandler {
	
	/**
	 * Returns previous icon
	 * @param iconSize
	 * @return
	 */
	Image getPreviousIcon(Dimension iconSize);
	
	/**
	 * Returns next icon
	 * @param iconSize
	 * @return
	 */
	Image getNextIcon(Dimension iconSize);
	
	/**
	 * Returns play icon
	 * @param iconSize
	 * @return
	 */
	Image getPlayIcon(Dimension iconSize);
	
	/**
	 * Returns pause icon
	 * @param iconSize
	 * @return
	 */
	Image getPauseIcon(Dimension iconSize);
	
	/**
	 * Returns stop icon
	 * @param iconSize
	 * @return
	 */
	Image getStopIcon(Dimension iconSize);
}
