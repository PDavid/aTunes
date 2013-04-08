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

import java.awt.image.RenderedImage;
import java.io.File;

/**
 * A temporal storage of files
 * 
 * @author alex
 * 
 */
public interface ITemporalDiskStorage {

	/**
	 * Copies a file to temporal folder.
	 * 
	 * @param srcFile
	 * @return File object to copied file in temporal folder
	 */
	File addFile(File srcFile);

	/**
	 * Copies a file to temporal folder with given name
	 * 
	 * @param srcFile
	 * @param name
	 * @return File object to copied file in temporal folder
	 */
	File addFile(File srcFile, String name);

	/**
	 * Writes an image to a file in temporal folder
	 * 
	 * @param image
	 * @param fileName
	 * @return
	 */
	File addImage(RenderedImage image, String fileName);

	/**
	 * Removes a file from temporal folder.
	 * 
	 * @param tempFile
	 * @return true, if removes the file
	 */
	boolean removeFile(File tempFile);

	/**
	 * Removes all files from temporal folder.
	 */
	void removeAll();

}