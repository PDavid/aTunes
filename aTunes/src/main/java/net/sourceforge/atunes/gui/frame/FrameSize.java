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

import net.sourceforge.atunes.model.IFrameSize;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Bean representing size of frame
 * 
 * @author alex
 * 
 */
public class FrameSize implements Serializable, IFrameSize {

	private static final long serialVersionUID = 4174654285204418759L;

	private boolean maximized;

	private int windowWidth;

	private int windowHeight;

	@Override
	public boolean isMaximized() {
		return this.maximized;
	}

	@Override
	public void setMaximized(final boolean maximized) {
		this.maximized = maximized;
	}

	@Override
	public int getWindowWidth() {
		return this.windowWidth;
	}

	@Override
	public void setWindowWidth(final int windowWidth) {
		this.windowWidth = windowWidth;
	}

	@Override
	public int getWindowHeight() {
		return this.windowHeight;
	}

	@Override
	public void setWindowHeight(final int windowHeight) {
		this.windowHeight = windowHeight;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
