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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.kernel.modules.repository.exception.InconsistentRepositoryException;
import net.sourceforge.atunes.utils.Logger;

import org.joda.time.DateTime;

public class Repository implements Serializable {

    private static final long serialVersionUID = -8278937514875788175L;

    /**
     * Root folders of repository
     */
    private List<File> folders;
    
    /**
     *  The total size in bytes of all files 
     */
    private long totalSizeInBytes;
    
    /** 
     * The total duration in seconds of all files 
     */
    private long totalDurationInSeconds;
    
    /**
     * File structure
     */
    private RepositoryStructure<ILocalAudioObject> filesStructure;
    
    /**
     * Artists structure
     */
    private RepositoryStructure<Artist> artistsStructure;
    
    /**
     * Folders structure
     */
    private RepositoryStructure<Folder> foldersStructure;
    
    /**
     * Genres structure
     */
    private RepositoryStructure<Genre> genresStructure;
    
    /**
     *  Year structure
     */
    private RepositoryStructure<Year> yearStructure;
    
    /**
     * Object to be notified when this repository becomes dirty
     */
    private transient IRepositoryListener listener;
    
    /**
     * Current transaction
     */
    private transient RepositoryTransaction transaction;

    /**
     * State
     */
    private transient IState state;
    
    /**
     * Instantiates a new repository.
     * 
     * @param folders
     *            the folders
     */
    public Repository(List<File> folders, IRepositoryListener listener, IState state) {
        this.folders = folders;
        this.filesStructure = new RepositoryStructure<ILocalAudioObject>();
        this.artistsStructure = new RepositoryStructure<Artist>();
        this.foldersStructure = new RepositoryStructure<Folder>();
        this.genresStructure = new RepositoryStructure<Genre>();
        this.yearStructure = new RepositoryStructure<Year>();
        this.listener = listener;
        this.state = state;
    }

    /**
     * This constructor can't be used
	 */
    @SuppressWarnings("unused")
    private Repository() {}

    /**
     * @param state
     */
    public void setState(IState state) {
    	this.state = state;
    }
    
    /**
     * Gets the folders.
     * 
     * @return the folders
     */
    public List<File> getRepositoryFolders() {
        return new ArrayList<File>(folders);
    }

    /**
     * Adds the duration in seconds.
     * 
     * @param seconds
     *            the seconds
     */
    public void addDurationInSeconds(long seconds) {
        this.totalDurationInSeconds += seconds;
    }
    
    /**
     * Removes the duration in seconds.
     * 
     * @param seconds
     *            the seconds
     */
    public void removeDurationInSeconds(long seconds) {
        totalDurationInSeconds -= seconds;
    }

    /**
     * Gets the total duration in seconds.
     * 
     * @return the total duration in seconds
     */
    public long getTotalDurationInSeconds() {
        return totalDurationInSeconds;
    }

    /**
     * Adds the size in bytes
     * @param bytes
     */
    public void addSizeInBytes(long bytes) {
    	totalSizeInBytes += bytes;
    }
    
    /**
     * Removes the size in bytes
     * @param bytes
     */
    public void removeSizeInBytes(long bytes) {
    	totalSizeInBytes -= bytes;
    }
    
    /**
     * Gets the total size in bytes.
     * 
     * @return the total size in bytes
     */
    public long getTotalSizeInBytes() {
        return totalSizeInBytes;
    }

    /**
     * Checks if repository exists on disk
     * 
     * @return if repository exists on disk
     */
    public boolean exists() {
    	// All folders must exist
    	for (File folder : getRepositoryFolders()) {
    		if (!folder.exists()) {
    			return false;
    		}
    	}
        return true;
    }

    /**
     * Validates this repository throwing exception if object is not consistent. 
     * For example when a new attribute is added a repository object without that attribute can be considered invalid
     * @throws InconsistentRepositoryException
     */
    public void validateRepository() throws InconsistentRepositoryException {
        if (filesStructure == null || 
            getRepositoryFolders() == null || 
            artistsStructure == null || 
            foldersStructure == null || 
            genresStructure == null ||
            yearStructure == null) {
                throw new InconsistentRepositoryException();
        }
    }

	public void setListener(IRepositoryListener listener) {
		this.listener = listener;
	}
	
	/**
	 * Starts a transaction to change repository
	 */
	public void startTransaction() {
		this.transaction = new RepositoryTransaction(this, listener);
	}
	
	/**
	 * Ends a transaction to change repository
	 */
	public void endTransaction() {
		if (this.transaction != null) {
			this.transaction.finishTransaction();
		}
	}
	
	/**
	 * Returns true if there is a repository transaction not finished
	 * @return
	 */
	public boolean transactionPending() {
		return this.transaction != null && this.transaction.isPending();
	}
	
	public static final class RepositoryTransaction {
		
		private Repository repository;
		private IRepositoryListener listener;
		private volatile boolean pending;
		
		private RepositoryTransaction(Repository repository, IRepositoryListener listener) {
			this.repository = repository;
			this.listener = listener;
			this.pending = true;
			Logger.debug("Creating new repository transaction: ", new DateTime().toString());
		}
		
		public void finishTransaction() {
			if (listener != null) {
				listener.repositoryChanged(this.repository);
			}
			this.pending = false;
			Logger.debug("Finished repository transaction: ", new DateTime().toString());
		}
		
		public boolean isPending() {
			return this.pending;
		}
	}
	
    //------------------------------------------ FILE OPERATIONS ------------------------------------- //
    
    /**
     * Count files.
     * 
     * @return the int
     */
    public int countFiles() {
        return filesStructure.count();
    }

