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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

abstract class AbstractMPlayerOutputReader extends Thread {

	private static final String ANS_TIME_POSITION = "ANS_TIME_POSITION";

	/** Pattern of end of play back */
	private static final Pattern END_PATTERN = Pattern
			.compile(".*\\x2e\\x2e\\x2e\\x20\\(.*\\x20.*\\).*");

	private int length;
	private int time;

	private boolean applyWorkaround = false;
	private boolean workaroundApplied;

	private IAudioObject audioObject;

	private MPlayerEngine engine;
	private MPlayerProcess process;

	private volatile boolean readStopped = false;

	private IOSManager osManager;

	private IFileManager fileManager;

	private volatile boolean seekInProgress;

	boolean isReadStopped() {
		return this.readStopped;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param audioObject
	 */
	public void setAudioObject(final IAudioObject audioObject) {
		this.audioObject = audioObject;
	}

	/**
	 * @param engine
	 */
	public void setEngine(final MPlayerEngine engine) {
		this.engine = engine;
	}

	/**
	 * @param process
	 */
	public void setProcess(final MPlayerProcess process) {
		this.process = process;
	}

	/**
	 * @param workaroundApplied
	 */
	public void setWorkaroundApplied(final boolean workaroundApplied) {
		this.workaroundApplied = workaroundApplied;
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
		// If seek has started ignore position until seek ends to avoid progress
		// bar to move which makes a strange effect
		if (!this.seekInProgress && line.contains(ANS_TIME_POSITION)
				&& !this.readStopped) {
			setTime((int) (Float
					.parseFloat(line.substring(line.indexOf('=') + 1)) * 1000.0));
			getEngine().setTime(getTime());
		}

		// End
		else if (END_PATTERN.matcher(line).matches() && !this.readStopped) {
			// Playback finished
			stopRead();
			getEngine().currentAudioObjectFinished();
		}

		else if (line.contains("Exiting... (Quit)") && !this.readStopped) {
			// Playback stopped
			stopRead();
		}

		else if (line.startsWith("File not found")) {
			// Stop output reader
			stopRead();
			if (!this.workaroundApplied) {
				this.applyWorkaround = true;
			} else {
				this.engine
						.currentAudioObjectFinishedWithError(generateError());
			}
		} else if (line.contains("Position:")) {
			// mplayer returns new position after seek
			this.seekInProgress = false;
		}
	}

	private void stopRead() {
		getEngine().setTime(0);
		this.readStopped = true;
	}

	protected abstract void init();

	@Override
	public final void run() {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				this.process.getInputStream()));
		try {
			init();
			controlAndProcess(in);
		} catch (final IOException e) {
			getEngine().handlePlayerEngineError(e);
		} catch (InterruptedException e) {
			// Process can be interrupted and it's controlled
		} finally {
			ClosingUtils.close(in);
		}

		if (this.applyWorkaround) {
			this.engine.applyMPlayerFilenamesWorkaround(this.audioObject);
		}
	}

	private void controlAndProcess(final BufferedReader in) throws IOException,
			InterruptedException {
		while (!this.readStopped && !this.applyWorkaround && !isInterrupted()) {
			if (sendCommand()) {
				List<String> response = getResponse(in);
				for (String line : response) {
					if (!line.contains(ANS_TIME_POSITION)) {
						// Ignore position output as it generates a lot of
						// output
						// that is not necessary for debugging purposes
						this.process.saveOutputLine(line);
					}
					read(line);
				}
			}
			Thread.sleep(400);
		}
	}

	private List<String> getResponse(final BufferedReader in)
			throws IOException {
		List<String> response = new ArrayList<String>();
		while (in.ready()) {
			String line = in.readLine();
			response.add(line);
		}
		return response;
	}

	private boolean sendCommand() {
		if (!this.engine.isPlaybackPaused()) {
			this.engine.getCommandWriter().sendGetPositionCommand();
			if (this.engine.getCurrentAudioObjectLength() == 0
					&& this.engine.getAudioObject().isSeekable()) {
				Logger.debug("Duration still unknown: sending get_duration_command");
				this.engine.getCommandWriter().sendGetDurationCommand();
			}
			return true;
		} else {
			return false;
		}
	}

	protected final void readAndApplyLength(final IAudioObject audioObject,
			final String line, final boolean readOnlyFromTags) {
		if (line.contains("ANS_LENGTH")) {
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

	private Exception generateError() {
		StringBuilder errorMessage = new StringBuilder(StringUtils.getString(
				I18nUtils.getString("FILE_NOT_FOUND"), ": ",
				this.audioObject.getUrl()));

		if (this.audioObject instanceof ILocalAudioObject) {
			errorMessage.append(this.osManager.getLineTerminator());
			errorMessage.append("Exists: ");
			errorMessage.append(this.fileManager
					.exists((ILocalAudioObject) this.audioObject));
			errorMessage.append(this.osManager.getLineTerminator());
			errorMessage.append("OS file name: ");
			errorMessage.append(this.fileManager
					.getSystemName((ILocalAudioObject) this.audioObject));
			errorMessage.append(this.osManager.getLineTerminator());
			errorMessage.append("Process command: ");
			errorMessage.append(this.process.getCommand());
			errorMessage.append(this.osManager.getLineTerminator());
			errorMessage.append("Process error ouput");
			errorMessage.append(this.osManager.getLineTerminator());
			for (String errorLine : this.process.getErrorLines()) {
				errorMessage.append(errorLine);
				errorMessage.append(this.osManager.getLineTerminator());
			}
		}
		return new Exception(errorMessage.toString());
	}

	public void seekStarted() {
		this.seekInProgress = true;
	}

}
