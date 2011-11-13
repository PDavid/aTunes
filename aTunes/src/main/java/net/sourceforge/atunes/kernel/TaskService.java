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

package net.sourceforge.atunes.kernel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.Logger;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class TaskService implements ITaskService {

	/**
	 * A task service runnable
	 * @author alex
	 *
	 */
	private static final class TaskServiceRunnable implements Runnable {
		
		private final String name;
		private final Runnable task;

		private TaskServiceRunnable(String name, Runnable task) {
			this.name = name;
			this.task = task;
		}

		@Override
		public void run() {
			Logger.debug("Started task: ", name);
			task.run();
			Logger.debug("Finished task: ", name);
		}
	}

	/**
	 * Service used
	 */
	private ScheduledExecutorService service;

	/**
	 * Number of threads in pool
	 */
	private int poolSize;
	
	/**
	 * Suffix for thread names
	 */
	private String threadSuffix;
	
	public void setThreadSuffix(String threadSuffix) {
		this.threadSuffix = threadSuffix;
	}
	
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	
	@Override
	public ScheduledFuture<?> submitNow(String name, Runnable task) {
		return getService().schedule(createRunnable(name, task), 0, TimeUnit.SECONDS);
	}
	
	@Override
	public ScheduledFuture<?> submitOnce(String name, long delay, Runnable task) {
		return getService().schedule(createRunnable(name, task), delay, TimeUnit.SECONDS);
	}

	@Override
	public ScheduledFuture<?> submitPeriodically(String name, long initialDelay, long delay, Runnable task) {
		return getService().scheduleWithFixedDelay(createRunnable(name, task), initialDelay,  delay, TimeUnit.SECONDS);
	}
	
	@Override
	public void shutdownService() {
		if (service != null) {
			service.shutdown();
		}
	}

	private ScheduledExecutorService getService() {
		if (service == null) {
			service = Executors.newScheduledThreadPool(poolSize, new CustomizableThreadFactory(threadSuffix));
		}
		return service;
	}
	
	private Runnable createRunnable(final String name, final Runnable task) {
		return new TaskServiceRunnable(name, task);
	}
}
