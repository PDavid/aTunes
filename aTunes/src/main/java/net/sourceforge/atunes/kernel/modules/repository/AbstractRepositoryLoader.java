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
import java.io.FileFilter;
import java.util.List;

import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryLoader;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;
import net.sourceforge.atunes.model.IRepositoryTransaction;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.DirectoryFileFilter;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;

/**
 * Class for loading audio files into repository.
 */
public abstract class AbstractRepositoryLoader implements IRepositoryLoader,
		Runnable {

	// Some attributes to speed up populate info process
	private IRepositoryLoaderListener listener;
	private List<File> folders;
	private boolean interrupt;
	private IRepository oldRepository;
	private IRepository repository;
	private int filesLoaded;
	private long startReadTime;
	private int fastFirstChar;
	private IStateNavigation stateNavigation;
	private ILocalAudioObjectFactory localAudioObjectFactory;
	private FileFilter validLocalAudioObjectFileFilter;
	private DirectoryFileFilter directoryFileFilter;

	private IRepositoryTransaction transaction;

	private IUnknownObjectChecker unknownObjectChecker;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * Starts load
	 * 
	 * @param state
	 * @param transaction
	 * @param folders
	 * @param oldRepository
	 * @param repository
	 */
	@Override
	public void start(final IRepositoryTransaction transaction,
			final List<File> folders, final IRepository oldRepository,
			final IRepository repository) {
		this.transaction = transaction;
		this.folders = folders;
		this.oldRepository = oldRepository;
		this.repository = repository;
		this.directoryFileFilter = new DirectoryFileFilter();
		if (this.listener == null) {
			this.listener = new VoidRepositoryLoaderListener();
		}
		execute();
	}

	/**
	 * @return
	 */
	protected final List<File> getFolders() {
		return this.folders;
	}

	/**
	 * @return
	 */
	protected final int getFilesLoaded() {
		return this.filesLoaded;
	}

	/**
	 * @return
	 */
	protected final boolean isInterrupt() {
		return this.interrupt;
	}

	/**
	 * Executes this loader
	 */
	protected abstract void execute();

	/**
	 * Use this method to call some specific actions before loading repository
	 */
	protected abstract void runTasksBeforeLoadRepository();

	/**
	 * @return
	 */
	protected final long getStartReadTime() {
		return this.startReadTime;
	}

	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(
			final ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}

	/**
	 * @param validLocalAudioObjectFileFilter
	 */
	public void setValidLocalAudioObjectFileFilter(
			final FileFilter validLocalAudioObjectFileFilter) {
		this.validLocalAudioObjectFileFilter = validLocalAudioObjectFileFilter;
	}

	/**
	 * Adds the repository loader listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	@Override
	public void setRepositoryLoaderListener(
			final IRepositoryLoaderListener listener) {
		this.listener = listener;
	}

	/**
	 * @return
	 */
	protected final IRepositoryLoaderListener getRepositoryLoaderListener() {
		return this.listener;
	}

	/**
	 * Interrupt load.
	 */
	@Override
	public void interruptLoad() {
		Logger.info("Load interrupted");
		this.interrupt = true;
	}

	/**
	 * Load repository.
	 */
	private void loadRepository() {
		runTasksBeforeLoadRepository();
		this.startReadTime = System.currentTimeMillis();

		RepositoryFiller filler = new RepositoryFiller(this.repository,
				this.stateNavigation, this.unknownObjectChecker,
				this.fileManager);
		for (File folder : this.folders) {
			String fastRepositoryPath = FileUtils.getNormalizedPath(folder);
			this.fastFirstChar = fastRepositoryPath.length() + 1;

			if (folder.exists()) {
				navigateDir(filler, folder, folder);
			}
		}
	}

	/**
	 * Navigates directory loading audio files and directories
	 * 
	 * @param filler
	 * @param relativeTo
	 * @param dir
	 */
	private void navigateDir(final RepositoryFiller filler,
			final File relativeTo, final File dir) {
		if (!this.interrupt) {
			// Process directories
			processDirectories(filler, dir, relativeTo);

			// Process audio files
			processAudioFiles(filler, dir, relativeTo);
		}
	}

	/**
	 * Process directory
	 * 
	 * @param filler
	 * @param dir
	 * @param relativeTo
	 */
	private void processDirectories(final RepositoryFiller filler,
			final File dir, final File relativeTo) {
		// Directories
		File[] dirs = dir.listFiles(this.directoryFileFilter);

		// Process directories
		if (dirs != null) {
			for (File directory : dirs) {
				navigateDir(filler, relativeTo, directory);
			}
		}
	}

	/**
	 * Process audio files in a directory
	 * 
	 * @param filler
	 * @param dir
	 * @param relativeTo
	 */
	private void processAudioFiles(final RepositoryFiller filler,
			final File dir, final File relativeTo) {
		// Get audio files
		File[] audiofiles = dir.listFiles(this.validLocalAudioObjectFileFilter);

		// Process audio files
		if (audiofiles != null && audiofiles.length > 0) {
			String pathToFile = FileUtils.getNormalizedPath(dir);
			int lastChar = pathToFile.lastIndexOf('/') + 1;
			final String relativePath;
			if (this.fastFirstChar <= lastChar) {
				relativePath = pathToFile.substring(this.fastFirstChar);
			} else {
				relativePath = ".";
			}

			notifyCurrentPath(relativePath);

			for (File audiofile : audiofiles) {
				if (!this.interrupt) {
					processAudioFile(audiofile, filler, relativeTo,
							relativePath);
				}
			}

			notifyCurrentProgress();
		}
	}

	/**
	 * Actions needed to notify current relative path
	 * 
	 * @param relativePath
	 */
	protected abstract void notifyCurrentPath(String relativePath);

	/**
	 * Actions needed to notify current progress
	 */
	protected abstract void notifyCurrentProgress();

	/**
	 * Actions needed to notify each file loaded
	 */
	protected abstract void notifyFileLoaded();

	/**
	 * Actions needed to notify current album being loaded
	 * 
	 * @param artist
	 * @param album
	 */
	protected abstract void notifyCurrentAlbum(String artist, String album);

	/**
	 * Processes a single audio file
	 * 
	 * @param audiofile
	 * @param filler
	 * @param relativeTo
	 * @param relativePath
	 */
	private void processAudioFile(final File audiofile,
			final RepositoryFiller filler, final File relativeTo,
			final String relativePath) {
		ILocalAudioObject audio = null;

		// If a previous repository exists, check if file already was loaded.
		// If so, compare modification date. If modification date is equal to
		// last repository load
		// don't read file again

		if (this.oldRepository == null) {
			audio = this.localAudioObjectFactory.getLocalAudioObject(audiofile);
		} else {
			ILocalAudioObject oldAudioFile = this.oldRepository
					.getFile(net.sourceforge.atunes.utils.FileUtils
							.getPath(audiofile));
			if (oldAudioFile != null
					&& this.fileManager.isUpToDate(oldAudioFile)) {
				audio = oldAudioFile;
			} else {
				audio = this.localAudioObjectFactory
						.getLocalAudioObject(audiofile);
			}
		}

		notifyFileLoaded();
		this.filesLoaded++;
		filler.addAudioFile(audio, relativeTo, relativePath);
		notifyCurrentAlbum(audio.getArtist(this.unknownObjectChecker),
				audio.getAlbum(this.unknownObjectChecker));
	}

	/**
	 * Notify finish.
	 */
	private void notifyFinish() {
		this.transaction.finishTransaction();
		notifyFinishLoader();
	}

	/**
	 * Actions needed when process finished
	 */
	protected abstract void notifyFinishLoader();

	@Override
	public void run() {
		Logger.info("Starting repository read");
		Timer timer = new Timer();
		timer.start();
		if (!this.folders.isEmpty()) {
			loadRepository();
		} else {
			Logger.error("No folders selected for repository");
		}
		if (!this.interrupt) {
			double time = timer.stop();
			long files = this.repository.countFiles();
			double averageFileTime = time / files;
			Logger.info(StringUtils.getString("Read repository process DONE (",
					files, " files, ", time, " seconds, ",
					StringUtils.toString(averageFileTime, 4),
					" seconds / file)"));
			notifyFinish();
		}
	}

	/**
	 * @return the oldRepository
	 */
	@Override
	public IRepository getOldRepository() {
		return this.oldRepository;
	}
}
