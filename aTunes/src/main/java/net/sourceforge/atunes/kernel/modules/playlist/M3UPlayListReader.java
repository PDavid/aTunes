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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.IOUtils;

/**
 * Reads M3U play lists
 * 
 * @author alex
 * 
 */
public class M3UPlayListReader {

	private static final String HTTP_PREFIX = "http://";

	/** The Constant M3U_START_COMMENT. */
	private static final String M3U_START_COMMENT = "#";

	/** The Constant M3U_UNIX_ABSOLUTE_PATH. */
	private static final String M3U_UNIX_ABSOLUTE_PATH = "/";

	/** The Constant M3U_WINDOWS_ABSOLUTE_PATH. */
	private static final String M3U_WINDOWS_ABSOLUTE_PATH = ":\\";

	/** The Constant UNC_ABSOLUTE_PATH. */
	private static final String M3U_UNC_ABSOLUTE_PATH = "\\\\";

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	List<String> read(final File file) {
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			List<String> lines = readUntilFirstUncommentedLine(IOUtils
					.readLines(fr));

			if (!lines.isEmpty()) {
				String firstLine = lines.get(0);

				if (isFormatSupported(firstLine)) {
					return readLines(
							lines,
							file.getParent()
									+ this.osManager.getFileSeparator(),
							isRelativePaths(firstLine));
				}
			}
		} catch (IOException e) {
			Logger.error(e);
		} finally {
			ClosingUtils.close(fr);
		}
		return Collections.emptyList();
	}

	private List<String> readLines(final List<String> lines,
			final String m3uPath, final boolean isRelative) {
		List<String> result = new ArrayList<String>();
		for (String line : lines) {
			if (!line.startsWith(M3U_START_COMMENT) && !line.isEmpty()) {
				String lineProcessed;
				if (isRelative) {
					// The path is relative! We must add it to the filename
					// But if entries are HTTP URLS then don't add any path
					lineProcessed = line.startsWith(HTTP_PREFIX) ? line
							: StringUtils.getString(m3uPath, line);
				} else {
					lineProcessed = line;
				}
				result.add(lineProcessed);
			}
		}
		return result;
	}

	/**
	 * @param firstLine
	 * @return true if line is valid
	 */
	private boolean isFormatSupported(final String firstLine) {
		// Let's check if we are at least using the right OS. Maybe a message
		// should be returned, but for now it doesn't. UNC paths are allowed for
		// all OS
		if (!isRelativePaths(firstLine)
				&& (isWindowsAndUnixAbsolutePath(firstLine) || isNotWindowsAndWindowsAbsolutePath(firstLine))) {
			return false;
		}
		return true;
	}

	/**
	 * @param firstLine
	 * @return
	 */
	private boolean isNotWindowsAndWindowsAbsolutePath(final String firstLine) {
		return (!this.osManager.isWindows() && firstLine.startsWith(
				M3U_WINDOWS_ABSOLUTE_PATH, 1));
	}

	/**
	 * @param firstLine
	 * @return
	 */
	private boolean isWindowsAndUnixAbsolutePath(final String firstLine) {
		return (this.osManager.isWindows() && firstLine
				.startsWith(M3U_UNIX_ABSOLUTE_PATH));
	}

	/**
	 * @param firstLine
	 * @return true if line is valid
	 */
	private boolean isRelativePaths(final String firstLine) {
		// First absolute path. Windows path detection is very rudimentary, but
		// should work
		if (firstLine.startsWith(M3U_WINDOWS_ABSOLUTE_PATH, 1)
				|| firstLine.startsWith(M3U_UNIX_ABSOLUTE_PATH)
				|| firstLine.startsWith(M3U_UNC_ABSOLUTE_PATH)) {
			return false;
		}
		return true;
	}

	private List<String> readUntilFirstUncommentedLine(final List<String> lines) {
		int i = 0;
		for (String line : lines) {
			i++;
			if (line.startsWith(M3U_START_COMMENT)) {
				break;
			}
		}
		return lines.subList(i, lines.size());
	}
}
