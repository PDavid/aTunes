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

import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.Timer;

import org.apache.commons.io.IOUtils;

/**
 * Reads play lists
 * 
 * @author alex
 * 
 */
public class PlayListReader {

	private IRepositoryHandler repositoryHandler;

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * This function reads the filenames from the playlist file, trying to find
	 * the ones present in repository, which are returned in a list
	 * 
	 * @param file
	 *            The playlist file
	 * 
	 * @return Returns an List of files of the playlist as String.
	 */
	List<String> read(final File file) {
		Timer t = new Timer();
		t.start();
		FileReader fr = null;
		try {
			List<File> repositoryFolders = this.repositoryHandler.getFolders();
			fr = new FileReader(file);
			List<String> fileContent = IOUtils.readLines(fr);
			List<String> result = new ArrayList<String>();
			for (String line : fileContent) {
				String fullPath = getFileIfExistsInRepository(
						repositoryFolders, line);
				if (fullPath != null) {
					result.add(fullPath);
				}
			}
			Logger.debug("Time to read playlist file: ", t.stop(), " seconds");
			return result;
		} catch (IOException e) {
			return Collections.emptyList();
		} finally {
			ClosingUtils.close(fr);
		}
	}

	private String getFileIfExistsInRepository(
			final List<File> repositoryFolders, final String relativeFile) {
		for (File repositoryFolder : repositoryFolders) {
			String filePath = FileUtils.getPath(repositoryFolder)
					+ relativeFile;
			if (new File(filePath).exists()) {
				return filePath;
			}
		}
		// File does not exist
		return null;
	}
}
