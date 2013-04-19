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

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

/**
 * A service to run background tasks
 * 
 * @author alex
 * 
 */
public interface ITaskService {

	/**
	 * Submits a task to run as soon as possible
	 * 
	 * @param name
	 * @param task
	 * @return
	 */
	public Future<?> submitNow(String name, Runnable task);

	/**
	 * Submits a task to run once after given delay in seconds
	 * 
	 * @param name
	 * @param delay
	 * @param task
	 * @return
	 */
	public ScheduledFuture<?> submitOnce(String name, long delay, Runnable task);

	/**
	 * Submits a task to run periodically with a given delay between end and
	 * start and after an initial delay. All delays must be in seconds
	 * 
	 * @param name
	 * @param task
	 * @param initialDelay
	 * @param delay
	 * @return
	 */
	public ScheduledFuture<?> submitPeriodically(String name,
			long initialDelay, long delay, Runnable task);

	/**
	 * Called to finish tasks
	 */
	public void shutdownService();
}
