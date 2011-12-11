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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;

import javax.swing.ImageIcon;


public class PreviousTrayImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -888985666372919140L;

	private Dimension size;
	
	/**
	 * @param size
	 */
	public void setSize(Dimension size) {
		this.size = size;
	}
	
	@Override
	protected ImageIcon createIcon(Color color) {
		// Optimized for low sizes
		int horizontalFactor = 10;
		int verticalFactor = 5;
		Polygon s1 = new Polygon();
		s1.addPoint(size.width / horizontalFactor, size.height / 2);
		s1.addPoint(size.width / 2 - size.width / horizontalFactor, size.height / verticalFactor);
		s1.addPoint(size.width / 2 - size.width / horizontalFactor, size.height - size.height / verticalFactor);

		Polygon s2 = new Polygon();
		s2.addPoint(size.width / 2 + size.width / horizontalFactor, size.height / 2);
		s2.addPoint(size.width / 2 + size.width / 2 - size.width / horizontalFactor, size.height / verticalFactor);
		s2.addPoint(size.width / 2 + size.width / 2 - size.width / horizontalFactor, size.height - size.height / verticalFactor);

		return IconGenerator.generateIcon(color, size.width, size.height, s1, s2);
	}

}
