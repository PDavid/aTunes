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
package net.sourceforge.atunes.kernel.modules.repository.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a folder with a name, and a list of files and more
 * folders.
 * 
 * @author fleax
 */
public class Folder implements Serializable, TreeObject {

    private static final long serialVersionUID = 2608221109707838025L;

    /** Name of the folder. */
    private String name;

    /** List of files in this folder. */
    private List<AudioFile> audioFiles;

    /** List of folders in this folder, indexed by name. */
    private Map<String, Folder> folders;

    /** Folder that contains this folder. */
    private Folder parentFolder;

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     */
    public Folder(String name) {
        this.name = name;
        this.audioFiles = new ArrayList<AudioFile>();
        folders = new HashMap<String, Folder>();
    }

    /**
     * Adds a file to this folder.
     * 
     * @param file
     *            the file
     */
    public void addAudioFile(AudioFile file) {
        audioFiles.add(file);
    }

    /**
     * Adds a folder as child of this folder.
     * 
     * @param f
     *            the f
     */
    public void addFolder(Folder f) {
        if (folders.containsKey(f.getName())) {
            Folder folder = folders.get(f.getName());
            folder.addFoldersOf(f);
        } else {
            folders.put(f.getName(), f);
            f.setParentFolder(this);
        }
    }

    /**
     * Adds all children folders of a given folder to this.
     * 
     * @param f
     *            the f
     */
    private void addFoldersOf(Folder f) {
        folders.putAll(f.getFolders());
    }

    /**
     * Returns true if folder contains a folder with given name.
     * 
     * @param folderName
     *            the folder name
     * 
     * @return true, if contains folder
     */
    public boolean containsFolder(String folderName) {
        return folders.containsKey(folderName);
    }

    /**
     * Gets the audio files.
     * 
     * @return the audio files
     */
    public List<AudioFile> getAudioFiles() {
        List<AudioFile> result = new ArrayList<AudioFile>();
        result.addAll(audioFiles);
        for (String string : folders.keySet()) {
            Folder f = folders.get(string);
            result.addAll(f.getAudioFiles());
        }
        return result;
    }

    /**
     * Returns a list of songs in this folder and in children folders.
     * 
     * @return the audio objects
     */
    @Override
    public List<AudioObject> getAudioObjects() {
        List<AudioObject> result = new ArrayList<AudioObject>();
        result.addAll(audioFiles);
        for (String string : folders.keySet()) {
            Folder f = folders.get(string);
            result.addAll(f.getAudioObjects());
        }
        return result;
    }

    /**
     * Returns files in this folder.
     * 
     * @return the files
     */
    public List<AudioFile> getFiles() {
        return audioFiles;
    }

    /**
     * Returns a child folder given a folder name.
     * 
     * @param folderName
     *            the folder name
     * 
     * @return the folder
     */
    public Folder getFolder(String folderName) {
        return folders.get(folderName);
    }

    /**
     * Returns all children folders.
     * 
     * @return the folders
     */
    public Map<String, Folder> getFolders() {
        return folders;
    }

    /**
     * Returns the name of this folder.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the parent folder.
     * 
     * @return the parentFolder
     */
    public Folder getParentFolder() {
        return parentFolder;
    }

    /**
     * Returns true if folder is empty (contains neither files nor folders).
     * 
     * @return true, if checks if is empty
     */
    public boolean isEmpty() {
        return audioFiles.isEmpty() && folders.isEmpty();
    }

    /**
     * Removes a file from this folder.
     * 
     * @param file
     *            the file
     */
    public void removeAudioFile(AudioFile file) {
        audioFiles.remove(file);
    }

    /**
     * Removes a folder from this folder.
     * 
     * @param f
     *            the f
     */
    public void removeFolder(Folder f) {
        folders.remove(f.getName());
    }

    /**
     * Sets the name of this folder.
     * 
     * @param name
     *            the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the parent folder.
     * 
     * @param parentFolder
     *            the parentFolder to set
     */
    private void setParentFolder(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }

    /**
     * String representation.
     * 
     * @return the string
     */
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Folder)) {
            return false;
        }
        Folder folder = (Folder) o;
        // Two folders are equal if have the same name and the same parent
        if (folder.parentFolder != null && parentFolder != null) {
            return folder.name.equals(name) && folder.parentFolder.equals(parentFolder);
        } else {
            if (folder.parentFolder == null && parentFolder == null) {
                return folder.name.equals(name);
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        if (parentFolder == null) {
            return name.hashCode();
        } else {
            return (name + parentFolder).hashCode();
        }
    }

    /**
     * Returns the path of this folder
     * 
     * @return
     */
    public File getFolderPath() {
        String path = name;
        Folder parent = this.parentFolder;
        while (parent != null) {
            path = StringUtils.getString(parent.getName(), SystemProperties.FILE_SEPARATOR, path);
            parent = parent.parentFolder;
        }
        return new File(path);
    }

    @Override
    public String getToolTip() {
        int songs = getAudioFiles().size();
        return StringUtils.getString(getName(), " (", songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG")), ")");
    }

    @Override
    public boolean isExtendedToolTipSupported() {
        return true;
    }

    @Override
    public void setExtendedToolTip(ExtendedToolTip toolTip) {
        toolTip.setLine1(name);
        int folderNumber = folders.size();
        if (folderNumber > 0) {
            toolTip.setLine2(StringUtils.getString(folderNumber, " ", (folderNumber > 1 ? I18nUtils.getString("FOLDERS") : I18nUtils.getString("FOLDER"))));
        } else {
            toolTip.setLine2(null);
        }
        int songs = getAudioObjects().size();
        toolTip.setLine3(StringUtils.getString(songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG"))));
    }

    @Override
    public ImageIcon getExtendedToolTipImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
        // TODO Auto-generated method stub
        return false;
    }
}
