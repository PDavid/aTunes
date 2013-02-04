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

import java.awt.Cursor;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;

/**
 * Runnable to play audio objects and (if needed) cache files
 * 
 * @author fleax
 * 
 */
final class PlayAudioObjectRunnable implements Runnable {

	private final AbstractPlayerEngine abstractPlayerEngine;
	private final IAudioObject audioObject;
	private final IFrame frame;

	PlayAudioObjectRunnable(final AbstractPlayerEngine abstractPlayerEngine,
			final IAudioObject audioObject, final IFrame frame) {
		this.abstractPlayerEngine = abstractPlayerEngine;
		this.audioObject = audioObject;
		this.frame = frame;
		this.frame.getFrame().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	@Override
	public void run() {
		IAudioObject audioObjectToPlay = this.abstractPlayerEngine
				.cacheAudioObject(this.audioObject);
		// Set default cursor again
		this.frame.getFrame().setCursor(Cursor.getDefaultCursor());
		this.abstractPlayerEngine.playAudioObjectAfterCache(audioObjectToPlay,
				this.audioObject);
		this.abstractPlayerEngine.setPlayAudioObjectThread(null);
	}
}