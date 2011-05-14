/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.ImageIcon;

public class PlayListStateImageIcon {

	private static final int WIDTH = 14;
	private static final int HEIGHT = 14;
	
	public static ImageIcon getStopIcon(Paint color) {
		return IconGenerator.generateIcon(color, WIDTH, HEIGHT, new Rectangle(4, 4, 8, 8));
	}

	public static ImageIcon getPlayIcon(Paint color) {
		Polygon shape = new Polygon();
		shape.addPoint(3, 3);
		shape.addPoint(3, 11);
		shape.addPoint(11, 7);
		return IconGenerator.generateIcon(color, WIDTH, HEIGHT, shape);
	}

	public static ImageIcon getPauseIcon(Paint color) {
		return IconGenerator.generateIcon(color, WIDTH, HEIGHT, new Rectangle(3, 3, 3, 8), new Rectangle(8, 3, 3, 8));
	}
}
