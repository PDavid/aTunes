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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.PropertyResourceBundle;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.log4j.PropertyConfigurator;

/**
 * The Class Log4jPropertiesLoader.
 */
public final class Log4jPropertiesLoader {

    private Log4jPropertiesLoader() {

    }

    /**
     * Set log4j properties from file, and changes properties if debug mode.
     * 
     * @param debug
     *            the debug
     */
    public static void loadProperties(boolean debug) {
    	PropertyResourceBundle bundle = null;
    	InputStream log4jProperties = Log4jPropertiesLoader.class.getResourceAsStream(Constants.LOG4J_FILE);
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
    					value = StringUtils.getString(OsManager.getUserConfigFolder(debug), OsManager.getFileSeparator(), "aTunes.log");
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
