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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

public class RepositoryAddService {
	
	private ILocalAudioObjectFactory localAudioObjectFactory;

	private IStateNavigation stateNavigation;
	
	private IUnknownObjectChecker unknownObjectChecker;
	
	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	
	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}
	
	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}

	/**
	 * Add files to repository.
	 * @param state
	 * @param rep
	 * @param files
	 * @param localAudioObjectFactory
	 */
	public void addToRepository(IRepository rep, List<File> files) {
		// Get folders where files are
		Set<File> folders = getFoldersOfFiles(files);
		RepositoryFiller filler = new RepositoryFiller(rep, stateNavigation, unknownObjectChecker);
		for (File folder : folders) {
			addFilesFromFolder(rep, files, filler, folder);
		}		
	}

	/**
	 * @param repository
	 * @param files
	 * @param filler
	 * @param folder
	 */
	private void addFilesFromFolder(IRepository repository, List<File> files, RepositoryFiller filler, File folder) {
		String repositoryPath = getRepositoryPathForFolder(repository, folder);
		int firstChar = repositoryPath.length() + 1;

		for (File f : files) {
			if (f.getParentFile().equals(folder)) {
				ILocalAudioObject audioObject = localAudioObjectFactory.getLocalAudioObject(f);
				filler.addAudioFile(audioObject, getRepositoryFolderContaining(repository, folder), getRelativePath(firstChar, audioObject));
			}
		}
	}

	/**
	 * @param firstChar
	 * @param audioObject
	 * @return
	 */
	private String getRelativePath(int firstChar, ILocalAudioObject audioObject) {
		String pathToFile = audioObject.getUrl().replace('\\', '/');
		int lastChar = pathToFile.lastIndexOf('/') + 1;
		String relativePath;
		if (firstChar < lastChar) {
			relativePath = pathToFile.substring(firstChar, lastChar);
		} else {
			relativePath = ".";
		}
		return relativePath;
	}

	/**
	 * @param rep
	 * @param folder
	 * @return
	 */
	private String getRepositoryPathForFolder(IRepository rep, File folder) {
		String repositoryPath = getRepositoryFolderContaining(rep, folder).getAbsolutePath().replace('\\', '/');
		if (repositoryPath.endsWith("/")) {
			repositoryPath = repositoryPath.substring(0, repositoryPath.length() - 2);
		}
		return repositoryPath;
	}

	/**
	 * @param files
	 * @return
	 */
	private Set<File> getFoldersOfFiles(List<File> files) {
		Set<File> folders = new HashSet<File>();
		for (File file : files) {
			folders.add(file.getParentFile());
		}
		return folders;
	}

	/**
	 * Gets the repository folder containing the given folder
	 * 
	 * @param rep
	 *            the rep
	 * @param folder
	 *            the folder
	 * 
	 * @return the repository folder containing
	 */
	private File getRepositoryFolderContaining(IRepository rep, File folder) {
		String path = folder.getAbsolutePath();
		for (File f : rep.getRepositoryFolders()) {
			if (path.startsWith(f.getAbsolutePath())) {
				return f;
			}
		}
		return null;
	}
}
