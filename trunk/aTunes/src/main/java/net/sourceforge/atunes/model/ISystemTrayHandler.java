/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.model;


/**
 * Responsible of showing controls or icons in system tray if available
 * @author alex
 *
 */
public interface ISystemTrayHandler extends IHandler {

	/**
	 * Sets the playing.
	 * 
	 * @param playing
	 *            the new playing
	 */
	public void setPlaying(boolean playing);

	/**
	 * Sets the tray tool tip.
	 * 
	 * @param msg
	 *            the new tray tool tip
	 */
	public void setTrayToolTip(String msg);

}