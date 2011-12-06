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

package net.sourceforge.atunes.kernel.modules.process;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.atunes.kernel.modules.tags.TagFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.ITagAttributesReviewed;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;

/**
 * Imports (song) files to repository
 */
public class ImportFilesProcess extends AbstractLocalAudioObjectTransferProcess {

	private static final Pattern NUMBER_SEPARATOR_PATTERN = Pattern.compile("[^0-9]+");
	
    /**
     * Folders to import
     */
    private List<File> folders;

    /** Set of audio files whose tag must be written */
    private HashSet<ILocalAudioObject> filesToChangeTag;
    
    private ILocalAudioObjectFactory localAudioObjectFactory;
    
    private ITagHandler tagHandler;
    
    private IWebServicesHandler webServicesHandler;
    
    /**
     * @param webServicesHandler
     */
    public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
    
    /**
     * @param tagHandler
     */
    public void setTagHandler(ITagHandler tagHandler) {
		this.tagHandler = tagHandler;
	}
    
    /**
     * Replaces tags before import audio objects
     * @param tagAttributesReviewed
     */
    public void initialize(ITagAttributesReviewed tagAttributesReviewed) {
    	this.filesToChangeTag = new HashSet<ILocalAudioObject>();
        for (ILocalAudioObject fileToImport : getFilesToTransfer()) {
            // Replace tags (in memory) before import audio files if necessary
            replaceTag(fileToImport, tagAttributesReviewed);

            // Set track number if necessary
            setTrackNumber(fileToImport);
        }
    }
    
    /**
     * @param folders
     */
    public void setFolders(List<File> folders) {
		this.folders = folders;
	}
    
    /**
     * @param localAudioObjectFactory
     */
    public void setLocalAudioObjectFactory(ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}
    
    @Override
    public String getProgressDialogTitle() {
        return StringUtils.getString(I18nUtils.getString("IMPORTING"), "...");
    }

    /**
     * Prepares the directory structure in which the song will be written.
     * 
     * @param song
     *            Song to be written
     * @param destinationBaseFolder
     *            Destination path
     * @return Returns the directory structure with full path where the file
     *         will be written
     */
    public File getDirectory(ILocalAudioObject song, File destinationBaseFolder) {
        // Get base folder or the first folder if there is any error
        File baseFolder = null;
        for (File folder : folders) {
            if (song.getFile().getAbsolutePath().startsWith(folder.getParentFile().getAbsolutePath())) {
                baseFolder = folder.getParentFile();
                break;
            }
        }
        if (baseFolder == null) {
            baseFolder = folders.get(0);
        }

        String songPath = song.getFile().getParentFile().getAbsolutePath();
        String songRelativePath = songPath.replaceFirst(baseFolder.getAbsolutePath().replace("\\", "\\\\").replace("$", "\\$"), "");
        if (getState().getImportExportFolderPathPattern() != null) {
            songRelativePath = FileNameUtils.getValidFolderName(FileNameUtils.getNewFolderPath(getState().getImportExportFolderPathPattern(), song, getOsManager()), getOsManager());
        }
        return new File(StringUtils.getString(destinationBaseFolder.getAbsolutePath(), getOsManager().getFileSeparator(), songRelativePath));
    }

    @Override
    protected File transferAudioFile(File destination, ILocalAudioObject file, List<Exception> thrownExceptions) {
        // Change title. As this can be a long-time task we get titles during transfer process instead of before to avoid not showing any progress dialog
        // while performing this task
        setTitle(file);

        // If necessary, apply changes to original files before copy
        if (getState().isApplyChangesToSourceFilesBeforeImport()) {
            changeTag(file, file);
        }

        // Import file
        File destFile = importFile(destination, file, thrownExceptions);

        // Change tag if necessary after import
        if (!getState().isApplyChangesToSourceFilesBeforeImport()) {
            changeTag(file, localAudioObjectFactory.getLocalAudioObject(destFile));
        }

        return destFile;
    }

