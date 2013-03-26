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
import java.util.List;


/**
 * A dialog to review tags of a set of files before importing them
 * @author alex
 *
 */
public interface IReviewImportDialog extends IDialog {

	/**
	 * @return the dialogCancelled
	 */
	boolean isDialogCancelled();

	/**
	 * Folders to review
	 * @param folders
	 */
	void setFolders(List<File> folders);
	
	/**
	 * Files to load
	 * @param filesToLoad
	 */
	void setFilesToLoad(List<ILocalAudioObject> filesToLoad);
	
	/**
	 * Returns result of reviewing tags
	 * 
	 * @return
	 */
	ITagAttributesReviewed getResult();
}