/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.misc.log;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * A custom logger for aTunes, using log4j.
 */
public class Logger {

    /** Categories to filter, i.e. will not be logged */
    private static List<String> filteredCategories;

    /**
     * Initialize logger
     */
    static {
        // Read filtered categories
        filteredCategories = new ArrayList<String>();
        try {
            PropertyResourceBundle bundle = new PropertyResourceBundle(Logger.class.getResourceAsStream(Constants.EXTENDED_LOG_FILE));
            String value = bundle.getString("log.filter");
            StringTokenizer st = new StringTokenizer(value, ",");
            while (st.hasMoreTokens()) {
                String v = st.nextToken().trim();
                filteredCategories.add(v);
            }

        } catch (Exception e) {
            System.out.println(StringUtils.getString(Constants.EXTENDED_LOG_FILE, " not found or incorrect. No filters will be applied to log"));
        }
    }

    /** Internal logger. */
    private org.apache.log4j.Logger logger;

    /**
     * Instantiates a new logger.
     */
    public Logger() {
        // Get invoker class and call Log4j getLogger
        Throwable t = new Throwable();
        logger = org.apache.log4j.Logger.getLogger(t.getStackTrace()[1].getClassName());
    }

    /**
     * Logs a debug event.
     * 
     * @param cat
     *            the cat
     */
    public void debug(String cat) {
        if (!Kernel.DEBUG) {
            return;
        }

        // If category is filtered, don't log anything
        if (filteredCategories.contains(cat.trim())) {
            return;
        }

        // Find calling method name and class
        Throwable t = new Throwable();
        StackTraceElement[] s = t.getStackTrace();
        String className = s[1].getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        String methodName = s[1].getMethodName();

        this.debug(cat, "--> ", className, '.', methodName, " [", LoggerTimer.getTimer(), "]");
    }

    /**
     * Logs a debug event.
     * 
     * @param cat
     *            the cat
     * @param objects
     *            the objects to show in log
     */
    public void debug(String cat, Object... objects) {
        if (!Kernel.DEBUG) {
            return;
        }

        if (filteredCategories.contains(cat.trim())) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[').append(cat).append("] ");
        for (Object object : objects) {
        	sb.append(object);
        }
        logger.debug(sb.toString());
    }

    /**
     * Logs a debug event. This method is specially designed to be called at the beginning of a method to log the execution of the method
     * 
     * @param cat
     *            the cat
     * @param args
     *            the args
     */
    public void debugMethodCall(String cat, String... args) {
        if (!Kernel.DEBUG) {
            return;
        }

        if (filteredCategories.contains(cat.trim())) {
            return;
        }

        // Find calling method name and class
        Throwable t = new Throwable();
        StackTraceElement[] s = t.getStackTrace();
        String className = s[1].getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        String methodName = s[1].getMethodName();

        // Build string with arguments
        StringBuilder sb = new StringBuilder();
        sb.append("--> ").append(className).append('.').append(methodName);
        if (args.length > 0) {
            sb.append('{');
            for (int i = 0; i < args.length - 1; i++) {
                sb.append(args[i]);
                sb.append(", ");
            }
            sb.append(args[args.length - 1]);
            sb.append('}');
        }

        // Get a timer count
        sb.append(" [").append(LoggerTimer.getTimer()).append(']');

        this.debug(cat, sb.toString());
    }

    /**
     * Logs an error event.
     * 
     * @param cat
     *            the cat
     * @param o
     *            the o
     */
    public void error(String cat, Object o) {
        // Find calling method name and class
        Throwable t = new Throwable();
        StackTraceElement[] s = t.getStackTrace();
        String className = s[1].getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        String methodName = s[1].getMethodName();

        long timer = LoggerTimer.getTimer();

        // Build string
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(cat).append("] ").append("--> ").append(className).append('.').append(methodName).append(" [").append(timer).append("] ").append(o);

        logger.error(sb.toString());

        if (o instanceof Throwable) {
            StackTraceElement[] trace = ((Throwable) o).getStackTrace();

            for (StackTraceElement element : trace) {
                error(cat, className, methodName, timer, element);
            }
        }
    }

    /**
     * Logs an error event.
     * 
     * @param cat
     *            the cat
     * @param className
     *            the class name
     * @param methodName
     *            the method name
     * @param timer
     *            the timer
     * @param o
     *            the o
     */
    private void error(String cat, String className, String methodName, long timer, StackTraceElement o) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(cat).append("] ").append("--> ").append(className).append('.').append(methodName).append(" [").append(timer).append("]\t ").append(o);

        logger.error(sb.toString());
    }

    /**
     * Logs an info event.
     * 
     * @param cat
     *            the cat
     * @param arg0
     *            the arg0
     */
    public void info(String cat, Object arg0) {
        if (filteredCategories.contains(cat.trim())) {
            return;
        }
        logger.info(StringUtils.getString("[", cat, "] ", arg0));
    }

    /**
     * Logs an internal error.
     * 
     * @param o
     *            the o
     */
    public void internalError(Object o) {
        error(LogCategories.INTERNAL_ERROR, o);
        if (o instanceof InvocationTargetException) {
            error(LogCategories.INTERNAL_ERROR, ((InvocationTargetException) o).getTargetException());
            error(LogCategories.INTERNAL_ERROR, ((InvocationTargetException) o).getTargetException().getCause());
        }
    }
}
