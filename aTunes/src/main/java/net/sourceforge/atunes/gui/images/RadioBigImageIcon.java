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
 * Big icon for radio
 * 
 * @author alex
 * 
 */
public class RadioBigImageIcon extends CachedIconFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7524361959178047909L;
	private static final int LARGE_SIZE = 150;

	@Override
	protected ImageIcon createIcon(final Color color) {
		return RadioImageIcon.getIcon(color, LARGE_SIZE, LARGE_SIZE);
	}
}
