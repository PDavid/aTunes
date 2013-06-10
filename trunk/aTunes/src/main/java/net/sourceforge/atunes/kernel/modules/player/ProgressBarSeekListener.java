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

package net.sourceforge.atunes.kernel.modules.player;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IProgressSlider;

/**
 * Listener for seek operation in progress slider
 * 
 * @author alex
 * 
 */
public class ProgressBarSeekListener extends MouseAdapter {

	private IProgressSlider progressBar;

	private IPlayerHandler playerHandler;

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
	 * @param progressBar
	 */
	public void bindToProgressBar(final IProgressSlider progressBar) {
		this.progressBar = progressBar;
		progressBar.addMouseListener(this);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (this.progressBar.isEnabled()) {
			// Calculate percentage as the position of mouse relative to
			// progress bar width
			// Round to next integer
			this.playerHandler.seekCurrentAudioObject((int) Math.ceil((float) e
					.getPoint().x
					/ this.progressBar.getProgressBarWidth()
					* 100));
		}
	}
}
