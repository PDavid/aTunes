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

package net.sourceforge.atunes.utils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * File utilities
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

	private FileUtils() {}

    /**
     * Sets write permissions if is not writable.
     * @param file
     * @throws FileNotFoundException
     */
    public static void setWritable(File file) throws FileNotFoundException {
    	if (file == null) {
    		throw new IllegalArgumentException("File is null");
    	} else if (!file.exists()) {
    		throw new FileNotFoundException(file.getAbsolutePath());
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
}
