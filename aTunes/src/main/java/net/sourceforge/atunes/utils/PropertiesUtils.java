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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Utilities for reading and writting properties files
 * 
 * @author alex
 * 
 */
public class PropertiesUtils {

	/**
	 * @param filePath
	 * @return properties loaded or empty properties
	 */
	public static Properties readProperties(final String filePath) {
		Properties properties = new Properties();
		FileReader fr = null;
		try {
			fr = new FileReader(filePath);
			properties.load(fr);
		} catch (FileNotFoundException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		} finally {
			ClosingUtils.close(fr);
		}
		return properties;
	}

	/**
	 * @param filePath
	 * @param properties
	 * @throws IOException
	 */
	public static void writeProperties(final String filePath,
			final Properties properties) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			properties.store(fos, null);
			fos.flush();
		} finally {
			ClosingUtils.close(fos);
		}
	}

}
