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
 * Icon for "downloaded" item
 * 
 * @author alex
 * 
 */
public class DownloadImageIcon extends CachedIconFactory {

    /**
	 * 
	 */
    private static final long serialVersionUID = -7668377655244896662L;

    private static final int SIZE = 18;

    @Override
    protected ImageIcon createIcon(final Color color) {
	Polygon a = new Polygon();
	a.addPoint(7, 4);
	a.addPoint(11, 4);
	a.addPoint(11, 11);
	a.addPoint(14, 11);
	a.addPoint(9, 15);
	a.addPoint(4, 11);
	a.addPoint(7, 11);
	return IconGenerator.generateIcon(color, SIZE, SIZE, a);
    }
}
