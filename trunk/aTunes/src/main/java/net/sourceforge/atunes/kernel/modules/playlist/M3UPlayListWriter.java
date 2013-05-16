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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Writes M3U play lists
 * 
 * @author alex
 * 
 */
public class M3UPlayListWriter {

	/** The Constant M3U_HEADER. */
	private static final String M3U_HEADER = "#EXTM3U";

	private IOSManager osManager;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * Writes a play list to a file in M3U format
	 * 
	 * @param playlist
	 * @param file
	 * @return
	 */
	boolean writeM3U(final IPlayList playlist, final File file) {
		return writeM3U(playlist.getAudioObjectsList(), file);
	}

	/**
	 * Writes a list of audio objects to a file in M3U format
	 * 
	 * @param audioObjects
	 * @param file
	 * @return
	 */
	boolean writeM3U(final List<IAudioObject> audioObjects, final File file) {
		FileWriter writer = null;
		try {
			if (file.exists() && !file.delete()) {
				Logger.error(StringUtils.getString(file, " not deleted"));
			}
			writer = new FileWriter(file);
			writer.append(StringUtils.getString(M3U_HEADER,
					this.osManager.getLineTerminator()));
			for (IAudioObject f : audioObjects) {
				if (f instanceof ILocalAudioObject) {
					writer.append(StringUtils.getString(this.fileManager
							.getSystemPath((ILocalAudioObject) f),
							this.osManager.getLineTerminator()));
				} else {
					writer.append(StringUtils.getString(f.getUrl(),
							this.osManager.getLineTerminator()));
				}
			}
			writer.flush();
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			ClosingUtils.close(writer);
		}
	}

}
