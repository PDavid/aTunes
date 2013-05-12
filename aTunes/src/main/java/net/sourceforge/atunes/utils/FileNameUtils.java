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

import net.sourceforge.atunes.model.IOSManager;

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

	private FileNameUtils() {
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
	 * @param osManager
	 * 
	 * @return Returns the filename with known illegal characters substituted.
	 */
	public static String getValidFileName(final String fileName,
			final IOSManager osManager) {
		return getValidFileName(fileName, false, osManager);
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
	 * @param osManager
	 * 
	 * @return Returns the filename with known illegal characters substituted.
	 */
	public static String getValidFileName(final String fileNameStr,
			final boolean isMp3Device, final IOSManager osManager) {
		// First call generic function
		String fileName = getValidName(fileNameStr, isMp3Device, osManager);

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
		if (osManager.isWindows() || isMp3Device) {
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
	 * @param osManager
	 * 
	 * @return Returns the path name with known illegal characters substituted.
	 */
	public static String getValidFolderName(final String folderName,
			final IOSManager osManager) {
		return getValidFolderName(folderName, false, osManager);
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
	 * @param osManager
	 * 
	 * @return Returns the path name with known illegal characters substituted.
	 */
	public static String getValidFolderName(final String folderNameStr,
			final boolean isMp3Device, final IOSManager osManager) {
		// First call generic function
		String folderName = getValidName(folderNameStr, isMp3Device, osManager);

		// This list is probably incomplete. Windows is quite picky.
		if (osManager.isWindows() || isMp3Device) {
			folderName = folderName.replace("\\.", "\\_");
			if (osManager.isWindows()) {
				folderName = folderName + "\\";
			}
			folderName = folderName.replace(".\\", "_\\");
			if (osManager.isWindows()) {
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
	private static String getValidName(final String fileNameStr,
			final boolean isMp3Device, final IOSManager osManager) {
		/*
		 * This list is probably incomplete. Windows is quite picky. We do not
		 * check for maximum length.
		 */
		String fileName = fileNameStr;
		if (osManager.isWindows() || isMp3Device) {
			fileName = fileName.replace("\"", "'");
			fileName = fileName.replace("?", "_");
			// Replace all ":" except at the drive letter
			if (fileName.length() > 2) {
				fileName = fileName.substring(0, 2)
						+ fileName.substring(2).replace(":", "-");
			}
			fileName = fileName.replace("<", "-");
			fileName = fileName.replace(">", "-");
			fileName = fileName.replace("|", "-");
			fileName = fileName.replace("*", "-");
		}

		// Unconfirmed, as no Mac available for testing.
		if (osManager.isMacOsX()) {
			fileName = fileName.replace("|", "-");
		}
		return fileName;
	}
}
