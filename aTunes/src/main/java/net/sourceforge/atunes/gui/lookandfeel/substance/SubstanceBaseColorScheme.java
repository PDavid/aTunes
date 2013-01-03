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

package net.sourceforge.atunes.gui.lookandfeel.substance;

import java.awt.Color;

import org.pushingpixels.substance.api.colorscheme.BaseColorScheme;

class SubstanceBaseColorScheme extends BaseColorScheme {

	private Color ultraDarkColor;
	private Color darkColor;
	private Color midColor;
	private Color lightColor;
	private Color extraLightColor;
	private Color ultraLightColor;
	private Color foregroundColor;

	/**
	 * @param displayName
	 * @param isDark
	 * @param ultraDarkColor
	 * @param darkColor
	 * @param midColor
	 * @param lightColor
	 * @param extraLightColor
	 * @param ultraLightColor
	 * @param foregroundColor
	 */
	public SubstanceBaseColorScheme(String displayName, boolean isDark,
			Color ultraDarkColor, Color darkColor, Color midColor,
			Color lightColor, Color extraLightColor, Color ultraLightColor,
			Color foregroundColor) {
		super(displayName, isDark);
		this.ultraDarkColor = ultraDarkColor;
		this.darkColor = darkColor;
		this.midColor = midColor;
		this.lightColor = lightColor;
		this.extraLightColor = extraLightColor;
		this.ultraLightColor = ultraLightColor;
		this.foregroundColor = foregroundColor;
	}

	@Override
	public Color getUltraDarkColor() {
		return ultraDarkColor;
	}

	@Override
	public Color getDarkColor() {
		return darkColor;
	}

	@Override
	public Color getMidColor() {
		return midColor;
	}

	@Override
	public Color getLightColor() {
		return lightColor;
	}

	@Override
	public Color getExtraLightColor() {
		return extraLightColor;
	}

	@Override
	public Color getUltraLightColor() {
		return ultraLightColor;
	}

	@Override
	public Color getForegroundColor() {
		return foregroundColor;
	}
}
