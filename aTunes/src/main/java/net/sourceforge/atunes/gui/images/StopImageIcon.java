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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * Icon for stop button
 * 
 * @author alex
 * 
 */
public class StopImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1069304212278748656L;

	private Dimension size;

	/**
	 * @param size
	 */
	public void setSize(final Dimension size) {
		this.size = size;
	}

	@Override
	protected ImageIcon createIcon(final Color color) {
		Rectangle stopShape = new Rectangle(-this.size.width / 6,
				-this.size.height / 6, 2 * this.size.width / 6,
				2 * this.size.width / 6);

		BufferedImage bi = new BufferedImage(this.size.width, this.size.height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(color);
		g2.translate(this.size.getWidth() / 2, this.size.getHeight()
				* (4f / 9f));

		g2.fill(stopShape);
		g2.dispose();

		return new ImageIcon(bi);
	}
}
