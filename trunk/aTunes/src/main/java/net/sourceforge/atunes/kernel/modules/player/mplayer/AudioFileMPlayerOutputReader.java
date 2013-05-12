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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.Logger;

/**
 * Reads output of mplayer for audio files
 * 
 * @author alex
 * 
 */
public class AudioFileMPlayerOutputReader extends AbstractMPlayerOutputReader {

	private ILocalAudioObject audioFile;

	/**
	 * @param audioFile
	 */
	public void setAudioFile(final ILocalAudioObject audioFile) {
		this.audioFile = audioFile;
	}

	@Override
	protected void init() {
	}

	@Override
	protected void read(final String line) {
		super.read(line);

		// Get length from mplayer where bitrate is variable
		// This is opposite to previous workaround, but let's see if mplayer
		// length detection
		// has been fixed
		readAndApplyLength(this.audioFile, line,
				!this.audioFile.isVariableBitrate());

		// MPlayer bug: Workaround (for audio files) for "mute bug" [1868482]
		if (getEngine().isMute() && getLength() > 0
				&& getLength() - getTime() < 2000) {
			Logger.debug("MPlayer 'mute bug' workaround applied");
			getEngine().currentAudioObjectFinished();
			interrupt();
		}
	}

}
