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

import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.gui.views.dialogs.RecommendedEventsDialog;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IEvent;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Shows recommended events retrieved from last.fm user profile
 * 
 * @author alex
 * 
 */
public class ShowRecommendedEventsFromLastFMAction extends CustomAbstractAction {

	private static final long serialVersionUID = 5620935204300321285L;

	private IIndeterminateProgressDialog dialog;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IWebServicesHandler webServicesHandler;

	private IDialogFactory dialogFactory;

	private ITaskService taskService;

	private IStateContext stateContext;

	/**
	 * @param stateContext
	 */
	public void setStateContext(IStateContext stateContext) {
		this.stateContext = stateContext;
	}

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
	  * 
	  */
	public ShowRecommendedEventsFromLastFMAction() {
		super(I18nUtils.getString("SHOW_RECOMMENDED_EVENTS_FROM_LASTFM"));
	}

	@Override
	protected void initialize() {
		super.initialize();
		setEnabled(this.stateContext.isLastFmEnabled());
	}

	@Override
	protected void executeAction() {
		IBackgroundWorker<List<IEvent>, Void> worker = this.backgroundWorkerFactory
				.getWorker();
		worker.setActionsBeforeBackgroundStarts(new Runnable() {
			@Override
			public void run() {
				ShowRecommendedEventsFromLastFMAction.this.dialog = ShowRecommendedEventsFromLastFMAction.this.dialogFactory
						.newDialog(IIndeterminateProgressDialog.class);
				ShowRecommendedEventsFromLastFMAction.this.dialog
						.setTitle(I18nUtils.getString("PLEASE_WAIT"));
				ShowRecommendedEventsFromLastFMAction.this.dialog.showDialog();
			}
		});

		worker.setBackgroundActions(new Callable<List<IEvent>>() {

			@Override
			public List<IEvent> call() {
				return webServicesHandler.getRecommendedEvents();
			}
		});

		worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<List<IEvent>>() {
			@Override
			public void call(final List<IEvent> events) {
				finishAction(events);
			}
		});

		worker.execute(this.taskService);
	}

	/**
	 * @param events
	 */
	private void finishAction(final List<IEvent> events) {
		this.dialog.hideDialog();

		RecommendedEventsDialog eventsDialog = this.dialogFactory
				.newDialog(RecommendedEventsDialog.class);
		eventsDialog.setEvents(events);
		eventsDialog.showDialog();
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
}
