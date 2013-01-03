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
public class AudioFileImageMediumIcon extends CachedIconFactory {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7954539546729244861L;

    private static final int MEDIUM_WIDTH = 70;
    private static final int MEDIUM_HEIGHT = 70;

    @Override
    protected ImageIcon createIcon(final Color color) {
	Ellipse2D.Float e1 = new Ellipse2D.Float(4, 44, 24, 12);
	Ellipse2D.Float e2 = new Ellipse2D.Float(32, 40, 24, 12);

	Rectangle r1 = new Rectangle(20, 16, 8, 34);
	Rectangle r2 = new Rectangle(48, 12, 8, 34);
	Polygon r3 = new Polygon();
	r3.addPoint(20, 16);
	r3.addPoint(20, 24);
	r3.addPoint(56, 16);
	r3.addPoint(56, 8);

	// This icon must be opaque since with this size shapes overlap is
	// visible using alpha
	return IconGenerator.generateIcon(color, MEDIUM_WIDTH, MEDIUM_HEIGHT,
		e1, e2, r1, r2, r3);
    }
}