    /**
     * Gets the file.
     * 
     * @param fileName
     *            the file name
     * 
     * @return the file
     */
    public ILocalAudioObject getFile(String fileName) {
        return filesStructure.get(fileName);
    }
    
    /**
     * Gets all files
     * @return
     */
    public Collection<ILocalAudioObject> getFiles() {
    	return filesStructure.getAll();
    }
    
    /**
     * Puts a new file
     * @param file
     * @return
     */
    public ILocalAudioObject putFile(ILocalAudioObject file) {
    	filesStructure.put(file.getUrl(), file);
    	return file;
    }

	/**
	 * Removes a file
	 * @param file
	 */
	public void removeFile(ILocalAudioObject file) {
		filesStructure.remove(file.getUrl());
	}
	
	/**
	 * Removes a file
	 * @param file
	 */
	public void removeFile(File file) {
		filesStructure.remove(file.getAbsolutePath());
	}
	
	// --------------------------------------- ARTIST OPERATIONS ------------------------------------- //
	
    /**
     * Access artist structure
     * @return
     */
    Map<String, Artist> getArtistStructure() {
        return artistsStructure.getStructure();
    }
    
	/**
	 * Return number of artists
	 * @return
	 */
	public int countArtists() {
		return artistsStructure.count();
	}
	
    /**
     * Returns artist given by name or null
     * @param artistName
     * @return
     */
    public Artist getArtist(String artistName) {
    	if (artistName == null) {
    		return null;
    	} else if (state.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		return artistsStructure.get(artistName);
    	} else {
    		return artistsStructure.get(artistName.toLowerCase());
    	}
    }
    
    /**
     * Returns all artists
     * @return
     */
    public Collection<Artist> getArtists() {
    	return artistsStructure.getAll();
    }
    
    /**
     * Adds an artist to repository
     * @param artistName
     * @return created artist
     */
    public Artist putArtist(String artistName) {
    	Artist artist = new Artist(artistName);
    	if (state.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		artistsStructure.put(artistName, artist);
    	} else {
    		artistsStructure.put(artistName.toLowerCase(), artist);
    	}
    	return artist;
    }
    
    /**
     * Removes artist from repository
     * @param artist
     */
    public void removeArtist(Artist artist) {
    	if (state.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		artistsStructure.remove(artist.getName());
    	} else {
    		artistsStructure.remove(artist.getName().toLowerCase());
    	}
    }
    
    // -------------------------------- ALBUM OPERATIONS ----------------------------------------- //
    
    /**
     * Access album structure
     * @return
     */
    Map<String, Album> getAlbumStructure() {
        Map<String, Album> albumsStructure = new HashMap<String, Album>();
        Collection<Artist> artistCollection = getArtists();
        for (Artist artist : artistCollection) {
            for (Album album : artist.getAlbums().values()) {
                albumsStructure.put(album.getNameAndArtist(), album);
            }
        }
        return albumsStructure;
    }
    
    // -------------------------------- FOLDER OPERATIONS ----------------------------------------- //
    
    /**
     * Access folder structure
     * @return
     */
    Map<String, Folder> getFolderStructure() {
        return foldersStructure.getStructure();
    }

    /**
     * Returns folder
     * @param path
     * @return
     */
    public Folder getFolder(String path) {
    	return foldersStructure.get(path);
    }
    
    /**
     * Returns all folders
     * @return
     */
    public Collection<Folder> getFolders() {
    	return foldersStructure.getAll();
    }
    
    /**
     * Puts folder
     * @param folder
     * @return
     */
    public Folder putFolder(Folder folder) {
    	foldersStructure.put(folder.getName(), folder);
    	return folder;
    }

    // --------------------------------------------------- GENRE OPERATIONS ---------------------------------------------- //
    
    /**
     * Returns genre structure
     * @return
     */
    Map<String, Genre> getGenreStructure() {
        return genresStructure.getStructure();
    }

    /**
     * Returns all genres
     * @return
     */
    public Collection<Genre> getGenres() {
    	return genresStructure.getAll();
    }
    
    /**
     * Returns genre given by name or null
     * @param artistName
     * @return
     */
    public Genre getGenre(String genre) {
    	if (state.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		return genresStructure.get(genre);
    	} else {
    		return genresStructure.get(genre.toLowerCase());
    	}
    }

    /**
     * Adds a genre to repository
     * @param genreName
     * @return created genre
     */
    public Genre putGenre(String genreName) {
    	Genre genre = new Genre(genreName);
    	if (state.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		genresStructure.put(genreName, genre);
    	} else {
    		genresStructure.put(genreName.toLowerCase(), genre);
    	}
    	return genre;
    }

    /**
     * Removes genre from repository
     * @param artist
     */
    public void removeGenre(Genre genre) {
    	if (state.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		genresStructure.remove(genre.getName());
    	} else {
    		genresStructure.remove(genre.getName().toLowerCase());
    	}
    }

    // ----------------------------------------------- YEAR OPERATIONS --------------------------------------------------- //
    
    /**
     * Structure
     * @return
     */
    Map<String, Year> getYearStructure() {
        return yearStructure.getStructure();
    }

    /**
     * Returns year
     * @param year
     * @return
     */
    public Year getYear(String year) {
    	return yearStructure.get(year);
    }
    
    /**
     * Gets all years
     * @return
     */
    public Collection<Year> getYears() {
    	return yearStructure.getAll();
    }
    
    /**
     * Puts a year
     * @param year
     * @return
     */
    public Year putYear(Year year) {
    	yearStructure.put(year.getName(), year);
    	return year;
    }

    /**
     * Removes a year
     * @param year
     */
    public void removeYear(Year year) {
    	yearStructure.remove(year.getName());
    }


}
