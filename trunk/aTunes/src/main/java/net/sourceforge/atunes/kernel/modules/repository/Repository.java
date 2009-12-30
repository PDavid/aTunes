/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.repository.model.Folder;
import net.sourceforge.atunes.kernel.modules.repository.model.Genre;

/**
 * The Class Repository.
 * 
 * @author fleax
 */
public class Repository implements Serializable {

    private static final long serialVersionUID = -8278937514875788175L;

    /** The folders. */
    private List<File> folders;

    /** The files. */
    private Map<String, AudioFile> files;

    /** The total size in bytes. */
    private long totalSizeInBytes;

    /** The total duration in seconds. */
    private long totalDurationInSeconds;

    /** The structure. */
    private RepositoryStructure structure;

    /** Attribute to indicate if repository needs to be written to disk */
    private transient boolean dirty;

    /**
     * Instantiates a new repository.
     * 
     * @param folders
     *            the folders
     */
    public Repository(List<File> folders) {
        this.folders = folders;
        files = new HashMap<String, AudioFile>();
        structure = new RepositoryStructure();
    }

    /**
     * Adds the duration in seconds.
     * 
     * @param seconds
     *            the seconds
     */
    public void addDurationInSeconds(long seconds) {
        totalDurationInSeconds += seconds;
    }

    /**
     * Count files.
     * 
     * @return the int
     */
    public int countFiles() {
        return files.size();
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
        return files.get(fileName);
    }

    /**
     * Gets the files.
     * 
     * @return the files
     */
    public Map<String, AudioFile> getAudioFiles() {
        return files;
    }

    /**
     * Gets the files list.
     * 
     * @return the files list
     */
    public List<AudioFile> getAudioFilesList() {
        return new ArrayList<AudioFile>(files.values());
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
        return getFolders().get(0).exists();
    }

    /**
     * @return the dirty
     */
    protected boolean isDirty() {
        return dirty;
    }

    /**
     * @param dirty
     *            the dirty to set
     */
    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}
