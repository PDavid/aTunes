/*
 * aTunes 2.2.0-SNAPSHOT
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

	/**
	 * Returns <code>true</code> if Windows (any version) is the current
	 * operating system.
	 * 
	 * @return If Windows is the current operating system
	 */
	public boolean isWindows() {
		return (this.equals(OperatingSystem.WINDOWS));
	}

	/**
	 * Returns <code>true</code> if Windows Vista is the current operating
	 * system.
	 * 
	 * @return If Windows Vista is the current operating system
	 */
	public boolean isWindowsVista() {
		return (this.equals(OperatingSystem.WINDOWS) && System
				.getProperty("os.name").toLowerCase().contains("vista"));
	}

	/**
	 * Returns <code>true</code> if Windows 7 is the current operating system.
	 * 
	 * @return If Windows 7 is the current operating system
	 */
	public boolean isWindows7() {
		return (this.equals(OperatingSystem.WINDOWS) && System
				.getProperty("os.name").toLowerCase().contains("7"));
	}

	/**
	 * Returns <code>true</code> if Windows is the current operating system, but
	 * is not Windows Vista.
	 * 
	 * @return If an old Windows version (XP,...) is the current operating
	 *         system
	 */
	public boolean isOldWindows() {
		return (this.equals(OperatingSystem.WINDOWS) && !System
				.getProperty("os.name").toLowerCase().contains("vista"));
	}

	/**
	 * Returns <code>true</code> if Linux is the current operating system.
	 * 
	 * @return If Linux is the current operating system
	 */
	public boolean isLinux() {
		return this.equals(OperatingSystem.LINUX);
	}

	/**
	 * Returns <code>true</code> if MacOsX is the current operating system.
	 * 
	 * @return If MacOsX is the current operating system
	 */
	public boolean isMacOsX() {
		return this.equals(OperatingSystem.MACOSX);
	}

	/**
	 * Returns <code>true</code> if Solaris is the current operating system.
	 * 
	 * @return If Solaris is the current operating system
	 */
	public boolean isSolaris() {
		return this.equals(OperatingSystem.SOLARIS);
	}

}
