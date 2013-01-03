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
 * @author alex
 *
 */
final class SaveFramePositionTask implements Runnable {
	
	private final IStateUI stateUI;
	private final int x;
	private final int y;

	/**
	 * @param stateUI
	 * @param x
	 * @param y
	 */
	SaveFramePositionTask(IStateUI stateUI, int x, int y) {
		this.stateUI = stateUI;
		this.x = x;
		this.y = y;
	}

	@Override
	public void run() {
		IFramePosition framePosition = stateUI.getFramePosition();
		framePosition.setXPosition(x);
		framePosition.setYPosition(y);
		stateUI.setFramePosition(framePosition);
	}
}