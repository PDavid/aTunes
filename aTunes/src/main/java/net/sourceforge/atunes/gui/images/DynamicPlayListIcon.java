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
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

/**
 * Icon for dynamic play list
 * 
 * @author alex
 * 
 */
public class DynamicPlayListIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2262229990261535398L;

	private static final int SIZE = 16;

	@Override
	protected ImageIcon createIcon(final Color color) {
		int margin = 3;
		int radius = 6;
		int internalMargin = SIZE / 2 - radius / 2;

		Area a = new Area(new Ellipse2D.Float(margin, margin,
				SIZE - 2 * margin, SIZE - 2 * margin));
		a.subtract(new Area(new Ellipse2D.Float(internalMargin, internalMargin,
				radius, radius)));

		int smallCircleSize = 2;
		Rectangle p1 = new Rectangle(SIZE / 2 - smallCircleSize / 2, margin
				- smallCircleSize / 2 - 1, smallCircleSize, smallCircleSize);

		Rectangle p2 = new Rectangle(SIZE / 2 - smallCircleSize / 2, SIZE
				- margin + smallCircleSize / 2 - 1, smallCircleSize,
				smallCircleSize);

		Rectangle p3 = new Rectangle(margin - smallCircleSize / 2 - 1, SIZE / 2
				- smallCircleSize / 2, smallCircleSize, smallCircleSize);

		Rectangle p4 = new Rectangle(SIZE - margin - smallCircleSize / 2 + 1,
				SIZE / 2 - smallCircleSize / 2, smallCircleSize,
				smallCircleSize);

		Rectangle p5 = new Rectangle(SIZE / 4 - smallCircleSize / 2, SIZE / 4
				- smallCircleSize / 2, smallCircleSize, smallCircleSize);

		Rectangle p6 = new Rectangle(SIZE * 3 / 4 - smallCircleSize / 2, SIZE
				/ 4 - smallCircleSize / 2, smallCircleSize, smallCircleSize);

		Rectangle p7 = new Rectangle(SIZE / 4 - smallCircleSize / 2, SIZE * 3
				/ 4 - smallCircleSize / 2, smallCircleSize, smallCircleSize);

		Rectangle p8 = new Rectangle(SIZE * 3 / 4 - smallCircleSize / 2, SIZE
				* 3 / 4 - smallCircleSize / 2, smallCircleSize, smallCircleSize);

		return IconGenerator.generateIcon(color, SIZE, SIZE, a, p1, p2, p3, p4,
				p5, p6, p7, p8);
	}
}
