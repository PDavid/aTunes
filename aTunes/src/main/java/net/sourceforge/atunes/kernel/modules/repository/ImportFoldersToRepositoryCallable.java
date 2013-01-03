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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectLocator;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;

/**
 * Finds all files to import
 * 
 * @author alex
 * 
 */
public class ImportFoldersToRepositoryCallable implements
	Callable<List<ILocalAudioObject>> {

    private List<File> folders;
    private IProgressDialog progressDialog;
    private ILocalAudioObjectValidator localAudioObjectValidator;
    private ILocalAudioObjectLocator localAudioObjectLocator;

    /**
     * @param folders
     */
    public void setFolders(final List<File> folders) {
	this.folders = folders;
    }

    /**
     * @param progressDialog
     */
    public void setProgressDialog(final IProgressDialog progressDialog) {
	this.progressDialog = progressDialog;
    }

    /**
     * @param localAudioObjectValidator
     */
    public void setLocalAudioObjectValidator(
	    final ILocalAudioObjectValidator localAudioObjectValidator) {
	this.localAudioObjectValidator = localAudioObjectValidator;
    }

    /**
     * @param localAudioObjectLocator
     */
    public void setLocalAudioObjectLocator(
	    final ILocalAudioObjectLocator localAudioObjectLocator) {
	this.localAudioObjectLocator = localAudioObjectLocator;
    }

    @Override
    public List<ILocalAudioObject> call() {
	return getSongsForFolders(folders, new ImportFoldersLoaderListener(
		progressDialog), localAudioObjectValidator);
    }

    /**
     * Gets the songs of a list of folders. Used in import
     * 
     * @param folders
     * @param listener
     * @param localAudioObjectValidator
     * @return
     */
    private List<ILocalAudioObject> getSongsForFolders(
	    final List<File> folders, final IRepositoryLoaderListener listener,
	    final ILocalAudioObjectValidator localAudioObjectValidator) {
	int filesCount = 0;
	for (File folder : folders) {
	    filesCount = filesCount
		    + countFiles(folder, localAudioObjectValidator);
	}
	if (listener != null) {
	    listener.notifyFilesInRepository(filesCount);
	}
	List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
	for (File folder : folders) {
	    result.addAll(localAudioObjectLocator
		    .locateLocalAudioObjectsInFolder(folder, listener));
	}
	if (listener != null) {
	    listener.notifyFinishRead(null);
	}
	return result;
    }

    /**
     * Count files.
     * 
     * @param dir
     * @param localAudioObjectValidator
     * @return
     */
    private int countFiles(final File dir,
	    final ILocalAudioObjectValidator localAudioObjectValidator) {
	int files = 0;
	File[] list = dir.listFiles();
	if (list == null) {
	    return files;
	}
	for (File element : list) {
	    if (localAudioObjectValidator.isValidAudioFile(element)) {
		files++;
	    } else if (element.isDirectory()) {
		files = files + countFiles(element, localAudioObjectValidator);
	    }
	}
	return files;
    }
}
