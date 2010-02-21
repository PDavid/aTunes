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
package net.sourceforge.atunes.misc;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The system properties.
 */
public final class SystemProperties {

    /** Path to config folder as passed as argument to app. */
    private static String customConfigFolder = null;

    /** Path to repository config folder as passed as argument to app. */
    private static String customRepositoryConfigFolder = null;

    /**
     * Operating Systems Enum.
     */
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

        /**
         * Returns a string with command to launch application This method is
         * used when restarting app
         * 
         * @return
         */
        public String getLaunchCommand() {
            if (isLinux() || isSolaris()) {
                return new File(StringUtils.getString("./", Constants.COMMAND_LINUX_SOLARIS)).getAbsolutePath();
            } else if (isMacOsX()) {
                return new File(StringUtils.getString("./", Constants.COMMAND_MACOSX)).getAbsolutePath();
            } else {
                return new File(StringUtils.getString("./", Constants.COMMAND_WINDOWS)).getAbsolutePath();
            }
        }
    }

    /** User home dir. */
    public static final String USER_HOME = System.getProperty("user.home");

    /** OS dependent file separator. */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /** OS dependent line terminator. */
    public static final String LINE_TERMINATOR = getSystemLineTerminator();

    /** Operating System. */
    public static final OperatingSystem OS = detectOperatingSystem();

    /** Java 6 Update 10 or later? (but not Java 7 yet). */
    public static final boolean IS_JAVA_6_UPDATE_10_OR_LATER = isJava6Update10OrLater();

    private SystemProperties() {
    }

    /**
     * Detect OS.
     * 
     * @return The detected OS
     */
    private static OperatingSystem detectOperatingSystem() {
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

    /**
     * Gets the system line terminator.
     * 
     * @return the system line terminator
     */
    private static String getSystemLineTerminator() {
        if (OS == OperatingSystem.WINDOWS) {
            return "\r\n";
        }
        return "\n";
    }

    /**
     * Gets folder where state is stored. If not exists, it's created
     * 
     * @param useWorkDir
     *            If the current working directory should be used
     * 
     * @return The folder where the state is stored
     */
    public static String getUserConfigFolder(boolean useWorkDir) {
        if (useWorkDir) {
            return "./debug";
        }
        if (customConfigFolder != null) {
            return customConfigFolder;
        }
        String userHomePath = SystemProperties.USER_HOME;
        if (userHomePath != null) {
            File userConfigFolder = new File(StringUtils.getString(userHomePath, "/.aTunes"));
            if (!userConfigFolder.exists()) {
                if (!userConfigFolder.mkdir()) {
                    return ".";
                }
            }
            return userConfigFolder.getAbsolutePath();
        }
        return ".";
    }

    /**
     * Returns file from the user config folder.
     * 
     * @param name
     *            The name of the file (Example: aTunes.log or folder/file.abc)
     * @param useWorkDir
     *            If the current working directory should be used
     * 
     * @return The file from the user config folder
     */
    public static File getFileFromUserConfigFolder(String name, boolean useWorkDir) {
        String userConfigFolder = getUserConfigFolder(useWorkDir);
        if (userConfigFolder.equals(".")) {
            return new File(name);
        }
        return new File(userConfigFolder + "/" + name);
    }

    /**
     * Sets the custom config folder.
     * 
     * @param customConfigFolder
     *            the customConfigFolder to set
     */
    public static void setCustomConfigFolder(String customConfigFolder) {
        SystemProperties.customConfigFolder = customConfigFolder;
    }

    /**
     * Sets the custom repository config folder.
     * 
     * @param customRepositoryConfigFolder
     *            the customRepositoryConfigFolder to set
     */
    public static void setCustomRepositoryConfigFolder(String customRepositoryConfigFolder) {
        SystemProperties.customRepositoryConfigFolder = customRepositoryConfigFolder;
    }

    /**
     * Return path to temp folder, which is inside user config folder.
     * 
     * @return the temp folder
     */
    public static String getTempFolder() {
        String userConfigFolder = getUserConfigFolder(Kernel.isDebug());
        String tempFolder = StringUtils.getString(userConfigFolder, FILE_SEPARATOR, Constants.TEMP_DIR);
        File tempFile = new File(tempFolder);
        if (!tempFile.exists()) {
            if (!tempFile.mkdir()) {
                return userConfigFolder;
            }
        }
        return tempFolder;
    }

    /**
     * Returns the current working directory
     * 
     * @return
     */
    public static String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    /**
     * @return the customRepositoryConfigFolder
     */
    public static String getCustomRepositoryConfigFolder() {
        return customRepositoryConfigFolder;
    }

}
