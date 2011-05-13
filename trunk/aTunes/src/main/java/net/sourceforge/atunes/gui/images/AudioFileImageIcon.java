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

import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelChangeListener;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;

public class AudioFileImageIcon implements LookAndFeelChangeListener {

	private static final int SMALL_WIDTH = 16;
	private static final int SMALL_HEIGHT = 16;

	private static final int MEDIUM_WIDTH = 70;
	private static final int MEDIUM_HEIGHT = 70;
	
	private static ImageIcon smallImageIcon; // Cached as it's heavily used
	
	private AudioFileImageIcon() {
		LookAndFeelSelector.getInstance().addLookAndFeelChangeListener(this);
	}
	
	public static ImageIcon getSmallImageIcon() {
		if (smallImageIcon == null) {
			smallImageIcon = getSmallImage(null);
		}
		return smallImageIcon;
	}
	
	private static ImageIcon getSmallImage(Paint color) {
		Ellipse2D.Float e1 = new Ellipse2D.Float(1, 11, 6, 3);
		Ellipse2D.Float e2 = new Ellipse2D.Float(8, 10, 6, 3);
		
		Rectangle r1 = new Rectangle(5, 5, 2, 7);
		Rectangle r2 = new Rectangle(12, 3, 2, 8);
		Polygon r3 = new Polygon();
		r3.addPoint(5, 5);
		r3.addPoint(5, 7);
		r3.addPoint(14, 4);
		r3.addPoint(14, 2);		
		
		return IconGenerator.generateIcon(color, SMALL_WIDTH, SMALL_HEIGHT, e1, e2, r1, r2, r3);
	}

	public static ImageIcon getMediumImage() {
		Ellipse2D.Float e1 = new Ellipse2D.Float(4, 44, 24, 12);
		Ellipse2D.Float e2 = new Ellipse2D.Float(32, 40, 24, 12);
		
		Rectangle r1 = new Rectangle(20, 20, 8, 28);
		Rectangle r2 = new Rectangle(48, 12, 8, 32);
		Polygon r3 = new Polygon();
		r3.addPoint(20, 20);
		r3.addPoint(20, 28);
		r3.addPoint(56, 16);
		r3.addPoint(56, 8);		
		
		// This icon must be opaque since with this size shapes overlap is visible using alpha
		return IconGenerator.generateIcon(MEDIUM_WIDTH, MEDIUM_HEIGHT, e1, e2, r1, r2, r3);
	}

	@Override
	public void lookAndFeelChanged() {
		smallImageIcon = getSmallImage(null);
	}

	public static ImageIcon getSmallImageIcon(Paint color) {
		if (color == null) {
			return getSmallImageIcon();
		}
		return getSmallImage(color);

	}
}
