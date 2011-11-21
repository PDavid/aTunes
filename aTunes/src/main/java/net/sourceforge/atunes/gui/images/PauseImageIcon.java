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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.CachedIconFactory;

public class PauseImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1386224526444536737L;
	
	private Dimension size;
	
	/**
	 * @param size
	 */
	public void setSize(Dimension size) {
		this.size = size;
	}

	@Override
	protected ImageIcon createIcon(Color color) {
        Rectangle pauseShape1 = new Rectangle(- size.width / 10 - size.width / 6, - size.height / 5, size.width / 7, (int) (size.height / (5f/2f)));
        Rectangle pauseShape2 = new Rectangle(- size.width / 20, - size.height / 5, size.width / 7, (int) (size.height / (5f/2f)));

		BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = bi.createGraphics();
    	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g2.setPaint(color);
    	g2.translate(size.getWidth() * 4/7, size.getHeight() / 2);    	
		
   		g2.fill(pauseShape1);
   		g2.fill(pauseShape2);
    	g2.dispose();

    	return new ImageIcon(bi);
	}
}
