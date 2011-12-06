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

import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.Logger;

/**
 * Refreshes a folder
 * @author alex
 *
 */
public class FolderRefresher {
	
	private IOSManager osManager;
	
	private IRepositoryHandler repositoryHandler;
	
	private ILocalAudioObjectFactory localAudioObjectFactory;
	
	private ILocalAudioObjectValidator localAudioObjectValidator;
	
	private LocalAudioObjectRefresher localAudioObjectRefresher;
	
	private RepositoryAddService repositoryAddService;
	
	/**
	 * @param repositoryAddService
	 */
	public void setRepositoryAddService(RepositoryAddService repositoryAddService) {
		this.repositoryAddService = repositoryAddService;
	}
	
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
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}
	
	/**
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}
	
	/**
	 * @param localAudioObjectRefresher
	 */
	public void setLocalAudioObjectRefresher(LocalAudioObjectRefresher localAudioObjectRefresher) {
		this.localAudioObjectRefresher = localAudioObjectRefresher;
	}
	
	/**
	 * Refreshes folder
	 * @param repository
	 * @param folders
	 */
	public void refreshFolders(IRepository repository, List<Folder> folders) {
		repositoryHandler.startTransaction();
		for (Folder folder : folders) {
			refreshFolder(repository, folder);
		}
		repositoryHandler.endTransaction();
	}

	/**
	 * @param repository
	 * @param folder
	 */
	private void refreshFolder(IRepository repository, Folder folder) {
		removeOrRefresh(repository, folder);
		addNewFiles(repository, folder);
	}

	/**
	 * @param repository
	 * @param folder
	 */
	private void addNewFiles(IRepository repository, Folder folder) {
		// Add new files
		List<ILocalAudioObject> allObjects = RepositoryLoader.getSongsForFolder(folder.getFolderPath(osManager), null, localAudioObjectFactory, localAudioObjectValidator);
		for (ILocalAudioObject ao : allObjects) {
			if (repository.getFile(ao.getFile().getAbsolutePath()) == null) {
				Logger.debug("Adding file: ", ao.getFile().getAbsolutePath());
				repositoryAddService.addToRepository(repository, Collections.singletonList(ao.getFile()));
			}
		}
	}

	/**
	 * @param repository
	 * @param folder
	 */
	private void removeOrRefresh(IRepository repository, Folder folder) {
		// Remove o refresh previous files		
		List<ILocalAudioObject> aos = folder.getAudioObjects();
		for (ILocalAudioObject ao : aos) {
			if (ao.getFile().exists()) {
				Logger.debug("Refreshing file: ", ao.getFile().getAbsolutePath());
				localAudioObjectRefresher.refreshFile(repository, ao);
			} else {
				Logger.debug("Removing file: ", ao.getFile().getAbsolutePath());
				repositoryHandler.remove(Collections.singletonList(ao));
			}
		}
	}

}
