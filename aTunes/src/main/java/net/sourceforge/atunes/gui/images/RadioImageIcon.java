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
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;

/**
 * Icon for radio
 * 
 * @author alex
 * 
 */
public final class RadioImageIcon {

	private RadioImageIcon() {
	}

	/**
	 * @param color
	 * @param w
	 * @param h
	 * @return
	 */
	protected static ImageIcon getIcon(final Paint color, final int w,
			final int h) {
		int marginX = w / 10;
		int marginTopY = h / 2;
		int marginBottomY = h / 10;
		int arc = 4;
		Area a = new Area(new RoundRectangle2D.Float(marginX, marginTopY, w - 2
				* marginX, h - marginTopY - marginBottomY, arc, arc));

		int antennaWidth = w / 12;
		Polygon p = new Polygon();
		p.addPoint(marginX + antennaWidth, marginBottomY);
		p.addPoint(marginX + 2 * antennaWidth, marginBottomY);
		p.addPoint(w - marginX - antennaWidth, h - marginTopY);
		p.addPoint(w - marginX - 2 * antennaWidth, h - marginTopY);

		a.add(new Area(p));

		int insideMargin = w / 16;
		marginX = marginX + insideMargin;
		marginBottomY = marginBottomY + 2 * insideMargin;
		marginTopY = marginTopY + insideMargin;
		a.subtract(new Area(new Rectangle(marginX, marginTopY, w - 2 * marginX,
				h - marginTopY - marginBottomY)));

		return IconGenerator.generateIcon(color, w, h, a);
	}

}
