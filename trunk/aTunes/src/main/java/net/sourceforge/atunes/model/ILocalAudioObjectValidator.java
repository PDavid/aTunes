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

package net.sourceforge.atunes.model;

import java.io.File;
import java.io.FileFilter;

/**
 * Tests if a file is a valid local audio object
 * 
 * @author alex
 * 
 */
public interface ILocalAudioObjectValidator {

	/**
	 * Checks if is valid audio file.
	 * 
	 * @param file
	 *            the file
	 * 
	 * @return true, if is valid audio file
	 */
	boolean isValidAudioFile(String file);

	/**
	 * Checks if is valid audio file.
	 * 
	 * @param file
	 *            the file
	 * 
	 * @return true, if is valid audio file
	 */
	boolean isValidAudioFile(File file);

	/**
	 * Checks if a file is a valid audio file given its name
	 * 
	 * @param localAudioObject
	 * @param formats
	 * @return if the file is a valid audio file
	 */
	boolean isOneOfTheseFormats(ILocalAudioObject localAudioObject,
			LocalAudioObjectFormat... formats);

	/**
	 * Returns a file filter to select valid local audio objects
	 * 
	 * @return
	 */
	FileFilter getValidLocalAudioObjectFileFilter();

	/**
	 * Checks if a file is a valid audio file given its name This method does
	 * not check if file exists or it's a directory or even if file is null
	 * 
	 * @param file
	 * @return if the file is a valid audio file
	 */
	boolean isOneOfValidFormats(final File file);
}