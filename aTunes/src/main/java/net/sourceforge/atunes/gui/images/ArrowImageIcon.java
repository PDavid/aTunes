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

import java.awt.Polygon;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.ILookAndFeel;

public class ArrowImageIcon {

	private static final int WIDTH = 12;
	private static final int HEIGHT = 12;
	
	private ArrowImageIcon() {}
	
	/**
	 * @param lookAndFeel
	 * @return
	 */
	public static ImageIcon getUpIcon(ILookAndFeel lookAndFeel) {
        Polygon upShape = new Polygon();
        upShape.addPoint(2, 9);
        upShape.addPoint(10, 9);
        upShape.addPoint(6, 5);        
		return IconGenerator.generateIcon(WIDTH, HEIGHT, lookAndFeel, upShape);
	}

	/**
	 * @param lookAndFeel 
	 * @return
	 */
	public static ImageIcon getDownIcon(ILookAndFeel lookAndFeel) {
        Polygon downShape = new Polygon();
        downShape.addPoint(2, 5);
        downShape.addPoint(10, 5);
        downShape.addPoint(6, 9);        
		return IconGenerator.generateIcon(WIDTH, HEIGHT, lookAndFeel, downShape);
	}
}
