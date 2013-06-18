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
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Responsible of managing repository
 * 
 * @author alex
 * 
 */
public interface IRepositoryHandler extends IHandler,
		IAudioFilesRemovedListener, IRepositoryListener {

	/**
	 * Returns folders of repository
	 * 
	 * @return
	 */
	public List<File> getFolders();

	/**
	 * Gets the albums.
	 * 
	 * @return the albums
	 */
	public List<IAlbum> getAlbums();

	/**
	 * Gets the artists.
	 * 
	 * @return the artists
	 */
	public List<IArtist> getArtists();

	/**
	 * @return the years
	 */
	public List<IYear> getYears();

	/**
	 * @return the genres
	 */
	public List<IGenre> getGenres();

	/**
	 * Returns artist with given name
	 * 
	 * @param name
	 * @return
	 */
	public IArtist getArtist(String name);

	/**
	 * Removes artist
	 * 
	 * @param artist
	 */
	public void removeArtist(IArtist artist);

	/**
	 * Returns genre with given name
	 * 
	 * @param genre
	 * @return
	 */
	public IGenre getGenre(String genre);

	/**
	 * Removes genre
	 * 
	 * @param genre
	 */
	public void removeGenre(IGenre genre);

	/**
	 * Gets the file if loaded.
	 * 
	 * @param fileName
	 *            the file name
	 * 
	 * @return the file if loaded
	 */
	public ILocalAudioObject getFileIfLoaded(String fileName);

	/**
	 * Returns number of root folders of repository
	 * 
	 * @return
	 */
	public int getFoldersCount();

	/**
	 * Returns repository root folder that contains file.
	 * 
	 * @param file
	 *            the file
	 * 
	 * @return the repository folder containing file
	 */
	public File getRepositoryFolderContainingFile(ILocalAudioObject file);

	/**
	 * Gets the repository path.
	 * 
	 * @return the repository path
	 */
	public String getRepositoryPath();

	/**
	 * Gets the repository total size.
	 * 
	 * @return the repository total size
	 */
	public long getRepositoryTotalSize();

	/**
	 * Gets the number of files of repository
	 * 
	 * @return
	 */
	public int getNumberOfFiles();

	/**
	 * Gets the audio files.
	 * 
	 * @return the audio files
	 */
	public Collection<ILocalAudioObject> getAudioFilesList();

	/**
	 * Gets the audio files for albums.
	 * 
	 * @param albums
	 *            the albums
	 * 
	 * @return the audio files for albums
	 */
	public List<ILocalAudioObject> getAudioFilesForAlbums(List<IAlbum> albums);

	/**
	 * Gets the audio files for artists.
	 * 
	 * @param artists
	 *            the artists
	 * 
	 * @return the audio files for artists
	 */
	public List<ILocalAudioObject> getAudioFilesForArtists(List<IArtist> artists);

	/**
	 * Returns true if folder is in repository.
	 * 
	 * @param folder
	 *            the folder
	 * 
	 * @return true, if checks if is repository
	 */
	public boolean isRepository(File folder);

	/**
	 * Notify cancel.
	 */
	public void notifyCancel();

	/**
	 * Refreshes files after being modified
	 * 
	 * @param file
	 */
	public void refreshFiles(List<ILocalAudioObject> file);

	/**
	 * Refreshes a list of folder
	 * 
	 * @param folders
	 */
	public void refreshFolders(List<IFolder> folders);

	/**
	 * Refresh repository.
	 */
	public void refreshRepository();

	/**
	 * Reloads repository (used after a change in settings that may need read
	 * ALL information again)
	 */
	public void reloadRepository();

	/**
	 * Removes a list of folders from repository.
	 * 
	 * @param foldersToRemove
	 */
	public void removeFolders(List<IFolder> foldersToRemove);

	/**
	 * Removes a list of files from repository
	 * 
	 * @param filesToRemove
	 *            Files that should be removed
	 */
	public void remove(List<ILocalAudioObject> filesToRemove);

	/**
	 * Renames an audio file
	 * 
	 * @param audioFile
	 *            the audio file that should be renamed
	 * @param name
	 *            the new name of the audio file
	 */
	public void rename(ILocalAudioObject audioFile, String name);

	/**
	 * Select repository.
	 * 
	 * @return true, if successful
	 */
	public boolean addFolderToRepository();

	/**
	 * Adds a listener to be notified when an audio file is removed
	 * 
	 * @param listener
	 */
	public void addAudioFilesRemovedListener(IAudioFilesRemovedListener listener);

	/**
	 * When a repository load in progress, executes task in background, hidding
	 * progress to user
	 */
	public void doInBackground();

	/**
	 * Returns data to show in tree
	 * 
	 * @param viewMode
	 * @return
	 */
	public Map<String, ?> getDataForView(ViewMode viewMode);

	/**
	 * Returns file of repository given its name
	 * 
	 * @param fileName
	 * @return
	 */
	public ILocalAudioObject getFile(String fileName);

	/**
	 * @param year
	 * @return
	 */
	public IYear getYear(String year);

	/**
	 * @param year
	 */
	public void removeYear(IYear year);

	/**
	 * @param file
	 * 
	 */
	public void removeFile(ILocalAudioObject file);

	/**
	 * @param path
	 * @return
	 */
	public IFolder getFolder(String path);

	/**
	 * Returns audio objects given its artist and titles
	 * 
	 * @param artist
	 * @param titles
	 * @return
	 */
	public List<ILocalAudioObject> getAudioObjectsByTitle(String artist,
			List<String> titles);

	/**
	 * Checks availability of each track of an artist
	 * 
	 * @param artist
	 * @param tracks
	 * @return
	 */
	void checkAvailability(String artist, List<ITrackInfo> tracks);

	/**
	 * Imports folders to repository
	 * 
	 * @param folders
	 * @param path
	 */
	void importFolders(final List<File> folders, final String path);

	/**
	 * Tell handler to use these folders as repository
	 * 
	 * @param folders
	 */
	void setRepositoryFolders(List<File> folders);

	/**
	 * Updates repository state when a folder is moved to another location
	 * 
	 * @param sourceFolder
	 * @param destination
	 */
	void folderMoved(IFolder sourceFolder, File destination);

	/**
	 * Adds the given audio objects to repository and refresh.
	 * 
	 * @param result
	 */
	void addAudioObjectsAndRefresh(List<ILocalAudioObject> result);

	/**
	 * @param ao
	 * @return cached audio object or given one
	 */
	IAudioObject getAudioObjectIfLoaded(IAudioObject ao);

	/**
	 * @param artist
	 * @return true if given artist exists
	 */
	boolean existsArtist(IArtist artist);

	/**
	 * @param artist
	 * @return true if given artist exists
	 */
	boolean existsArtist(String artist);

	/**
	 * @param album
	 * @return true if given album exists
	 */
	public boolean existsAlbum(IAlbum album);

	/**
	 * @param artist
	 * @param album
	 * @return true if given album exists
	 */
	public boolean existsAlbum(String artist, String album);

	/**
	 * @param ao
	 * @return true if audio object exists
	 */
	public boolean existsFile(ILocalAudioObject ao);

	/**
	 * @return true when repository is initialized
	 */
	public boolean isRepositoryNotSelected();

}