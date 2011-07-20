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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;

public class PreviousImageIcon {

	public static ImageIcon getIcon(Dimension size) {
		return getIcon(null, size);
	}
	
	public static ImageIcon getTrayIcon(Paint color, Dimension size) {
		return getCustomTrayIcon(color, size);
	}
	
	private static ImageIcon getIcon(Paint color, Dimension size) {
		Polygon previousShape = new Polygon();
		previousShape.addPoint(size.width / 5, size.height / 6);
		previousShape.addPoint(size.width / 5, - size.height / 6);
		previousShape.addPoint(0,  0);        

		BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = bi.createGraphics();
    	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g2.setPaint(color != null ? color : LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForSpecialControls());
		g2.translate(size.getWidth() / (double) 2 - size.width / (double) 16, size.getHeight() / (double) 2);
    	
   		g2.fill(previousShape);
   		g2.translate(-size.width / 5, 0);
   		g2.fill(previousShape);
    	g2.dispose();

    	return new ImageIcon(bi);
	}

	private static ImageIcon getCustomTrayIcon(Paint color, Dimension size) {
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
