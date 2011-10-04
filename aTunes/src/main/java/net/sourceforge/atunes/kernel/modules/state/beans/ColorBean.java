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

package net.sourceforge.atunes.kernel.modules.state.beans;

import java.awt.Color;
import java.io.Serializable;

public class ColorBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7992112016045974559L;
	
	private int red;
	private int green;
	private int blue;
	private int alpha;
	
	/**
	 * Creates new color bean
	 * @param c
	 */
	public ColorBean(Color c) {
		this.red = c.getRed();
		this.green = c.getGreen();
		this.blue = c.getBlue();
		this.alpha = c.getAlpha();
	}

	/**
	 * Creates new default color bean (black)
	 * @param c
	 */
	public ColorBean() {
		this(Color.BLACK);
	}

	/**
	 * Returns original color object
	 * @return
	 */
	public Color getColor() {
		return new Color(red, green, blue, alpha);
	}
}
