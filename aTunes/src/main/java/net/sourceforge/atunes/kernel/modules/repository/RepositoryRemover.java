/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.util.StringTokenizer;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

/**
 * Removes from repository
 * @author alex
 *
 */
public class RepositoryRemover {
	
	private IOSManager osManager;
	
	private IRepositoryHandler repositoryHandler;
	
	private IDeviceHandler deviceHandler;
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
	
	/**
	 * @param deviceHandler
	 */
	public void setDeviceHandler(IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}
	
	/**
	 * Permanently deletes an audio file from the repository meta-information
	 * 
	 * This method marks repository as dirty but new state is not notified
	 * To notify repository change call Repository.setDirty(true, true) when finish
	 * 
	 * @param file
	 * @param osManager
	 * @param repositoryHandler
	 * @param deviceHandler
	 */
	public void deleteFile(ILocalAudioObject file) {
		// Only do this if file is in repository
		if (getFolderForFile(file, osManager, repositoryHandler) != null) {
			removeFromRepository(file);
		}
		// File is on a device
		else if (deviceHandler.isDevicePath(file.getUrl())) {
			Logger.info("Deleted file ", file, " from device");
		}
	}

	/**
	 * @param file
	 */
	private void removeFromRepository(ILocalAudioObject file) {
		// Remove from file structure
		removeFromFileStructure(file);

		// Remove from tree structure
		removeFromTreeStructure(file);

		// Remove from genre structure
		removeFromGenreStructure(file);

		// Remove from year structure
		removeFromYearStructure(file);

		// Remove from repository
		repositoryHandler.removeFile(file);
	}

	/**
	 * @param file
	 */
	private void removeFromYearStructure(ILocalAudioObject file) {
		String year = file.getYear();

		IYear y = repositoryHandler.getYear(year);
		if (y != null) {
			y.removeAudioObject(file);
			if (y.size() <= 1) {
				repositoryHandler.removeYear(y);
			}
		}
	}

	/**
	 * @param file
	 */
	private void removeFromGenreStructure(ILocalAudioObject file) {
		String genre = file.getGenre();

		IGenre g = repositoryHandler.getGenre(genre);
		if (g != null) {
			g.removeAudioObject(file);
			if (g.size() <= 1) {
				repositoryHandler.removeGenre(g);
			}
		}
	}

	/**
	 * @param file
	 */
	private void removeFromTreeStructure(ILocalAudioObject file) {
		String albumArtist = file.getAlbumArtist();
		String artist = file.getArtist();
		String album = file.getAlbum();

		IArtist a = repositoryHandler.getArtist(albumArtist);
		if (a == null || UnknownObjectCheck.isUnknownArtist(a)) {
			a = repositoryHandler.getArtist(artist);
		}
		if (a != null) {
			IAlbum alb = a.getAlbum(album);
			if (alb != null) {
				if (alb.size() == 1) {
					a.removeAlbum(alb);
				} else {
					alb.removeAudioFile(file);
				}

				if (a.size() <= 1) {
					repositoryHandler.removeArtist(a);
				}
			}
		}
	}

	/**
	 * @param file
	 */
	private void removeFromFileStructure(ILocalAudioObject file) {
		IFolder f = getFolderForFile(file, osManager, repositoryHandler);
		if (f != null) {
			f.removeAudioFile(file);
			// If folder is empty, remove too
			if (f.isEmpty()) {
				f.getParentFolder().removeFolder(f);
			}
		}
	}
	
	/**
	 * Finds folder that contains file.
	 * 
	 * @param file
	 *            Audio file for which the folder is wanted
	 * 
	 * @param osManager
	 * @param repositoryHandler
	 * @return Either folder or null if file is not in repository
	 */
	private IFolder getFolderForFile(ILocalAudioObject file, IOSManager osManager, IRepositoryHandler repositoryHandler) {
		// Get repository folder where file is
		File repositoryFolder = repositoryHandler.getRepositoryFolderContainingFile(file);
		// If the file is not in the repository, return null
		if (repositoryFolder == null) {
			return null;
		}

		// Get root folder
		IFolder rootFolder = repositoryHandler.getFolder(repositoryFolder.getAbsolutePath());

		// Now navigate through folder until find folder that contains file
		String path = file.getFile().getParentFile().getAbsolutePath();
		path = path.replace(repositoryFolder.getAbsolutePath(), "");

		IFolder f = rootFolder;
		StringTokenizer st = new StringTokenizer(path, osManager.getFileSeparator());
		while (st.hasMoreTokens()) {
			String folderName = st.nextToken();
			f = f.getFolder(folderName);
		}
		return f;
	}
}
