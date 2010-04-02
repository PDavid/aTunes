/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.util.Enumeration;
import java.util.Properties;
import java.util.PropertyResourceBundle;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.misc.SystemProperties;
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
        PropertyResourceBundle bundle;
        try {
            bundle = new PropertyResourceBundle(Log4jPropertiesLoader.class.getResourceAsStream(Constants.LOG4J_FILE));
            Enumeration<String> keys = bundle.getKeys();
            Properties props = new Properties();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = bundle.getString(key);

                // Change to DEBUG MODE if debug
                if (key.equals("log4j.rootLogger") && debug) {
                    value = value.replace("INFO", "DEBUG");
                } else if (key.equals("log4j.appender.A2.file")) {
                    value = StringUtils.getString(SystemProperties.getUserConfigFolder(debug), SystemProperties.FILE_SEPARATOR, "aTunes.log");
                }

                props.setProperty(key, value);
            }
            PropertyConfigurator.configure(props);
        } catch (Exception e) {
            System.out.println("ERROR trying to read logger configuration");
        }
    }
}
