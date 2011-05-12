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

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;

class IconGenerator {

	/**
	 * Creates an image icon drawing a list of shapes
	 * @param width
	 * @param height
	 * @param shapes
	 * @return
	 */
	protected static final ImageIcon generateIcon(int width, int height, Shape... shapes) {
        return generateIcon(null, width, height, shapes);
	}

	/**
	 * Creates an image icon drawing a list of shapes
	 * @param paint
	 * @param width
	 * @param height
	 * @param shapes
	 * @return
	 */
	protected static final ImageIcon generateIcon(Paint paint, int width, int height, Shape... shapes) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(paint != null ? paint : LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintFor(null));
        for (Shape s : shapes) {
        	g.fill(s);
        }
        g.dispose();
        return new ImageIcon(bi);
	}

	/**
	 * Creates an image icon drawing a list of shapes
	 * @param width
	 * @param height
	 * @param shapes
	 * @return
	 */
	protected static final ImageIcon generateOpaqueIcon(int width, int height, Shape... shapes) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForOpaqueIcon());
        for (Shape s : shapes) {
        	g.fill(s);
        }
        g.dispose();
        return new ImageIcon(bi);
	}
}
