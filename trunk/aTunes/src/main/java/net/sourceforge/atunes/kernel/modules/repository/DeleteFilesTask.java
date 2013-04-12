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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Removes files from repository
 * 
 * @author alex
 * 
 */
public final class DeleteFilesTask {

	private IDialogFactory dialogFactory;

	private IRepositoryHandler repositoryHandler;

	private IIndeterminateProgressDialog dialog;

	private IFileManager fileManager;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private ITaskService taskService;

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(
			final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * Deletes files
	 * 
	 * @param files
	 */
	public void execute(final List<ILocalAudioObject> files) {
		IBackgroundWorker<Void> worker = this.backgroundWorkerFactory
				.getWorker();
		worker.setActionsBeforeBackgroundStarts(new Runnable() {

			@Override
			public void run() {
				showDialog();
			}
		});
		worker.setBackgroundActions(new Callable<Void>() {

			@Override
			public Void call() {
				List<ILocalAudioObject> filesDeleted = new ArrayList<ILocalAudioObject>();
				for (ILocalAudioObject audioFile : files) {
					if (DeleteFilesTask.this.fileManager.delete(audioFile)) {
						filesDeleted.add(audioFile);
					}
				}
				DeleteFilesTask.this.repositoryHandler.remove(filesDeleted);
				return null;
			}
		});
		worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<Void>() {

			@Override
			public void call(final Void result) {
				DeleteFilesTask.this.dialog.hideDialog();
			}
		});
		worker.execute(this.taskService);
	}

	private void showDialog() {
		this.dialog = this.dialogFactory
				.newDialog(IIndeterminateProgressDialog.class);
		this.dialog.setTitle(I18nUtils.getString("PLEASE_WAIT"));
		this.dialog.showDialog();
	}
}