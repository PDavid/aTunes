/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.model;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;

public interface IRepositoryHandler extends IHandler, IRepositoryLoaderListener, IAudioFilesRemovedListener, IRepositoryListener {

	/**
	 * Adds the given files to repository and refresh.
	 * 
	 * @param files
	 *            the files
	 */
	public void addFilesAndRefresh(final List<File> files);

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
	public void addExternalPictureForAlbum(String artistName, String albumName,
			File picture);

	public List<File> getFolders();

	/**
	 * Gets the albums.
	 * 
	 * @return the albums
	 */
	public List<Album> getAlbums();

	/**
	 * Gets the artists.
	 * 
	 * @return the artists
	 */
	public List<Artist> getArtists();

	/**
	 * Returns artist with given name
	 * @param name
	 * @return
	 */
	public Artist getArtist(String name);

	/**
	 * Removes artist
	 * @param artist
	 */
	public void removeArtist(Artist artist);

	/**
	 * Returns genre with given name
	 * @param genre
	 * @return
	 */
	public Genre getGenre(String genre);

	/**
	 * Removes genre
	 * @param genre
	 */
	public void removeGenre(Genre genre);

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
	 * Gets the path for new audio files ripped.
	 * 
	 * @return the path for new audio files ripped
	 */
	public String getPathForNewAudioFilesRipped();

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
	public List<ILocalAudioObject> getAudioFilesForAlbums(
			Map<String, Album> albums);

	/**
	 * Gets the audio files for artists.
	 * 
	 * @param artists
	 *            the artists
	 * 
	 * @return the audio files for artists
	 */
	public List<ILocalAudioObject> getAudioFilesForArtists(
			Map<String, Artist> artists);

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
	 * Refreshes a file after being modified
	 * 
	 * @param file
	 *            the file
	 */
	public void refreshFile(ILocalAudioObject file);

	/**
	 * Refreshes a folder
	 * 
	 * @param file
	 *            the file
	 */
	public void refreshFolders(List<Folder> folders);

	/**
	 * Refresh repository.
	 */
	public void refreshRepository();

	/**
	 * Removes a list of folders from repository.
	 * 
	 * @param foldersToRemove
	 */
	public void removeFolders(List<Folder> foldersToRemove);

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
	 * Repository is null.
	 * 
	 * @return true, if successful
	 */
	public boolean repositoryIsNull();

	/**
	 * Retrieve.
	 * 
	 * @param folders
	 *            the folders
	 * 
	 * @return true, if successful
	 */
	public boolean retrieve(List<File> folders);

	/**
	 * Select repository.
	 * 
	 * @return true, if successful
	 */
	public boolean selectRepository();

	/**
	 * Imports folders to repository
	 */
	public void importFoldersToRepository();

	/**
	 * Imports folders passed as argument to repository
	 * 
	 * @param folders
	 * @param path
	 */
	public void importFolders(final List<File> folders, final String path);

	/**
	 * Adds a listener to be notified when an audio file is removed
	 * 
	 * @param listener
	 */
	public void addAudioFilesRemovedListener(IAudioFilesRemovedListener listener);

	/**
	 * When a repository load in progress, executes task in background, hidding progress to user
	 */
	public void doInBackground();

	/**
	 * Returns folder where repository configuration is stored
	 * 
	 * @return
	 */
	public String getRepositoryConfigurationFolder();

	/**
	 * Starts a transaction
	 */
	public void startTransaction();

	/**
	 * Ends a transaction
	 */
	public void endTransaction();

	/**
	 * Returns data to show in tree
	 * @param viewMode
	 * @return
	 */
	public Map<String, ?> getDataForView(ViewMode viewMode);

	public ILocalAudioObject getFile(String fileName);

	/**
	 * @param year
	 * @return
	 * @see net.sourceforge.atunes.model.Repository#getYear(java.lang.String)
	 */
	public Year getYear(String year);

	/**
	 * @param year
	 * @see net.sourceforge.atunes.model.Repository#removeYear(net.sourceforge.atunes.kernel.modules.repository.data.Year)
	 */
	public void removeYear(Year year);

	/**
	 * @param file
	 * 
	 */
	public void removeFile(ILocalAudioObject file);

	/**
	 * @param path
	 * @return
	 * @see net.sourceforge.atunes.model.Repository#getFolder(java.lang.String)
	 */
	public Folder getFolder(String path);

}