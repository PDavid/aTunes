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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.kernel.modules.repository.exception.InconsistentRepositoryException;

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
     *  Structure of information
     */
    private RepositoryStructure structure;
    
    /** 
     * Attribute to indicate if repository needs to be written to disk 
     */
    private transient boolean dirty;

    /**
     * Instantiates a new repository.
     * 
     * @param folders
     *            the folders
     */
    public Repository(List<File> folders) {
        this.folders = folders;
        this.structure = new RepositoryStructure();
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
     * Count files.
     * 
     * @return the int
     */
    public int countFiles() {
        return structure.getFilesStructure().size();
    }

    /**
     * Gets the file.
     * 
     * @param fileName
     *            the file name
     * 
     * @return the file
     */
    public AudioFile getFile(String fileName) {
        return structure.getFilesStructure().get(fileName);
    }

    /**
     * Gets the files.
     * 
     * @return the files
     */
    public Map<String, AudioFile> getAudioFiles() {
        return structure.getFilesStructure();
    }

    /**
     * Gets the files list.
     * 
     * @return the files list
     */
    public Collection<AudioFile> getAudioFilesList() {
        return structure.getFilesStructure().values();
    }

    /**
     * Gets the folders.
     * 
     * @return the folders
     */
    public List<File> getFolders() {
        return new ArrayList<File>(folders);
    }

    public Map<String, Artist> getArtistStructure() {
        return structure.getArtistStructure();
    }

    public Map<String, Folder> getFolderStructure() {
        return structure.getFolderStructure();
    }

    public Map<String, Genre> getGenreStructure() {
        return structure.getGenreStructure();
    }

    public Map<String, Year> getYearStructure() {
        return structure.getYearStructure();
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
     * Gets the total size in bytes.
     * 
     * @return the total size in bytes
     */
    public long getTotalSizeInBytes() {
        return totalSizeInBytes;
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
     * Sets the total size in bytes.
     * 
     * @param totalSizeInBytes
     *            the new total size in bytes
     */
    public void setTotalSizeInBytes(long totalSizeInBytes) {
        this.totalSizeInBytes = totalSizeInBytes;
    }

    /**
     * Checks if repository exists on disk
     * 
     * @return if repository exists on disk
     */
    public boolean exists() {
    	// All folders must exist
    	for (File folder : getFolders()) {
    		if (!folder.exists()) {
    			return false;
    		}
    	}
        return true;
    }

    /**
     * @return the dirty
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * @param dirty
     *            the dirty to set
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    /**
     * Validates this repository throwing exception if object is not consistent. 
     * For example when a new attribute is added a repository object without that attribute can be considered invalid
     * @throws InconsistentRepositoryException
     */
    public void validateRepository() throws InconsistentRepositoryException {
        if (getAudioFiles() == null || 
            getFolders() == null || 
            getArtistStructure() == null || 
            getFolderStructure() == null || 
            getGenreStructure() == null ||
            getYearStructure() == null) {
                throw new InconsistentRepositoryException();
        }
    }
}
