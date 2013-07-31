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

import javax.swing.ImageIcon;

/**
 * Icon for play list
 * 
 * @author alex
 * 
 */
public class PlayListImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9026267553013591250L;

	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;

	@Override
	protected ImageIcon createIcon(final Color color) {
		int x = 2;
		int widht = WIDTH - x * 2;
		int height = 1;
		Rectangle p1 = new Rectangle(x, 3, widht, height);
		Rectangle p2 = new Rectangle(x, 6, widht, height);
		Rectangle p3 = new Rectangle(x, 9, widht, height);
		Rectangle p4 = new Rectangle(x, 12, widht, height);
		return IconGenerator.generateIcon(color, WIDTH, HEIGHT, p1, p2, p3, p4);
	}
}
