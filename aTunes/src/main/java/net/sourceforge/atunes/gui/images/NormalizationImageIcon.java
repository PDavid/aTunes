/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * Icon for normalization
 * 
 * @author alex
 * 
 */
public class NormalizationImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3474284929336191066L;

	private static final int SIZE = 18;

	@Override
	protected ImageIcon createIcon(final Color color) {
		Line2D l1 = new Line2D.Float(2, 9, 6, 9);
		Line2D l2 = new Line2D.Float(6, 9, 8, 3);
		Line2D l3 = new Line2D.Float(8, 3, 11, 14);
		Line2D l4 = new Line2D.Float(11, 14, 13, 9);
		Line2D l5 = new Line2D.Float(13, 9, 16, 9);
		BufferedImage bi = new BufferedImage(SIZE, SIZE,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setPaint(color);
		g.setStroke(new BasicStroke(2));
		g.draw(l1);
		g.draw(l2);
		g.draw(l3);
		g.draw(l4);
		g.draw(l5);
		g.dispose();
		return new ImageIcon(bi);
	}
}
