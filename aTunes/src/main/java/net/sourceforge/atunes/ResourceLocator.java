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

package net.sourceforge.atunes;

import java.io.File;

import net.sourceforge.atunes.kernel.OperatingSystemDetector;
import net.sourceforge.atunes.model.OperatingSystem;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Locates resources in installation folder
 * @author alex
 *
 */
public final class ResourceLocator {

	private ResourceLocator() {}
	
	/**
	 * Returns file in installation folder or null if file does not exist
	 * @param relativePath
	 * @return
	 */
	public static File getFile(String relativePath) {
		File file = null;
		if (OperatingSystemDetector.getOperatingSystem().equals(OperatingSystem.MACOSX)) {
			file = new File(StringUtils.getString(System.getProperty("atunes.package"), "/Contents/Resources/", relativePath));
		} else {
			file = new File(StringUtils.getString(System.getProperty("user.dir"), relativePath));
		}
		if (file.exists()) {
			return file;
		} else {
			return null;
		}
	}
}
