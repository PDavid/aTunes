/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryLoader;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;
import net.sourceforge.atunes.model.IRepositoryTransaction;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.DirectoryFileFilter;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;

/**
 * Class for loading audio files into repository.
 */
public abstract class AbstractRepositoryLoader implements IRepositoryLoader, Runnable {

	// Some attributes to speed up populate info process
	private IRepositoryLoaderListener listener;
	private List<File> folders;
	private boolean interrupt;
	private IRepository oldRepository;
	private IRepository repository;
	private int filesLoaded;
	private long startReadTime;
	private String fastRepositoryPath;
	private int fastFirstChar;
	private IState state;
	private ILocalAudioObjectFactory localAudioObjectFactory;
	private FileFilter validLocalAudioObjectFileFilter;
	private DirectoryFileFilter directoryFileFilter;
	
	private IRepositoryTransaction transaction;

	/**
	 * Starts load
	 * @param state
	 * @param transaction
	 * @param folders
	 * @param oldRepository
	 * @param repository
	 */
	@Override
	public void start(IRepositoryTransaction transaction, List<File> folders, IRepository oldRepository, IRepository repository) {
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
		return folders;
	}
	
	/**
	 * @return
	 */
	protected final int getFilesLoaded() {
		return filesLoaded;
	}
	
	/**
	 * @return
	 */
	protected final boolean isInterrupt() {
		return interrupt;
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
		return startReadTime;
	}
	
	/**
	 * @param state
	 */
	public void setState(IState state) {
		this.state = state;
	}
	
	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}
	
	/**
	 * @param validLocalAudioObjectFileFilter
	 */
	public void setValidLocalAudioObjectFileFilter(FileFilter validLocalAudioObjectFileFilter) {
		this.validLocalAudioObjectFileFilter = validLocalAudioObjectFileFilter;
	}

	/**
	 * Adds the repository loader listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	@Override
	public void setRepositoryLoaderListener(IRepositoryLoaderListener listener) {
		this.listener = listener;
	}
	
	/**
	 * @return
	 */
	protected final IRepositoryLoaderListener getRepositoryLoaderListener() {
		return listener;
	}

	/**
	 * Interrupt load.
	 */
	@Override
	public void interruptLoad() {
		Logger.info("Load interrupted");
		interrupt = true;
	}

	/**
	 * Load repository.
	 */
	private void loadRepository() {
		runTasksBeforeLoadRepository();
		startReadTime = System.currentTimeMillis();

		RepositoryFiller filler = new RepositoryFiller(repository, state);
		for (File folder : folders) {
			fastRepositoryPath = folder.getAbsolutePath().replace('\\', '/');
			if (fastRepositoryPath.endsWith("/")) {
				fastRepositoryPath = fastRepositoryPath.substring(0, fastRepositoryPath.length() - 2);
			}
			fastFirstChar = fastRepositoryPath.length() + 1;

			if (folder.exists()) {
				navigateDir(filler, folder, folder);
			}
		}
	}

	/**
	 * Navigates directory loading audio files and directories
	 * @param filler
	 * @param relativeTo
	 * @param dir
	 */
	private void navigateDir(RepositoryFiller filler, File relativeTo, File dir) {
        if (!interrupt) {
            // Process directories
            processDirectories(filler, dir, relativeTo);
            
            // Process audio files
            processAudioFiles(filler, dir, relativeTo);            
        }
    }
	
	/**
	 * Process directory
	 * @param filler
	 * @param dir
	 * @param relativeTo
	 */
	private void processDirectories(RepositoryFiller filler, File dir, File relativeTo) {
        // Directories
        File[] dirs = dir.listFiles(directoryFileFilter);
        
        // Process directories
        if (dirs != null) {
        	for (File directory : dirs) {
        		navigateDir(filler, relativeTo, directory);
        	}
        }
	}
	
	/**
	 * Process audio files in a directory
	 * @param filler
	 * @param dir
	 * @param relativeTo
	 */
	private void processAudioFiles(RepositoryFiller filler, File dir, File relativeTo) {
        // Get audio files
        File[] audiofiles = dir.listFiles(validLocalAudioObjectFileFilter);
        
        // Process audio files
        if (audiofiles != null && audiofiles.length > 0) {
            String pathToFile = dir.getAbsolutePath().replace('\\', '/');
            int lastChar = pathToFile.lastIndexOf('/') + 1;
            final String relativePath;
            if (fastFirstChar <= lastChar) {
                relativePath = pathToFile.substring(fastFirstChar);
            } else {
                relativePath = ".";
            }

            notifyCurrentPath(relativePath);

        	for (File audiofile : audiofiles) {
        		if (!interrupt) {
        			processAudioFile(audiofile, filler, relativeTo, relativePath);
        		}
        	}
        	
        	notifyCurrentProgress();        	
        }
	}
	
	/**
	 * Actions needed to notify current relative path
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
	 * @param artist
	 * @param album
	 */
	protected abstract void notifyCurrentAlbum(String artist, String album);

	
	/**
	 * Processes a single audio file
	 * @param audiofile
	 * @param filler
	 * @param relativeTo
	 * @param relativePath
	 */
	private void processAudioFile(File audiofile, RepositoryFiller filler, File relativeTo, String relativePath) {
		ILocalAudioObject audio = null;

		// If a previous repository exists, check if file already was loaded.
		// If so, compare modification date. If modification date is equal to last repository load
		// don't read file again

		if (oldRepository == null) {
			audio = localAudioObjectFactory.getLocalAudioObject(audiofile);
		} else {
			ILocalAudioObject oldAudioFile = oldRepository.getFile(audiofile.getAbsolutePath());
			if (oldAudioFile != null && oldAudioFile.isUpToDate()) {
				audio = oldAudioFile;
			} else {
				audio = localAudioObjectFactory.getLocalAudioObject(audiofile);
			}
		}

		notifyFileLoaded();
		filesLoaded++;
		filler.addAudioFile(audio, relativeTo, relativePath);
		notifyCurrentAlbum(audio.getArtist(), audio.getAlbum());
	}

	/**
	 * Notify finish.
	 */
	private void notifyFinish() {
		transaction.finishTransaction();
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
		if (!folders.isEmpty()) {
			loadRepository();
		} else {
			Logger.error("No folders selected for repository");
		}
		if (!interrupt) {
			double time = timer.stop();
			long files = repository.countFiles();
			double averageFileTime = time / files;
			Logger.info(StringUtils.getString("Read repository process DONE (", files, " files, ", time, " seconds, ", StringUtils.toString(averageFileTime, 4), " seconds / file)"));
			notifyFinish();
		}
	}

	/**
	 * @return the oldRepository
	 */
	@Override
	public IRepository getOldRepository() {
		return oldRepository;
	}
}
