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

import net.sourceforge.atunes.model.OperatingSystem;

/**
 * Detects in which operating system is running
 * @author alex
 *
 */
public final class OperatingSystemDetector {

	private OperatingSystemDetector() {}
	
    /**
     * Detect OS.
     * 
     * @return The detected OS
     */
    public static OperatingSystem getOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return OperatingSystem.WINDOWS;
        } else if (osName.contains("mac os x")) {
            return OperatingSystem.MACOSX;
        } else if (osName.contains("sunos")) {
            return OperatingSystem.SOLARIS;
        }
        return OperatingSystem.LINUX;
    }
}
