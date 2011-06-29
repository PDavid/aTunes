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

package net.sourceforge.atunes.misc.log;

import java.lang.reflect.InvocationTargetException;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * A custom logger for aTunes, using log4j.
 */
public class Logger {

    /** Internal logger. */
    private static org.apache.log4j.Logger logger;

    /**
     * Initialize logger
     */
    static {
        logger = org.apache.log4j.Logger.getLogger(Logger.class);
    }

    private Logger() {}

    /**
     * Logs a debug event.
     * 
     * @param objects
     *            the objects to show in log
     */
    public static void debug(Object... objects) {
        if (!Kernel.isDebug()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Object object : objects) {
            sb.append(object);
        }
        logger.debug(sb.toString());
    }

    /**
     * Logs an error event.
     * 
     * @param o
     *            the o
     */
    public static void error(Object o) {
        // Find calling method name and class
        Throwable t = new Throwable();
        StackTraceElement[] s = t.getStackTrace();
        String className = s[1].getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        String methodName = s[1].getMethodName();

        long timer = LoggerTimer.getTimer();

        // Build string
        StringBuilder sb = new StringBuilder();
        sb.append("--> ").append(className).append('.').append(methodName).append(" [").append(timer).append("] ").append(o);

        logger.error(sb.toString());

        if (o instanceof Throwable) {
        	Throwable throwable = (Throwable) o;
            StackTraceElement[] trace = throwable.getStackTrace();

            for (StackTraceElement element : trace) {
                error(className, methodName, timer, element);
            }
            
            if (throwable.getCause() != null) {
            	error(StringUtils.getString(throwable.getCause().getClass().getName(), ": ", throwable.getCause().getMessage()));

                StackTraceElement[] causeTrace = throwable.getCause().getStackTrace();

                for (StackTraceElement element : causeTrace) {
                    error( className, methodName, timer, element);
                }
            }
            
            if (o instanceof InvocationTargetException && ((InvocationTargetException)o).getTargetException() != null) {
            	Throwable target = ((InvocationTargetException) o).getTargetException();
            	error(StringUtils.getString(target.getClass().getName(), ": ", target.getMessage()));

                StackTraceElement[] causeTrace = target.getStackTrace();

                for (StackTraceElement element : causeTrace) {
                    error(className, methodName, timer, element);
                }
            }
        }
    }

    /**
     * Logs an error event.
     * 
     * @param className
     *            the class name
     * @param methodName
     *            the method name
     * @param timer
     *            the timer
     * @param o
     *            the o
     */
    private static void error(String className, String methodName, long timer, StackTraceElement o) {
        StringBuilder sb = new StringBuilder();
        sb.append("--> ").append(className).append('.').append(methodName).append(" [").append(timer).append("]\t ").append(o);

        logger.error(sb.toString());
    }

    /**
     * Logs an info event.
     * 
     * @param objs
     *            the objects
     */
    public static void info(Object... objs) {
        StringBuilder sb = new StringBuilder();
        for (Object o : objs) {
        	sb.append(o.toString());
        }
        logger.info(sb.toString());
    }
}
