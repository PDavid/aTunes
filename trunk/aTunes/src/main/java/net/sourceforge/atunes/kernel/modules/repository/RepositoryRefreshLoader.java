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

import java.util.concurrent.Callable;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Executes repository load when loading a new repository
 * @author alex
 *
 */
public class RepositoryRefreshLoader extends AbstractRepositoryLoader {

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IFrame frame;

	private RepositoryActionsHelper repositoryActions;

	/**
	 * @param repositoryActions
	 */
	public void setRepositoryActions(final RepositoryActionsHelper repositoryActions) {
		this.repositoryActions = repositoryActions;
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
	public void setBackgroundWorkerFactory(final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	@Override
	protected void execute() {
		IBackgroundWorker<Void> worker = backgroundWorkerFactory.getWorker();
		worker.setActionsBeforeBackgroundStarts(new Runnable() {

			@Override
			public void run() {
				String text = StringUtils.getString(I18nUtils.getString("REFRESHING"), "...");
				frame.showProgressBar(true, text);
				repositoryActions.enableRepositoryActions(false);
			}
		});
		worker.setBackgroundActions(new Callable<Void>() {

			@Override
			public Void call() {
				run();
				return null;
			}
		});
		worker.execute();
	}

	@Override
	protected void runTasksBeforeLoadRepository() {
		// Nothing to do
	}

	@Override
	protected void notifyCurrentPath(final String relativePath) {
		// Nothing to do
	}

	@Override
	protected void notifyCurrentProgress() {
		// Nothing to do
	}

	@Override
	protected void notifyFileLoaded() {
		// Nothing to do
	}

	@Override
	protected void notifyCurrentAlbum(final String artist, final String album) {
		// Nothing to do
	}

	@Override
	protected void notifyFinishLoader() {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				getRepositoryLoaderListener().notifyFinishRefresh(RepositoryRefreshLoader.this);
			}
		});
	}
}
