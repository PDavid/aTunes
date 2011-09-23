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

package net.sourceforge.atunes.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The system properties.
 */
public final class SystemProperties {

    /** Java 6 Update 10 or later? (but not Java 7 yet). */
    public static final boolean IS_JAVA_6_UPDATE_10_OR_LATER = isJava6Update10OrLater();

    private SystemProperties() {
    }

    /**
     * Checks if java 6 update 10 or later is used.
     * 
     * @return true, if java 6 update 10 or later is used
     */
    private static boolean isJava6Update10OrLater() {
        String versionString = System.getProperty("java.version");
        Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)_(\\d+).*");
        Matcher matcher = pattern.matcher(versionString);
        if (matcher.find()) {
            try {
                int v1 = Integer.parseInt(matcher.group(1));
                int v2 = Integer.parseInt(matcher.group(2));
                int v3 = Integer.parseInt(matcher.group(3));
                int update = Integer.parseInt(matcher.group(4));
                return v1 == 1 && v2 == 6 && v3 >= 0 && update >= 10;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
}
