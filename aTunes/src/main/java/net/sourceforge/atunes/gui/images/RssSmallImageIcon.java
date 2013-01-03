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

import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;

/**
 * Small icon for RSS
 * 
 * @author alex
 * 
 */
public class RssSmallImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7142232675565271849L;

	private static final int SMALL_SIZE = 16;

	@Override
	protected ImageIcon createIcon(final Color color) {
		return IconGenerator.generateIcon(color, SMALL_SIZE, SMALL_SIZE,
				getSmallIconArea(SMALL_SIZE));
	}

	private Area getSmallIconArea(final int size) {
		int margin = 0;
		int arc = size / 4;
		arc = arc > 4 ? arc : 4;

		Area a = new Area(new RoundRectangle2D.Float(margin, margin, size - 2
				* margin, size - 2 * margin, arc, arc));

		int pathWidth = 2;

		int internalMargin = 1;

		int startX = 2 * internalMargin;
		int startY = 2 * internalMargin;
		int endX = size - 2 * internalMargin;
		int endY = size - 2 * internalMargin;

		int cpX = size - 2 * internalMargin;
		int cpY = 2 * internalMargin;

		int arcGap = 1;

		int deltaArc2 = pathWidth + arcGap;
		int deltaArc3 = 2 * pathWidth + 2 * arcGap;

		a.subtract(RssImageIcon.getArc(pathWidth, cpX, cpY, startX, startY,
				endX, endY));
		a.subtract(RssImageIcon
				.getArc(pathWidth, cpX - deltaArc2, cpY + deltaArc2, startX,
						startY + deltaArc2, endX - deltaArc2, endY));
		a.subtract(RssImageIcon
				.getArc(pathWidth, cpX - deltaArc3, cpY + deltaArc3, startX,
						startY + deltaArc3, endX - deltaArc3, endY));

		return a;
	}
}
