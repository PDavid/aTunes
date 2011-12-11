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

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryLoader;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.DirectoryFileFilter;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;

/**
 * Class for loading audio files into repository.
 */
public class RepositoryLoader implements IRepositoryLoader, Runnable {

	// Some attributes to speed up populate info process
	private IRepositoryLoaderListener listener;
	private List<File> folders;
	private boolean refresh;
	private boolean interrupt;
	private IRepository oldRepository;
	private IRepository repository;
	private int totalFilesToLoad;
	private int filesLoaded;
	private long startReadTime;
	private String fastRepositoryPath;
	private int fastFirstChar;
	private IState state;
	private ILocalAudioObjectFactory localAudioObjectFactory;
	private ILocalAudioObjectValidator localAudioObjectValidator;
	private FileFilter validLocalAudioObjectFileFilter;
	
	private RepositoryTransaction transaction;

	/**
	 * Starts load
	 * @param state
	 * @param transaction
	 * @param folders
	 * @param oldRepository
	 * @param repository
	 * @param refresh
	 */
	@Override
	public void start(RepositoryTransaction transaction, List<File> folders, IRepository oldRepository, IRepository repository, boolean refresh) {
		this.transaction = transaction;
		this.refresh = refresh;
		this.folders = folders;
		this.oldRepository = oldRepository;
		this.repository = repository;
		Thread t = new Thread(this);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
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
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
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
	public void addRepositoryLoaderListener(IRepositoryLoaderListener listener) {
		this.listener = listener;
	}

	/**
	 * Count files in dir.
	 * 
	 * @param dir
	 *            the dir
	 * 
	 * @return the int
	 */
	private int countFilesInDir(File dir) {
		int files = 0;
		if (!interrupt) {
			File[] list = dir.listFiles();
			if (list == null) {
				return files;
			}
			for (File element : list) {
				if (localAudioObjectValidator.isValidAudioFile(element)) {
					files++;
				} else if (element.isDirectory()) {
					files = files + countFilesInDir(element);
				}
			}
		}
		return files;
	}

	/**
	 * Count files in dir.
	 * 
	 * @param folders1
	 *            the folders1
	 * 
	 * @return the int
	 */
	private int countFilesInDir(List<File> folders1) {
		int files = 0;
		for (File f : folders1) {
			files = files + countFilesInDir(f);
		}
		return files;
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
		if (!refresh) {
			totalFilesToLoad = countFilesInDir(folders);
			if (listener != null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						listener.notifyFilesInRepository(totalFilesToLoad);
					}
				});
			}
		}
		startReadTime = System.currentTimeMillis();

		for (File folder : folders) {
			fastRepositoryPath = folder.getAbsolutePath()
			.replace('\\', '/');
			if (fastRepositoryPath.endsWith("/")) {
				fastRepositoryPath = fastRepositoryPath.substring(0,
						fastRepositoryPath.length() - 2);
			}
			fastFirstChar = fastRepositoryPath.length() + 1;

			if (folder.exists()) {
				navigateDir(folder, folder);
			}
		}

	}

	/**
	 * Navigate dir.
	 * 
	 * @param relativeTo
	 *            the relative to
	 * @param dir
	 *            the dir
	 */
	private void navigateDir(File relativeTo, File dir) {
        if (!interrupt) {

            // Process directories
            processDirectories(dir, relativeTo);
            
            // Process audio files
            processAudioFiles(dir, relativeTo);            
        }
    }
	
	/**
	 * Process directory
	 * @param dir
	 * @param relativeTo
	 */
	private void processDirectories(File dir, File relativeTo) {
        // Directories
        File[] dirs = dir.listFiles(new DirectoryFileFilter());
        
        // Process directories
        if (dirs != null) {
        	for (File directory : dirs) {
        		navigateDir(relativeTo, directory);
        	}
        }
	}
	
	/**
	 * Process audio files in a directory
	 * @param dir
	 * @param relativeTo
	 */
	private void processAudioFiles(File dir, File relativeTo) {
        // Get audio files
        File[] audiofiles = dir.listFiles(validLocalAudioObjectFileFilter);
        
        String pathToFile = dir.getAbsolutePath().replace('\\', '/');

        int lastChar = pathToFile.lastIndexOf('/') + 1;
        final String relativePath;
        if (fastFirstChar <= lastChar) {
            relativePath = pathToFile.substring(fastFirstChar);
        } else {
            relativePath = ".";
        }

        if (listener != null && !refresh) {
            listener.notifyCurrentPath(relativePath);
        }

        // Process audio files
        if (audiofiles != null) {
        	RepositoryFiller filler = new RepositoryFiller(repository, state);
        	for (File audiofile : audiofiles) {
        		if (!interrupt) {
        			processAudioFile(audiofile, filler, relativeTo, relativePath);
        		}
        	}
        	
    		// Update remaining time after each folder
    		if (!refresh && listener != null && !interrupt) {
    			long t1 = System.currentTimeMillis();
    			final long remainingTime = filesLoaded != 0 ? (totalFilesToLoad - filesLoaded) * (t1 - startReadTime) / filesLoaded : 0;
    			listener.notifyRemainingTime(remainingTime);
    			listener.notifyReadProgress();
    		}
        }
	}
	
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

		if (!refresh && listener != null) {
			listener.notifyFileLoaded();
		}
		filesLoaded++;
		filler.addAudioFile(audio, relativeTo, relativePath);

		// Update current artist and album
		if (!refresh && listener != null) {
			listener.notifyCurrentAlbum(audio.getArtist(), audio.getAlbum());
		}		
	}

	/**
	 * Notify finish.
	 */
	private void notifyFinish() {
		transaction.finishTransaction();
		
		if (listener == null) {
			return;
		}
		if (refresh) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					listener.notifyFinishRefresh(RepositoryLoader.this);
				}
			});
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					listener.notifyFinishRead(RepositoryLoader.this);
				}
			});
		}
	}

	@Override
	public void run() {
		Logger.info("Starting repository read");
		
		Timer timer = new Timer();
		timer.start();
		if (!folders.isEmpty()) {
			loadRepository();
		} else {
			Logger.error(					"No folders selected for repository");
		}
		if (!interrupt) {
			double time = timer.stop();
			long files = repository.countFiles();
			double averageFileTime = time / files;
			Logger.info(
										StringUtils.getString("Read repository process DONE (",
							files, " files, ", time, " seconds, ", StringUtils
									.toString(averageFileTime, 4),
							" seconds / file)"));

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
