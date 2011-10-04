/*
 * aTunes 2.2.0-SNAPSHOT
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

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.ILookAndFeel;

public class ShuffleImageIcon {

	/**
	 * @param color
	 * @param lookAndFeel
	 * @return
	 */
	public static ImageIcon getIcon(Paint color, ILookAndFeel lookAndFeel) {
		Polygon p = new Polygon();
		p.addPoint(2, 3);
		p.addPoint(12, 3);
		p.addPoint(12, 5);
		p.addPoint(4, 5);
		p.addPoint(4, 8);
		p.addPoint(16, 8);
		p.addPoint(16, 15);
		p.addPoint(6, 15);
		p.addPoint(6, 13);
		p.addPoint(14, 13);
		p.addPoint(14, 10);
		p.addPoint(2, 10);
		
		Polygon p3 = new Polygon();
		p3.addPoint(12, 1);
		p3.addPoint(16, 4);
		p3.addPoint(12, 7);

		Polygon p4 = new Polygon();
		p4.addPoint(2, 14);
		p4.addPoint(6, 11);
		p4.addPoint(6, 17);

		
		return IconGenerator.generateIcon(color, 18, 18, lookAndFeel, p, p3, p4);
	}
}
