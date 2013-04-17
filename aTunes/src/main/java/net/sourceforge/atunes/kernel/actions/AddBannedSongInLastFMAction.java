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

package net.sourceforge.atunes.kernel.actions;

import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds a song to banned tracks in Last.fm profile
 * 
 * @author fleax
 * 
 */
public class AddBannedSongInLastFMAction extends CustomAbstractAction {

	private static final long serialVersionUID = -2687851398606488392L;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IWebServicesHandler webServicesHandler;

	private IContextHandler contextHandler;

	private IStateContext stateContext;

	private ITaskService taskService;

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(
			final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param contextHandler
	 */
	public void setContextHandler(final IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}

	/**
     * 
     */
	public AddBannedSongInLastFMAction() {
		super(I18nUtils.getString("ADD_BANNED_SONG_IN_LASTFM"));
	}

	@Override
	protected void initialize() {
		super.initialize();
		setEnabled(this.stateContext.isLastFmEnabled());
	}

	@Override
	protected void executeAction() {
		setEnabled(false);
		IBackgroundWorker<Void, Void> backgroundWorker = this.backgroundWorkerFactory
				.getWorker();
		backgroundWorker.setBackgroundActions(new Callable<Void>() {
			@Override
			public Void call() {
				AddBannedSongInLastFMAction.this.webServicesHandler
						.addBannedSong(AddBannedSongInLastFMAction.this.contextHandler
								.getCurrentAudioObject());
				return null;
			}
		});
		backgroundWorker
				.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<Void>() {

					@Override
					public void call(final Void result) {
						setEnabled(true);
					}
				});
		backgroundWorker.execute(this.taskService);
	}
}
