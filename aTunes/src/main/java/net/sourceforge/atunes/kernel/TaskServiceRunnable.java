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

import net.sourceforge.atunes.utils.Logger;

/**
 * A task service runnable
 * @author alex
 *
 */
final class TaskServiceRunnable implements Runnable {
	
	private final String name;
	private final Runnable task;

	TaskServiceRunnable(String name, Runnable task) {
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