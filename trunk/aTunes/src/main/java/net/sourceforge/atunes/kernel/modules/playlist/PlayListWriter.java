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
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Writes play lists using relative paths
 * 
 * @author alex
 * 
 */
public class PlayListWriter {

	private IOSManager osManager;

	private IRepositoryHandler repositoryHandler;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * Writes a play list to a file using paths relative to repository
	 * 
	 * @param playlist
	 * @param file
	 * @return
	 */
	boolean write(final IPlayList playlist, final File file) {
		return write(playlist.getAudioObjectsList(), file);
	}

	/**
	 * Writes a list of audio objects to a file using paths relative to
	 * repository
	 * 
	 * @param audioObjects
	 * @param file
	 * @return
	 */
	boolean write(final List<IAudioObject> audioObjects, final File file) {
		FileWriter writer = null;
		try {
			if (file.exists() && !file.delete()) {
				Logger.error(StringUtils.getString(file, " not deleted"));
			}
			writer = new FileWriter(file);
			List<File> repositoryFolders = this.repositoryHandler.getFolders();
			for (IAudioObject f : audioObjects) {
				writer.append(StringUtils.getString(
						getRelativePath(repositoryFolders, f),
						this.osManager.getLineTerminator()));
			}
			writer.flush();
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			ClosingUtils.close(writer);
		}
	}

	private String getRelativePath(final List<File> repositoryFolders,
			final IAudioObject f) {
		String url = f.getUrl();
		for (File repositoryFolder : repositoryFolders) {
			url = url
					.replace(FileUtils.getNormalizedPath(repositoryFolder), "");
		}
		return url;
	}
}
