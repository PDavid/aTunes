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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.util.StringTokenizer;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.utils.Logger;

/**
 * Removes from repository
 * 
 * @author alex
 * 
 */
public class RepositoryRemover {

	private IOSManager osManager;

	private IRepositoryHandler repositoryHandler;

	private IDeviceHandler deviceHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

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
	 * @param deviceHandler
	 */
	public void setDeviceHandler(final IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}

	/**
	 * Permanently deletes an audio file from the repository meta-information
	 * 
	 * This method marks repository as dirty but new state is not notified To
	 * notify repository change call Repository.setDirty(true, true) when finish
	 * 
	 * @param file
	 * @param osManager
	 * @param repositoryHandler
	 * @param deviceHandler
	 */
	public void deleteFile(final ILocalAudioObject file) {
		// Only do this if file is in repository
		if (getFolderForFile(file) != null) {
			removeFromRepository(file);
		}
		// File is on a device
		else if (this.deviceHandler.isDevicePath(file.getUrl())) {
			Logger.info("Deleted file ", file, " from device");
		}
	}

	/**
	 * @param file
	 */
	private void removeFromRepository(final ILocalAudioObject file) {
		// Remove from file structure
		removeFromFolderStructure(file);

		// Remove from tree structure
		removeFromTreeStructure(file);

		// Remove from genre structure
		removeFromGenreStructure(file);

		// Remove from year structure
		removeFromYearStructure(file);

		// Remove from repository
		this.repositoryHandler.removeFile(file);
	}

	/**
	 * @param file
	 */
	void removeFromYearStructure(final ILocalAudioObject file) {
		String year = file.getYear(this.unknownObjectChecker);

		IYear y = this.repositoryHandler.getYear(year);
		if (y != null) {
			y.removeAudioObject(file);
			if (y.size() == 0) {
				this.repositoryHandler.removeYear(y);
			}
		}
	}

	/**
	 * @param file
	 */
	void removeFromGenreStructure(final ILocalAudioObject file) {
		String genre = file.getGenre(this.unknownObjectChecker);

		IGenre g = this.repositoryHandler.getGenre(genre);
		if (g != null) {
			g.removeAudioObject(file);
			if (g.size() == 0) {
				this.repositoryHandler.removeGenre(g);
			}
		}
	}

	/**
	 * @param file
	 */
	void removeFromTreeStructure(final ILocalAudioObject file) {
		String albumArtist = file.getAlbumArtist(this.unknownObjectChecker);
		String artist = file.getArtist(this.unknownObjectChecker);
		String album = file.getAlbum(this.unknownObjectChecker);

		IArtist a = this.repositoryHandler.getArtist(albumArtist);
		if (a == null || this.unknownObjectChecker.isUnknownArtist(a)) {
			a = this.repositoryHandler.getArtist(artist);
		}
		if (a != null) {
			IAlbum alb = a.getAlbum(album);
			if (alb != null) {
				if (alb.size() == 1) {
					a.removeAlbum(alb);
				} else {
					alb.removeAudioFile(file);
				}

				if (a.isEmpty()) {
					this.repositoryHandler.removeArtist(a);
				}
			}
		}
	}

	/**
	 * @param file
	 */
	void removeFromFolderStructure(final ILocalAudioObject file) {
		IFolder f = getFolderForFile(file);
		if (f != null) {
			f.removeAudioFile(file);
			// Remove all empty parent folders
			while (f != null && f.isEmpty()) {
				if (f.getParentFolder() != null) {
					f.getParentFolder().removeFolder(f);
				}
				f = f.getParentFolder();
			}
		}
	}

	/**
	 * Finds folder that contains file.
	 * 
	 * @param file
	 *            Audio file for which the folder is wanted
	 * 
	 * @return Either folder or null if file is not in repository
	 */
	private IFolder getFolderForFile(final ILocalAudioObject file) {
		// Get repository folder where file is
		File repositoryFolder = this.repositoryHandler
				.getRepositoryFolderContainingFile(file);
		// If the file is not in the repository, return null
		if (repositoryFolder == null) {
			return null;
		}

		// Get root folder
		IFolder rootFolder = this.repositoryHandler
				.getFolder(net.sourceforge.atunes.utils.FileUtils
						.getPath(repositoryFolder));

		// Now navigate through folder until find folder that contains file
		String path = this.fileManager.getFolderPath(file);
		path = path.replace(net.sourceforge.atunes.utils.FileUtils
				.getPath(repositoryFolder), "");

		IFolder f = rootFolder;
		StringTokenizer st = new StringTokenizer(path,
				this.osManager.getFileSeparator());
		while (st.hasMoreTokens()) {
			String folderName = st.nextToken();
			f = f.getFolder(folderName);
		}
		return f;
	}
}
