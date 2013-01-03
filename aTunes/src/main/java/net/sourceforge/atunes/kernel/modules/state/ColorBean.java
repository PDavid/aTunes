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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.Color;

import net.sourceforge.atunes.model.IColorBean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Stores a color, by storing its components (red, green, blue, alpha)
 * 
 * @author alex
 * 
 */
public class ColorBean implements IColorBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7992112016045974559L;

	private final int red;
	private final int green;
	private final int blue;
	private final int alpha;

	/**
	 * Creates new color bean
	 * 
	 * @param c
	 */
	public ColorBean(final Color c) {
		this.red = c.getRed();
		this.green = c.getGreen();
		this.blue = c.getBlue();
		this.alpha = c.getAlpha();
	}

	/**
	 * Returns original color object
	 * 
	 * @return
	 */
	@Override
	public Color getColor() {
		return new Color(this.red, this.green, this.blue, this.alpha);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
