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

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelChangeListener;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;

public class AudioFileImageIcon implements LookAndFeelChangeListener {

	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;
	
	private static AudioFileImageIcon icon;
	
	private static ImageIcon imageIcon;
	
	private AudioFileImageIcon() {
		LookAndFeelSelector.getInstance().addLookAndFeelChangeListener(this);
	}
	
	public static ImageIcon getImageIcon() {
		if (imageIcon == null) {
			icon = new AudioFileImageIcon();
			imageIcon = icon.getImage();
		}
		return imageIcon;
	}
	
	private ImageIcon getImage() {
		Ellipse2D.Float e1 = new Ellipse2D.Float(1, 12, 6, 3);
		Ellipse2D.Float e2 = new Ellipse2D.Float(8, 11, 6, 3);
		
		Rectangle r1 = new Rectangle(5, 6, 2, 7);
		Rectangle r2 = new Rectangle(12, 4, 2, 8);
		Polygon r3 = new Polygon();
		r3.addPoint(5, 6);
		r3.addPoint(5, 8);
		r3.addPoint(14, 5);
		r3.addPoint(14, 3);		
		
		return IconGenerator.generateIcon(WIDTH, HEIGHT, e1, e2, r1, r2, r3);
	}

	@Override
	public void lookAndFeelChanged() {
		imageIcon = icon.getImage();
	}
}
