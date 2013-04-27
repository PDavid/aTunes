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
import java.io.FilenameFilter;

/**
 * A dialog to select files
 * 
 * @author alex
 * 
 */
public interface IFileSelectorDialog extends IDialog {

	/**
	 * @param fileFilter
	 */
	void setFileFilter(FilenameFilter fileFilter);

	/**
	 * Selects a file to load
	 * 
	 * @param path
	 * @return
	 */
	File loadFile(String path);

	/**
	 * Selects a file to load
	 * 
	 * @param path
	 * @return
	 */
	File loadFile(File path);

	/**
	 * Selects a file to save
	 * 
	 * @param path
	 * @param suggestedName
	 * @return
	 */
	File saveFile(String path, String suggestedName);

	/**
	 * Selects a file to save
	 * 
	 * @param path
	 * @param suggestedName
	 * @return
	 */
	File saveFile(File path, String suggestedName);

}