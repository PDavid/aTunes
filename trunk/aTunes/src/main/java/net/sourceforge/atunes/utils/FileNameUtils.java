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
package net.sourceforge.atunes.utils;

import net.sourceforge.atunes.kernel.modules.pattern.AbstractPattern;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.WString;

/**
 * <p>
 * Utility methods for replacing illegal characters in filenames
 * </p>
 * 
 * <p>
 * The getValidFileName method is designed to handle filenames only, so do not
 * pass the whole path. For folders, use the getValidFolderName function, but do
 * not pass the filename with the folder name. Folder name can include full
 * path, except as said the filename. We are as permissive as possible, which
 * means this can cause interoperability problems (songs ripped on a Unix-like
 * system then played on a Windows OS for example). Further, we do not check
 * what file system is used, which can cause problems when writing to a
 * FAT/FAT32 partition for example. We do not yet check the filename length (255
 * characters max for all OS).
 * </p>
 * 
 * <p>
 * The characters in the list are probably incomplete and users might want other
 * substitutions so please change and complete accordingly.
 * </p>
 */
public final class FileNameUtils {

    private static final Logger logger = new Logger();
    private static final int CHAR_BYTE_WIDTH = 2;

    private static Kernel32 kernel32;

    private FileNameUtils() {
    }

    static {
        try {
            Native.setProtected(true);
            kernel32 = (Kernel32) Native.loadLibrary("Kernel32", Kernel32.class);
        } catch (UnsatisfiedLinkError e) {
            logger.debug(LogCategories.NATIVE, "kernel32 not found");
        }
    }

    public interface Kernel32 extends Library {

        /*
         * http://msdn2.microsoft.com/en-us/library/aa364989(VS.85).aspx
         */
        /**
         * <p>
         * Unicode (wchar_t*) version of GetShortPathName()
         * </p>
         * <code>
         * DWORD WINAPI GetShortPathNameW( __in LPCTSTR lpszLongPath,
         * __out LPTSTR lpdzShortPath,
         * __in DWORD cchBuffer );
         * </code>.
         * 
         * @param inPath
         *            the in path
         * @param outPathBuffer
         *            the out path buffer
         * @param outPathBufferSize
         *            the out path buffer size
         * 
         * @return the int
         */
        public int GetShortPathNameW(WString inPath, Memory outPathBuffer, int outPathBufferSize);

    }

    /**
     * Checks for valid filenames. Only pass the filename without path! The
     * filename is not checked for maximum filename length of 255 characters
     * (including path!). To check for valid folder names please use
     * getValidFolderName(String folderName)
     * 
     * @param fileName
     *            The filename to be checked. Please make sure to check for
     *            escape sequences (\, $) and add a \ before them before calling
     *            this method.
     * 
     * @return Returns the filename with known illegal characters substituted.
     */
    public static String getValidFileName(String fileName) {
        return getValidFileName(fileName, false);
    }

    /**
     * Checks for valid filenames. Only pass the filename without path! The
     * filename is not checked for maximum filename lenght of 255 characters
     * (including path!). To check for valid folder names please use
     * getValidFolderName(String folderName)
     * 
     * @param fileNameStr
     *            The filename to be checked. Please make sure to check for
     *            escape sequences (\, $) and add a \ before them before calling
     *            this method.
     * @param isMp3Device
     *            if valid file name for Mp3 device (->FAT/FAT32)
     * 
     * @return Returns the filename with known illegal characters substituted.
     */
    public static String getValidFileName(String fileNameStr, boolean isMp3Device) {
        // First call generic function
        String fileName = getValidName(fileNameStr, isMp3Device);

        // Most OS do not like a slash, so replace it by default.
        fileName = fileName.replaceAll("/", "-");
        // Do the same with double quotes...
        fileName = fileName.replaceAll("\"", "'");
        // ...question mark ?
        fileName = fileName.replaceAll("\\?", "");
        // ... asterisk
        fileName = fileName.replaceAll("\\*", "");
        // ... colon
        fileName = fileName.replaceAll(":", "");

        // This list is probably incomplete. Windows is quite picky.
        if (SystemProperties.OS == OperatingSystem.WINDOWS || isMp3Device) {
            fileName = fileName.replace("\\", "-");
        }
        return fileName;
    }

    /**
     * Checks for valid folder names. Do pass the path WITHOUT the filename. The
     * folder name is not checked for maximum length of 255 characters
     * (including filename!). To check for valid filenames please use
     * getValidFileName(String fileName)
     * 
     * @param folderName
     *            The folder name to be checked.
     * 
     * @return Returns the path name with known illegal characters substituted.
     */
    public static String getValidFolderName(String folderName) {
        return getValidFolderName(folderName, false);
    }

