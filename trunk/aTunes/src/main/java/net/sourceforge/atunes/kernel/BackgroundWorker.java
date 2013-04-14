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

package net.sourceforge.atunes.kernel;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerCallback;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ITaskService;

/**
 * Abstract class to help creation of background workers
 * 
 * @author alex
 * @param <T>
 */
public abstract class BackgroundWorker<T> {

	private IBeanFactory beanFactory;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private ITaskService taskService;

	private ScheduledFuture<?> future;

	/**
	 * @param taskService
	 */
	public final void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public final void setBackgroundWorkerFactory(
			final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param beanFactory
	 */
	public final void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return bean factory
	 */
	protected final IBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Execute this task
	 */
	public final void execute() {
		execute(null);
	}

	/**
	 * Execute this task with given callback
	 * 
	 * @param callback
	 */
	public final void execute(final IBackgroundWorkerCallback<T> callback) {
		IBackgroundWorker<T> worker = this.backgroundWorkerFactory.getWorker();
		worker.setActionsBeforeBackgroundStarts(new Runnable() {
			@Override
			public void run() {
				before();
			}
		});
		worker.setBackgroundActions(new Callable<T>() {

			@Override
			public T call() throws Exception {
				return doInBackground();
			}
		});
		worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<T>() {

			@Override
			public void call(final T result) {
				done(result);
				if (callback != null) {
					callback.workerFinished(result);
				}
			}
		});
		this.future = worker.execute(this.taskService);
	}

	/**
	 * @return if cancelled
	 */
	public final boolean isCancelled() {
		return this.future.isCancelled();
	}

	/**
	 * @param interrupt
	 */
	public final void cancel(final boolean interrupt) {
		this.future.cancel(interrupt);
	}

	/**
	 * Actions before background starts
	 */
	protected abstract void before();

	/**
	 * @return result of background
	 */
	protected abstract T doInBackground();

	/**
	 * Called with result
	 * 
	 * @param result
	 */
	protected abstract void done(T result);
}
