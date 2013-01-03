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
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;

/**
 * Icon for folder
 * 
 * @author alex
 * 
 */
public class FolderImageIcon extends CachedIconFactory {

    /**
	 * 
	 */
    private static final long serialVersionUID = -827295313871199188L;

    private static final int SIZE = 18;

    @Override
    protected ImageIcon createIcon(final Color color) {
	RoundRectangle2D r = new RoundRectangle2D.Float(2, 5, 14, 10, 4, 4);
	Polygon p = new Polygon();
	p.addPoint(2, 8);
	p.addPoint(2, 5);
	p.addPoint(4, 3);
	p.addPoint(8, 3);
	p.addPoint(10, 5);
	p.addPoint(10, 8);

	return IconGenerator.generateIcon(color, SIZE, SIZE, r, p);
    }

}