    /**
     * Imports a single file to a destination
     * 
     * @param destination
     * @param file
     * @param list
     *            to add exceptions when thrown
     * @return A reference to the created file
     * @throws IOException
     */
    private File importFile(File destination, ILocalAudioObject file, List<Exception> thrownExceptions) {
        File destDir = getDirectory(file, destination);
        String newName;
        if (getState().getImportExportFileNamePattern() != null) {
            newName = FileNameUtils.getNewFileName(getState().getImportExportFileNamePattern(), file, getOsManager());
        } else {
            newName = FileNameUtils.getValidFileName(file.getFile().getName().replace("\\", "\\\\").replace("$", "\\$"), false, getOsManager());
        }

        File destFile = new File(StringUtils.getString(destDir.getAbsolutePath(), getOsManager().getFileSeparator(), newName));

        try {
            // Now that we (supposedly) have a valid filename write file
            FileUtils.copyFile(file.getFile(), destFile);
        } catch (IOException e) {
            thrownExceptions.add(e);
        }

        return destFile;
    }

    /**
     * Changes tag if necessary in disk
     * 
     * @param sourceFile
     *            original AudioFile
     * @param destFile
     *            destination file
     */
    private void changeTag(ILocalAudioObject sourceFile, ILocalAudioObject destFile) {
        if (filesToChangeTag.contains(sourceFile)) {
            tagHandler.setTag(destFile, sourceFile.getTag());
        }
    }

    /**
     * Changes tag of a file if it is defined in a TagAttributesReviewed object
     * LocalAudioObject is added to list of files to change tag physically on disk
     * 
     * @param fileToImport
     * @param tagAttributesReviewed
     */
    private void replaceTag(ILocalAudioObject fileToImport, ITagAttributesReviewed tagAttributesReviewed) {
        if (tagAttributesReviewed != null) {
            ITag modifiedTag = tagAttributesReviewed.getTagForAudioFile(fileToImport);
            // This file must be changed
            if (modifiedTag != null) {
                fileToImport.setTag(modifiedTag);
                filesToChangeTag.add(fileToImport);
            }
        }
    }

    /**
     * Changes track number of a file. LocalAudioObject is added to list of files to
     * change tag physically on disk
     * 
     * @param fileToImport
     */
    private void setTrackNumber(ILocalAudioObject fileToImport) {
        if (getState().isSetTrackNumbersWhenImporting() && fileToImport.getTrackNumber() < 1) {
        	int newTrackNumber = getTrackNumber(fileToImport);
        	if (newTrackNumber > 0) {
        		if (fileToImport.getTag() == null) {
        			fileToImport.setTag(TagFactory.getNewTag());
        		}
        		fileToImport.getTag().setTrackNumber(newTrackNumber);
        		if (!filesToChangeTag.contains(fileToImport)) {
        			filesToChangeTag.add(fileToImport);
        		}
        	}
        }
    }
    
    /**
     * Returns track number for a given audio file
     * 
     * @param audioFile
     * @return
     */
    private int getTrackNumber(ILocalAudioObject audioFile) {
        // Try to get a number from file name
        String fileName = audioFile.getNameWithoutExtension();
        String[] aux = NUMBER_SEPARATOR_PATTERN.split(fileName);
        int trackNumber = 0;
        int i = 0;
        while (trackNumber == 0 && i < aux.length) {
            String token = aux[i];
            try {
                trackNumber = Integer.parseInt(token);
                // If trackNumber >= 1000 maybe it's not a track number (year?) 
                if (trackNumber >= 1000) {
                    trackNumber = 0;
                }
            } catch (NumberFormatException e) {
                // Ok, it's not a valid number, skip it
            }
            i++;
        }

        // If trackNumber could not be retrieved from file name, try to get from last.fm
        // To get this, titles must match
        if (trackNumber == 0) {
            trackNumber = webServicesHandler.getTrackNumber(audioFile);
        }

        return trackNumber;
    }


    /**
     * Changes title of a file. LocalAudioObject is added to list of files to change
     * tag physically on disk
     * 
     * @param fileToImport
     */
    private void setTitle(ILocalAudioObject fileToImport) {
        if (getState().isSetTitlesWhenImporting()) {
            String newTitle = webServicesHandler.getTitleForAudioObject(fileToImport);
            if (newTitle != null) {
                if (fileToImport.getTag() == null) {
                    fileToImport.setTag(TagFactory.getNewTag());
                }
                fileToImport.getTag().setTitle(newTitle);
                if (!filesToChangeTag.contains(fileToImport)) {
                    filesToChangeTag.add(fileToImport);
                }
            }
        }
    }
}
