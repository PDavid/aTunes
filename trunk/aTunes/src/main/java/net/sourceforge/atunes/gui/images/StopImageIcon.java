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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;

public class StopImageIcon {

	public static ImageIcon getIcon(Dimension size) {
		return getIcon(null, size);
	}
	
	public static ImageIcon getTrayIcon(Paint color, Dimension size) {
		return getCustomTrayIcon(color, size);
	}
	
	private static ImageIcon getIcon(Paint color, Dimension size) {
		Rectangle stopShape = new Rectangle(- size.width / 6, - size.height / 6, 2 * size.width / 6, 2 * size.width / 6);

		BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = bi.createGraphics();
    	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g2.setPaint(color != null ? color : LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintFor(null));
    	g2.translate(size.getWidth() / 2, size.getHeight() * (4f/9f));    	
		
   		g2.fill(stopShape);
    	g2.dispose();

    	return new ImageIcon(bi);
	}

	private static ImageIcon getCustomTrayIcon(Paint color, Dimension size) {
		// Optimized for low sizes
		int factor = 5;
		Rectangle r = new Rectangle(size.width / factor, size.height / factor, size.width - 2 * size.width / factor, size.height - 2 * size.height / factor);
		return IconGenerator.generateIcon(color, size.width, size.height, r);
	}

}
