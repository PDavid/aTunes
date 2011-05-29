/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.lookandfeel.system;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;

public class OSXLookAndFeel extends SystemLookAndFeel {

	@Override
	public Dimension getPopUpButtonSize() {
		return new Dimension(40, 10);
	}
	
	@Override
	public void initializeLookAndFeel() {
		super.initializeLookAndFeel();
	}
	
	/**
	 * Returns paint to be used with certain controls (player controls)
	 * @return
	 */
	public Paint getPaintForSpecialControls() {
		return new Color(20, 20, 20, 190); 
	}

	/**
	 * Returns paint to be used to draw a color mutable icon in given component
	 * @param c
	 * @param isSelected
	 * @return
	 */
	public Paint getPaintForColorMutableIcon(Component c, boolean isSelected) {
		return new Color(20, 20, 20, 190);	
	}
	
	@Override
	public void initializeFonts(Font baseFont) {
	}
	
}
