/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.repository.model.Folder;
import net.sourceforge.atunes.kernel.modules.repository.model.Genre;

public class RepositoryStructure implements Serializable {

    private static final long serialVersionUID = -2230698137764691254L;

    /** The tree structure. */
    private Map<String, Artist> artistStructure;

    /** The folder structure. */
    private Map<String, Folder> folderStructure;

    /** The genre structure. */
    private Map<String, Genre> genreStructure;

    /**
     * Instantiates a new repository structure.
     */
    protected RepositoryStructure() {
        artistStructure = new HashMap<String, Artist>();
        folderStructure = new HashMap<String, Folder>();
        genreStructure = new HashMap<String, Genre>();
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
     * Gets the tree structure.
     * 
     * @return the tree structure
     */
    public Map<String, Artist> getArtistStructure() {
        return artistStructure;
    }
}
