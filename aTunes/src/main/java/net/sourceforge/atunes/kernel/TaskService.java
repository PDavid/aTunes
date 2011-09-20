/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.ITaskService;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class TaskService implements ITaskService {

	private ExecutorService singleService;
	
	private ScheduledExecutorService periodicService;
	
	private ExecutorService getSingleService() {
		if (singleService == null) {
			singleService = Executors.newSingleThreadScheduledExecutor(new CustomizableThreadFactory("SingleTaskService"));
		}
		return singleService;
	}
	
	private ScheduledExecutorService getPeriodicService() {
		if (periodicService == null) {
			periodicService = Executors.newSingleThreadScheduledExecutor(new CustomizableThreadFactory("PeriodicTaskService"));
		}
		return periodicService;
	}
	
	@Override
	public Future<?> submitOnce(String name, Runnable task) {
		Logger.debug("Submitted task to run once: ", name);
		return getSingleService().submit(task);
	}

	@Override
	public ScheduledFuture<?> submitPeriodically(String name, Runnable task, long seconds) {
		Logger.debug("Submitted task to run every ", seconds, " seconds: ", name);
		return getPeriodicService().schedule(task, seconds, TimeUnit.SECONDS);
	}
	
	@Override
	public void shutdownService() {
		if (singleService != null) {
			singleService.shutdown();
		}
		if (periodicService != null) {
			periodicService.shutdown();
		}
	}

}
