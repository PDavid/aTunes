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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Several utilities to manage files of local audio objects
 * 
 * @author alex
 * 
 */
public class FileManager implements IFileManager {

	private IOSManager osManager;

	private ILocalAudioObjectFactory localAudioObjectFactory;

	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(
			final ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public String getPath(final ILocalAudioObject ao) {
		return FileUtils.getPath(getAudioObjectFile(ao));
	}

	@Override
	public File getFile(final ILocalAudioObject ao) {
		return getAudioObjectFile(ao);
	}

	@Override
	public boolean fileExists(final ILocalAudioObject ao) {
		return getAudioObjectFile(ao).exists();
	}

	@Override
	public long getFileSize(final ILocalAudioObject ao) {
		return getAudioObjectFile(ao).length();
	}

	@Override
	public String getFileName(final ILocalAudioObject ao) {
		return getAudioObjectFile(ao).getName();
	}

	@Override
	public String getFolderPath(final ILocalAudioObject ao) {
		return FileUtils.getPath(getAudioObjectFile(ao).getParentFile());
	}

	/**
	 * @param ao
	 * @return
	 */
	private File getAudioObjectFile(final ILocalAudioObject ao) {
		Preconditions.checkNotNull(ao);
		return ao.getFile();
	}

	@Override
	public String getParentName(final ILocalAudioObject ao) {
		return getAudioObjectFile(ao).getParentFile().getName();
	}

	@Override
	public boolean delete(final ILocalAudioObject ao) {
		boolean deleted = getAudioObjectFile(ao).delete();
		if (!deleted) {
			Logger.error(StringUtils.getString(ao, " not deleted"));
		}
		return deleted;
	}

	@Override
	public File getFolder(final ILocalAudioObject ao) {
		return getAudioObjectFile(ao).getParentFile();
	}

	@Override
	public Set<File> getFolders(final List<ILocalAudioObject> aos) {
		Set<File> folders = new HashSet<File>();
		for (ILocalAudioObject ao : aos) {
			folders.add(getAudioObjectFile(ao).getParentFile());
		}
		return folders;
	}

	@Override
	public void copyFile(final ILocalAudioObject file, final File destFile)
			throws IOException {
		org.apache.commons.io.FileUtils.copyFile(getAudioObjectFile(file),
				destFile);
	}

	@Override
	public ILocalAudioObject copyFile(final ILocalAudioObject source,
			final String destinationPath, final String fileName)
			throws IOException {
		File destFile = new File(StringUtils.getString(destinationPath,
				this.osManager.getFileSeparator(), fileName));
		org.apache.commons.io.FileUtils.copyFile(getAudioObjectFile(source),
				destFile);
		return this.localAudioObjectFactory.getLocalAudioObject(destFile);
	}

	@Override
	public long getModificationTime(ILocalAudioObject ao) {
		return getAudioObjectFile(ao).lastModified();
	}
}
