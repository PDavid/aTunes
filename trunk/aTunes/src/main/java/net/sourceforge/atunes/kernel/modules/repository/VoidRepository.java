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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.model.InconsistentRepositoryException;

/**
 * A repository used before repository is loaded or selected, it does nothing
 * @author alex
 *
 */
public class VoidRepository implements IRepository {

	@Override
	public void setStateRepository(IStateRepository state) {}

	@Override
	public List<File> getRepositoryFolders() {
		return Collections.emptyList();
	}

	@Override
	public void addDurationInSeconds(long seconds) {}

	@Override
	public void removeDurationInSeconds(long seconds) {}

	@Override
	public long getTotalDurationInSeconds() {
		return 0;
	}

	@Override
	public void addSizeInBytes(long bytes) {}

	@Override
	public void removeSizeInBytes(long bytes) {}

	@Override
	public long getTotalSizeInBytes() {
		return 0;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public void validateRepository() throws InconsistentRepositoryException {}

	@Override
	public int countFiles() {
		return 0;
	}

	@Override
	public ILocalAudioObject getFile(String fileName) {
		return null;
	}

	@Override
	public Collection<ILocalAudioObject> getFiles() {
		return Collections.emptyList();
	}

	@Override
	public ILocalAudioObject putFile(ILocalAudioObject file) {
		return null;
	}

	@Override
	public void removeFile(ILocalAudioObject file) {}

	@Override
	public void removeFile(File file) {}

	@Override
	public int countArtists() {
		return 0;
	}

	@Override
	public IArtist getArtist(String artistName) {
		return null;
	}

	@Override
	public Collection<IArtist> getArtists() {
		return Collections.emptyList();
	}
	
	@Override
	public IArtist putArtist(IArtist artist) {
		return null;
	}

	@Override
	public void removeArtist(IArtist artist) {}

	@Override
	public IFolder getFolder(String path) {
		return null;
	}

	@Override
	public Collection<IFolder> getFolders() {
		return null;
	}

	@Override
	public IFolder putFolder(IFolder folder) {
		return null;
	}

	@Override
	public Collection<IGenre> getGenres() {
		return null;
	}

	@Override
	public IGenre getGenre(String genre) {
		return null;
	}

	@Override
	public IGenre putGenre(IGenre genre) {
		return null;
	}

	@Override
	public void removeGenre(IGenre genre) {}

	@Override
	public IYear getYear(String year) {
		return null;
	}

	@Override
	public Collection<IYear> getYears() {
		return null;
	}

	@Override
	public IYear putYear(IYear year) {
		return null;
	}

	@Override
	public void removeYear(IYear year) {}

	@Override
	public Map<String, ?> getYearStructure() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, ?> getGenreStructure() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, ?> getFolderStructure() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, ?> getAlbumStructure() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, ?> getArtistStructure() {
		return Collections.emptyMap();
	}
}
