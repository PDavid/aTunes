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
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;

public class RssImageIcon {

	private static final int SMALL_SIZE = 18;
	private static final int SIZE = 70;
	private static final int BIG_SIZE = 300;
	
	private static ImageIcon icon;
	
	public static ImageIcon getSmallIcon() {
		if (icon == null) {
			icon = getSmallIcon(null);	
		}
		return icon;
	}
	
	public static ImageIcon getSmallIcon(Paint color) {
		return IconGenerator.generateIcon(color, SMALL_SIZE, SMALL_SIZE, getIconArea(SMALL_SIZE));
	}

	public static ImageIcon getIcon() {
		return getIcon(null);
	}

	public static ImageIcon getIcon(Paint color) {
		return IconGenerator.generateIcon(color, SIZE, SIZE, getIconArea(SIZE));
	}

	public static ImageIcon getBigIcon() {
		return getBigIcon(null);
	}

	public static ImageIcon getBigIcon(Paint color) {
		return IconGenerator.generateIcon(color, BIG_SIZE, BIG_SIZE, getIconArea(BIG_SIZE));
	}

	private static Area getIconArea(int size) {
		int margin = size / 9;
		int arc = size / 4;
		arc = arc > 4 ? arc : 4;

		Area a = new Area(new RoundRectangle2D.Float(margin, margin, size - 2 * margin, size - 2 * margin, arc, arc));

		int pathWidth = size / 10;
		
		int startX = 2 * margin;
		int startY = 2 * margin;
		int endX = size - 2 * margin;
		int endY = size - 2 * margin;
		
		int cpX = size - 2 * margin;
		int cpY = 2 * margin;

		int arcGap = size / 12;
		
		int deltaArc2 = pathWidth + arcGap;
		int deltaArc3 = 2 * pathWidth + 2 * arcGap;

		a.subtract(getArc(pathWidth, cpX, cpY, startX, startY, endX, endY));
		a.subtract(getArc(pathWidth, cpX - deltaArc2, cpY + deltaArc2, startX, startY + deltaArc2, endX - deltaArc2, endY));
		a.subtract(getArc(pathWidth, cpX - deltaArc3, cpY + deltaArc3, startX, startY + deltaArc3, endX - deltaArc3, endY));
		
        return a;
	}
	
	private static Area getArc(int arcWidth, int c1x, int c1y, int sX, int sY, int eX, int eY) {
		GeneralPath gp = new GeneralPath();
		gp.moveTo(sX, sY);
		gp.curveTo(c1x, c1y, c1x, c1y, eX, eY);		
		gp.lineTo(eX - arcWidth, eY);
		gp.curveTo(c1x - arcWidth, c1y + arcWidth, c1x -arcWidth, c1y + arcWidth, sX, sY + arcWidth);
		gp.closePath();
		return new Area(gp);
	}
}
