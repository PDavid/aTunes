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

import net.sourceforge.atunes.model.IFrameState;
import net.sourceforge.atunes.model.IState;

/**
 * Saves frame state of frame
 * @author alex
 *
 */
final class SaveFrameStateTask implements Runnable {
	
	private final AbstractSingleFrame abstractSingleFrame;
	private final IState state;
	private final int width;
	private final int height;
	private final int x;
	private final int y;

	/**
	 * @param abstractSingleFrame
	 * @param state
	 * @param width
	 * @param height
	 * @param x
	 * @param y
	 */
	SaveFrameStateTask(AbstractSingleFrame abstractSingleFrame, IState state, int width, int height, int x, int y) {
		this.abstractSingleFrame = abstractSingleFrame;
		this.state = state;
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}

	@Override
	public void run() {
		Class<? extends AbstractSingleFrame> frameClass = this.abstractSingleFrame.getClass();
		IFrameState frameState = state.getFrameState(frameClass);
		fillFrameState(frameState);
		state.setFrameState(frameClass, frameState);
	}
	
	/**
	 * Saves state to frame state object
	 * @param frameState
	 */
	private void fillFrameState(IFrameState frameState) {
		frameState.setXPosition(x);
		frameState.setYPosition(y);
		frameState.setWindowWidth(width);
		frameState.setWindowHeight(height);
		frameState.setMaximized(abstractSingleFrame.getExtendedState() == java.awt.Frame.MAXIMIZED_BOTH);		
	}
}