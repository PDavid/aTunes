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

package net.sourceforge.atunes.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.PropertyResourceBundle;

import org.apache.log4j.PropertyConfigurator;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.model.IOSManager;

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
        for (Object o : objects) {
        	sb.append(o != null ? o.toString() : "null");
        }
        logger.debug(sb.toString());
    }

    /**
     * Logs an error event
     * @param strings
     */
    public static void error(String...strings) {
    	StringBuilder sb = new StringBuilder();
    	for (String s : strings) {
    		sb.append(s);
    	}
    	error(sb.toString());
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

        logger.error(StringUtils.getString("--> ", className, ".", methodName, " [", "]\t ", o));

        if (o instanceof Throwable) {
        	Throwable throwable = (Throwable) o;
            StackTraceElement[] trace = throwable.getStackTrace();

            for (StackTraceElement element : trace) {
                error(className, methodName, element);
            }
            
            if (throwable.getCause() != null) {
            	error(StringUtils.getString(throwable.getCause().getClass().getName(), ": ", throwable.getCause().getMessage()));

                StackTraceElement[] causeTrace = throwable.getCause().getStackTrace();

                for (StackTraceElement element : causeTrace) {
                    error( className, methodName, element);
                }
            }
            
            if (o instanceof InvocationTargetException && ((InvocationTargetException)o).getTargetException() != null) {
            	Throwable target = ((InvocationTargetException) o).getTargetException();
            	error(StringUtils.getString(target.getClass().getName(), ": ", target.getMessage()));

                StackTraceElement[] causeTrace = target.getStackTrace();

                for (StackTraceElement element : causeTrace) {
                    error(className, methodName, element);
                }
            }
        }
    }

    /**
     * Logs an error event.
     * 
     * @param className
     * @param methodName
     * @param o
     */
    private static void error(String className, String methodName, StackTraceElement o) {
    	logger.error(StringUtils.getString("--> ", className, ".", methodName, " [", "]\t ", o));
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
       		sb.append(o != null ? o.toString() : "null");
        }
        logger.info(sb.toString());
    }
    
    /**
     * Set log4j properties from file, and changes properties if debug mode.
     * 
     * @param debug
     *            the debug
     */
    public static void loadProperties(boolean debug, IOSManager osManager) {
    	PropertyResourceBundle bundle = null;
    	InputStream log4jProperties = Logger.class.getResourceAsStream(Constants.LOG4J_FILE);
    	Properties props = new Properties();
    	if (log4jProperties != null) {
    		try {
    			bundle = new PropertyResourceBundle(log4jProperties);
    		} catch (IOException e) {
    			System.out.println("ERROR trying to read logger configuration: ");
    			e.printStackTrace();
    		}
    		if (bundle != null) {
    			Enumeration<String> keys = bundle.getKeys();
    			while (keys.hasMoreElements()) {
    				String key = keys.nextElement();
    				String value = bundle.getString(key);

    				// Change to DEBUG MODE if debug
    				if (key.equals("log4j.rootLogger") && debug) {
    					value = value.replace("INFO", "DEBUG");
    				} else if (key.equals("log4j.appender.A2.file")) {
    					value = StringUtils.getString(osManager.getUserConfigFolder(debug), osManager.getFileSeparator(), "aTunes.log");
    				}

    				props.setProperty(key, value);
    			}
    		}
    	} else {
    		// Load default debug log4j properties
    		props.put("log4j.rootLogger", "DEBUG, A");
    		props.put("log4j.appender.A", "org.apache.log4j.ConsoleAppender");
    		props.put("log4j.appender.A.layout", "org.apache.log4j.PatternLayout");
    		props.put("log4j.appender.A.layout.ConversionPattern", "%-7p %m%n");
    	}
		PropertyConfigurator.configure(props);
    }
}
