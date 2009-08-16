/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class Scheduler.
 */
public class Scheduler {

    /** Logger. */
    protected static Logger logger = new Logger();

    /**
     * Schedules a task to be executed after X seconds.
     * 
     * @param taskName
     *            the task name
     * @param task
     *            the task
     * @param seconds
     *            the seconds
     */
    public static void scheduleTaskAfter(final String taskName, final Runnable task, final int seconds) {
        Thread taskExecutor = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(seconds * 1000l);

                    logger.debug(LogCategories.SCHEDULER, StringUtils.getString("Running task ", taskName));
                    task.run();
                    logger.debug(LogCategories.SCHEDULER, StringUtils.getString("Task ", taskName, " ended"));

                } catch (InterruptedException e) {
                    logger.internalError(e);
                }
            }
        };
        taskExecutor.start();
    }

    /**
     * Executes different tasks in parallel.
     * 
     * @param wait
     *            the wait
     * @param runnables
     *            the runnables
     */
    public static void runInParallel(boolean wait, Runnable... runnables) {
        try {
            List<Thread> threads = new ArrayList<Thread>();
            for (Runnable r : runnables) {
                Thread t = new Thread(r);
                threads.add(t);
            }

            // Execute threads
            for (Thread t : threads) {
                t.start();
            }

            // Wait
            if (wait) {
                for (Thread t : threads) {
                    t.join();
                }
            }
        } catch (Exception e) {
            logger.error(LogCategories.SCHEDULER, e);
        }
    }
}
