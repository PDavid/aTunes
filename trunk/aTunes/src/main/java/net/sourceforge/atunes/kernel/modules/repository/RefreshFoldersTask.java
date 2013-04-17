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

import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Refresh a list of folders from repository
 * 
 * @author alex
 * 
 */
public final class RefreshFoldersTask {

	private FolderRefresher folderRefresher;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IFrame frame;

	private IBeanFactory beanFactory;

	private ITaskService taskService;

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param frame
	 */
	public void setFrame(final IFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(
			final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param folderRefresher
	 */
	public void setFolderRefresher(final FolderRefresher folderRefresher) {
		this.folderRefresher = folderRefresher;
	}

	/**
	 * Executes task to refresh folders of repository
	 * 
	 * @param repository
	 * @param folders
	 */
	public void execute(final IRepository repository,
			final List<IFolder> folders) {
		IBackgroundWorker<Void, Void> worker = this.backgroundWorkerFactory
				.getWorker();
		worker.setActionsBeforeBackgroundStarts(new Runnable() {
			@Override
			public void run() {
				RefreshFoldersTask.this.frame.showProgressBar(true, StringUtils
						.getString(I18nUtils.getString("REFRESHING"), "..."));
				RefreshFoldersTask.this.beanFactory.getBean(
						RepositoryActionsHelper.class)
						.disableAllRepositoryActions();
			}
		});
		worker.setBackgroundActions(new Callable<Void>() {

			@Override
			public Void call() {
				RefreshFoldersTask.this.folderRefresher.refreshFolders(
						repository, folders);
				return null;
			}
		});
		worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<Void>() {

			@Override
			public void call(final Void result) {
				RefreshFoldersTask.this.beanFactory.getBean(
						RepositoryLoadedActions.class).repositoryReadCompleted(
						repository);
			}
		});
		worker.execute(this.taskService);
	}
}