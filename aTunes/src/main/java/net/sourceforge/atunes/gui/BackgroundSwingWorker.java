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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IBackgroundWorker.IActionsWithBackgroundResult;
import net.sourceforge.atunes.model.IBackgroundWorker.IActionsWithIntermediateResult;
import net.sourceforge.atunes.model.IBackgroundWorkerCallback;
import net.sourceforge.atunes.utils.Logger;

final class BackgroundSwingWorker<T, I> extends SwingWorker<T, I> {

	private final Runnable graphicalActionsBeforeStart;

	private final Callable<T> backgroundActions;

	private final IActionsWithBackgroundResult<T> graphicalActionsWhenDone;

	private final IActionsWithIntermediateResult<I> intermediateActions;

	private final IBackgroundWorkerCallback<T> callback;

	/**
	 * @param graphicalActionsBeforeStart
	 * @param backgroundActions
	 * @param graphicalActionsWhenDone
	 * @param callback
	 * @param intermediateActions
	 */
	public BackgroundSwingWorker(final Runnable graphicalActionsBeforeStart,
			final Callable<T> backgroundActions,
			final IActionsWithBackgroundResult<T> graphicalActionsWhenDone,
			final IBackgroundWorkerCallback<T> callback,
			IActionsWithIntermediateResult<I> intermediateActions) {
		this.graphicalActionsBeforeStart = graphicalActionsBeforeStart;
		this.backgroundActions = backgroundActions;
		this.graphicalActionsWhenDone = graphicalActionsWhenDone;
		this.callback = callback;
		this.intermediateActions = intermediateActions;
	}

	@Override
	protected T doInBackground() {
		if (this.graphicalActionsBeforeStart != null) {
			GuiUtils.callInEventDispatchThread(this.graphicalActionsBeforeStart);
		}
		try {
			return this.backgroundActions.call();
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	@Override
	protected void process(List<I> chunks) {
		super.process(chunks);
		if (this.intermediateActions != null) {
			this.intermediateActions.intermediateResult(chunks);
		}
	}

	@Override
	protected void done() {
		T backgroundResult = null;
		try {
			backgroundResult = get();
		} catch (InterruptedException e) {
			Logger.error(e);
		} catch (ExecutionException e) {
			Logger.error(e);
		}
		if (this.graphicalActionsWhenDone != null) {
			this.graphicalActionsWhenDone.call(backgroundResult);
		}
		if (this.callback != null) {
			this.callback.workerFinished(backgroundResult);
		}
	}

	@SuppressWarnings("unchecked")
	public void publish(I chunk) {
		super.publish(chunk);
	}
}