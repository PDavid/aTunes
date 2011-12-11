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
import java.awt.Rectangle;

import javax.swing.ImageIcon;


public class StopTrayImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2286594851677924981L;

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
		int factor = 5;
		Rectangle r = new Rectangle(size.width / factor, size.height / factor, size.width - 2 * size.width / factor, size.height - 2 * size.height / factor);
		return IconGenerator.generateIcon(color, size.width, size.height, r);
	}
}
