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
import java.awt.geom.Area;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.ILookAndFeel;

import org.jdesktop.swingx.geom.Star2D;

public class StarImageIcon {

	private static final int STAR_SIZE = 16;
	private static final int STAR_GAP = 3;
	
	/**
	 * @param color
	 * @param stars
	 * @param lookAndFeel
	 * @return
	 */
	public static ImageIcon getIcon(Paint color, int stars, ILookAndFeel lookAndFeel) {
		Area a = new Area();
		for (int i = 0; i < stars; i++) {
			a.add(new Area(new Star2D((STAR_SIZE + STAR_GAP) * i + STAR_SIZE / 2, STAR_SIZE / 2, STAR_SIZE - 12, STAR_SIZE - 6, 5)));
		}

        return IconGenerator.generateIcon(color, STAR_SIZE * stars + STAR_GAP * (stars - 1), STAR_SIZE, lookAndFeel, a);
	}
}
