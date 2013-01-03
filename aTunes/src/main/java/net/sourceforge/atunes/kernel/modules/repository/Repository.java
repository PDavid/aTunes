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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.model.InconsistentRepositoryException;
import net.sourceforge.atunes.utils.StringUtils;


/**
 * Stores information about collection of local audio files
 * @author alex
 *
 */
public class Repository implements Serializable, IRepository {

	private static final long serialVersionUID = -8278937514875788175L;

	/**
	 * Root folders of repository
	 */
	List<File> folders;

	/**
	 *  The total size in bytes of all files
	 */
	long totalSizeInBytes;

	/**
	 * The total duration in seconds of all files
	 */
	long totalDurationInSeconds;

	/**
	 * File structure
	 */
	RepositoryStructure<ILocalAudioObject> filesStructure;

	/**
	 * Artists structure
	 */
	RepositoryStructure<IArtist> artistsStructure;

	/**
	 * Folders structure
	 */
	RepositoryStructure<IFolder> foldersStructure;

	/**
	 * Genres structure
	 */
	RepositoryStructure<IGenre> genresStructure;

	/**
	 *  Year structure
	 */
	RepositoryStructure<IYear> yearStructure;

	/**
	 * State
	 */
	transient IStateRepository stateRepository;

	/**
	 * Instantiates a new repository.
	 * @param folders
	 * @param stateRepository
	 */
	public Repository(final List<File> folders, final IStateRepository stateRepository) {
		this.folders = folders;
		this.filesStructure = new RepositoryStructure<ILocalAudioObject>();
		this.artistsStructure = new RepositoryStructure<IArtist>();
		this.foldersStructure = new RepositoryStructure<IFolder>();
		this.genresStructure = new RepositoryStructure<IGenre>();
		this.yearStructure = new RepositoryStructure<IYear>();
		this.stateRepository = stateRepository;
	}

	/**
	 * This constructor can't be used
	 */
	@SuppressWarnings("unused")
	private Repository() {}

	@Override
	public void setStateRepository(final IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	@Override
	public List<File> getRepositoryFolders() {
		return new ArrayList<File>(folders);
	}

	@Override
	public void addDurationInSeconds(final long seconds) {
		this.totalDurationInSeconds += seconds;
	}

	@Override
	public void removeDurationInSeconds(final long seconds) {
		totalDurationInSeconds -= seconds;
	}

	@Override
	public long getTotalDurationInSeconds() {
		return totalDurationInSeconds;
	}

	@Override
	public void addSizeInBytes(final long bytes) {
		totalSizeInBytes += bytes;
	}

	@Override
	public void removeSizeInBytes(final long bytes) {
		totalSizeInBytes -= bytes;
	}

	@Override
	public long getTotalSizeInBytes() {
		return totalSizeInBytes;
	}

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

	@Override
	public void validateRepository() throws InconsistentRepositoryException {
		checkConsistency(filesStructure);
		checkConsistency(artistsStructure);
		checkConsistency(foldersStructure);
		checkConsistency(genresStructure);
		checkConsistency(yearStructure);
		if (folders == null) {
			throw new InconsistentRepositoryException();
		}
	}

	/**
	 * @param structure
	 * @throws InconsistentRepositoryException
	 */
	private void checkConsistency(final RepositoryStructure<?> structure) throws InconsistentRepositoryException {
		if (structure == null) {
			throw new InconsistentRepositoryException();
		}
	}

	@Override
	public int countFiles() {
		return filesStructure.count();
	}

	@Override
	public ILocalAudioObject getFile(final String fileName) {
		return filesStructure.get(fileName);
	}

	@Override
	public Collection<ILocalAudioObject> getFiles() {
		return filesStructure.getAll();
	}

	@Override
	public ILocalAudioObject putFile(final ILocalAudioObject file) {
		filesStructure.put(file.getUrl(), file);
		return file;
	}

	@Override
	public void removeFile(final ILocalAudioObject file) {
		filesStructure.remove(file.getUrl());
	}

	@Override
	public void removeFile(final File file) {
		filesStructure.remove(net.sourceforge.atunes.utils.FileUtils.getPath(file));
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

	@Override
	public int countArtists() {
		return artistsStructure.count();
	}

	@Override
	public IArtist getArtist(final String artistName) {
		if (artistName == null) {
			return null;
		} else if (stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
			return artistsStructure.get(artistName);
		} else {
			return artistsStructure.get(artistName.toLowerCase());
		}
	}

	@Override
	public Collection<IArtist> getArtists() {
		return artistsStructure.getAll();
	}

	@Override
	public IArtist putArtist(final IArtist artist) {
		if (stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
			artistsStructure.put(artist.getName(), artist);
		} else {
			artistsStructure.put(artist.getName().toLowerCase(), artist);
		}
		return artist;
	}

	@Override
	public void removeArtist(final IArtist artist) {
		if (stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
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
	public Map<String, IFolder> getFolderStructure() {
		return foldersStructure.getStructure();
	}

	@Override
	public IFolder getFolder(final String path) {
		return foldersStructure.get(path);
	}

	@Override
	public Collection<IFolder> getFolders() {
		return foldersStructure.getAll();
	}

	@Override
	public IFolder putFolder(final IFolder folder) {
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

	@Override
	public Collection<IGenre> getGenres() {
		return genresStructure.getAll();
	}

	@Override
	public IGenre getGenre(final String genre) {
		if (stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
			return genresStructure.get(genre);
		} else {
			return genresStructure.get(genre.toLowerCase());
		}
	}

	@Override
	public IGenre putGenre(final IGenre genre) {
		if (stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
			genresStructure.put(genre.getName(), genre);
		} else {
			genresStructure.put(genre.getName().toLowerCase(), genre);
		}
		return genre;
	}

	@Override
	public void removeGenre(final IGenre genre) {
		if (stateRepository.isKeyAlwaysCaseSensitiveInRepositoryStructure()) {
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

	@Override
	public IYear getYear(final String year) {
		return yearStructure.get(year);
	}

	@Override
	public Collection<IYear> getYears() {
		return yearStructure.getAll();
	}

	@Override
	public IYear putYear(final IYear year, final IUnknownObjectChecker unknownObjectChecker) {
		yearStructure.put(year.getName(unknownObjectChecker), year);
		return year;
	}

	@Override
	public void removeYear(final IYear year, final IUnknownObjectChecker unknownObjectChecker) {
		yearStructure.remove(year.getName(unknownObjectChecker));
	}
}
