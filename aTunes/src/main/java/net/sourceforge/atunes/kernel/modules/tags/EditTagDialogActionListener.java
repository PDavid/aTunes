/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.tags;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;

/**
 * The listener interface for receiving editTagDialogAction events.
 */
public final class EditTagDialogActionListener implements ActionListener {

	private final EditTagDialogController controller;
	private final EditTagDialog dialog;
	private final IPlayListHandler playListHandler;
	private final ILocalAudioObjectValidator localAudioObjectValidator;
	private final IControlsBuilder controlsBuilder;

	/**
	 * Instantiates a new edits the tag dialog action listener.
	 * 
	 * @param controller
	 * @param dialog
	 * @param playListHandler
	 * @param localAudioObjectValidator
	 * @param controlsBuilder
	 */
	public EditTagDialogActionListener(
			final EditTagDialogController controller,
			final EditTagDialog dialog, final IPlayListHandler playListHandler,
			final ILocalAudioObjectValidator localAudioObjectValidator,
			final IControlsBuilder controlsBuilder) {
		this.controller = controller;
		this.dialog = dialog;
		this.playListHandler = playListHandler;
		this.localAudioObjectValidator = localAudioObjectValidator;
		this.controlsBuilder = controlsBuilder;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == this.dialog.getOkButton()) {
			processOkAction();
		} else if (e.getSource() == this.dialog.getNextButton()) {
			processNextAction();
		} else if (e.getSource() == this.dialog.getPrevButton()) {
			processPreviousAction();
		} else if (e.getSource() == this.dialog.getCancelButton()) {
			processCancelAction();
		} else if (e.getSource() == this.dialog.getCoverButton()) {
			processSelectCover();
		} else if (e.getSource() == this.dialog.getRemoveCoverButton()) {
			processRemoveCover();
		}
	}

	/**
	 * 
	 */
	private void processRemoveCover() {
		this.dialog.getCover().setIcon(null);
		this.controller.setNewCover(null);
		this.controller.setCoverEdited(true);
	}

	/**
	 * 
	 */
	private void processSelectCover() {
		JFileChooser fc = this.controlsBuilder.getFileChooser();
		fc.setFileFilter(new ImagesFileFiler());
		fc.setCurrentDirectory(getCommonDirectoryForAudioFiles());
		int fileChooserState = fc.showOpenDialog(this.dialog);
		if (fileChooserState == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			try {
				// Read image and scale using ImageIO as Sanselan can't read
				// JPEG files
				BufferedImage bi = ImageIO.read(file);
				BufferedImage bi2 = ImageUtils.scaleBufferedImageBicubic(bi,
						Constants.DIALOG_LARGE_IMAGE_WIDTH,
						Constants.DIALOG_LARGE_IMAGE_HEIGHT);

				// Write as PNG
				this.controller.setNewCover(Sanselan.writeImageToBytes(bi,
						ImageFormat.IMAGE_FORMAT_PNG, null));
				this.dialog.getCover().setIcon(
						new ImageIcon(Sanselan.writeImageToBytes(bi2,
								ImageFormat.IMAGE_FORMAT_PNG, null)));
				this.controller.setCoverEdited(true);
			} catch (ImageWriteException ex) {
				this.controller.setNewCover(null);
				this.controller.setCoverEdited(false);
				Logger.error(ex);
			} catch (IOException ex) {
				this.controller.setNewCover(null);
				this.controller.setCoverEdited(false);
				Logger.error(ex);
			}
		}
	}

	/**
	 * 
	 */
	private void processCancelAction() {
		this.dialog.setVisible(false);
		this.controller.clear();
	}

	/**
	 * 
	 */
	private void processPreviousAction() {
		this.controller.editTag();
		this.controller.clear();

		// get the index of the first selected song in the play list
		List<IAudioObject> selectedFiles = this.playListHandler
				.getSelectedAudioObjects();
		IAudioObject currentSelectedSong = selectedFiles.get(0);
		int currentSelectedSongIndex = this.playListHandler
				.getIndexOfAudioObject(currentSelectedSong);

		List<ILocalAudioObject> prevFile = new ArrayList<ILocalAudioObject>();
		boolean validAudioFile = false;
		// Before moving down check if we need to jump an audio object like a
		// radio stream
		while (!validAudioFile) {
			this.playListHandler
					.changeSelectedAudioObjectToIndex(--currentSelectedSongIndex);
			selectedFiles.clear();
			selectedFiles.add(this.playListHandler.getSelectedAudioObjects()
					.get(0));
			validAudioFile = this.localAudioObjectValidator
					.isValidAudioFile(selectedFiles.get(0).getUrl());
			// Reaching the begin of the playlist
			if (currentSelectedSongIndex == -1) {
				// Set to false to make tag edit dialog disappear
				validAudioFile = false;
				break;
			}
		}
		if (validAudioFile) {
			prevFile.add((ILocalAudioObject) selectedFiles.get(0));
			this.controller.editFiles(prevFile);
		}
	}

	/**
	 * 
	 */
	private void processNextAction() {
		this.controller.editTag();
		this.controller.clear();
		// get the index of the first selected song in the play list
		List<IAudioObject> selectedFiles = this.playListHandler
				.getSelectedAudioObjects();
		IAudioObject currentSelectedSong = selectedFiles.get(0);
		int currentSelectedSongIndex = this.playListHandler
				.getIndexOfAudioObject(currentSelectedSong);

		// get the LocalAudioObject of the next song in the play list after the
		// first selection
		// nextFile.add((AudioFile)playListHandler.getAudioObjectAtIndexRelativeToCurrentlyPlaying(++currentSelectedSongIndex
		// - playListHandler.getCurrentAudioObjectIndexInVisiblePlayList()));

		List<ILocalAudioObject> nextFile = new ArrayList<ILocalAudioObject>();
		boolean validAudioFile = false;
		int length = this.playListHandler.getVisiblePlayList().size();
		// Before moving down check if we need to jump an audio object like a
		// radio stream
		while (!validAudioFile) {
			// Reaching the end of the playlist
			if (length < currentSelectedSongIndex + 2) {
				break;
			}
			this.playListHandler
					.changeSelectedAudioObjectToIndex(++currentSelectedSongIndex);
			selectedFiles.clear();
			selectedFiles.add(this.playListHandler.getSelectedAudioObjects()
					.get(0));
			validAudioFile = this.localAudioObjectValidator
					.isValidAudioFile(selectedFiles.get(0).getUrl());
		}
		if (validAudioFile) {
			nextFile.add((ILocalAudioObject) selectedFiles.get(0));
			this.controller.editFiles(nextFile);
		}
	}

	/**
	 * 
	 */
	private void processOkAction() {
		this.controller.editTag();
		this.controller.clear();
		this.dialog.setVisible(false);
	}

	/**
	 * Returns the common parent directory of the audio files or
	 * <code>null</code> if there is no common parent directory.
	 * 
	 * @return the common parent directory or <code>null</code>
	 */
	private File getCommonDirectoryForAudioFiles() {
		List<ILocalAudioObject> audioFilesEditing = this.controller
				.getAudioFilesEditing();
		if (audioFilesEditing.size() == 1) {
			return audioFilesEditing.get(0).getFile().getParentFile();
		} else {
			return null;
		}
	}

	private static class ImagesFileFiler extends FileFilter {
		@Override
		public boolean accept(final File pathname) {
			if (pathname.isDirectory()) {
				return true;
			}
			String fileName = pathname.getName().toUpperCase();
			return fileName.endsWith("JPG") || fileName.endsWith("JPEG")
					|| fileName.endsWith("PNG");
		}

		@Override
		public String getDescription() {
			return I18nUtils.getString("IMAGES");
		}
	}

}
