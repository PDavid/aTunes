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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a folder with a name, and a list of files and more
 * folders.
 * 
 * @author fleax
 */
public class Folder implements Serializable, TreeObject<ILocalAudioObject> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7175829016580923961L;

	/** Name of the folder. */
    private String name;

    /** List of files in this folder. */
    private List<ILocalAudioObject> files;

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
    }

    /**
     * Adds a file to this folder.
     * 
     * @param file
     *            the file
     */
    public void addAudioFile(ILocalAudioObject file) {
        getFiles().add(file);
    }

    /**
     * Adds a folder as child of this folder.
     * 
     * @param f
     *            the f
     */
    public void addFolder(Folder f) {
        if (getFolders().containsKey(f.getName())) {
            getFolders().get(f.getName()).addFoldersOf(f);
        } else {
        	getFolders().put(f.getName(), f);
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
    	getFolders().putAll(f.getFolders());
    }

    /**
     * Returns a list of songs in this folder and in children folders.
     * 
     * @return the audio objects
     */
    @Override
    public List<ILocalAudioObject> getAudioObjects() {
        List<ILocalAudioObject> result = null;
        for (Folder f : getFolders().values()) {
        	if (result == null) {
        		result = f.getAudioObjects();
        	} else {
        		result.addAll(f.getAudioObjects());
        	}
        }
        if (result == null) {
        	result = new ArrayList<ILocalAudioObject>();
        }
       	result.addAll(getFiles());
        return result;
    }

    /**
     * Returns files in this folder.
     * 
     * @return the files
     */
    private List<ILocalAudioObject> getFiles() {
    	if (files == null) {
    		files = new ArrayList<ILocalAudioObject>();
    	}
        return files;
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
        return getFolders().get(folderName);
    }

    /**
     * Returns all children folders.
     * 
     * @return the folders
     */
    public Map<String, Folder> getFolders() {
    	if (folders == null) {
    		folders = new HashMap<String, Folder>();
    	}
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
        return getFiles().isEmpty() && getFolders().isEmpty();
    }

    /**
     * Removes a file from this folder
     * 
     * @param file
     *            the file
     */
    public void removeAudioFile(ILocalAudioObject file) {
        getFiles().remove(file);
    }

    /**
     * Removes a folder from this folder.
     * 
     * @param f
     *            the f
     */
    public void removeFolder(Folder f) {
    	getFolders().remove(f.getName());
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
        if (!(o instanceof Folder)) {
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
     * @param osManager
     * @return
     */
    public File getFolderPath(IOSManager osManager) {
        String path = name;
        Folder parent = this.parentFolder;
        while (parent != null) {
            path = StringUtils.getString(parent.getName(), osManager.getFileSeparator(), path);
            parent = parent.parentFolder;
        }
        return new File(path);
    }

    @Override
    public String getToolTip() {
        int songs = getAudioObjects().size();
        return StringUtils.getString(getName(), " (", songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG")), ")");
    }

    @Override
    public boolean isExtendedToolTipSupported() {
        return true;
    }

    @Override
    public void setExtendedToolTip(ExtendedToolTip toolTip) {
        toolTip.setLine1(name);
        int folderNumber = getFolders().size();
        if (folderNumber > 0) {
            toolTip.setLine2(StringUtils.getString(folderNumber, " ", (folderNumber > 1 ? I18nUtils.getString("FOLDERS") : I18nUtils.getString("FOLDER"))));
        } else {
            toolTip.setLine2(null);
        }
        int songs = getAudioObjects().size();
        toolTip.setLine3(StringUtils.getString(songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG"))));
    }

    @Override
    public ImageIcon getExtendedToolTipImage(IOSManager osManager) {
        return null;
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
        return false;
    }
    
	/**
	 * Returns number of audio object
	 * @return
	 */
	public int size() {
		int size = getFiles().size();
        for (Folder f : getFolders().values()) {
        	size = size + f.size();
        }       	
		return size;
	}

}
