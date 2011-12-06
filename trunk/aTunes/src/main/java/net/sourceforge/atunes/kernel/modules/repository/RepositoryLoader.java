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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.Repository;
import net.sourceforge.atunes.utils.DirectoryFileFilter;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;

/**
 * Class for loading audio files into repository.
 */
public class RepositoryLoader extends Thread {

	private FileFilter validAudioFileFilter =  new FileFilter() {
		
		@Override
		public boolean accept(File pathname) {
			return localAudioObjectValidator.isValidAudioFile(pathname);
		}
	};

	
	// Some attributes to speed up populate info process
	private IRepositoryLoaderListener listener;
	private List<File> folders;
	private boolean refresh;
	private boolean interrupt;
	private Repository oldRepository;
	private IRepository repository;
	private int totalFilesToLoad;
	private int filesLoaded;
	private long startReadTime;
	private String fastRepositoryPath;
	private int fastFirstChar;
	private IState state;
	private ILocalAudioObjectFactory localAudioObjectFactory;
	private ILocalAudioObjectValidator localAudioObjectValidator;
	
	private RepositoryTransaction transaction;

	/**
	 * Instantiates a new repository loader.
	 * @param state
	 * @param transaction
	 * @param folders
	 * @param oldRepository
	 * @param repository
	 * @param refresh
	 * @param localAudioObjectFactory
	 * @param localAudioObjectValidator
	 */
	public RepositoryLoader(IState state, RepositoryTransaction transaction, List<File> folders, Repository oldRepository, IRepository repository, boolean refresh, ILocalAudioObjectFactory localAudioObjectFactory, ILocalAudioObjectValidator localAudioObjectValidator) {
		this.transaction = transaction;
		this.refresh = refresh;
		this.folders = folders;
		this.oldRepository = oldRepository;
		this.repository = repository;
		this.state = state;
		this.localAudioObjectFactory = localAudioObjectFactory;
		this.localAudioObjectValidator = localAudioObjectValidator;
		setPriority(Thread.MAX_PRIORITY);
	}

	/**
	 * Add files to repository.
	 * @param state
	 * @param rep
	 * @param files
	 * @param localAudioObjectFactory
	 */
	static void addToRepository(IState state, IRepository rep, List<File> files, ILocalAudioObjectFactory localAudioObjectFactory) {
		// Get folders where files are
		Set<File> folders = new HashSet<File>();
		for (File file : files) {
			folders.add(file.getParentFile());
		}

		for (File folder : folders) {
			String repositoryPath = getRepositoryFolderContaining(rep, folder)
					.getAbsolutePath().replace('\\', '/');
			if (repositoryPath.endsWith("/")) {
				repositoryPath = repositoryPath.substring(0, repositoryPath
						.length() - 2);
			}
			int firstChar = repositoryPath.length() + 1;

			RepositoryFiller filler = new RepositoryFiller(rep, state);
			for (File f : files) {
				if (f.getParentFile().equals(folder)) {
					ILocalAudioObject audioObject = localAudioObjectFactory.getLocalAudioObject(f);

					String pathToFile = audioObject.getUrl().replace('\\', '/');
					int lastChar = pathToFile.lastIndexOf('/') + 1;
					String relativePath;
					if (firstChar < lastChar) {
						relativePath = pathToFile.substring(firstChar, lastChar);
					} else {
						relativePath = ".";
					}

					filler.addAudioFile(audioObject, getRepositoryFolderContaining(rep, folder), relativePath);
				}
			}
		}		
	}

	/**
	 * Count files.
	 * 
	 * @param dir
	 * @param localAudioObjectValidator
	 * @return
	 */
	private static int countFiles(File dir, ILocalAudioObjectValidator localAudioObjectValidator) {
		int files = 0;
		File[] list = dir.listFiles();
		if (list == null) {
			return files;
		}
		for (File element : list) {
			if (localAudioObjectValidator.isValidAudioFile(element)) {
				files++;
			} else if (element.isDirectory()) {
				files = files + countFiles(element, localAudioObjectValidator);
			}
		}
		return files;
	}

	/**
	 * Count files in repository.
	 * 
	 * @param rep
	 * @param localAudioObjectValidator
	 * @return
	 */
	static int countFilesInRepository(IRepository rep, ILocalAudioObjectValidator localAudioObjectValidator) {
		int files = 0;
		for (File dir : rep.getRepositoryFolders()) {
			files = files + countFiles(dir, localAudioObjectValidator);
		}
		return files;
	}

	/**
	 * Gets the repository folder containing.
	 * 
	 * @param rep
	 *            the rep
	 * @param folder
	 *            the folder
	 * 
	 * @return the repository folder containing
	 */
	private static File getRepositoryFolderContaining(IRepository rep,
			File folder) {
		String path = folder.getAbsolutePath();
		for (File f : rep.getRepositoryFolders()) {
			if (path.startsWith(f.getAbsolutePath())) {
				return f;
			}
		}
		return null;
	}

	/**
	 * Gets the songs for dir.
	 * @param folder
	 * @param listener
	 * @param localAudioObjectFactory
	 * @param localAudioObjectValidator
	 * @return
	 */
	public static List<ILocalAudioObject> getSongsForFolder(File folder, IRepositoryLoaderListener listener, ILocalAudioObjectFactory localAudioObjectFactory, ILocalAudioObjectValidator localAudioObjectValidator) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();

		File[] list = folder.listFiles();
		List<File> files = new ArrayList<File>();
		if (list != null) {
			// First find audio and files
			for (File element : list) {
				if (localAudioObjectValidator.isValidAudioFile(element)) {
					files.add(element);
				} else if (element.isDirectory()) {
					result.addAll(getSongsForFolder(element, listener, localAudioObjectFactory, localAudioObjectValidator));
				}
			}

			for (int i = 0; i < files.size(); i++) {
				ILocalAudioObject audioObject = localAudioObjectFactory.getLocalAudioObject(files.get(i));
				result.add(audioObject);
				if (listener != null) {
					listener.notifyFileLoaded();
				}
			}
		}
		return result;
	}

	/**
	 * Gets the songs of a list of folders. Used in import
	 * @param folders
	 * @param listener
	 * @param localAudioObjectFactory
	 * @param localAudioObjectValidator
	 * @return
	 */
	public static List<ILocalAudioObject> getSongsForFolders(List<File> folders, IRepositoryLoaderListener listener, ILocalAudioObjectFactory localAudioObjectFactory, ILocalAudioObjectValidator localAudioObjectValidator) {
		int filesCount = 0;
		for (File folder : folders) {
			filesCount = filesCount + countFiles(folder, localAudioObjectValidator);
		}
		if (listener != null) {
			listener.notifyFilesInRepository(filesCount);
		}
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (File folder : folders) {
			result.addAll(getSongsForFolder(folder, listener, localAudioObjectFactory, localAudioObjectValidator));
		}
		if (listener != null) {
			listener.notifyFinishRead(null);
		}
		return result;
	}

	/**
	 * Adds the repository loader listener.
	 * 
	 * @param listener
	 *            the listener
	 */
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
	void interruptLoad() {
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
        File[] audiofiles = dir.listFiles(validAudioFileFilter);
        
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
		
		ImageCache.getImageCache().clearCache();

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
	Repository getOldRepository() {
		return oldRepository;
	}
}
