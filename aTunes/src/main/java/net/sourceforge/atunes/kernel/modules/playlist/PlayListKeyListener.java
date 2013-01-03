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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.sourceforge.atunes.model.IPlayerHandler;

/**
 * The listener interface for receiving playList key events.
 */
public final class PlayListKeyListener extends KeyAdapter {

	private IPlayerHandler playerHandler;

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		// FIX: ENTER key event can't be used as it's also fired from
		// "PlayAction" which is associated to Play menu item
		// If user presses SPACE -> Pause actually playing song
		if (e.getKeyChar() == KeyEvent.VK_SPACE) {
			this.playerHandler.resumeOrPauseCurrentAudioObject();
		}
	}
}
