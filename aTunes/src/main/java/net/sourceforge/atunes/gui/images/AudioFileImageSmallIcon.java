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
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

/**
 * Icon for audio file
 * 
 * @author alex
 * 
 */
public class AudioFileImageSmallIcon extends CachedIconFactory {

    /**
	 * 
	 */
    private static final long serialVersionUID = 4256539676766817966L;

    private static final int SMALL_WIDTH = 16;
    private static final int SMALL_HEIGHT = 16;

    @Override
    protected ImageIcon createIcon(final Color color) {
	Ellipse2D.Float e1 = new Ellipse2D.Float(1, 11, 6, 3);
	Ellipse2D.Float e2 = new Ellipse2D.Float(8, 10, 6, 3);

	Rectangle r1 = new Rectangle(5, 4, 2, 8);
	Rectangle r2 = new Rectangle(12, 3, 2, 8);
	Polygon r3 = new Polygon();
	r3.addPoint(5, 4);
	r3.addPoint(5, 7);
	r3.addPoint(14, 4);
	r3.addPoint(14, 2);

	return IconGenerator.generateIcon(color, SMALL_WIDTH, SMALL_HEIGHT, e1,
		e2, r1, r2, r3);
    }
}
