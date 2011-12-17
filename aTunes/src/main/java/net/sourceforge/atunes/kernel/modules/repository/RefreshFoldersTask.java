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

import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class RefreshFoldersTask {
	
	private RepositoryReader repositoryReader;
	
	private IRepositoryHandler repositoryHandler;
	
	private FolderRefresher folderRefresher;
	
	private IBackgroundWorkerFactory backgroundWorkerFactory;
	
	private IFrame frame;
	
	private RepositoryActionsHelper repositoryActions;
	
	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * @param repositoryActions
	 */
	public void setRepositoryActions(RepositoryActionsHelper repositoryActions) {
		this.repositoryActions = repositoryActions;
	}
	
	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
	
	/**
	 * @param repositoryReader
	 */
	public void setRepositoryReader(RepositoryReader repositoryReader) {
		this.repositoryReader = repositoryReader;
	}
	
	/**
	 * @param folderRefresher
	 */
	public void setFolderRefresher(FolderRefresher folderRefresher) {
		this.folderRefresher = folderRefresher;
	}
	
	/**
	 * Executes task to refresh folders of repository
	 * @param repository
	 * @param folders
	 */
	public void execute(final IRepository repository, final List<IFolder> folders) {
		IBackgroundWorker<Void> worker = backgroundWorkerFactory.getWorker();
		worker.setActionsAfterBackgroundStarted(new Runnable() {
			@Override
			public void run() {
		    	frame.showProgressBar(true, StringUtils.getString(I18nUtils.getString("REFRESHING"), "..."));
		    	repositoryActions.enableRepositoryActions(false);
			}
		});
		worker.setBackgroundActions(new Callable<Void>() {
			
			@Override
			public Void call() throws Exception {
				repositoryHandler.startTransaction();
				folderRefresher.refreshFolders(repository, folders);
		        repositoryHandler.endTransaction();
				return null;
			}
		});
		worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<Void>() {
			
			@Override
			public void call(Void result) {
				repositoryReader.notifyFinishRefresh(null);
			}
		});
		worker.execute();
	}
}