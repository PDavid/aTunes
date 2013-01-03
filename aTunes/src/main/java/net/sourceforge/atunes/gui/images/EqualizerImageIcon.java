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
 * Icon for equalizer
 * 
 * @author alex
 * 
 */
public class EqualizerImageIcon extends CachedIconFactory {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4285705355901030724L;

    private static final int SIZE = 18;

    @Override
    protected ImageIcon createIcon(final Color color) {
	Rectangle r1 = new Rectangle(3, 3, 2, 12);
	Rectangle r2 = new Rectangle(8, 3, 2, 12);
	Rectangle r3 = new Rectangle(13, 3, 2, 12);
	Rectangle r4 = new Rectangle(2, 5, 4, 2);
	Rectangle r5 = new Rectangle(7, 8, 4, 2);
	Rectangle r6 = new Rectangle(12, 11, 4, 2);

	return IconGenerator.generateIcon(color, SIZE, SIZE, r1, r2, r3, r4,
		r5, r6);
    }
}
