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

package net.sourceforge.atunes.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents state of frame
 * @author alex
 *
 */
public interface IFrameState extends Serializable {

	/**
	 * Sets split pane position
	 * @param splitPane
	 * @param pos
	 */
	public void putSplitPaneDividerPos(String splitPane, int pos);

	/**
	 * Gets split pane position
	 * @param splitPane
	 * @return
	 */
	public int getSplitPaneDividerPos(String splitPane);

	/**
	 * Gets all split pane positions
	 * @return
	 */
	public Map<String, Integer> getSplitPaneDividerPositions();

	/**
	 * Get x position of frame
	 * @return
	 */
	public int getXPosition();

	/**
	 * Set x position of frame
	 * @param xPosition
	 */
	public void setXPosition(int xPosition);

	/**
	 * Get y position of frame
	 * @return
	 */
	public int getYPosition();

	/**
	 * Set y position of frame
	 * @param yPosition
	 */
	public void setYPosition(int yPosition);

	/**
	 * Returns if frame is maximized
	 * @return
	 */
	public boolean isMaximized();

	/**
	 * Sets frame maximized
	 * @param maximized
	 */
	public void setMaximized(boolean maximized);

	/**
	 * Returns frame width
	 * @return
	 */
	public int getWindowWidth();

	/**
	 * Sets frame width
	 * @param windowWidth
	 */
	public void setWindowWidth(int windowWidth);

	/**
	 * Returns frame height
	 * @return
	 */
	public int getWindowHeight();

	/**
	 * Sets window height
	 * @param windowHeight
	 */
	public void setWindowHeight(int windowHeight);

}