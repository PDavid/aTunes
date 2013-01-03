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

import javax.swing.ImageIcon;

/**
 * Medium icon for RSS
 * 
 * @author alex
 * 
 */
public class RssMediumImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2649932171666569310L;

	private static final int SIZE = 70;

	@Override
	protected ImageIcon createIcon(final Color color) {
		return IconGenerator.generateIcon(color, SIZE, SIZE,
				RssImageIcon.getIconArea(SIZE));
	}
}
