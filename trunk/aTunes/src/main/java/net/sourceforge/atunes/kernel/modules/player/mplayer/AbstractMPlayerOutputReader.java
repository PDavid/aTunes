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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;

abstract class AbstractMPlayerOutputReader extends Thread {

	/** Pattern of end of play back */
	private static final Pattern END_PATTERN = Pattern
			.compile(".*\\x2e\\x2e\\x2e\\x20\\(.*\\x20.*\\).*");

	private final MPlayerEngine engine;
	private int length;
	private int time;

	private final MPlayerProcess process;

	private volatile boolean readStopped = false;

	/**
	 * Instantiates a new mplayer output reader.
	 * 
	 * @param engine
	 *            the engine
	 * @param process
	 *            the process
	 */
	protected AbstractMPlayerOutputReader(final MPlayerEngine engine,
			final MPlayerProcess process) {
		this.engine = engine;
		this.process = process;
	}

	/**
	 * Reads a line
	 * 
	 * @param line
	 *            the line
	 */
	protected void read(final String line) {
		// Read progress
		// MPlayer bug: Duration still inaccurate with mp3 VBR files! Flac
		// duration bug
		if (line.contains("ANS_TIME_POSITION")) {
			setTime((int) (Float
					.parseFloat(line.substring(line.indexOf('=') + 1)) * 1000.0));
			getEngine().setTime(getTime());
		}

		// End
		if (END_PATTERN.matcher(line).matches() && !this.readStopped) {
			// Playback finished
			getEngine().currentAudioObjectFinished();
		}
	}

	protected void stopRead() {
		this.readStopped = true;
	}

	protected abstract void init();

	@Override
	public final void run() {
		String line = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(
				this.process.getInputStream()));
		try {
			init();
			line = in.readLine();
			while (line != null && getEngine().isEnginePlaying()
					&& !isInterrupted()) {
				read(line);
				line = in.readLine();
			}
		} catch (final IOException e) {
			getEngine().handlePlayerEngineError(e);
		} finally {
			ClosingUtils.close(in);
		}
	}

	protected final void readAndApplyLength(final IAudioObject audioObject,
			final String line, final boolean readOnlyFromTags) {
		if (line.contains("ANS_LENGTH")) {
			// Length still inaccurate with mp3 VBR files!
			// Apply workaround to get length from audio file properties (read
			// by jaudiotagger) instead of mplayer
			if (readOnlyFromTags) {
				setLength((audioObject.getDuration() * 1000));
			} else {
				setLength((int) (Float.parseFloat(line.substring(line
						.indexOf('=') + 1)) * 1000.0));
				if (getLength() == 0) {
					// Length zero is unlikely, so try if tagging library did
					// not do a better job
					setLength((audioObject.getDuration() * 1000));
				}
			}
			getEngine().setCurrentLength(getLength());
		}
	}

	protected final void setTime(final int time) {
		this.time = time;
	}

	protected final int getTime() {
		return this.time;
	}

	protected final void setLength(final int length) {
		this.length = length;
	}

	protected final int getLength() {
		return this.length;
	}

	protected final MPlayerEngine getEngine() {
		return this.engine;
	}
}
