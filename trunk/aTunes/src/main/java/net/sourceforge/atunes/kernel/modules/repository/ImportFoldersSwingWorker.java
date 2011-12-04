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
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IReviewImportDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITagAttributesReviewed;
import net.sourceforge.atunes.utils.Logger;

final class ImportFoldersSwingWorker extends SwingWorker<List<ILocalAudioObject>, Void> {
	
	private final IRepositoryHandler repositoryHandler;
	private final List<File> folders;
	private final String path;
	private final IProgressDialog progressDialog;
	private final IFrame frame;
	private final IState state;
	private final IErrorDialogFactory errorDialogFactory;
	private final IOSManager osManager;
	private final ILocalAudioObjectFactory localAudioObjectFactory;
	private final ILocalAudioObjectValidator localAudioObjectValidator;

	/**
	 * @param repositoryHandler
	 * @param folders
	 * @param path
	 * @param progressDialog
	 * @param frame
	 * @param state
	 * @param errorDialogFactory
	 * @param osManager
	 * @param localAudioObjectFactory
	 * @param localAudioObjectValidator
	 */
	public ImportFoldersSwingWorker(IRepositoryHandler repositoryHandler, List<File> folders, String path, IProgressDialog progressDialog, IFrame frame, IState state, IErrorDialogFactory errorDialogFactory, IOSManager osManager, ILocalAudioObjectFactory localAudioObjectFactory, ILocalAudioObjectValidator localAudioObjectValidator) {
		this.repositoryHandler = repositoryHandler;
		this.folders = folders;
		this.path = path;
		this.progressDialog = progressDialog;
		this.frame = frame;
		this.state = state;
		this.errorDialogFactory = errorDialogFactory;
		this.osManager = osManager;
		this.localAudioObjectFactory = localAudioObjectFactory;
		this.localAudioObjectValidator = localAudioObjectValidator;
	}

	@Override
	protected List<ILocalAudioObject> doInBackground() throws Exception {
	    return RepositoryLoader.getSongsForFolders(folders, new ImportFoldersLoaderListener(progressDialog), localAudioObjectFactory, localAudioObjectValidator);
	}

	@Override
	protected void done() {
	    super.done();

	    try {
	        final List<ILocalAudioObject> filesToLoad = get();

	        ITagAttributesReviewed tagAttributesReviewed = null;
	        // Review tags if selected in settings
	        if (state.isReviewTagsBeforeImport()) {
	            IReviewImportDialog reviewImportDialog = Context.getBean(IReviewImportDialog.class);
	            reviewImportDialog.showDialog(folders, filesToLoad);
	            if (reviewImportDialog.isDialogCancelled()) {
	                return;
	            }
	            tagAttributesReviewed = reviewImportDialog.getResult();
	        }

	        final ImportFilesProcess process = new ImportFilesProcess(filesToLoad, folders, path, tagAttributesReviewed, state, frame, osManager, localAudioObjectFactory, localAudioObjectValidator);
	        process.addProcessListener(new ImportFilesProcessListener(process, repositoryHandler, frame, errorDialogFactory));
	        process.execute();

	    } catch (InterruptedException e) {
	        Logger.error(e);
	    } catch (ExecutionException e) {
	        Logger.error(e);
	    }
	}
}