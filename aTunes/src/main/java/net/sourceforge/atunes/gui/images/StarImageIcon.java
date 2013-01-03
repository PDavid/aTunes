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

import java.awt.Paint;
import java.awt.geom.Area;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.geom.Star2D;

final class StarImageIcon {

	private static final int STAR_SIZE = 12;
	private static final int INNER_RADIUS = 2;
	private static final int OUTER_RADIUS = 6;
	private static final int STAR_GAP = 4;

	private StarImageIcon() {
	}

	/**
	 * @param color
	 * @param stars
	 * @return
	 */
	static ImageIcon getIcon(final Paint color, final int stars) {
		Area a = new Area();
		for (int i = 0; i < stars; i++) {
			a.add(new Area(new Star2D((STAR_SIZE + STAR_GAP) * i + STAR_SIZE
					/ 2, STAR_SIZE / 2, INNER_RADIUS, OUTER_RADIUS, 5)));
		}

		return IconGenerator.generateIcon(color, STAR_SIZE * stars + STAR_GAP
				* (stars - 1), STAR_SIZE, a);
	}
}
