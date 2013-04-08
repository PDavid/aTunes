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

package net.sourceforge.atunes.model;

/**
 * Type of operating system
 * 
 * @author alex
 * 
 */
public enum OperatingSystem {

	/**
	 * All windows family
	 */
	WINDOWS,

	/**
	 * All linux family
	 */
	LINUX,

	/**
	 * Max OS X
	 */
	MACOSX,

	/**
	 * Solaris
	 */
	SOLARIS;

	// METHODS USED FOR SPRING INJECTION
	/**
	 * @return Windows
	 */
	public static OperatingSystem getWindows() {
		return WINDOWS;
	}

	/**
	 * @return Linux
	 */
	public static OperatingSystem getLinux() {
		return LINUX;
	}

	/**
	 * @return Mac OS X
	 */
	public static OperatingSystem getMacOSX() {
		return MACOSX;
	}

	/**
	 * @return Solaris
	 */
	public static OperatingSystem getSolaris() {
		return SOLARIS;
	}
}
