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

import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectLocator;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.utils.Logger;

/**
 * Refreshes a folder
 * 
 * @author alex
 * 
 */
public class FolderRefresher {

	private IOSManager osManager;

	private RepositoryHandler repositoryHandler;

	private LocalAudioObjectRefresher localAudioObjectRefresher;

	private RepositoryAddService repositoryAddService;

	private ILocalAudioObjectLocator localAudioObjectLocator;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param localAudioObjectLocator
	 */
	public void setLocalAudioObjectLocator(
			final ILocalAudioObjectLocator localAudioObjectLocator) {
		this.localAudioObjectLocator = localAudioObjectLocator;
	}

	/**
	 * @param repositoryAddService
	 */
	public void setRepositoryAddService(
			final RepositoryAddService repositoryAddService) {
		this.repositoryAddService = repositoryAddService;
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
	public void setRepositoryHandler(final RepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param localAudioObjectRefresher
	 */
	public void setLocalAudioObjectRefresher(
			final LocalAudioObjectRefresher localAudioObjectRefresher) {
		this.localAudioObjectRefresher = localAudioObjectRefresher;
	}

	/**
	 * Refreshes folder
	 * 
	 * @param repository
	 * @param folders
	 */
	public void refreshFolders(final IRepository repository,
			final List<IFolder> folders) {
		repositoryHandler.startTransaction();
		for (IFolder folder : folders) {
			refreshFolder(repository, folder);
		}
		repositoryHandler.endTransaction();
	}

	/**
	 * @param repository
	 * @param folder
	 */
	private void refreshFolder(final IRepository repository,
			final IFolder folder) {
		removeOrRefresh(repository, folder);
		addNewFiles(repository, folder);
	}

	/**
	 * @param repository
	 * @param folder
	 */
	private void addNewFiles(final IRepository repository, final IFolder folder) {
		// Add new files
		List<ILocalAudioObject> allObjects = localAudioObjectLocator
				.locateLocalAudioObjectsInFolder(
						folder.getFolderPath(osManager), null);
		for (ILocalAudioObject ao : allObjects) {
			String path = fileManager.getPath(ao);
			if (repository.getFile(path) == null) {
				Logger.debug("Adding file: ", path);
				repositoryAddService.addFilesToRepository(repository,
						Collections.singletonList(fileManager.getFile(ao)));
			}
		}
	}

	/**
	 * @param repository
	 * @param folder
	 */
	private void removeOrRefresh(final IRepository repository,
			final IFolder folder) {
		// Remove o refresh previous files
		List<ILocalAudioObject> aos = folder.getAudioObjects();
		for (ILocalAudioObject ao : aos) {
			if (fileManager.fileExists(ao)) {
				Logger.debug("Refreshing file: ", fileManager.getPath(ao));
				localAudioObjectRefresher.refreshFile(repository, ao);
			} else {
				Logger.debug("Removing file: ", fileManager.getPath(ao));
				repositoryHandler.remove(Collections.singletonList(ao));
			}
		}
	}
}
