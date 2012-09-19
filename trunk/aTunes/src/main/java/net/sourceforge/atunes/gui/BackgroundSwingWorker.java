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

package net.sourceforge.atunes.gui;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IBackgroundWorker.IActionsWithBackgroundResult;
import net.sourceforge.atunes.utils.Logger;

final class BackgroundSwingWorker<T> extends SwingWorker<T, Void> {

	private final Runnable graphicalActionsBeforeStart;

	private final Callable<T> backgroundActions;

	private final IActionsWithBackgroundResult<T> graphicalActionsWhenDone;

	/**
	 * @param graphicalActionsBeforeStart
	 * @param backgroundActions
	 * @param graphicalActionsWhenDone
	 */
	public BackgroundSwingWorker(final Runnable graphicalActionsBeforeStart, final Callable<T> backgroundActions, final IActionsWithBackgroundResult<T> graphicalActionsWhenDone) {
		this.graphicalActionsBeforeStart = graphicalActionsBeforeStart;
		this.backgroundActions = backgroundActions;
		this.graphicalActionsWhenDone = graphicalActionsWhenDone;
	}

	@Override
	protected T doInBackground() {
		if (graphicalActionsBeforeStart != null) {
			GuiUtils.callInEventDispatchThread(graphicalActionsBeforeStart);
		}
		try {
			return backgroundActions.call();
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
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
		if (graphicalActionsWhenDone != null) {
			graphicalActionsWhenDone.call(backgroundResult);
		}
	}
}