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

import net.sourceforge.atunes.kernel.modules.process.ImportFilesProcess;
import net.sourceforge.atunes.model.IBackgroundWorker.IActionsWithBackgroundResult;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IReviewImportDialog;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.ITagAttributesReviewed;

public class ImportFoldersToRepositoryActionsWithBackgroundResult implements IActionsWithBackgroundResult<List<ILocalAudioObject>> {

	private List<File> folders;
	private String path;
	private IProcessFactory processFactory;
	private IStateRepository stateRepository;
	private IIndeterminateProgressDialog indeterminateProgressDialog;
	
	private IDialogFactory dialogFactory;
	
	private ImportToRepositoryProcessListener importToRepositoryProcessListener;
	
	/**
	 * @param importToRepositoryProcessListener
	 */
	public void setImportToRepositoryProcessListener(ImportToRepositoryProcessListener importToRepositoryProcessListener) {
		this.importToRepositoryProcessListener = importToRepositoryProcessListener;
	}
	
	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param folders
	 */
	public void setFolders(List<File> folders) {
		this.folders = folders;
	}

	/**
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	@Override
	public void call(List<ILocalAudioObject> result) {
		indeterminateProgressDialog.hideDialog();
		ITagAttributesReviewed tagAttributesReviewed = null;
		// Review tags if selected in settings
		if (stateRepository.isReviewTagsBeforeImport()) {
			IReviewImportDialog reviewImportDialog = dialogFactory.newDialog(IReviewImportDialog.class);
			reviewImportDialog.setFolders(folders);
			reviewImportDialog.setFilesToLoad(result);
			reviewImportDialog.showDialog();
			if (reviewImportDialog.isDialogCancelled()) {
				return;
			}
			tagAttributesReviewed = reviewImportDialog.getResult();
		}

		ImportFilesProcess process = (ImportFilesProcess) processFactory.getProcessByName("importFilesProcess");
		process.setFilesToTransfer(result);
		process.setFolders(folders);
		process.setDestination(path);
		process.initialize(tagAttributesReviewed);
		process.addProcessListener(importToRepositoryProcessListener);
		process.execute();
	}

	/**
	 * @param indeterminateDialog
	 */
	public void setIndeterminateDialog(IIndeterminateProgressDialog indeterminateDialog) {
		this.indeterminateProgressDialog = indeterminateDialog;
	}

}
