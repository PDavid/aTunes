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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITemporalDiskStorage;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FilenameUtils;

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

	private ITemporalDiskStorage temporalDiskStorage;

	/**
	 * Extensions of files associated to a local audio object (picture,
	 * metadata...)
	 */
	private List<String> associatedFiles;

	/**
	 * @param associatedFiles
	 */
	public void setAssociatedFiles(final List<String> associatedFiles) {
		this.associatedFiles = associatedFiles;
	}

	/**
	 * @param temporalDiskStorage
	 */
	public void setTemporalDiskStorage(
			final ITemporalDiskStorage temporalDiskStorage) {
		this.temporalDiskStorage = temporalDiskStorage;
	}

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
	public String getSystemPath(final ILocalAudioObject ao) {
		return FilenameUtils.separatorsToSystem(getPath(ao));
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
		return new File(ao.getUrl());
	}

	@Override
	public String getParentName(final ILocalAudioObject ao) {
		return getAudioObjectFile(ao).getParentFile().getName();
	}

	private List<File> getAssociatedFiles(final ILocalAudioObject ao) {
		List<File> associatedFiles = new ArrayList<File>();
		for (String extension : this.associatedFiles) {
			File associatedFile = new File(StringUtils.getString(FilenameUtils
					.removeExtension(getAudioObjectFile(ao).getAbsolutePath()),
					".", extension));
			if (associatedFile.exists()) {
				associatedFiles.add(associatedFile);
			}
		}
		return associatedFiles;
	}

	@Override
	public boolean delete(final ILocalAudioObject ao) {
		boolean deleted = getAudioObjectFile(ao).delete();
		if (!deleted) {
			Logger.error(StringUtils.getString(ao, " not deleted"));
		} else {
			// Delete associated files
			for (File associatedFile : getAssociatedFiles(ao)) {
				boolean associatedFileDeleted = associatedFile.delete();
				if (!associatedFileDeleted) {
					Logger.error(StringUtils.getString(
							associatedFile.getAbsolutePath(), " not deleted"));
				}
			}
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
		copyAssociatedFiles(file, destFile);
	}

	private void copyAssociatedFiles(final ILocalAudioObject file,
			final File destFile) throws IOException {
		// Copy associated files
		for (File associatedFile : getAssociatedFiles(file)) {
			String extension = FilenameUtils.getExtension(associatedFile
					.getName());
			String copiedAssociatedFile = StringUtils.getString(
					FilenameUtils.removeExtension(destFile.getAbsolutePath()),
					".", extension);
			org.apache.commons.io.FileUtils.copyFile(associatedFile, new File(
					copiedAssociatedFile));
		}
	}

	@Override
	public ILocalAudioObject copyFile(final ILocalAudioObject source,
			final String destinationPath, final String fileName)
			throws IOException {
		File destFile = new File(StringUtils.getString(destinationPath,
				this.osManager.getFileSeparator(), fileName));
		org.apache.commons.io.FileUtils.copyFile(getAudioObjectFile(source),
				destFile);
		copyAssociatedFiles(source, destFile);
		return this.localAudioObjectFactory.getLocalAudioObject(destFile);
	}

	@Override
	public long getModificationTime(final ILocalAudioObject ao) {
		return getAudioObjectFile(ao).lastModified();
	}

	@Override
	public ILocalAudioObject cacheAudioObject(
			final ILocalAudioObject audioObject) {
		String name = Integer.toString(getAudioObjectFile(audioObject)
				.getAbsolutePath().hashCode());
		File tempFile = this.temporalDiskStorage.addFile(
				getAudioObjectFile(audioObject), name);
		if (tempFile != null) {
			// Cache associated files
			for (File associatedFile : getAssociatedFiles(audioObject)) {
				this.temporalDiskStorage.addFile(associatedFile, name);
			}
			return this.localAudioObjectFactory.getLocalAudioObject(tempFile);
		}
		return null;
	}

	@Override
	public void removeCachedAudioObject(final ILocalAudioObject audioObject) {
		this.temporalDiskStorage.removeFile(getAudioObjectFile(audioObject));
		// Remove associated files
		for (File associatedFile : getAssociatedFiles(audioObject)) {
			this.temporalDiskStorage.removeFile(associatedFile);
		}
	}

	@Override
	public void makeWritable(final ILocalAudioObject audioObject) {
		try {
			FileUtils.setWritable(getAudioObjectFile(audioObject));
			// Make also associated files
			for (File associatedFile : getAssociatedFiles(audioObject)) {
				FileUtils.setWritable(associatedFile);
			}
		} catch (FileNotFoundException e) {
			Logger.error(e);
		}
	}

	@Override
	public boolean exists(final ILocalAudioObject ao) {
		return getAudioObjectFile(ao).exists();
	}

	@Override
	public boolean rename(final ILocalAudioObject audioFile, final String name) {
		File file = getAudioObjectFile(audioFile);
		String extension = FilenameUtils.getExtension(getPath(audioFile));
		String validName = FileNameUtils.getValidFileName(name, this.osManager);
		File newFile = this.osManager.getFile(getFolderPath(audioFile),
				StringUtils.getString(validName, ".", extension));
		// Get associated files before renaming
		List<File> associatedFiles = getAssociatedFiles(audioFile);
		boolean succeeded = file.renameTo(newFile);
		if (succeeded) {
			if (audioFile instanceof AudioFile) {
				((AudioFile) audioFile).setFile(FileUtils
						.getNormalizedPath(newFile));
			} else {
				throw new IllegalArgumentException(StringUtils.getString(
						"setFile operation not implemented for ", audioFile
								.getClass().getName()));
			}
			// Rename associated files
			for (File associatedFile : associatedFiles) {
				String associatedExtension = FilenameUtils
						.getExtension(associatedFile.getAbsolutePath());
				File newAssociatedFile = this.osManager.getFile(
						getFolderPath(audioFile), StringUtils.getString(
								validName, ".", associatedExtension));
				associatedFile.renameTo(newAssociatedFile);
			}
		}
		return succeeded;
	}

	@Override
	public boolean isUpToDate(final ILocalAudioObject audioFile) {
		return audioFile.getReadTime() > getAudioObjectFile(audioFile)
				.lastModified();
	}

	@Override
	public String getSystemName(ILocalAudioObject audioObject) {
		try {
			return getAudioObjectFile(audioObject).getCanonicalPath();
		} catch (IOException e) {
			Logger.error(e);
			return "ERROR: Could not get canonical path";
		}
	}
}
