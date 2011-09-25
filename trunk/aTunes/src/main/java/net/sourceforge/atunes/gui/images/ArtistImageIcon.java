/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.ILookAndFeel;

public class ArtistImageIcon {

	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;
	
	private static ImageIcon icon;
	
	/**
	 * @param lookAndFeel 
	 * @return
	 */
	public static ImageIcon getIcon(ILookAndFeel lookAndFeel) {
		if (icon == null) {
			icon = getIcon(null, lookAndFeel);	
		}
		return icon;
	}
	
	/**
	 * @param color
	 * @param lookAndFeel
	 * @return
	 */
	public static ImageIcon getIcon(Paint color, ILookAndFeel lookAndFeel) {
		Rectangle clip = new Rectangle(2, 1, WIDTH - 4, HEIGHT - 2);
		return IconGenerator.generateIcon(color, clip, WIDTH, HEIGHT, lookAndFeel, getArtistIconArea(0));
	}
	
	protected static Area getArtistIconArea(int distanceFromCenter) {
		Area a = new Area();
		a.add(new Area(new Ellipse2D.Float(WIDTH / 2 - WIDTH * 2/6 + distanceFromCenter, HEIGHT / 2, WIDTH * 2/3, HEIGHT)));
		float headWidth = WIDTH / 2.5f;
		float headHeight = HEIGHT / 2.5f;
		a.add(new Area(new Ellipse2D.Float(WIDTH / 2 - headWidth / 2 + distanceFromCenter, HEIGHT / 2 - headHeight * 0.8f, headWidth, headHeight)));
		return a;
	}
}
