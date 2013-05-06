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

package net.sourceforge.atunes.gui;

import net.sourceforge.atunes.model.IColorMutableImageIcon;

/**
 * A bean containing text and icon to be drawn in UI
 * 
 * @author alex
 * 
 */
public class TextAndIcon {

	private final String text;

	private final IColorMutableImageIcon icon;

	private final int horizontalTextPosition;

	/**
	 * @param text
	 * @param icon
	 * @param horizontalAlignment
	 */
	public TextAndIcon(final String text, final IColorMutableImageIcon icon,
			final int horizontalAlignment) {
		this.text = text;
		this.icon = icon;
		this.horizontalTextPosition = horizontalAlignment;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the icon
	 */
	public IColorMutableImageIcon getIcon() {
		return icon;
	}

	/**
	 * @return the horizontalAlignment
	 */
	public int getHorizontalTextPosition() {
		return horizontalTextPosition;
	}

	@Override
	public String toString() {
		return getText();
	}
}
