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
		int centerPointTopX = WIDTH / 2;
		int centerPointTopY = HEIGHT / 3;
		
		int lCtlP1X = WIDTH / 4;
		int lCtlP1Y = 0;
		int rCtlP1X = WIDTH - WIDTH / 4;
		int rCtlP1Y = lCtlP1Y;

		int lCtlP2X = - WIDTH / 3;
		int lCtlP2Y = HEIGHT * 3/7;
		int rCtlP2X = WIDTH + WIDTH / 3;
		int rCtlP2Y = lCtlP2Y;

		int centerPointBottomX = WIDTH / 2;
		int centerPointBottomY = HEIGHT - HEIGHT / 5;
		
		CubicCurve2D q = new CubicCurve2D.Float(centerPointTopX, centerPointTopY, lCtlP1X, lCtlP1Y, lCtlP2X, lCtlP2Y, centerPointBottomX, centerPointBottomY);
		CubicCurve2D q2 = new CubicCurve2D.Float(centerPointTopX, centerPointTopY, rCtlP1X, rCtlP1Y, rCtlP2X, rCtlP2Y, centerPointBottomX, centerPointBottomY);
		
        return IconGenerator.generateIcon(color, WIDTH, HEIGHT, q, q2);
	}
}
