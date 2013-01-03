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
import java.awt.Shape;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.geom.Star2D;

/**
 * Icon for new elements
 * 
 * @author alex
 * 
 */
public class NewImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4433286891763946857L;

	private static final int SIZE = 18;
	private static final int STAR_SIZE = 16;

	@Override
	protected ImageIcon createIcon(final Color color) {
		Shape star = new Star2D(SIZE / 2, SIZE / 2, STAR_SIZE - 12,
				STAR_SIZE - 8, 9);
		return IconGenerator.generateIcon(color, SIZE, SIZE, star);
	}
}
