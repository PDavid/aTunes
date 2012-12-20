/*
 * aTunes 3.1.0
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

import net.sourceforge.atunes.model.IFrameSize;
import net.sourceforge.atunes.model.IStateUI;

/**
 * Saves frame size of frame
 * @author alex
 *
 */
final class SaveFrameSizeTask implements Runnable {
	
	private final AbstractSingleFrame abstractSingleFrame;
	private final IStateUI stateUI;
	private final int width;
	private final int height;

	/**
	 * @param abstractSingleFrame
	 * @param stateUI
	 * @param width
	 * @param height
	 */
	SaveFrameSizeTask(AbstractSingleFrame abstractSingleFrame, IStateUI stateUI, int width, int height) {
		this.abstractSingleFrame = abstractSingleFrame;
		this.stateUI = stateUI;
		this.width = width;
		this.height = height;
	}

	@Override
	public void run() {
		IFrameSize frameSize = stateUI.getFrameSize();
		frameSize.setWindowWidth(width);
		frameSize.setWindowHeight(height);
		frameSize.setMaximized(abstractSingleFrame.getExtendedState() == java.awt.Frame.MAXIMIZED_BOTH);
		stateUI.setFrameSize(frameSize);
	}
}