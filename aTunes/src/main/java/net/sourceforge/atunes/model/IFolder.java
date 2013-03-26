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
import java.io.Serializable;
import java.util.Map;

/**
 * Represents a folder with a name, and a list of files and more
 * 
 * @author alex
 * 
 */
public interface IFolder extends Serializable, ITreeObject<ILocalAudioObject> {

	/**
	 * Adds a file to this folder.
	 * 
	 * @param file
	 *            the file
	 */
	public void addAudioFile(ILocalAudioObject file);

	/**
	 * Adds a folder as child of this folder.
	 * 
	 * @param f
	 *            the f
	 */
	public void addFolder(IFolder f);

	/**
	 * Returns a child folder given a folder name.
	 * 
	 * @param folderName
	 *            the folder name
	 * 
	 * @return the folder
	 */
	public IFolder getFolder(String folderName);

	/**
	 * Returns all children folders.
	 * 
	 * @return the folders
	 */
	public Map<String, IFolder> getFolders();

	/**
	 * Returns the name of this folder.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Gets the parent folder.
	 * 
	 * @return the parentFolder
	 */
	public IFolder getParentFolder();

	/**
	 * Returns true if folder is empty (contains neither files nor folders).
	 * 
	 * @return true, if checks if is empty
	 */
	public boolean isEmpty();

	/**
	 * Removes a file from this folder
	 * 
	 * @param file
	 *            the file
	 */
	public void removeAudioFile(ILocalAudioObject file);

	/**
	 * Removes a folder from this folder.
	 * 
	 * @param f
	 *            the f
	 */
	public void removeFolder(IFolder f);

	/**
	 * Returns the path of this folder
	 * 
	 * @param osManager
	 * @return
	 */
	public File getFolderPath(IOSManager osManager);

	/**
	 * Sets the parent folder.
	 * 
	 * @param parentFolder
	 *            the parentFolder to set
	 */
	public void setParentFolder(IFolder parentFolder);

	/**
	 * @return true if folder has no child folders
	 */
	public boolean isLeaf();

}