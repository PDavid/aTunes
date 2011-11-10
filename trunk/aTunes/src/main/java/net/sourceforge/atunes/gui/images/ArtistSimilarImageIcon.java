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

import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.ILookAndFeel;

public class ArtistSimilarImageIcon {

	private static final int WIDTH = 16;
	private static final int HEIGHT = 16;
	
	private static ImageIcon icon;
	
	private ArtistSimilarImageIcon() {}
	
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
		Rectangle s = new Rectangle(WIDTH - 6, 5, 6, 2);
		Rectangle s2 = new Rectangle (WIDTH - 4, 3, 2, 6);
		Rectangle clip = new Rectangle(0, 1, WIDTH, HEIGHT - 2);
		return IconGenerator.generateIcon(color, clip, WIDTH, HEIGHT, lookAndFeel, ArtistImageIcon.getArtistIconArea(-2), s, s2);
	}
}
