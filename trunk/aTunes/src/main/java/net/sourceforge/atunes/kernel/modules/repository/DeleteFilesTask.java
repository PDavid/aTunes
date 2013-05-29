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

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Removes files from repository
 * 
 * @author alex
 * 
 */
public final class DeleteFilesTask extends BackgroundWorker<Void, Void> {

	private IDialogFactory dialogFactory;

	private IRepositoryHandler repositoryHandler;

	private IIndeterminateProgressDialog dialog;

	private IFileManager fileManager;
	
	private List<ILocalAudioObject> files;
	
	/**
	 * @param files
	 */
	public void setFiles(List<ILocalAudioObject> files) {
		this.files = files;
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

	@Override
	protected void before() {
		this.dialog = this.dialogFactory
				.newDialog(IIndeterminateProgressDialog.class);
		this.dialog.setTitle(I18nUtils.getString("PLEASE_WAIT"));
		this.dialog.showDialog();
	}
	
	@Override
	protected Void doInBackground() {
		List<ILocalAudioObject> filesDeleted = new ArrayList<ILocalAudioObject>();
		for (ILocalAudioObject audioFile : files) {
			if (DeleteFilesTask.this.fileManager.delete(audioFile)) {
				filesDeleted.add(audioFile);
			}
		}
		DeleteFilesTask.this.repositoryHandler.remove(filesDeleted);
		return null;
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected void done(Void result) {
		this.dialog.hideDialog();
	}
	
}