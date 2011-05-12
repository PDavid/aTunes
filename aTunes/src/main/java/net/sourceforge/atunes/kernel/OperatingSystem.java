/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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


public enum OperatingSystem {
	
    WINDOWS, LINUX, MACOSX, SOLARIS;

    /**
     * Returns <code>true</code> if Windows Vista is the current operating
     * system.
     * 
     * @return If Windows Vista is the current operating system
     */
    public boolean isWindowsVista() {
        return (this.equals(OperatingSystem.WINDOWS) && System.getProperty("os.name").toLowerCase().contains("vista"));
    }

    /**
     * Returns <code>true</code> if Windows 7 is the current operating
     * system.
     * 
     * @return If Windows 7 is the current operating system
     */
    public boolean isWindows7() {
        return (this.equals(OperatingSystem.WINDOWS) && System.getProperty("os.name").toLowerCase().contains("7"));
    }

    /**
     * Returns <code>true</code> if Windows is the current operating system,
     * but is not Windows Vista.
     * 
     * @return If an old Windows version (XP,...) is the current operating
     *         system
     */
    public boolean isOldWindows() {
        return (this.equals(OperatingSystem.WINDOWS) && !System.getProperty("os.name").toLowerCase().contains("vista"));
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

    /**
     * Returns <code>true</code> if the current operating system (actually
     * the VM) is 64 bit.
     * 
     * @return If the current operating system is 64 bit
     */
    public boolean is64Bit() {
        return System.getProperty("os.arch").contains("64");
    }

}
