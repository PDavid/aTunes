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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import net.sourceforge.atunes.utils.Logger;

/**
 * Returns the build number
 * @author alex
 *
 */
public class BuildNumber {

	/**
	 * @return build number (svn revision number)
	 */
	public static int getBuildNumber() {
		try {
			Class<?> clazz = BuildNumber.class;
			String className = clazz.getSimpleName() + ".class";
			String classPath = clazz.getResource(className).toString();
			if (classPath.startsWith("jar")) { // Check class in jar file
				String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
				Manifest manifest = new Manifest(new URL(manifestPath).openStream());
				Attributes attr = manifest.getMainAttributes();
				String value = attr.getValue("Implementation-Build");
				return Integer.parseInt(value);
			}
		} catch (MalformedURLException e) {
			Logger.error(e);
			return 0;
		} catch (IOException e) {
			Logger.error(e);
			return 0;
		} catch (NumberFormatException e) {
			Logger.error(e);
			return 0;
		}
		return 0;
	}
}
