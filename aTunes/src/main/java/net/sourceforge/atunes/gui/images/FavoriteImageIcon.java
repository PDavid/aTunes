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
import java.awt.geom.CubicCurve2D;

import javax.swing.ImageIcon;

public class FavoriteImageIcon {

	private static final int WIDTH = 18;
	private static final int HEIGHT = 18;
	
	private static ImageIcon icon;
	
	public static ImageIcon getIcon() {
		if (icon == null) {
			icon = getIcon(null);	
		}
		return icon;
	}
	
	public static ImageIcon getIcon(Paint color) {
        return IconGenerator.generateIcon(color, WIDTH, HEIGHT, getIconArea(WIDTH, HEIGHT, 0, 0));
	}
	
	protected static Area getIconArea(int width, int height, int xAxis, int yAxis) {
		int centerPointTopX = width / 2 + xAxis;
		int centerPointTopY = height / 3 + yAxis;
		
		int lCtlP1X = width / 4 + xAxis;
		int lCtlP1Y = yAxis;
		int rCtlP1X = width - width / 4 + xAxis;
		int rCtlP1Y = lCtlP1Y;

		int lCtlP2X = - width / 3 + xAxis;
		int lCtlP2Y = height * 3/7 + yAxis;
		int rCtlP2X = width + width / 3 + xAxis;
		int rCtlP2Y = lCtlP2Y;

		int centerPointBottomX = width / 2 + xAxis;
		int centerPointBottomY = height - height / 5 + yAxis;
		
		CubicCurve2D q = new CubicCurve2D.Float(centerPointTopX, centerPointTopY, lCtlP1X, lCtlP1Y, lCtlP2X, lCtlP2Y, centerPointBottomX, centerPointBottomY);
		CubicCurve2D q2 = new CubicCurve2D.Float(centerPointTopX, centerPointTopY, rCtlP1X, rCtlP1Y, rCtlP2X, rCtlP2Y, centerPointBottomX, centerPointBottomY);
		Area a = new Area();
		a.add(new Area(q));
		a.add(new Area(q2));
		
		return a;
	}
}
