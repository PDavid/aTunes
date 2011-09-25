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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.ILookAndFeel;

public class VideoImageIcon {

	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;
	
	/**
	 * @param lookAndFeel
	 * @return
	 */
	public static ImageIcon getIcon(ILookAndFeel lookAndFeel) {
		return getIcon(null, lookAndFeel);	
	}
	
	/**
	 * @param color
	 * @param lookAndFeel
	 * @return
	 */
	public static ImageIcon getIcon(Paint color, ILookAndFeel lookAndFeel) {
		int margin = 2;
		int internalMarginX = 6;
		int internalMarginY = 3;
		Rectangle r = new Rectangle(margin, margin, WIDTH - 2 * margin, HEIGHT - 2 * margin);
		Rectangle r2 = new Rectangle(internalMarginX, internalMarginY, WIDTH - 2 * internalMarginX, HEIGHT - 2 * internalMarginY);
		
		Rectangle r3 = new Rectangle(internalMarginX - margin - 2, internalMarginX - margin - 2);
		
		BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(color != null ? color : lookAndFeel.getPaintForSpecialControls());
       	g.fill(r);
       	g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
       	g.fill(r2);
       	
       	int gap = 3;
       	for (int i = 0; i < 4; i++) {
       		r3.setLocation(margin + 1, margin + i * gap);
       		g.fill(r3);
       		r3.setLocation(WIDTH - margin - r3.width - 1, margin + i * gap);
       		g.fill(r3);
       	}

        g.dispose();
        return new ImageIcon(bi);
	}
}
