/*
 * aTunes 2.2.0-SNAPSHOT
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
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.ILookAndFeel;

public class DeviceImageIcon {

	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;
	
	private static ImageIcon icon;
	
	/**
	 * @param lookAndFeel
	 * @return
	 */
	public static ImageIcon getIcon(ILookAndFeel lookAndFeel) {
		if (icon == null) {
			icon = getIcon(null, lookAndFeel);	
		}
		return icon;
	}
	
	/**
	 * @param color
	 * @param lookAndFeel
	 * @return
	 */
	public static ImageIcon getIcon(Paint color, ILookAndFeel lookAndFeel) {
		int marginX = 3;
		int marginY = 1;
		int arc = 4;

		int internalMarginX = 5;
		int internalMarginY = 3;
		
		int circleDiameter = HEIGHT / 4;
		
		BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(color != null ? color : lookAndFeel.getPaintForSpecialControls());
        
       	g.fill(new RoundRectangle2D.Float(marginX, marginY, WIDTH - 2 * marginX, HEIGHT - 2 * marginY, arc, arc));
       	g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
       	g.fill(new Rectangle(internalMarginX, internalMarginY, WIDTH - 2 * internalMarginX, HEIGHT / 2 - internalMarginY - 1));
       	g.fill(new Ellipse2D.Float(WIDTH / 2 - circleDiameter / 2, HEIGHT - internalMarginY - circleDiameter, circleDiameter, circleDiameter));
       	
        g.dispose();
        return new ImageIcon(bi);
	}
}
