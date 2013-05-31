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

package net.sourceforge.atunes.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.PropertyResourceBundle;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IOSManager;

import org.apache.log4j.PropertyConfigurator;

/**
 * A custom logger for aTunes, using log4j.
 */
public final class Logger {

	/** Internal logger. */
	private static org.apache.log4j.Logger logger;

	private static boolean debug;

	/**
	 * sets debug level
	 * 
	 * @param debug
	 */
	public static void setDebug(final boolean debug) {
		Logger.debug = debug;
	}

	/**
	 * Initialize logger
	 */
	static {
		logger = org.apache.log4j.Logger.getLogger(Logger.class);
	}

	private Logger() {
	}

	/**
	 * Logs a debug event.
	 * 
	 * @param objects
	 *            the objects to show in log
	 */
	public static void debug(final Object... objects) {
		if (!debug) {
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
	 * 
	 * @param strings
	 */
	public static void error(final String... strings) {
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
	public static void error(final Object o) {
		// Find calling method name and class
		StackTraceElement s = new Throwable().getStackTrace()[1];
		String className = s.getClassName();
		className = className.substring(className.lastIndexOf('.') + 1);
		String methodName = s.getMethodName();

		logger.error(StringUtils.getString("--> ", className, ".", methodName,
				" [", "]\t ", o));

		if (o instanceof Throwable) {
			Throwable throwable = (Throwable) o;
			StackTraceElement[] trace = throwable.getStackTrace();

			error(throwable.getMessage());

			for (StackTraceElement element : trace) {
				error(className, methodName, element);
			}

			if (throwable.getCause() != null) {
				error(StringUtils.getString(throwable.getCause().getClass()
						.getName(), ": ", throwable.getCause().getMessage()));

				StackTraceElement[] causeTrace = throwable.getCause()
						.getStackTrace();

				for (StackTraceElement element : causeTrace) {
					error(className, methodName, element);
				}
			}

			if (o instanceof InvocationTargetException
					&& ((InvocationTargetException) o).getTargetException() != null) {
				Throwable target = ((InvocationTargetException) o)
						.getTargetException();
				error(StringUtils.getString(target.getClass().getName(), ": ",
						target.getMessage()));

				StackTraceElement[] causeTrace = target.getStackTrace();

				for (StackTraceElement element : causeTrace) {
					error(className, methodName, element);
				}
			}
		}
	}

	/**
	 * Logs a fatal exception
	 * 
	 * @param t
	 */
	public static void fatal(final Throwable t) {
		logger.fatal(t);
	}

	/**
	 * Logs an error event.
	 * 
	 * @param className
	 * @param methodName
	 * @param o
	 */
	private static void error(final String className, final String methodName,
			final StackTraceElement o) {
		logger.error(StringUtils.getString("--> ", className, ".", methodName,
				" [", "]\t ", o));
	}

	/**
	 * Logs an info event.
	 * 
	 * @param objs
	 *            the objects
	 */
	public static void info(final Object... objs) {
		StringBuilder sb = new StringBuilder();
		for (Object o : objs) {
			sb.append(o != null ? o.toString() : "null");
		}
		logger.info(sb.toString());
	}

	/**
	 * Set log4j properties from file, and changes properties if debug mode or
	 * debug level
	 * 
	 * @param debug
	 * @param debugLevel
	 * @param osManager
	 */
	public static void loadProperties(final boolean debug,
			final boolean debugLevel, final IOSManager osManager) {
		PropertyResourceBundle bundle = null;
		InputStream log4jProperties = Logger.class
				.getResourceAsStream(Constants.LOG4J_FILE);
		Properties props = new Properties();
		if (log4jProperties != null) {
			try {
				bundle = new PropertyResourceBundle(log4jProperties);
			} catch (IOException e) {
				System.out
						.println("ERROR trying to read logger configuration: ");
				e.printStackTrace();
			}
			if (bundle != null) {
				changeProperties(debug, debugLevel, osManager, bundle, props);
			}
		} else {
			// Load default debug log4j properties
			props.put("log4j.rootLogger", "DEBUG, A");
			props.put("log4j.appender.A", "org.apache.log4j.ConsoleAppender");
			props.put("log4j.appender.A.layout",
					"org.apache.log4j.PatternLayout");
			props.put("log4j.appender.A.layout.ConversionPattern", "%-7p %m%n");
		}
		PropertyConfigurator.configure(props);
		Logger.setDebug(debug || debugLevel);
	}

	/**
	 * Set log4j start properties
	 * 
	 * @param debug
	 */
	public static void loadInitProperties(final boolean debug) {
		Properties props = new Properties();
		// Load default debug log4j properties
		props.put("log4j.logger.org.springframework", "WARN");
		props.put("log4j.rootLogger", debug ? "DEBUG, A" : "INFO, A");
		props.put("log4j.appender.A", "org.apache.log4j.ConsoleAppender");
		props.put("log4j.appender.A.layout", "org.apache.log4j.PatternLayout");
		props.put("log4j.appender.A.layout.ConversionPattern", "%-7p %m%n");
		PropertyConfigurator.configure(props);
	}

	private static void changeProperties(final boolean debug,
			final boolean debugLevel, final IOSManager osManager,
			final PropertyResourceBundle bundle, final Properties props) {
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = bundle.getString(key);

			// Change to DEBUG MODE if debug
			if (key.equals("log4j.rootLogger") && (debug || debugLevel)) {
				value = value.replace("INFO", "DEBUG");
			}

			if (key.equals("log4j.appender.A2.file") && !debug) {
				value = StringUtils.getString(osManager.getUserConfigFolder(),
						osManager.getFileSeparator(), "aTunes.log");
			}

			props.setProperty(key, value);
		}
	}
}
