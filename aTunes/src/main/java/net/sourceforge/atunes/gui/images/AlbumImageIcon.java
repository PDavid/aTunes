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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;

public class AlbumImageIcon {

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
		int margin = 2;
		int internalMargin = WIDTH / 2 - 2;
		Ellipse2D.Float p = new Ellipse2D.Float(margin, margin, WIDTH - 2 * margin, HEIGHT - 2 * margin);
		Ellipse2D.Float p2 = new Ellipse2D.Float(internalMargin, internalMargin, WIDTH - 2 * internalMargin, HEIGHT - 2 * internalMargin);
		
		AlphaComposite a = AlphaComposite.getInstance(AlphaComposite.CLEAR);
		
		BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(color != null ? color : LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintFor(null));
       	g.fill(p);
       	g.setComposite(a);
       	g.fill(p2);
        g.dispose();
        return new ImageIcon(bi);
	}
}