    /**
     * Checks for valid folder names. Do pass the path WITHOUT the filename. The
     * folder name is not checked for maximum length of 255 characters
     * (including filename!). To check for valid filenames please use
     * getValidFileName(String fileName)
     * 
     * @param folderNameStr
     *            The folder name to be checked.
     * @param isMp3Device
     *            if valid folder name for Mp3 device (->FAT/FAT32)
     * 
     * @return Returns the path name with known illegal characters substituted.
     */
    public static String getValidFolderName(String folderNameStr, boolean isMp3Device) {
        // First call generic function
        String folderName = getValidName(folderNameStr, isMp3Device);

        // This list is probably incomplete. Windows is quite picky.
        if (SystemProperties.OS == OperatingSystem.WINDOWS || isMp3Device) {
            folderName = folderName.replace("\\.", "\\_");
            if (SystemProperties.OS == OperatingSystem.WINDOWS) {
                folderName = folderName + "\\";
            }
            folderName = folderName.replace(".\\", "_\\");
            if (SystemProperties.OS == OperatingSystem.WINDOWS) {
                folderName = folderName.replace("/", "-");
            }
        }

        return folderName;
    }

    /*
     * Generic method that does the substitution for folder- and filenames. Do
     * not call directly but call either getValidFileName or getValidFolderName
     * to verify file/folder names
     */
    /**
     * Gets the valid name.
     * 
     * @param fileNameStr
     *            the file name
     * @param isMp3Device
     *            the is mp3 device
     * 
     * @return the valid name
     */
    private static String getValidName(String fileNameStr, boolean isMp3Device) {
        /*
         * This list is probably incomplete. Windows is quite picky. We do not
         * check for maximum length.
         */
        String fileName = fileNameStr;
        if (SystemProperties.OS == OperatingSystem.WINDOWS || isMp3Device) {
            fileName = fileName.replace("\"", "'");
            fileName = fileName.replace("?", "_");
            // Replace all ":" except at the drive letter
            if (fileName.length() > 2) {
                fileName = fileName.substring(0, 2) + fileName.substring(2).replace(":", "-");
            }
            fileName = fileName.replace("<", "-");
            fileName = fileName.replace(">", "-");
            fileName = fileName.replace("|", "-");
            fileName = fileName.replace("*", "-");
        }

        // Unconfirmed, as no Mac available for testing.
        if (SystemProperties.OS == OperatingSystem.MACOSX) {
            fileName = fileName.replace("|", "-");
        }
        return fileName;
    }

    /**
     * Prepares the filename in order to write it.
     * 
     * @param pattern
     *            Filename pattern
     * @param song
     *            Song file to be written
     * 
     * @return Returns a (hopefully) valid filename
     */
    public static String getNewFileName(String pattern, AudioFile song) {
        String result = AbstractPattern.applyPatternTransformations(pattern, song);
        // We need to place \\ before escape sequences otherwise the ripper hangs. We can not do this later.
        result = result.replace("\\", "\\\\").replace("$", "\\$");
        result = StringUtils.getString(result, song.getFile().getName().substring(song.getFile().getName().lastIndexOf('.')));
        result = getValidFileName(result);
        return result;
    }

    /**
     * Prepares the folder path in order to write it.
     * 
     * @param pattern
     *            Folder path pattern
     * @param song
     *            Song file to be written
     * 
     * @return Returns a (hopefully) valid filename
     */
    public static String getNewFolderPath(String pattern, AudioFile song) {
        String result = AbstractPattern.applyPatternTransformations(pattern, song);
        // We need to place \\ before escape sequences otherwise the ripper hangs. We can not do this later.
        result = result.replace("\\", "\\\\").replace("$", "\\$");
        result = getValidName(result, false);
        return result;
    }

    /*
     * Thanks to Paul Loy from the JNA mailing list ->
     * https://jna.dev.java.net/servlets/ReadMsg?list=users&msgNo=928
     * 
     * Requires: JNA https://jna.dev.java.net/#getting_started
     */
    /**
     * Returns the 8.3 (DOS) file-/pathname for a given file. Only avaible for
     * 32-bit Windows, so check if this operating system is used before calling.
     * The filename must include the path as whole and be passed as String.
     * 
     * @param longPathName
     *            the long path name
     * 
     * @return File/Path in 8.3 format
     */
    public static String getShortPathNameW(String longPathName) {
        if (SystemProperties.OS != OperatingSystem.WINDOWS) {
            return longPathName;
        }
        WString pathname = new WString(longPathName);
        int bufferSize = (pathname.length() * CHAR_BYTE_WIDTH) + CHAR_BYTE_WIDTH;
        Memory buffer = new Memory(bufferSize);

        if (kernel32.GetShortPathNameW(pathname, buffer, bufferSize) == 0) {
            return "";
        }
        return buffer.getString(0, true);
    }
}
