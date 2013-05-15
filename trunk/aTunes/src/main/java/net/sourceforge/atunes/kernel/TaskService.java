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

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sourceforge.atunes.model.IApplicationLifeCycleListener;
import net.sourceforge.atunes.model.ITaskService;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

/**
 * Responsible of executing tasks. Keeps two thread pools: one of size 1 for
 * delayed or repeated tasks and another with dynamic thread number for the rest
 * of tasks
 * 
 * The reason to keep two pools is this bug:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7091003
 * 
 * @author alex
 * 
 */
public class TaskService implements ITaskService, IApplicationLifeCycleListener {

	/**
	 * Service used for scheduled or delayed tasks
	 */
	private ScheduledExecutorService scheduledService;

	/**
	 * Service used for non-scheduled nor delayed tasks
	 */
	private ThreadPoolExecutor poolService;

	/**
	 * Number of threads in scheduled thread pool
	 */
	private int scheduledThreadPoolSize;

	/**
	 * Suffix for thread names in scheduled thread pool
	 */
	private String scheduledThreadPoolSuffix;

	/**
	 * Thread pool core size
	 */
	private int threadPoolCoreSize;

	/**
	 * Maximum number of threads in thread pool
	 */
	private int threadPoolMaximumSize;

	/**
	 * Suffix for threads in thread pool
	 */
	private String threadPoolSuffix;

	/**
	 * @param threadPoolCoreSize
	 */
	public void setThreadPoolCoreSize(final int threadPoolCoreSize) {
		this.threadPoolCoreSize = threadPoolCoreSize;
	}

	/**
	 * @param threadPoolMaximumSize
	 */
	public void setThreadPoolMaximumSize(final int threadPoolMaximumSize) {
		this.threadPoolMaximumSize = threadPoolMaximumSize;
	}

	/**
	 * @param threadPoolSuffix
	 */
	public void setThreadPoolSuffix(final String threadPoolSuffix) {
		this.threadPoolSuffix = threadPoolSuffix;
	}

	/**
	 * @param scheduledThreadPoolSize
	 */
	public void setScheduledThreadPoolSize(final int scheduledThreadPoolSize) {
		this.scheduledThreadPoolSize = scheduledThreadPoolSize;
	}

	/**
	 * @param scheduledThreadPoolSuffix
	 */
	public void setScheduledThreadPoolSuffix(
			final String scheduledThreadPoolSuffix) {
		this.scheduledThreadPoolSuffix = scheduledThreadPoolSuffix;
	}

	@Override
	public Future<?> submitNow(final String name, final Runnable task) {
		return getPoolService().submit(createRunnable(name, task));
	}

	@Override
	public ScheduledFuture<?> submitOnce(final String name, final long delay,
			final Runnable task) {
		return getScheduledService().schedule(createRunnable(name, task),
				delay, TimeUnit.SECONDS);
	}

	@Override
	public ScheduledFuture<?> submitPeriodically(final String name,
			final long initialDelay, final long delay, final Runnable task) {
		return getScheduledService().scheduleWithFixedDelay(
				createRunnable(name, task), initialDelay, delay,
				TimeUnit.SECONDS);
	}

	@Override
	public void shutdownService() {
		if (this.scheduledService != null) {
			this.scheduledService.shutdown();
		}
		if (this.poolService != null) {
			this.poolService.shutdown();
		}
	}

	private ScheduledExecutorService getScheduledService() {
		if (this.scheduledService == null) {
			this.scheduledService = Executors.newScheduledThreadPool(
					this.scheduledThreadPoolSize,
					new CustomizableThreadFactory(
							this.scheduledThreadPoolSuffix));
		}
		return this.scheduledService;
	}

	private ThreadPoolExecutor getPoolService() {
		if (this.poolService == null) {
			this.poolService = new ThreadPoolExecutor(this.threadPoolCoreSize,
					this.threadPoolMaximumSize, 5, TimeUnit.SECONDS,
					new LinkedBlockingDeque<Runnable>(),
					new CustomizableThreadFactory(this.threadPoolSuffix));
		}
		return this.poolService;
	}

	private Runnable createRunnable(final String name, final Runnable task) {
		return new TaskServiceRunnable(name, task);
	}

	@Override
	public void allHandlersInitialized() {
	}

	@Override
	public void applicationFinish() {
		shutdownService();
	}

	@Override
	public void applicationStarted() {
	}

	@Override
	public void deferredInitialization() {
	}
}
