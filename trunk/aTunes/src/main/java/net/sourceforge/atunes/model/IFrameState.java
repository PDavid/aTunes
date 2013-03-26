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
}