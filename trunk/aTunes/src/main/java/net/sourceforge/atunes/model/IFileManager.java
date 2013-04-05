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
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Manages basic operations for local files
 * 
 * @author alex
 * 
 */
public interface IFileManager {

	/**
	 * @param ao
	 * @return path of local audio object
	 */
	String getPath(ILocalAudioObject ao);

	/**
	 * @param ao
	 * @return system representation of path of local audio object
	 */
	String getSystemPath(ILocalAudioObject ao);

	/**
	 * @param ao
	 * @return file of local audio object
	 */
	File getFile(ILocalAudioObject ao);

	/**
	 * @param ao
	 * @return check if file of local audio object exists
	 */
	boolean fileExists(ILocalAudioObject ao);

	/**
	 * @param ao
	 * @return file size
	 */
	long getFileSize(ILocalAudioObject ao);

	/**
	 * @param ao
	 * @return file name of local audio object
	 */
	String getFileName(ILocalAudioObject ao);

	/**
	 * @param ao
	 * @return path of parent file
	 */
	String getFolderPath(ILocalAudioObject ao);

	/**
	 * @param ao
	 * @return folder of audio object
	 */
	File getFolder(ILocalAudioObject ao);

	/**
	 * @param aos
	 * @return folders of audio objects without repeated folders
	 */
	Set<File> getFolders(final List<ILocalAudioObject> aos);

	/**
	 * @param file
	 * @return name of parent file of audio object
	 */
	String getParentName(ILocalAudioObject file);

	/**
	 * Deletes audio object
	 * 
	 * @param audioFile
	 * @return true if deleted
	 */
	boolean delete(ILocalAudioObject audioFile);

	/**
	 * Copies audio object to given destination file
	 * 
	 * @param file
	 * @param destFile
	 * @throws IOException
	 */
	void copyFile(ILocalAudioObject file, File destFile) throws IOException;

	/**
	 * Copies audio object to given destination directory and file name
	 * 
	 * @param source
	 * @param destinationPath
	 * @param fileName
	 * @return reference to copied file
	 * @throws IOException
	 */
	ILocalAudioObject copyFile(ILocalAudioObject source,
			String destinationPath, String fileName) throws IOException;

	/**
	 * Returns modification time in milliseconds
	 * 
	 * @param ao
	 * @return modification time in milliseconds
	 */
	long getModificationTime(ILocalAudioObject ao);

	/**
	 * Caches audio object in temporal disk storage
	 * 
	 * @param audioObject
	 * @return
	 */
	ILocalAudioObject cacheAudioObject(ILocalAudioObject audioObject);

	/**
	 * Removes cached audio object
	 * 
	 * @param ao
	 */
	void removeCachedAudioObject(ILocalAudioObject ao);

	/**
	 * Makes audio object writable
	 * 
	 * @param file
	 */
	void makeWritable(ILocalAudioObject file);

	/**
	 * @param ao
	 * @return true if audio object exists
	 */
	boolean exists(ILocalAudioObject ao);

	/**
	 * @param audioFile
	 * @param name
	 * @return true if rename completed successfully
	 */
	boolean rename(ILocalAudioObject audioFile, String name);

	/**
	 * Checks if is up to date.
	 * 
	 * @param audioFile
	 * @return true, if is up to date
	 */
	boolean isUpToDate(ILocalAudioObject audioFile);

	/**
	 * OS-dependent name
	 * 
	 * @param audioObject
	 * @return
	 */
	String getSystemName(ILocalAudioObject audioObject);
}
