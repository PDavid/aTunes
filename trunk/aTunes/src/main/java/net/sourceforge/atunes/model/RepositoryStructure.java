/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;

public class RepositoryStructure implements Serializable {

    private static final long serialVersionUID = -2230698137764691254L;

    private Map<String, LocalAudioObject> filesStructure;
    private Map<String, Artist> artistStructure;
    private Map<String, Folder> folderStructure;
    private Map<String, Genre> genreStructure;
    private Map<String, Year> yearStructure;

    /**
     * Instantiates a new repository structure.
     */
    protected RepositoryStructure() {
    	this.filesStructure = new HashMap<String, LocalAudioObject>();
    	this.artistStructure = new HashMap<String, Artist>();
    	this.folderStructure = new HashMap<String, Folder>();
    	this.genreStructure = new HashMap<String, Genre>();
    	this.yearStructure = new HashMap<String, Year>();
    }

    /**
     * Gets the folder structure.
     * 
     * @return the folder structure
     */
    public Map<String, Folder> getFolderStructure() {
        return folderStructure;
    }

    /**
     * Gets the genre structure.
     * 
     * @return the genre structure
     */
    public Map<String, Genre> getGenreStructure() {
        return genreStructure;
    }

    /**
     * Gets the artist structure.
     * 
     * @return the artist structure
     */
    public Map<String, Artist> getArtistStructure() {
        return artistStructure;
    }

    /**
     * Gets the year structure.
     * 
     * @return the year structure
     */
    public Map<String, Year> getYearStructure() {
        return yearStructure;
    }

	/**
	 * Gets the files structure
	 * @return
	 */
	public Map<String, LocalAudioObject> getFilesStructure() {
		return filesStructure;
	}
}
