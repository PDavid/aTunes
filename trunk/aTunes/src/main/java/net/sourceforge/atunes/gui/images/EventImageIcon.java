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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * Icon for event
 * 
 * @author alex
 * 
 */
public class EventImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2262229990261535398L;

	private static final int SIZE = 16;

	@Override
	protected ImageIcon createIcon(final Color color) {
		int margin = 1;

		BufferedImage bi = new BufferedImage(SIZE, SIZE,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setPaint(color);
		g.draw(new Ellipse2D.Float(margin * 4, margin, SIZE - 8 * margin, SIZE
				- 2 * margin));
		g.draw(new Ellipse2D.Float(margin, margin, SIZE - 2 * margin, SIZE - 2
				* margin));
		g.draw(new Line2D.Float(margin, SIZE / 2, SIZE - margin, SIZE / 2));
		g.draw(new Line2D.Float(SIZE / 2, margin, SIZE / 2, SIZE - margin));
		g.dispose();
		return new ImageIcon(bi);
	}
}
