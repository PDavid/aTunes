/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.Polygon;

import javax.swing.ImageIcon;

public class RepeatImageIcon {

	public static ImageIcon getIcon() {
		Polygon p = new Polygon();
		p.addPoint(1, 5);
		p.addPoint(1, 3);
		p.addPoint(17, 3);
		p.addPoint(17, 15);
		p.addPoint(1, 15);
		p.addPoint(1, 5);
		p.addPoint(3, 5);
		p.addPoint(3, 13);
		p.addPoint(15, 13);
		p.addPoint(15, 5);
		
		Polygon p3 = new Polygon();
		p3.addPoint(11, 1);
		p3.addPoint(15, 4);
		p3.addPoint(11, 7);

		Polygon p4 = new Polygon();
		p4.addPoint(3, 14);
		p4.addPoint(7, 11);
		p4.addPoint(7, 17);

		
		return IconGenerator.generateIcon(18, 18, p, p3, p4);
	}
}
