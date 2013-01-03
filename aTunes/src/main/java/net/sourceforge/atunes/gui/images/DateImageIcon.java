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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * Icon for a date
 * 
 * @author alex
 * 
 */
public class DateImageIcon extends CachedIconFactory {

    /**
	 * 
	 */
    private static final long serialVersionUID = -3113996469467317328L;

    private static final int SIZE = 18;

    @Override
    protected ImageIcon createIcon(final Color color) {
	Rectangle r = new Rectangle(1, 2, 16, 14);
	Rectangle r2 = new Rectangle(2, 5, 14, 10);

	BufferedImage bi = new BufferedImage(SIZE, SIZE,
		BufferedImage.TYPE_4BYTE_ABGR);
	Graphics2D g = bi.createGraphics();
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
	g.setPaint(color);

	g.fill(r);

	g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
	g.fill(r2);

	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));

	g.setFont(new Font("Dialog", Font.BOLD, 8));
	g.drawString("31", 3, 13);

	g.dispose();
	return new ImageIcon(bi);
    }
}
