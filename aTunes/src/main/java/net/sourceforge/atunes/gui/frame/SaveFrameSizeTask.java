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

import java.awt.Dimension;

import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IFrameSize;
import net.sourceforge.atunes.model.IStateUI;

/**
 * Saves frame size of frame
 * 
 * @author alex
 * 
 */
final class SaveFrameSizeTask implements Runnable {

	private IFrame frame;
	private IStateUI stateUI;
	private int width;
	private int height;

	private Dimension screenSize;

	/**
	 * @param screenSize
	 */
	public void setScreenSize(final Dimension screenSize) {
		this.screenSize = screenSize;
	}

	/**
	 * @param frame
	 */
	public void setFrame(final IFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * @param width
	 */
	public void setWidth(final int width) {
		this.width = width;
	}

	/**
	 * @param height
	 */
	public void setHeight(final int height) {
		this.height = height;
	}

	@Override
	public void run() {
		IFrameSize frameSize = this.stateUI.getFrameSize();
		frameSize.setWindowWidth(this.width);
		frameSize.setWindowHeight(this.height);

		// Window is maximized if Swing constant has needed value and at least
		// one of the
		// dimensions is equals to screen dimension
		frameSize
				.setMaximized(this.frame.getExtendedState() == java.awt.Frame.MAXIMIZED_BOTH
						&& (this.width == this.screenSize.width || this.height == this.screenSize.height));
		this.stateUI.setFrameSize(frameSize);
	}
}