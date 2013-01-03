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
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

/**
 * Icon for filter
 * 
 * @author alex
 * 
 */
public class FilterImageIcon extends CachedIconFactory {

    /**
	 * 
	 */
    private static final long serialVersionUID = 2262229990261535398L;

    private static final int SIZE = 16;

    @Override
    protected ImageIcon createIcon(final Color color) {
	return IconGenerator.generateIcon(color, SIZE, SIZE,
		getIconArea(SIZE, SIZE, 0, 0));
    }

    /**
     * @param width
     * @param height
     * @param xAxis
     * @param yAxis
     * @return
     */
    protected static Area getIconArea(final int width, final int height,
	    final int xAxis, final int yAxis) {
	Ellipse2D.Float p = new Ellipse2D.Float(0, 1, width / 2 + 2,
		height / 2 + 2);
	Ellipse2D.Float p2 = new Ellipse2D.Float(2, 3, width / 2 - 2,
		height / 2 - 2);

	Area a = new Area(p);
	a.subtract(new Area(p2));

	Polygon p3 = new Polygon();
	p3.addPoint(width / 2, height / 2 + 1);
	p3.addPoint(width - 3, height - 2);
	p3.addPoint(width - 5, height - 1);
	p3.addPoint(width / 2 - 3, height / 2 + 1);

	a.add(new Area(p3));

	return a;
    }

}
