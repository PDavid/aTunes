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
 * Icon for genre
 * 
 * @author alex
 * 
 */
public class GenreImageIcon extends CachedIconFactory {

    /**
	 * 
	 */
    private static final long serialVersionUID = -939539268509387704L;

    private static final int SIZE = 18;

    @Override
    protected ImageIcon createIcon(final Color color) {
	Ellipse2D e1 = new Ellipse2D.Float(2, 10, 7, 7);
	Ellipse2D e2 = new Ellipse2D.Float(6, 8, 5, 5);
	Polygon p = new Polygon();
	p.addPoint(8, 10);
	p.addPoint(14, 4);
	p.addPoint(14, 3);
	p.addPoint(15, 3);
	p.addPoint(16, 4);
	p.addPoint(16, 5);
	p.addPoint(15, 5);
	p.addPoint(9, 11);

	Ellipse2D e3 = new Ellipse2D.Float(8, 10, 1, 1);

	Area a = new Area(e1);
	a.add(new Area(e2));
	a.add(new Area(p));
	a.subtract(new Area(e3));

	return IconGenerator.generateIcon(color, SIZE, SIZE, a);
    }
}
