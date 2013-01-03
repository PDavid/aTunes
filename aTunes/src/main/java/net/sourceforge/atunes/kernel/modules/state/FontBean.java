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

import java.awt.Font;
import java.beans.ConstructorProperties;

import net.sourceforge.atunes.model.IFontBean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Bean for java.awt.Font
 */
public final class FontBean implements IFontBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6799372094994034976L;
	private final String name;
	private final int style;
	private final int size;

	/**
	 * @param name
	 * @param style
	 * @param size
	 */
	@ConstructorProperties({ "name", "style", "size" })
	public FontBean(final String name, final int style, final int size) {
		this.name = name;
		this.style = style;
		this.size = size;
	}

	/**
	 * @param font
	 */
	public FontBean(final Font font) {
		this.name = font.getName();
		this.style = font.getStyle();
		this.size = font.getSize();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getStyle() {
		return this.style;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public Font toFont() {
		return new Font(this.name, this.style, this.size);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + this.size;
		result = prime * result + this.style;
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
		FontBean other = (FontBean) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.size != other.size) {
			return false;
		}
		if (this.style != other.style) {
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
