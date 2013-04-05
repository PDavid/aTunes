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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class MPlayerErrorReader.
 */
class MPlayerErrorReader extends Thread {

	private final MPlayerEngine engine;
	private final BufferedReader in;
	private final IAudioObject audioObject;
	private final AbstractMPlayerOutputReader outputReader;
	private final IFileManager fileManager;
	private final IOSManager osManager;
	private MPlayerProcess process;

	/**
	 * Instantiates a new m player error reader.
	 * 
	 * @param engine
	 * @param process
	 * @param audioObject
	 * @param fileManager
	 */
	MPlayerErrorReader(final MPlayerEngine engine,
			final MPlayerProcess process,
			final AbstractMPlayerOutputReader outputReader,
			final IAudioObject audioObject, IFileManager fileManager,
			IOSManager osManager) {
		this.engine = engine;
		this.process = process;
		this.in = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));
		this.audioObject = audioObject;
		this.outputReader = outputReader;
		this.fileManager = fileManager;
		this.osManager = osManager;
	}

	@Override
	public void run() {
		String line = null;
		try {
			line = in.readLine();
			while (line != null && !isInterrupted()) {
				this.process.saveErrorLine(line);
				if (line.startsWith("File not found")) {
					// Stop output reader
					outputReader.stopRead();
					// Playback finished with error
					engine.currentAudioObjectFinishedWithError(generateError());
				}
				line = in.readLine();
			}
			Logger.debug("Finished MPlayerErrorReader");
		} catch (final IOException e) {
			engine.handlePlayerEngineError(e);
		} finally {
			ClosingUtils.close(in);
		}
	}

	private Exception generateError() {
		StringBuilder errorMessage = new StringBuilder(StringUtils.getString(
				I18nUtils.getString("FILE_NOT_FOUND"), ": ",
				audioObject.getUrl()));

		if (this.audioObject instanceof ILocalAudioObject) {
			errorMessage.append(osManager.getLineTerminator());
			errorMessage.append("Exists: ");
			errorMessage.append(fileManager
					.exists((ILocalAudioObject) this.audioObject));
			errorMessage.append(osManager.getLineTerminator());
			errorMessage.append("OS file name: ");
			errorMessage.append(fileManager
					.getSystemName((ILocalAudioObject) this.audioObject));
			errorMessage.append(osManager.getLineTerminator());
			errorMessage.append("Process command: ");
			errorMessage.append(process.getCommand());
			errorMessage.append(osManager.getLineTerminator());
			errorMessage.append("Process error ouput");
			errorMessage.append(osManager.getLineTerminator());
			for (String errorLine : process.getErrorLines()) {
				errorMessage.append(errorLine);
				errorMessage.append(osManager.getLineTerminator());
			}
		}
		return new Exception(errorMessage.toString());
	}
}
