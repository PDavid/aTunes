/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.model;

/**
 * A task to be performed in background while starting handlers
 * @author alex
 *
 */
public interface IHandlerBackgroundInitializationTask {
	
    /**
     * Returns a task to be executed in background while continue starting application
     * @return runnable task
     */
    Runnable getInitializationTask();
    
    /**
     * Task to do after initialization task completed
     * @return runnable task
     */
    Runnable getInitializationCompletedTask();

}
