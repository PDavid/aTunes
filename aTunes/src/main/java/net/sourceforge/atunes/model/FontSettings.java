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

package net.sourceforge.atunes.model;

import java.beans.ConstructorProperties;
import java.io.Serializable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Font settings for application
 * 
 * @author alex
 * 
 */
public class FontSettings implements Serializable {

	private static final long serialVersionUID = -8438133427543514976L;
	private IFontBean font;
	private boolean useFontSmoothing;
	private boolean useFontSmoothingSettingsFromOs;

	/**
	 * @param font
	 * @param useFontSmoothing
	 * @param useFontSmoothingSettingsFromOs
	 */
	@ConstructorProperties({ "font", "useFontSmoothing",
			"useFontSmoothingSettingsFromOs" })
	public FontSettings(final IFontBean font, final boolean useFontSmoothing,
			final boolean useFontSmoothingSettingsFromOs) {
		super();
		this.font = font;
		this.useFontSmoothing = useFontSmoothing;
		this.useFontSmoothingSettingsFromOs = useFontSmoothingSettingsFromOs;
	}

	/**
	 * Default constructor
	 */
	public FontSettings() {
	}

	/**
	 * @param font
	 */
	public void setFont(final IFontBean font) {
		this.font = font;
	}

	/**
	 * @return font
	 */
	public IFontBean getFont() {
		return this.font;
	}

	/**
	 * @param useFontSmoothing
	 */
	public void setUseFontSmoothing(final boolean useFontSmoothing) {
		this.useFontSmoothing = useFontSmoothing;
	}

	/**
	 * @return use font smoothing
	 */
	public boolean isUseFontSmoothing() {
		return this.useFontSmoothing;
	}

	/**
	 * @param useFontSmoothingSettingsFromOs
	 */
	public void setUseFontSmoothingSettingsFromOs(
			final boolean useFontSmoothingSettingsFromOs) {
		this.useFontSmoothingSettingsFromOs = useFontSmoothingSettingsFromOs;
	}

	/**
	 * @return use font smotthing settings from os
	 */
	public boolean isUseFontSmoothingSettingsFromOs() {
		return this.useFontSmoothingSettingsFromOs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.font == null) ? 0 : this.font.hashCode());
		result = prime * result + (this.useFontSmoothing ? 1231 : 1237);
		result = prime * result
				+ (this.useFontSmoothingSettingsFromOs ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FontSettings other = (FontSettings) obj;
		if (this.font == null) {
			if (other.font != null) {
				return false;
			}
		} else if (!this.font.equals(other.font)) {
			return false;
		}
		if (this.useFontSmoothing != other.useFontSmoothing) {
			return false;
		}
		if (this.useFontSmoothingSettingsFromOs != other.useFontSmoothingSettingsFromOs) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

}