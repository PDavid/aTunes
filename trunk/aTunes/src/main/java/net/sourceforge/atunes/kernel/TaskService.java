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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
public class TaskService implements ITaskService {

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
	public void setThreadPoolCoreSize(int threadPoolCoreSize) {
		this.threadPoolCoreSize = threadPoolCoreSize;
	}

	/**
	 * @param threadPoolMaximumSize
	 */
	public void setThreadPoolMaximumSize(int threadPoolMaximumSize) {
		this.threadPoolMaximumSize = threadPoolMaximumSize;
	}

	/**
	 * @param threadPoolSuffix
	 */
	public void setThreadPoolSuffix(String threadPoolSuffix) {
		this.threadPoolSuffix = threadPoolSuffix;
	}

	/**
	 * @param scheduledThreadPoolSize
	 */
	public void setScheduledThreadPoolSize(int scheduledThreadPoolSize) {
		this.scheduledThreadPoolSize = scheduledThreadPoolSize;
	}

	/**
	 * @param scheduledThreadPoolSuffix
	 */
	public void setScheduledThreadPoolSuffix(String scheduledThreadPoolSuffix) {
		this.scheduledThreadPoolSuffix = scheduledThreadPoolSuffix;
	}

	@Override
	public Future<?> submitNow(String name, Runnable task) {
		return getPoolService().submit(createRunnable(name, task));
	}

	@Override
	public ScheduledFuture<?> submitOnce(String name, long delay, Runnable task) {
		return getScheduledService().schedule(createRunnable(name, task),
				delay, TimeUnit.SECONDS);
	}

	@Override
	public ScheduledFuture<?> submitPeriodically(String name,
			long initialDelay, long delay, Runnable task) {
		return getScheduledService().scheduleWithFixedDelay(
				createRunnable(name, task), initialDelay, delay,
				TimeUnit.SECONDS);
	}

	@Override
	public void shutdownService() {
		if (scheduledService != null) {
			scheduledService.shutdown();
		}
		if (poolService != null) {
			poolService.shutdown();
		}
	}

	private ScheduledExecutorService getScheduledService() {
		if (scheduledService == null) {
			scheduledService = Executors.newScheduledThreadPool(
					scheduledThreadPoolSize, new CustomizableThreadFactory(
							scheduledThreadPoolSuffix));
		}
		return scheduledService;
	}

	private ThreadPoolExecutor getPoolService() {
		if (poolService == null) {
			poolService = new ThreadPoolExecutor(threadPoolCoreSize,
					threadPoolMaximumSize, 5, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>(),
					new CustomizableThreadFactory(threadPoolSuffix));
		}
		return poolService;
	}

	private Runnable createRunnable(final String name, final Runnable task) {
		return new TaskServiceRunnable(name, task);
	}
}
