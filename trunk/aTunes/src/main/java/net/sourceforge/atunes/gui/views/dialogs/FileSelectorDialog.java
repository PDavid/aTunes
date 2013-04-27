/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileSelectorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Dialog to select file
 * 
 * @author alex
 * 
 */
public class FileSelectorDialog implements IFileSelectorDialog {

	private IFrame frame;

	private FilenameFilter fileFilter;

	private IOSManager osManager;

	private IDialogFactory dialogFactory;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param fileFilter
	 */
	@Override
	public void setFileFilter(final FilenameFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	/**
	 * @param frame
	 */
	public void setFrame(final IFrame frame) {
		this.frame = frame;
	}

	/**
	 * Selects a file
	 * 
	 * @param path
	 * @return
	 */
	@Override
	public File loadFile(final String path) {
		return getFile(path, null, I18nUtils.getString("LOAD"), FileDialog.LOAD);
	}

	/**
	 * Selects a file
	 * 
	 * @param path
	 * @return
	 */
	@Override
	public File loadFile(final File path) {
		if (path == null) {
			throw new IllegalArgumentException("Null path");
		}
		return loadFile(FileUtils.getPath(path));
	}

	@Override
	public File saveFile(final File path, final String suggestedName) {
		if (path == null) {
			throw new IllegalArgumentException("Null path");
		}
		return saveFile(FileUtils.getPath(path), suggestedName);
	}

	@Override
	public File saveFile(final String path, final String suggestedName) {
		return getFile(path, suggestedName, I18nUtils.getString("SAVE"),
				FileDialog.SAVE);
	}

	private File getFile(final String path, final String name,
			final String title, final int mode) {
		if (this.osManager.isMacOsX()) {
			return getFileWithFileDialog(path, name, title, mode);
		} else {
			return getFileWithJFileChooser(path, name, title, mode);
		}
	}

	/**
	 * Opens file dialog to get file
	 * 
	 * @param path
	 * @param name
	 * @param title
	 * @param mode
	 * @return
	 */
	private File getFileWithFileDialog(final String path, final String name,
			final String title, final int mode) {
		FileDialog dialog = new FileDialog(this.frame.getFrame(), title, mode);
		dialog.setDirectory(path);
		dialog.setFile(name);
		if (this.fileFilter != null) {
			dialog.setFilenameFilter(this.fileFilter);
		}
		dialog.setVisible(true);
		String file = dialog.getFile();
		String dir = dialog.getDirectory();
		if (file != null) {
			// Get selected file
			return this.osManager.getFile(dir, file);
		}
		return null;
	}

	/**
	 * Opens file dialog to get file
	 * 
	 * @param path
	 * @param name
	 * @param title
	 * @param mode
	 * @return
	 */
	private File getFileWithJFileChooser(final String path, final String name,
			final String title, final int mode) {
		JFileChooser dialog = this.controlsBuilder.getFileChooser(path, name);
		if (this.fileFilter != null) {
			dialog.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return FileSelectorDialog.this.fileFilter.toString();
				}

				@Override
				public boolean accept(final File f) {
					return FileSelectorDialog.this.fileFilter.accept(
							f.getParentFile(), f.getName());
				}
			});
		}
		int userResponse;
		if (mode == FileDialog.LOAD) {
			userResponse = dialog.showOpenDialog(this.frame.getFrame());
		} else {
			userResponse = dialog.showSaveDialog(this.frame.getFrame());
		}
		if (userResponse == JFileChooser.APPROVE_OPTION) {
			File file = dialog.getSelectedFile();
			if (mode == FileDialog.SAVE) {
				// If file exists ask user to confirm
				boolean canWrite = !file.exists();
				if (!canWrite) {
					IConfirmationDialog confirmationDialog = this.dialogFactory
							.newDialog(IConfirmationDialog.class);
					confirmationDialog.setMessage(I18nUtils
							.getString("OVERWRITE_FILE"));
					confirmationDialog.showDialog();
					canWrite = confirmationDialog.userAccepted();
				}
				if (!canWrite) {
					// User rejected to overwrite file so ask again to select a
					// file
					file = getFileWithJFileChooser(path, name, title, mode);
				}
			}
			return file;
		}
		return null;
	}

	@Override
	public void hideDialog() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void showDialog() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initialize() {
		// do nothing
	}

	@Override
	@Deprecated
	public void setTitle(final String title) {
		// Not used
	}
}
