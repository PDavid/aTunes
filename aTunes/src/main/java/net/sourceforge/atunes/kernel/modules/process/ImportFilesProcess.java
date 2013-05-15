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

package net.sourceforge.atunes.kernel.modules.process;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.ITagAttributesReviewed;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Imports (song) files to repository
 */
public class ImportFilesProcess extends AbstractLocalAudioObjectTransferProcess {

	private static final Pattern NUMBER_SEPARATOR_PATTERN = Pattern
			.compile("[^0-9]+");

	/**
	 * Folders to import
	 */
	private List<File> folders;

	/** Set of audio files whose tag must be written */
	private Set<ILocalAudioObject> filesToChangeTag;

	private ITagHandler tagHandler;

	private IWebServicesHandler webServicesHandler;

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param tagHandler
	 */
	public void setTagHandler(final ITagHandler tagHandler) {
		this.tagHandler = tagHandler;
	}

	/**
	 * Replaces tags before import audio objects
	 * 
	 * @param tagAttributesReviewed
	 */
	public void initialize(final ITagAttributesReviewed tagAttributesReviewed) {
		this.filesToChangeTag = new HashSet<ILocalAudioObject>();
		for (ILocalAudioObject fileToImport : getFilesToTransfer()) {
			// Replace tags (in memory) before import audio files if necessary
			replaceTag(fileToImport, tagAttributesReviewed);

			// Set track number if necessary
			setTrackNumber(fileToImport);
		}
	}

	/**
	 * @param folders
	 */
	public void setFolders(final List<File> folders) {
		this.folders = folders;
	}

	@Override
	public String getProgressDialogTitle() {
		return StringUtils.getString(I18nUtils.getString("IMPORTING"), "...");
	}

	/**
	 * Prepares the directory structure in which the song will be written.
	 * 
	 * @param song
	 *            Song to be written
	 * @param destinationBaseFolder
	 *            Destination path
	 * @return Returns the directory structure with full path where the file
	 *         will be written
	 */
	private File getDirectory(final ILocalAudioObject song,
			final File destinationBaseFolder) {
		// Get base folder or the first folder if there is any error
		File baseFolder = null;
		for (File folder : this.folders) {
			String filePath = getFileManager().getFolderPath(song);
			String folderParentPath = folder.getParentFile() != null ? FileUtils
					.getPath(folder.getParentFile()) : null;
			if (filePath != null && folderParentPath != null
					&& filePath.startsWith(folderParentPath)) {
				baseFolder = folder.getParentFile();
				break;
			}
		}
		if (baseFolder == null) {
			baseFolder = this.folders.get(0);
		}

		String songPath = this.getFileManager().getFolderPath(song);
		String songRelativePath = songPath.replace(FileUtils
				.getPath(baseFolder).replace("\\", "\\\\").replace("$", "\\$"),
				"");
		if (getStateRepository().getImportFolderPathPattern() != null) {
			songRelativePath = FileNameUtils
					.getValidFolderName(
							getNewFolderPath(getStateRepository()
									.getImportFolderPathPattern(), song,
									getOsManager()), getOsManager());
		}
		return new File(StringUtils.getString(FileUtils
				.getPath(destinationBaseFolder), getOsManager()
				.getFileSeparator(), songRelativePath));
	}

	@Override
	protected ILocalAudioObject transferAudioFile(final File destination,
			final ILocalAudioObject file, final List<Exception> thrownExceptions) {
		// Change title. As this can be a long-time task we get titles during
		// transfer process instead of before to avoid not showing any progress
		// dialog
		// while performing this task
		setTitle(file);

		// If necessary, apply changes to original files before copy
		if (getStateRepository().isApplyChangesToSourceFilesBeforeImport()) {
			changeTag(file, file);
		}

		// Import file
		ILocalAudioObject destFile = importFile(destination, file,
				thrownExceptions);

		if (destFile != null) {
			// Change tag if necessary after import
			if (!getStateRepository().isApplyChangesToSourceFilesBeforeImport()) {
				changeTag(file, destFile);
			}
		}

		return destFile;
	}

