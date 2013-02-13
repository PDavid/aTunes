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

import net.sourceforge.atunes.model.IFramePosition;
import net.sourceforge.atunes.model.IStateUI;

/**
 * Saves frame position of frame
 * 
 * @author alex
 * 
 */
public final class SaveFramePositionTask implements Runnable {

	private IStateUI stateUI;
	private int x;
	private int y;

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * @param x
	 */
	public void setX(final int x) {
		this.x = x;
	}

	/**
	 * @param y
	 */
	public void setY(final int y) {
		this.y = y;
	}

	@Override
	public void run() {
		IFramePosition framePosition = this.stateUI.getFramePosition();
		framePosition.setXPosition(this.x);
		framePosition.setYPosition(this.y);
		this.stateUI.setFramePosition(framePosition);
	}
}