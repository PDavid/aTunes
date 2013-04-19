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

package net.sourceforge.atunes.gui;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerCallback;
import net.sourceforge.atunes.model.ITaskService;

/**
 * Implementation of a IBackgroundWorker using Swing
 * 
 * @author alex
 * @param <T>
 * @param <I>
 */
public class SwingBackgroundWorker<T, I> implements IBackgroundWorker<T, I> {

	private Callable<T> backgroundActions;

	private Runnable graphicalActionsBeforeStart;

	private IActionsWithBackgroundResult<T> graphicalActionsWhenDone;

	private IActionsWithIntermediateResult<I> intermediateActions;

	private BackgroundSwingWorker<T, I> backgroundSwingWorker;

	private IBackgroundWorkerCallback<T> callback;

	@Override
	public void setBackgroundActions(final Callable<T> backgroundActions) {
		this.backgroundActions = backgroundActions;
	}

	@Override
	public void setActionsBeforeBackgroundStarts(
			final Runnable afterStartActions) {
		this.graphicalActionsBeforeStart = afterStartActions;
	}

	@Override
	public void setActionsWhenDone(
			final IActionsWithBackgroundResult<T> graphicalActionsWhenDone) {
		this.graphicalActionsWhenDone = graphicalActionsWhenDone;
	}

	@Override
	public void setCallback(final IBackgroundWorkerCallback<T> callback) {
		this.callback = callback;
	}

	@Override
	public void setActionsWithIntermediateResult(
			net.sourceforge.atunes.model.IBackgroundWorker.IActionsWithIntermediateResult<I> actionsWithIntermediateResult) {
		this.intermediateActions = actionsWithIntermediateResult;
	}

	@Override
	public Future<?> execute(final ITaskService taskService) {
		this.backgroundSwingWorker = new BackgroundSwingWorker<T, I>(
				this.graphicalActionsBeforeStart, this.backgroundActions,
				this.graphicalActionsWhenDone, this.callback,
				this.intermediateActions);
		return taskService.submitNow("Swing Background Task",
				this.backgroundSwingWorker);
	}

	@Override
	public boolean isDone() {
		return this.backgroundSwingWorker != null ? this.backgroundSwingWorker
				.isDone() : false;
	}

	@Override
	public void publish(I chunk) {
		this.backgroundSwingWorker.publish(chunk);
	}
}
