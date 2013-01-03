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

package net.sourceforge.atunes.gui.frame;

import java.io.Serializable;

import net.sourceforge.atunes.model.IFramePosition;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Bean representing position in screen of frame
 * 
 * @author alex
 * 
 */
public class FramePosition implements Serializable, IFramePosition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7151433173240724338L;

	private int xPosition = -1;

	private int yPosition = -1;

	@Override
	public int getXPosition() {
		return this.xPosition;
	}

	@Override
	public void setXPosition(final int xPosition) {
		this.xPosition = xPosition;
	}

	@Override
	public int getYPosition() {
		return this.yPosition;
	}

	@Override
	public void setYPosition(final int yPosition) {
		this.yPosition = yPosition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.xPosition;
		result = prime * result + this.yPosition;
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
		FramePosition other = (FramePosition) obj;
		if (this.xPosition != other.xPosition) {
			return false;
		}
		if (this.yPosition != other.yPosition) {
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
