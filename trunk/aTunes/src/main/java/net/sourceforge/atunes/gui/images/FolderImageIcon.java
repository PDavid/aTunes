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

import java.awt.Paint;
import java.awt.Polygon;

import javax.swing.ImageIcon;

public class FolderImageIcon {

	private static final int SIZE = 18;

	public static ImageIcon getIcon() {
		return getIcon(null);
	}
	
	public static ImageIcon getIcon(Paint color) {
		Polygon p = new Polygon();
		p.addPoint(2, 3);
		p.addPoint(8, 3);
		p.addPoint(10, 5);
		p.addPoint(16, 5);
		p.addPoint(16, 15);
		p.addPoint(2, 15);
		
		return IconGenerator.generateIcon(color, SIZE, SIZE, p);
	}

}
