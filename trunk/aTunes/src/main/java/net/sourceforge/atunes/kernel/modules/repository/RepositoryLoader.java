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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.Repository;
import net.sourceforge.atunes.utils.DirectoryFileFilter;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;
import net.sourceforge.atunes.utils.ValidPicturesFileFilter;

/**
 * Class for loading audio files into repository.
 */
public class RepositoryLoader extends Thread {

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
	 */
	public RepositoryLoader(IState state, RepositoryTransaction transaction, List<File> folders, Repository oldRepository, IRepository repository, boolean refresh, ILocalAudioObjectFactory localAudioObjectFactory) {
		this.transaction = transaction;
		this.refresh = refresh;
		this.folders = folders;
		this.oldRepository = oldRepository;
		this.repository = repository;
		this.state = state;
		this.localAudioObjectFactory = localAudioObjectFactory;
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

			File[] list = folder.listFiles();
			List<File> pictures = new ArrayList<File>();
			if (list != null) {
				for (File element : list) {
					if (element.getName().toUpperCase().endsWith("JPG")) {
						pictures.add(element);
					}
				}
			}

			RepositoryFiller filler = new RepositoryFiller(rep, state);
			for (File f : files) {
				if (f.getParentFile().equals(folder)) {
					ILocalAudioObject audioObject = localAudioObjectFactory.getLocalAudioObject(f);
					audioObject.setExternalPictures(pictures);

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
	 *            the dir
	 * 
	 * @return the int
	 */
	private static int countFiles(File dir) {
		int files = 0;
		File[] list = dir.listFiles();
		if (list == null) {
			return files;
		}
		for (File element : list) {
			if (LocalAudioObjectValidator.isValidAudioFile(element)) {
				files++;
			} else if (element.isDirectory()) {
				files = files + countFiles(element);
			}
		}
		return files;
	}

	/**
	 * Count files in repository.
	 * 
	 * @param rep
	 *            the rep
	 * 
	 * @return the int
	 */
	static int countFilesInRepository(IRepository rep) {
		int files = 0;
		for (File dir : rep.getRepositoryFolders()) {
			files = files + countFiles(dir);
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
	 * 
	 * @param folder
	 * @param listener
	 * @param localAudioObjectFactory
	 * @return
	 */
	public static List<ILocalAudioObject> getSongsForFolder(File folder, IRepositoryLoaderListener listener, ILocalAudioObjectFactory localAudioObjectFactory) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();

		File[] list = folder.listFiles();
		List<File> pictures = new ArrayList<File>();
		List<File> files = new ArrayList<File>();
		if (list != null) {
			// First find pictures, audio and files
			for (File element : list) {
				if (LocalAudioObjectValidator.isValidAudioFile(element)) {
					files.add(element);
				} else if (element.isDirectory()) {
					result.addAll(getSongsForFolder(element, listener, localAudioObjectFactory));
				} else if (element.getName().toUpperCase().endsWith("JPG")) {
					pictures.add(element);
				}
			}

			for (int i = 0; i < files.size(); i++) {
				ILocalAudioObject audioObject = localAudioObjectFactory.getLocalAudioObject(files.get(i));
				audioObject.setExternalPictures(pictures);
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
	 * 
	 * @param folders
	 * @param listener
	 * @param localAudioObjectFactory
	 * @return
	 */
	public static List<ILocalAudioObject> getSongsForFolders(List<File> folders, IRepositoryLoaderListener listener, ILocalAudioObjectFactory localAudioObjectFactory) {
		int filesCount = 0;
		for (File folder : folders) {
			filesCount = filesCount + countFiles(folder);
		}
		if (listener != null) {
			listener.notifyFilesInRepository(filesCount);
		}
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (File folder : folders) {
			result.addAll(getSongsForFolder(folder, listener, localAudioObjectFactory));
		}
		if (listener != null) {
			listener.notifyFinishRead(null);
		}
		return result;
	}

	/**
	 * Refresh file
	 * 
	 * @param repository
	 *            the repository
	 * @param file
	 *            the file
	 */
	static void refreshFile(IState state, IRepository repository, ILocalAudioObject file, IStatisticsHandler statisticsHandler) {
		try {
			// Get old tag
			ITag oldTag = file.getTag();
			
			// Update tag
			file.refreshTag();		
			
			// Update repository
			new RepositoryFiller(repository, state).refreshAudioFile(file, oldTag);

			// Compare old tag with new tag
			ITag newTag = file.getTag();
			if (newTag != null) {
				boolean artistChanged = oldTag.getArtist() == null && newTag.getArtist() != null ||
									    oldTag.getArtist() != null && newTag.getArtist() == null || 
									    !oldTag.getArtist().equals(newTag.getArtist());
				
				boolean albumChanged = oldTag.getAlbum() == null && newTag.getAlbum() != null ||
									   oldTag.getAlbum() != null && newTag.getAlbum() == null ||
									   !oldTag.getAlbum().equals(newTag.getAlbum());
				
				boolean oldArtistRemoved = false;
				
				if (artistChanged) {
					Artist oldArtist = repository.getArtist(oldTag.getArtist());
					if (oldArtist == null) {
						// Artist has been renamed -> Update statistics
						statisticsHandler.replaceArtist(oldTag.getArtist(), newTag.getArtist());
						oldArtistRemoved = true;
					}
				}
				if (albumChanged) {
					Artist artistWithOldAlbum = repository.getArtist(oldArtistRemoved ? newTag.getArtist() : oldTag.getArtist()); 
					Album oldAlbum = artistWithOldAlbum.getAlbum(oldTag.getAlbum());
					if (oldAlbum == null) {
						// Album has been renamed -> Update statistics
						statisticsHandler.replaceAlbum(artistWithOldAlbum.getName(), oldTag.getAlbum(), newTag.getAlbum());
					}
				}
			}

		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
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
				if (LocalAudioObjectValidator.isValidAudioFile(element)) {
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
            
            // Get pictures
            List<File> picturesList = processPictures(dir);
            
            // Process audio files
            processAudioFiles(dir, picturesList, relativeTo);            
        }
    }
	
	/**
	 * Finds pictures in a folder
	 * @param dir
	 * @return
	 */
	private List<File> processPictures(File dir) {
        // Get Pictures
        File[] pictures = dir.listFiles(new ValidPicturesFileFilter());
        return pictures != null && pictures.length > 0 ? Arrays.asList(pictures) : null;
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
	 * @param picturesList
	 * @param relativeTo
	 */
	private void processAudioFiles( File dir, List<File> picturesList, File relativeTo) {
        // Get audio files
        File[] audiofiles = dir.listFiles(LocalAudioObjectValidator.validAudioFileFilter());
        
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
        			processAudioFile(audiofile, picturesList, filler, relativeTo, relativePath);
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
	 * @param picturesList
	 * @param filler
	 * @param relativeTo
	 * @param relativePath
	 */
	private void processAudioFile(File audiofile, List<File> picturesList, RepositoryFiller filler, File relativeTo, String relativePath) {
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

		audio.setExternalPictures(picturesList);
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

	/**
	 * Adds the external picture for album.
	 * 
	 * @param artistName
	 *            the artist name
	 * @param albumName
	 *            the album name
	 * @param picture
	 *            the picture
	 */
	static void addExternalPictureForAlbum(IRepository repository,
			String artistName, String albumName, File picture) {
		if (repository != null) {
			Artist artist = repository.getArtist(artistName);
			if (artist == null) {
				return;
			}
			Album album = artist.getAlbum(albumName);
			if (album == null) {
				return;
			}

			List<ILocalAudioObject> audioFiles = album.getAudioObjects();
			for (ILocalAudioObject af : audioFiles) {
				af.addExternalPicture(picture);
			}
		}
	}

	/**
	 * Permanently deletes an audio file from the repository metainformation
	 * 
	 * This method marks repository as dirty but new state is not notified
	 * To notify repository change call Repository.setDirty(true, true) when finish
	 * 
	 * @param file
	 * @param osManager
	 * @param repositoryHandler
	 * @param deviceHandler
	 */
	static void deleteFile(ILocalAudioObject file, IOSManager osManager, IRepositoryHandler repositoryHandler, IDeviceHandler deviceHandler) {
		String albumArtist = file.getAlbumArtist();
		String artist = file.getArtist();
		String album = file.getAlbum();
		String genre = file.getGenre();
		String year = file.getYear();

		// Only do this if file is in repository
		if (getFolderForFile(file, osManager, repositoryHandler) != null) {
			// Remove from file structure
			Folder f = getFolderForFile(file, osManager, repositoryHandler);
			if (f != null) {
				f.removeAudioFile(file);
				// If folder is empty, remove too
				if (f.isEmpty()) {
					f.getParentFolder().removeFolder(f);
				}
			}

			// Remove from tree structure
			Artist a = repositoryHandler.getArtist(albumArtist);
			if (a == null) {
				a = repositoryHandler.getArtist(artist);
			}
			if (a != null) {
				Album alb = a.getAlbum(album);
				if (alb != null) {
					if (alb.size() == 1) {
						a.removeAlbum(alb);
					} else {
						alb.removeAudioFile(file);
					}

					if (a.size() <= 1) {
						repositoryHandler.removeArtist(a);
					}
				}
			}

			// Remove from genre structure
			Genre g = repositoryHandler.getGenre(genre);
			if (g != null) {
				g.removeAudioFile(file);
				if (g.size() <= 1) {
					repositoryHandler.removeGenre(g);
				}
			}

			// Remove from year structure
			Year y = repositoryHandler.getYear(year);
			if (y != null) {
				y.removeAudioFile(file);
				if (y.size() <= 1) {
					repositoryHandler.removeYear(y);
				}
			}

			// Remove from repository
			repositoryHandler.removeFile(file);
		}
		// File is on a device
		else if (deviceHandler.isDevicePath(file.getUrl())) {
			Logger.info("Deleted file ", file, " from device");
		}
	}

	/**
	 * Finds folder that contains file.
	 * 
	 * @param file
	 *            Audio file for which the folder is wanted
	 * 
	 * @param osManager
	 * @param repositoryHandler
	 * @return Either folder or null if file is not in repository
	 */
	private static Folder getFolderForFile(ILocalAudioObject file, IOSManager osManager, IRepositoryHandler repositoryHandler) {
		// Get repository folder where file is
		File repositoryFolder = repositoryHandler.getRepositoryFolderContainingFile(file);
		// If the file is not in the repository, return null
		if (repositoryFolder == null) {
			return null;
		}

		// Get root folder
		Folder rootFolder = repositoryHandler.getFolder(repositoryFolder.getAbsolutePath());

		// Now navigate through folder until find folder that contains file
		String path = file.getFile().getParentFile().getAbsolutePath();
		path = path.replace(repositoryFolder.getAbsolutePath(), "");

		Folder f = rootFolder;
		StringTokenizer st = new StringTokenizer(path, osManager.getFileSeparator());
		while (st.hasMoreTokens()) {
			String folderName = st.nextToken();
			f = f.getFolder(folderName);
		}
		return f;
	}

	/**
	 * Refreshes folder
	 * @param state
	 * @param repository
	 * @param folders
	 * @param statisticsHandler
	 * @param osManager
	 * @param repositoryHandler
	 * @param localAudioObjectFactory
	 */
	public static void refreshFolders(IState state, IRepository repository, List<Folder> folders, IStatisticsHandler statisticsHandler, IOSManager osManager, IRepositoryHandler repositoryHandler, ILocalAudioObjectFactory localAudioObjectFactory) {
		repositoryHandler.startTransaction();
		
		for (Folder folder : folders) {
			// Remove o refresh previous files		
			List<ILocalAudioObject> aos = folder.getAudioObjects();
			for (ILocalAudioObject ao : aos) {
				if (ao.getFile().exists()) {
					Logger.debug("Refreshing file: ", ao.getFile().getAbsolutePath());
					refreshFile(state, repository, ao, statisticsHandler);
				} else {
					Logger.debug("Removing file: ", ao.getFile().getAbsolutePath());
					repositoryHandler.remove(Collections.singletonList(ao));
				}
			}

			// Add new files
			List<ILocalAudioObject> allObjects = getSongsForFolder(folder.getFolderPath(osManager), null, localAudioObjectFactory);
			for (ILocalAudioObject ao : allObjects) {
				if (repository.getFile(ao.getFile().getAbsolutePath()) == null) {
					Logger.debug("Adding file: ", ao.getFile().getAbsolutePath());
					addToRepository(state, repository, Collections.singletonList(ao.getFile()), localAudioObjectFactory);
				}
			}
		}
		
		repositoryHandler.endTransaction();
	}

}
