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
import net.sourceforge.atunes.model.ITemporalDiskStorage;

/**
 * Runnable to play audio objects and (if needed) cache files
 * @author fleax
 *
 */
final class PlayAudioObjectRunnable implements Runnable {
	
	private AbstractPlayerEngine abstractPlayerEngine;
	private IAudioObject audioObject;
	private IFrame frame;
	private ITemporalDiskStorage temporalDiskStorage;
	
	PlayAudioObjectRunnable(AbstractPlayerEngine abstractPlayerEngine, IAudioObject audioObject, IFrame frame, ITemporalDiskStorage temporalDiskStorage) {
		this.abstractPlayerEngine = abstractPlayerEngine;
		this.audioObject = audioObject;			
		this.frame = frame;
		this.temporalDiskStorage = temporalDiskStorage;
		this.frame.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
			
	@Override
	public void run() {
	    IAudioObject audioObjectToPlay = this.abstractPlayerEngine.cacheAudioObject(audioObject, temporalDiskStorage);
		// Set default cursor again
		this.frame.getFrame().setCursor(Cursor.getDefaultCursor());
		this.abstractPlayerEngine.playAudioObjectAfterCache(audioObjectToPlay, audioObject);
		this.abstractPlayerEngine.setPlayAudioObjectThread(null);
	}		
}