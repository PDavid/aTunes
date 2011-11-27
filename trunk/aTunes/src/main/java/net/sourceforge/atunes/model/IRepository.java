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

package net.sourceforge.atunes.model;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.kernel.modules.repository.exception.InconsistentRepositoryException;

public interface IRepository {

	/**
	 * @param state
	 */
	public void setState(IState state);

	/**
	 * Gets the folders.
	 * 
	 * @return the folders
	 */
	public List<File> getRepositoryFolders();

	/**
	 * Adds the duration in seconds.
	 * 
	 * @param seconds
	 *            the seconds
	 */
	public void addDurationInSeconds(long seconds);

	/**
	 * Removes the duration in seconds.
	 * 
	 * @param seconds
	 *            the seconds
	 */
	public void removeDurationInSeconds(long seconds);

	/**
	 * Gets the total duration in seconds.
	 * 
	 * @return the total duration in seconds
	 */
	public long getTotalDurationInSeconds();

	/**
	 * Adds the size in bytes
	 * @param bytes
	 */
	public void addSizeInBytes(long bytes);

	/**
	 * Removes the size in bytes
	 * @param bytes
	 */
	public void removeSizeInBytes(long bytes);

	/**
	 * Gets the total size in bytes.
	 * 
	 * @return the total size in bytes
	 */
	public long getTotalSizeInBytes();

	/**
	 * Checks if repository exists on disk
	 * 
	 * @return if repository exists on disk
	 */
	public boolean exists();

	/**
	 * Validates this repository throwing exception if object is not consistent. 
	 * For example when a new attribute is added a repository object without that attribute can be considered invalid
	 * @throws InconsistentRepositoryException
	 */
	public void validateRepository() throws InconsistentRepositoryException;

	/**
	 * Count files.
	 * 
	 * @return the int
	 */
	public int countFiles();

	/**
	 * Gets the file.
	 * 
	 * @param fileName
	 *            the file name
	 * 
	 * @return the file
	 */
	public ILocalAudioObject getFile(String fileName);

	/**
	 * Gets all files
	 * @return
	 */
	public Collection<ILocalAudioObject> getFiles();

	/**
	 * Puts a new file
	 * @param file
	 * @return
	 */
	public ILocalAudioObject putFile(ILocalAudioObject file);

	/**
	 * Removes a file
	 * @param file
	 */
	public void removeFile(ILocalAudioObject file);

	/**
	 * Removes a file
	 * @param file
	 */
	public void removeFile(File file);

	/**
	 * Return number of artists
	 * @return
	 */
	public int countArtists();

	/**
	 * Returns artist given by name or null
	 * @param artistName
	 * @return
	 */
	public Artist getArtist(String artistName);

	/**
	 * Returns all artists
	 * @return
	 */
	public Collection<Artist> getArtists();

	/**
	 * Adds an artist to repository
	 * @param artistName
	 * @return created artist
	 */
	public Artist putArtist(String artistName);

	/**
	 * Removes artist from repository
	 * @param artist
	 */
	public void removeArtist(Artist artist);

	/**
	 * Returns folder
	 * @param path
	 * @return
	 */
	public Folder getFolder(String path);

	/**
	 * Returns all folders
	 * @return
	 */
	public Collection<Folder> getFolders();

	/**
	 * Puts folder
	 * @param folder
	 * @return
	 */
	public Folder putFolder(Folder folder);

	/**
	 * Returns all genres
	 * @return
	 */
	public Collection<Genre> getGenres();

	/**
	 * Returns genre given by name or null
	 * @param artistName
	 * @return
	 */
	public Genre getGenre(String genre);

	/**
	 * Adds a genre to repository
	 * @param genreName
	 * @return created genre
	 */
	public Genre putGenre(String genreName);

	/**
	 * Removes genre from repository
	 * @param artist
	 */
	public void removeGenre(Genre genre);

	/**
	 * Returns year
	 * @param year
	 * @return
	 */
	public Year getYear(String year);

	/**
	 * Gets all years
	 * @return
	 */
	public Collection<Year> getYears();

	/**
	 * Puts a year
	 * @param year
	 * @return
	 */
	public Year putYear(Year year);

	/**
	 * Removes a year
	 * @param year
	 */
	public void removeYear(Year year);

	/**
	 * Returns data organized by years
	 * @return
	 */
	public Map<String, ?> getYearStructure();

	/**
	 * Returns data organized by genre
	 * @return
	 */
	public Map<String, ?> getGenreStructure();

	/**
	 * Returns data organized by folder
	 * @return
	 */
	public Map<String, ?> getFolderStructure();

	/**
	 * Returns data organized by folder
	 * @return
	 */
	public Map<String, ?> getAlbumStructure();

	/**
	 * Returns data organized by artist
	 * @return
	 */
	public Map<String, ?> getArtistStructure();

}