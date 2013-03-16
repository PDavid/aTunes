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

import javax.swing.ImageIcon;

/**
 * Icon for left arrow
 * 
 * @author alex
 * 
 */
public class ArrowLeftImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -939539268509387704L;

	private static final int SIZE = 14;

	@Override
	protected ImageIcon createIcon(final Color color) {
		Polygon shape = new Polygon();
		shape.addPoint(3, 7);
		shape.addPoint(11, 11);
		shape.addPoint(11, 3);

		return IconGenerator.generateIcon(color, SIZE, SIZE, shape);
	}
}
