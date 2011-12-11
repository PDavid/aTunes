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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.model.InconsistentRepositoryException;
import net.sourceforge.atunes.utils.StringUtils;


public class Repository implements Serializable, IRepository {

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
    private RepositoryStructure<IArtist> artistsStructure;
    
    /**
     * Folders structure
     */
    private RepositoryStructure<Folder> foldersStructure;
    
    /**
     * Genres structure
     */
    private RepositoryStructure<IGenre> genresStructure;
    
    /**
     *  Year structure
     */
    private RepositoryStructure<IYear> yearStructure;
    
    /**
     * State
     */
    private transient IState state;
    
    /**
     * Instantiates a new repository.
     * @param folders
     * @param state
     */
    public Repository(List<File> folders, IState state) {
        this.folders = folders;
        this.filesStructure = new RepositoryStructure<ILocalAudioObject>();
        this.artistsStructure = new RepositoryStructure<IArtist>();
        this.foldersStructure = new RepositoryStructure<Folder>();
        this.genresStructure = new RepositoryStructure<IGenre>();
        this.yearStructure = new RepositoryStructure<IYear>();
        this.state = state;
    }

    /**
     * This constructor can't be used
	 */
    @SuppressWarnings("unused")
    private Repository() {}

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#setState(net.sourceforge.atunes.model.IState)
	 */
    @Override
	public void setState(IState state) {
    	this.state = state;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getRepositoryFolders()
	 */
    @Override
	public List<File> getRepositoryFolders() {
        return new ArrayList<File>(folders);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#addDurationInSeconds(long)
	 */
    @Override
	public void addDurationInSeconds(long seconds) {
        this.totalDurationInSeconds += seconds;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#removeDurationInSeconds(long)
	 */
    @Override
	public void removeDurationInSeconds(long seconds) {
        totalDurationInSeconds -= seconds;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getTotalDurationInSeconds()
	 */
    @Override
	public long getTotalDurationInSeconds() {
        return totalDurationInSeconds;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#addSizeInBytes(long)
	 */
    @Override
	public void addSizeInBytes(long bytes) {
    	totalSizeInBytes += bytes;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#removeSizeInBytes(long)
	 */
    @Override
	public void removeSizeInBytes(long bytes) {
    	totalSizeInBytes -= bytes;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getTotalSizeInBytes()
	 */
    @Override
	public long getTotalSizeInBytes() {
        return totalSizeInBytes;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#exists()
	 */
    @Override
	public boolean exists() {
    	// All folders must exist
    	for (File folder : getRepositoryFolders()) {
    		if (!folder.exists()) {
    			return false;
    		}
    	}
        return true;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#validateRepository()
	 */
    @Override
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
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#countFiles()
	 */
    @Override
	public int countFiles() {
        return filesStructure.count();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getFile(java.lang.String)
	 */
    @Override
	public ILocalAudioObject getFile(String fileName) {
        return filesStructure.get(fileName);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getFiles()
	 */
    @Override
	public Collection<ILocalAudioObject> getFiles() {
    	return filesStructure.getAll();
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#putFile(net.sourceforge.atunes.model.ILocalAudioObject)
	 */
    @Override
	public ILocalAudioObject putFile(ILocalAudioObject file) {
    	filesStructure.put(file.getUrl(), file);
    	return file;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#removeFile(net.sourceforge.atunes.model.ILocalAudioObject)
	 */
	@Override
	public void removeFile(ILocalAudioObject file) {
		filesStructure.remove(file.getUrl());
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#removeFile(java.io.File)
	 */
	@Override
	public void removeFile(File file) {
		filesStructure.remove(file.getAbsolutePath());
	}
	
	// --------------------------------------- ARTIST OPERATIONS ------------------------------------- //
	
    /**
     * Access artist structure
     * @return
     */
	@Override
    public Map<String, IArtist> getArtistStructure() {
        return artistsStructure.getStructure();
    }
    
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#countArtists()
	 */
	@Override
	public int countArtists() {
		return artistsStructure.count();
	}
	
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getArtist(java.lang.String)
	 */
    @Override
	public IArtist getArtist(String artistName) {
    	if (artistName == null) {
    		return null;
    	} else if (state.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		return artistsStructure.get(artistName);
    	} else {
    		return artistsStructure.get(artistName.toLowerCase());
    	}
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getArtists()
	 */
    @Override
	public Collection<IArtist> getArtists() {
    	return artistsStructure.getAll();
    }
    
    @Override
	public IArtist putArtist(IArtist artist) {
    	if (state.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		artistsStructure.put(artist.getName(), artist);
    	} else {
    		artistsStructure.put(artist.getName().toLowerCase(), artist);
    	}
    	return artist;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#removeArtist(net.sourceforge.atunes.model.Artist)
	 */
    @Override
	public void removeArtist(IArtist artist) {
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
	@Override
    public Map<String, IAlbum> getAlbumStructure() {
        Map<String, IAlbum> albumsStructure = new HashMap<String, IAlbum>();
        Collection<IArtist> artistCollection = getArtists();
        for (IArtist artist : artistCollection) {
            for (IAlbum album : artist.getAlbums().values()) {
                albumsStructure.put(StringUtils.getString(album.getName(), " (", album.getArtist(), ")"), album);
            }
        }
        return albumsStructure;
    }
    
    // -------------------------------- FOLDER OPERATIONS ----------------------------------------- //
    
    /**
     * Access folder structure
     * @return
     */
	@Override
    public Map<String, Folder> getFolderStructure() {
        return foldersStructure.getStructure();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getFolder(java.lang.String)
	 */
    @Override
	public Folder getFolder(String path) {
    	return foldersStructure.get(path);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getFolders()
	 */
    @Override
	public Collection<Folder> getFolders() {
    	return foldersStructure.getAll();
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#putFolder(net.sourceforge.atunes.model.Folder)
	 */
    @Override
	public Folder putFolder(Folder folder) {
    	foldersStructure.put(folder.getName(), folder);
    	return folder;
    }

    // --------------------------------------------------- GENRE OPERATIONS ---------------------------------------------- //
    
    /**
     * Returns genre structure
     * @return
     */
	@Override
    public Map<String, IGenre> getGenreStructure() {
        return genresStructure.getStructure();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getGenres()
	 */
    @Override
	public Collection<IGenre> getGenres() {
    	return genresStructure.getAll();
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getGenre(java.lang.String)
	 */
    @Override
	public IGenre getGenre(String genre) {
    	if (state.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		return genresStructure.get(genre);
    	} else {
    		return genresStructure.get(genre.toLowerCase());
    	}
    }

    @Override
	public IGenre putGenre(IGenre genre) {
    	if (state.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
    		genresStructure.put(genre.getName(), genre);
    	} else {
    		genresStructure.put(genre.getName().toLowerCase(), genre);
    	}
    	return genre;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#removeGenre(net.sourceforge.atunes.kernel.modules.repository.data.Genre)
	 */
    @Override
	public void removeGenre(IGenre genre) {
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
	@Override
    public Map<String, IYear> getYearStructure() {
        return yearStructure.getStructure();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getYear(java.lang.String)
	 */
    @Override
	public IYear getYear(String year) {
    	return yearStructure.get(year);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#getYears()
	 */
    @Override
	public Collection<IYear> getYears() {
    	return yearStructure.getAll();
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#putYear(net.sourceforge.atunes.kernel.modules.repository.data.Year)
	 */
    @Override
	public IYear putYear(IYear year) {
    	yearStructure.put(year.getName(), year);
    	return year;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.model.IRepository#removeYear(net.sourceforge.atunes.kernel.modules.repository.data.Year)
	 */
    @Override
	public void removeYear(IYear year) {
    	yearStructure.remove(year.getName());
    }
}
