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

package net.sourceforge.atunes.gui.frame;

import java.io.Serializable;

import net.sourceforge.atunes.model.IFrameSize;

public class FrameSize implements Serializable, IFrameSize {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4174654285204418759L;

	private boolean maximized;
	
	private int windowWidth;
	
	private int windowHeight;

	@Override
	public boolean isMaximized() {
		return maximized;
	}

	@Override
	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}

	@Override
	public int getWindowWidth() {
		return windowWidth;
	}

	@Override
	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	@Override
	public int getWindowHeight() {
		return windowHeight;
	}

	@Override
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}
}
