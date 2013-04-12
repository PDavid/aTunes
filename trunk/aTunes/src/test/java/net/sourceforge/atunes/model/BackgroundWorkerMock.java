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

package net.sourceforge.atunes.model;

import java.util.concurrent.Callable;

/**
 * Basic implementation for tests
 * 
 * @author alex
 * @param <T>
 */
public class BackgroundWorkerMock<T> implements IBackgroundWorker<T> {

	private Callable<T> backgroundActions;

	private Runnable afterStartActions;

	private IActionsWithBackgroundResult<T> graphicalActions;

	@Override
	public void execute(final ITaskService taskService) {
		T result = null;
		if (this.backgroundActions != null) {
			try {
				result = this.backgroundActions.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (this.afterStartActions != null) {
			this.afterStartActions.run();
		}
		if (this.graphicalActions != null) {
			this.graphicalActions.call(result);
		}
	}

	@Override
	public void setBackgroundActions(final Callable<T> backgroundActions) {
		this.backgroundActions = backgroundActions;
	}

	@Override
	public void setActionsBeforeBackgroundStarts(
			final Runnable afterStartActions) {
		this.afterStartActions = afterStartActions;
	}

	@Override
	public void setActionsWhenDone(
			final IActionsWithBackgroundResult<T> graphicalActions) {
		this.graphicalActions = graphicalActions;
	}

	@Override
	public boolean isDone() {
		return false;
	}
}
