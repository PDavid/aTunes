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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Interface implemented by tasks done in background that perform some graphical
 * actions when done
 * 
 * @author alex
 * 
 * @param <T>
 *            Final result
 * @param <I>
 *            Intermediate result
 */
public interface IBackgroundWorker<T, I> {

	/**
	 * Set background actions
	 * 
	 * @param backgroundActions
	 */
	public void setBackgroundActions(Callable<T> backgroundActions);

	/**
	 * Graphical actions to do before start background
	 * 
	 * @param beforeStartActions
	 */
	public void setActionsBeforeBackgroundStarts(Runnable beforeStartActions);

	/**
	 * Graphical actions to do when finish
	 * 
	 * @param graphicalActions
	 */
	public void setActionsWhenDone(
			IActionsWithBackgroundResult<T> graphicalActions);

	/**
	 * Callback when finished
	 * 
	 * @param callback
	 */
	public void setCallback(IBackgroundWorkerCallback<T> callback);

	/**
	 * Callback with intermediate result
	 * 
	 * @param actionsWithIntermediateResult
	 */
	public void setActionsWithIntermediateResult(
			IActionsWithIntermediateResult<I> actionsWithIntermediateResult);

	/**
	 * Execute actions in task service
	 * 
	 * @param taskService
	 * @return
	 */
	public Future<?> execute(ITaskService taskService);

	/**
	 * @return true if worker finished
	 */
	public boolean isDone();

	/**
	 * Publish a chunk of work
	 * 
	 * @param chunk
	 */
	public void publish(final I chunk);

	/**
	 * Result of a background work
	 * 
	 * @author alex
	 * 
	 * @param <T>
	 */
	public interface IActionsWithBackgroundResult<T> {

		/**
		 * @param result
		 */
		void call(T result);
	}

	/**
	 * Called with an intermediate result
	 * 
	 * @author alex
	 * 
	 * @param <I>
	 */
	public interface IActionsWithIntermediateResult<I> {

		/**
		 * @param result
		 */
		void intermediateResult(List<I> result);
	}
}
