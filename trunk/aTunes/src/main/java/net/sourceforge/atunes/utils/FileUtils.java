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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * File utilities
 * 
 * @author alex
 * 
 */
public final class FileUtils {

	/** Kilobyte 1024 bytes. */
	public static final long KILOBYTE = 1024;

	/** Megabyte 1024 Kilobytes. */
	public static final long MEGABYTE = KILOBYTE * 1024;

	/** Gigabyte 1024 Megabytes. */
	public static final long GIGABYTE = MEGABYTE * 1024;

	private FileUtils() {
	}

	/**
	 * Sets write permissions if is not writable.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public static void setWritable(final File file)
			throws FileNotFoundException {
		if (file == null) {
			throw new IllegalArgumentException("File is null");
		} else if (!file.exists()) {
			throw new FileNotFoundException(getPath(file));
		}
		// Set write permission on file
		if (!file.canWrite()) {
			file.setWritable(true);
		}
		// Set write permission on parent
		if (!file.getParentFile().canWrite()) {
			file.getParentFile().setWritable(true);
		}
	}

	/**
	 * Returns path of a file. This is a utility method to use the same way to
	 * retrieve path
	 * 
	 * @param file
	 * @return
	 */
	public static String getPath(final File file) {
		if (file == null) {
			throw new IllegalArgumentException("File is null");
		}
		String path = null;
		try {
			path = file.getCanonicalPath();
		} catch (IOException e) {
			path = file.getAbsolutePath();
			Logger.error("Error retrieving path of file: ", path);
			Logger.error(e);
		}
		return path;
	}

	/**
	 * Returns path of a file. This is a utility method to use the same way to
	 * retrieve path
	 * 
	 * @param file
	 * @return
	 */
	public static String getNormalizedPath(final File file) {
		String path = getPath(file);
		// Basic replace
		path = path.replace('\\', '/');
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 2);
		}
		return path;
	}

	/**
	 * This method is needed as File.canWrite seems to fail with network shares,
	 * returning always true
	 * 
	 * @param file
	 * @return true if this folder can be written
	 */
	public static boolean canWriteFolder(final File file) {
		if (file == null || !file.exists() || !file.isDirectory()) {
			return false;
		}
		File dummy = new File(file, "/dummy");
		try {
			/*
			 * Create and delete a dummy file in order to check file
			 * permissions. Maybe there is a safer way for this check.
			 */
			dummy.createNewFile();
			dummy.delete();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
