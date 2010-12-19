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

package net.sourceforge.atunes.kernel.controllers.editTagDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.ImageInfo;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * The listener interface for receiving editTagDialogAction events.
 */
public final class EditTagDialogActionListener implements ActionListener {

    private Logger logger;

    private EditTagDialogController controller;
    private EditTagDialog dialog;

    /**
     * Instantiates a new edits the tag dialog action listener.
     * 
     * @param controller
     *            the controller
     * @param dialog
     *            the dialog
     */
    public EditTagDialogActionListener(EditTagDialogController controller, EditTagDialog dialog) {
        this.controller = controller;
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == dialog.getOkButton()) {
            controller.editTag();
            controller.clear();
            dialog.setVisible(false);
        } else if (e.getSource() == dialog.getNextButton()) {
            controller.editTag();
            controller.clear();
            // get the index of the first selected song in the play list
            List<AudioObject> selectedFiles = PlayListHandler.getInstance().getSelectedAudioObjects();
            AudioObject currentSelectedSong = selectedFiles.get(0);
            int currentSelectedSongIndex = PlayListHandler.getInstance().getIndexOfAudioObject(currentSelectedSong);

            // get the AudioFile of the next song in the play list after the first selection
            //nextFile.add((AudioFile)PlayListHandler.getInstance().getAudioObjectAtIndexRelativeToCurrentlyPlaying(++currentSelectedSongIndex - PlayListHandler.getInstance().getCurrentAudioObjectIndexInVisiblePlayList()));

            List<AudioFile> nextFile = new ArrayList<AudioFile>();
            boolean validAudioFile = false;
            int length = PlayListHandler.getInstance().getCurrentPlayList(true).size();
            // Before moving down check if we need to jump an audio object like a radio stream
            while (validAudioFile == false) {
                // Reaching the end of the playlist
                if (length < currentSelectedSongIndex + 2) {
                    break;
                }
                PlayListHandler.getInstance().changeSelectedAudioObjectToIndex(++currentSelectedSongIndex);
                selectedFiles.clear();
                selectedFiles.add(PlayListHandler.getInstance().getSelectedAudioObjects().get(0));
                validAudioFile = AudioFile.isValidAudioFile(selectedFiles.get(0).getUrl());
            }
            if (validAudioFile) {
                nextFile.add((AudioFile) selectedFiles.get(0));
                controller.editFiles(nextFile);
            }
        } else if (e.getSource() == dialog.getPrevButton()) {
            controller.editTag();
            controller.clear();

            // get the index of the first selected song in the play list
            List<AudioObject> selectedFiles = PlayListHandler.getInstance().getSelectedAudioObjects();
            AudioObject currentSelectedSong = selectedFiles.get(0);
            int currentSelectedSongIndex = PlayListHandler.getInstance().getIndexOfAudioObject(currentSelectedSong);

            List<AudioFile> prevFile = new ArrayList<AudioFile>();
            boolean validAudioFile = false;
            // Before moving down check if we need to jump an audio object like a radio stream
            while (validAudioFile == false) {
                PlayListHandler.getInstance().changeSelectedAudioObjectToIndex(--currentSelectedSongIndex);
                selectedFiles.clear();
                selectedFiles.add(PlayListHandler.getInstance().getSelectedAudioObjects().get(0));
                validAudioFile = AudioFile.isValidAudioFile(selectedFiles.get(0).getUrl());
                // Reaching the begin of the playlist
                if (currentSelectedSongIndex == -1) {
                    // Set to false to make tag edit dialog disappear 
                    validAudioFile = false;
                    break;
                }
            }
            if (validAudioFile) {
                prevFile.add((AudioFile) selectedFiles.get(0));
                controller.editFiles(prevFile);
            }
        } else if (e.getSource() == dialog.getCancelButton()) {
            dialog.setVisible(false);
            controller.clear();
        } else if (e.getSource() == dialog.getCoverButton()) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new ImagesFileFiler());
            fc.setCurrentDirectory(getCommonDirectoryForAudioFiles());
            int fileChooserState = fc.showOpenDialog(dialog);
            if (fileChooserState == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    BufferedImage bi = ImageIO.read(file);
                    BufferedImage bi2 = ImageUtils.toBufferedImage(ImageUtils.scaleImageBicubic(bi, Constants.DIALOG_LARGE_IMAGE_WIDTH, Constants.DIALOG_LARGE_IMAGE_HEIGHT)
                            .getImage());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setInput(new FileInputStream(file));
                    ImageIO.write(bi, imageInfo.getFormatName(), byteArrayOutputStream);
                    ImageIO.write(bi2, imageInfo.getFormatName(), byteArrayOutputStream2);
                    controller.setNewCover(byteArrayOutputStream.toByteArray());
                    dialog.getCover().setIcon(new ImageIcon(byteArrayOutputStream2.toByteArray()));
                    controller.setCoverEdited(true);
                } catch (IOException ex) {
                    controller.setNewCover(null);
                    controller.setCoverEdited(false);
                    getLogger().error(LogCategories.FILE_READ, ex);
                }
            }
        } else if (e.getSource() == dialog.getRemoveCoverButton()) {
            dialog.getCover().setIcon(null);
            controller.setNewCover(null);
            controller.setCoverEdited(true);
        }
    }

    /**
     * Returns the common parent directory of the audio files or
     * <code>null</code> if there is no common parent directory.
     * 
     * @return the common parent directory or <code>null</code>
     */
    private File getCommonDirectoryForAudioFiles() {
        List<AudioFile> audioFilesEditing = controller.getAudioFilesEditing();
        if (audioFilesEditing.size() == 1) {
            return audioFilesEditing.get(0).getFile().getParentFile();
        } else {
            return null;
        }
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    private static class ImagesFileFiler extends FileFilter {
        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }
            String fileName = pathname.getName().toUpperCase();
            return fileName.endsWith("JPG") || fileName.endsWith("JPEG") || fileName.endsWith("PNG");
        }

        @Override
        public String getDescription() {
            return I18nUtils.getString("IMAGES");
        }
    }

}
