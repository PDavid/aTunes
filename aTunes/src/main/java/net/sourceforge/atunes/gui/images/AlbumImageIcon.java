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

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

final class AlbumImageIcon {
	
	private AlbumImageIcon() {}

	/**
	 * @param width
	 * @param height
	 * @param xAxis
	 * @param yAxis
	 * @return
	 */
	protected static Area getIconArea(int width, int height, int xAxis, int yAxis) {
		int margin = 2 + xAxis;
		int radius = width / 4;
		int internalMargin = width / 2 - radius / 2 + xAxis;
		Ellipse2D.Float p = new Ellipse2D.Float(margin + xAxis, margin + yAxis, width - 2 * margin, height - 2 * margin);
		Ellipse2D.Float p2 = new Ellipse2D.Float(internalMargin + xAxis, internalMargin + yAxis, radius, radius);

		Area a = new Area(p);
		a.subtract(new Area(p2));
		
        return a;
	}
}
