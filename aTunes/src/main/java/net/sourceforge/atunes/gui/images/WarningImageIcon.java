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
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;

/**
 * Icon to show a warning
 * 
 * @author alex
 * 
 */
public class WarningImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3768203239923923944L;

	private static final int SIZE = 18;

	@Override
	protected ImageIcon createIcon(final Color color) {
		Polygon p = new Polygon();
		p.addPoint(9, 2);
		p.addPoint(2, 16);
		p.addPoint(16, 16);

		Area a = new Area(p);
		a.subtract(new Area(new RoundRectangle2D.Float(8, 6, 2, 5, 4, 4)));
		a.subtract(new Area(new RoundRectangle2D.Float(8, 12, 2, 2, 4, 4)));

		return IconGenerator.generateIcon(color, SIZE, SIZE, a);
	}
}
