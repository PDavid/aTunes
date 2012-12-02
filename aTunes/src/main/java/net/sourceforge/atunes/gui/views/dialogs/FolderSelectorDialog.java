/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFileChooser;

import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.FileUtils;

/**
 * Dialog to select folder
 * @author alex
 *
 */
public class FolderSelectorDialog implements IFolderSelectorDialog {

	private static final class RejectAllFilesFileFilter implements FilenameFilter {
		@Override
		public boolean accept(final File dir, final String name) {
			return false;
		}
	}

	private IFrame frame;

	private String title;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param title
	 */
	@Override
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @param frame
	 */
	public void setFrame(final IFrame frame) {
		this.frame = frame;
	}

	@Override
	public void showDialog() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void hideDialog() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initialize() {
		// Do nothing
	}

	@Override
	public File selectFolder(final String path) {
		File file = null;
		if (osManager.isMacOsX()) {
			file = selectFolderWithFileChooser(path);
		} else {
			file = selectFolderWithJFileChooser(path);
		}
		return file;
	}

	/**
	 * @param path
	 * @return
	 */
	private File selectFolderWithJFileChooser(final String path) {
		JFileChooser dialog = new JFileChooser(path);
		dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dialog.showOpenDialog(frame.getFrame());
		return dialog.getSelectedFile();
	}

	/**
	 * @param path
	 * @return
	 */
	private File selectFolderWithFileChooser(final String path) {
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
		FileDialog dialog = new FileDialog(frame.getFrame(), title, FileDialog.LOAD);
		dialog.setFilenameFilter(new RejectAllFilesFileFilter());
		dialog.setDirectory(path);
		dialog.setVisible(true);
		String parent = dialog.getDirectory();
		String folder = dialog.getFile();
		System.setProperty("apple.awt.fileDialogForDirectories", "false");
		if (parent != null && folder != null) {
			return new File(parent + '/' + folder);
		}
		return null;

	}

	@Override
	public File selectFolder(final File path) {
		return selectFolder(FileUtils.getPath(path));
	}
}
