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
import java.awt.Rectangle;
import java.awt.geom.Area;

import javax.swing.ImageIcon;

/**
 * Icon for favorite artist
 * 
 * @author alex
 * 
 */
public class ArtistFavoriteImageIcon extends CachedIconFactory {

    /**
	 * 
	 */
    private static final long serialVersionUID = 5699311532940378979L;

    private static final int WIDTH = 26;
    private static final int HEIGHT = 16;

    @Override
    protected ImageIcon createIcon(final Color color) {
	Rectangle clip = new Rectangle(0, 1, WIDTH, HEIGHT - 2);
	Area heart = FavoriteImageIcon.getIconArea(10, 10, WIDTH - 10, 1);
	return IconGenerator.generateIcon(color, clip, WIDTH, HEIGHT,
		ArtistImageIcon.getArtistIconArea(0), heart);
    }
}
