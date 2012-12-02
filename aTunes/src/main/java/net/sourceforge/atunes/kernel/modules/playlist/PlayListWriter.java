/*
 * aTunes 3.0.0
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

class PlayListWriter {

	private final IOSManager osManager;

	private final IRepositoryHandler repositoryHandler;

	/**
	 * @param osManager
	 * @param repositoryHandler
	 */
	public PlayListWriter(final IOSManager osManager, final IRepositoryHandler repositoryHandler) {
		this.osManager = osManager;
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
		FileWriter writer = null;
		try {
			if (file.exists() && !file.delete()) {
				Logger.error(StringUtils.getString(file, " not deleted"));
			}
			writer = new FileWriter(file);
			List<File> repositoryFolders = repositoryHandler.getFolders();
			for (int i = 0; i < playlist.size(); i++) {
				IAudioObject f = playlist.get(i);
				writer.append(StringUtils.getString(getRelativePath(repositoryFolders, f), osManager.getLineTerminator()));
			}
			writer.flush();
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			ClosingUtils.close(writer);
		}
	}

	private String getRelativePath(final List<File> repositoryFolders, final IAudioObject f) {
		String url = f.getUrl();
		for (File repositoryFolder : repositoryFolders) {
			url = url.replaceAll(FileUtils.getNormalizedPath(repositoryFolder), "");
		}
		return url;
	}

}
