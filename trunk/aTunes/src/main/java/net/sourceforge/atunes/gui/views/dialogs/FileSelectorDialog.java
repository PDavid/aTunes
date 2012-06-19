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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.FileDialog;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;

import net.sourceforge.atunes.model.IFileSelectorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

public class FileSelectorDialog implements IFileSelectorDialog {

	private IFrame frame;
	
	private FilenameFilter fileFilter;
	
	/**
	 * @param fileFilter
	 */
	@Override
	public void setFileFilter(FilenameFilter fileFilter) {
		this.fileFilter = fileFilter;
	}
	
	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * Selects a file
	 * @param path
	 * @return
	 */
	@Override
	public File loadFile(String path) {
		return getFile(path, I18nUtils.getString("LOAD"), FileDialog.LOAD);
    }
	
	/**
	 * Selects a file
	 * @param path
	 * @return
	 */
	@Override
	public File loadFile(File path) {
		if (path == null) {
			throw new IllegalArgumentException("Null path");
		}
		return loadFile(path.getAbsolutePath());
	}
	
	@Override
	public File saveFile(File path) {
		if (path == null) {
			throw new IllegalArgumentException("Null path");
		}
		return saveFile(path.getAbsolutePath());
	}
	
	@Override
	public File saveFile(String path) {
		return getFile(path, I18nUtils.getString("SAVE"), FileDialog.SAVE);
	}
	
	/**
	 * Opens file dialog to get file
	 * @param path
	 * @param title
	 * @param mode
	 * @return
	 */
	private File getFile(String path, String title, int mode) {
		FileDialog dialog = new FileDialog(frame.getFrame(), title, mode);
		dialog.setDirectory(path);
        if (fileFilter != null) {
        	dialog.setFilenameFilter(fileFilter);
        }
        dialog.setVisible(true);
        String file = dialog.getFile();
        String dir = dialog.getDirectory();
        if (file != null) {
            // Get selected file
            return new File(dir + "/" + file);
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
}