	/**
	 * Imports a single file to a destination
	 * 
	 * @param destination
	 * @param file
	 * @param list
	 *            to add exceptions when thrown
	 * @return A reference to the created file or null if error
	 * @throws IOException
	 */
	private ILocalAudioObject importFile(final File destination,
			final ILocalAudioObject file, final List<Exception> thrownExceptions) {
		File destDir = getDirectory(file, destination);
		String newName;
		if (getStateRepository().getImportFileNamePattern() != null) {
			newName = getNewFileName(getStateRepository()
					.getImportFileNamePattern(), file, getOsManager());
		} else {
			newName = FileNameUtils.getValidFileName(this.getFileManager()
					.getFileName(file).replace("\\", "\\\\")
					.replace("$", "\\$"), false, getOsManager());
		}

		try {
			return this.getFileManager().copyFile(file,
					FileUtils.getPath(destDir), newName);
		} catch (IOException e) {
			thrownExceptions.add(e);
			return null;
		}
	}

	/**
	 * Changes tag if necessary in disk
	 * 
	 * @param sourceFile
	 *            original AudioFile
	 * @param destFile
	 *            destination file
	 */
	private void changeTag(final ILocalAudioObject sourceFile,
			final ILocalAudioObject destFile) {
		if (this.filesToChangeTag.contains(sourceFile)) {
			this.tagHandler.setTag(destFile, sourceFile.getTag());
		}
	}

	/**
	 * Changes tag of a file if it is defined in a TagAttributesReviewed object
	 * LocalAudioObject is added to list of files to change tag physically on
	 * disk
	 * 
	 * @param fileToImport
	 * @param tagAttributesReviewed
	 */
	private void replaceTag(final ILocalAudioObject fileToImport,
			final ITagAttributesReviewed tagAttributesReviewed) {
		if (tagAttributesReviewed != null) {
			ITag modifiedTag = tagAttributesReviewed
					.getTagForAudioFile(fileToImport);
			// This file must be changed
			if (modifiedTag != null) {
				fileToImport.setTag(modifiedTag);
				this.filesToChangeTag.add(fileToImport);
			}
		}
	}

	/**
	 * Changes track number of a file. LocalAudioObject is added to list of
	 * files to change tag physically on disk
	 * 
	 * @param fileToImport
	 */
	private void setTrackNumber(final ILocalAudioObject fileToImport) {
		if (getStateRepository().isSetTrackNumbersWhenImporting()
				&& fileToImport.getTrackNumber() < 1) {
			int newTrackNumber = getTrackNumber(fileToImport);
			if (newTrackNumber > 0) {
				if (fileToImport.getTag() == null) {
					fileToImport.setTag(this.tagHandler.getNewTag());
				}
				fileToImport.getTag().setTrackNumber(newTrackNumber);
				if (!this.filesToChangeTag.contains(fileToImport)) {
					this.filesToChangeTag.add(fileToImport);
				}
			}
		}
	}

	/**
	 * Returns track number for a given audio file
	 * 
	 * @param audioFile
	 * @return
	 */
	private int getTrackNumber(final ILocalAudioObject audioFile) {
		// Try to get a number from file name
		String fileName = audioFile.getNameWithoutExtension();
		String[] aux = NUMBER_SEPARATOR_PATTERN.split(fileName);
		int trackNumber = 0;
		int i = 0;
		while (trackNumber == 0 && i < aux.length) {
			String token = aux[i];
			try {
				trackNumber = Integer.parseInt(token);
				// If trackNumber >= 1000 maybe it's not a track number (year?)
				if (trackNumber >= 1000) {
					trackNumber = 0;
				}
			} catch (NumberFormatException e) {
				// Ok, it's not a valid number, skip it
			}
			i++;
		}

		// If trackNumber could not be retrieved from file name, try to get from
		// last.fm
		// To get this, titles must match
		if (trackNumber == 0) {
			trackNumber = this.webServicesHandler.getTrackNumber(audioFile);
		}

		return trackNumber;
	}

	/**
	 * Changes title of a file. LocalAudioObject is added to list of files to
	 * change tag physically on disk
	 * 
	 * @param fileToImport
	 */
	private void setTitle(final ILocalAudioObject fileToImport) {
		if (getStateRepository().isSetTitlesWhenImporting()) {
			String newTitle = this.webServicesHandler
					.getTitleForAudioObject(fileToImport);
			if (newTitle != null) {
				if (fileToImport.getTag() == null) {
					fileToImport.setTag(this.tagHandler.getNewTag());
				}
				fileToImport.getTag().setTitle(newTitle);
				if (!this.filesToChangeTag.contains(fileToImport)) {
					this.filesToChangeTag.add(fileToImport);
				}
			}
		}
	}

	@Override
	protected String getFileNamePattern() {
		return getStateRepository().getImportFileNamePattern();
	}

	@Override
	protected String getFolderPathPattern() {
		return getStateRepository().getImportFolderPathPattern();
	}
}
