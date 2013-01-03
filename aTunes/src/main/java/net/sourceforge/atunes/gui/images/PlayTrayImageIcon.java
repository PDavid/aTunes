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
import java.awt.Dimension;
import java.awt.Polygon;

import javax.swing.ImageIcon;

/**
 * Icon for play in tray icon
 * 
 * @author alex
 * 
 */
public class PlayTrayImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5269753321532297780L;

	private Dimension size;

	/**
	 * @param size
	 */
	public void setSize(final Dimension size) {
		this.size = size;
	}

	@Override
	protected ImageIcon createIcon(final Color color) {
		// Optimized for low sizes
		int horizontalFactor = 4;
		int verticalFactor = 5;

		Polygon s1 = new Polygon();
		s1.addPoint(this.size.width / horizontalFactor, this.size.height
				/ verticalFactor);
		s1.addPoint(this.size.width / horizontalFactor, this.size.height
				- this.size.height / verticalFactor);
		s1.addPoint(this.size.width - this.size.width / horizontalFactor,
				this.size.height / 2);

		return IconGenerator.generateIcon(color, this.size.width,
				this.size.height, s1);
	}

}
