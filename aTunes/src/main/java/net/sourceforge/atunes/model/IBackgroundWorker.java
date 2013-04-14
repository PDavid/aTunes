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
import java.util.concurrent.ScheduledFuture;

/**
 * Interface implemented by tasks done in background that perform some graphical
 * actions when done
 * 
 * @author alex
 * 
 * @param <T>
 */
public interface IBackgroundWorker<T> {

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
	 * Execute actions in task service
	 * 
	 * @param taskService
	 * @return
	 */
	public ScheduledFuture<?> execute(ITaskService taskService);

	/**
	 * @return true if worker finished
	 */
	public boolean isDone();

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
}
