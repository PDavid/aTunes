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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.atunes.model.IOSManager;

/**
 * Encapsulates a process running mplayer
 * 
 * @author alex
 * 
 */
class MPlayerProcess {

	private final Process process;

	private final List<String> command;

	private final List<String> errorLines;

	MPlayerProcess(final List<String> command) throws IOException {
		this.command = command;
		this.errorLines = new ArrayList<String>();
		this.process = new ProcessBuilder().redirectErrorStream(true)
				.command(command).start();
	}

	/**
	 * Returns new command writer for mplayer process
	 * 
	 * @param osManager
	 * @return
	 */
	MPlayerCommandWriter newCommandWriter(final IOSManager osManager) {
		if (osManager.isMacOsX()) {
			return new MPlayerXCommandWriter(this.process);
		}
		return new MPlayerCommandWriter(this.process);
	}

	/**
	 * @return input stream
	 */
	InputStream getInputStream() {
		if (this.process != null) {
			return this.process.getInputStream();
		}
		return null;
	}

	/**
	 * @return error stream
	 */
	InputStream getErrorStream() {
		if (this.process != null) {
			return this.process.getErrorStream();
		}
		return null;
	}

	/**
	 * Adds a new error line
	 * 
	 * @param line
	 */
	void saveOutputLine(final String line) {
		this.errorLines.add(line);
	}

	/**
	 * @return error lines of this process
	 */
	List<String> getErrorLines() {
		return this.errorLines;
	}

	/**
	 * @return command used to create process
	 */
	String getCommand() {
		return Arrays.toString(this.command.toArray());
	}
}
