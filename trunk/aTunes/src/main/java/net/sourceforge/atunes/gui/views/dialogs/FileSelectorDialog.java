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

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.model.IFileSelectorDialog;
import net.sourceforge.atunes.model.IFrame;

public class FileSelectorDialog implements IFileSelectorDialog {

	private IFrame frame;
	
	private FileFilter fileFilter;
	
	/**
	 * @param fileFilter
	 */
	@Override
	public void setFileFilter(FileFilter fileFilter) {
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
	public File selectFile(String path) {
        JFileChooser fileChooser = new JFileChooser(path);
        if (fileFilter != null) {
        	fileChooser.setFileFilter(fileFilter);
        }
        if (fileChooser.showOpenDialog(frame.getFrame()) == JFileChooser.APPROVE_OPTION) {
            // Get selected file
            return fileChooser.getSelectedFile();
        }
        return null;
    }
	
	/**
	 * Selects a file
	 * @param path
	 * @return
	 */
	@Override
	public File selectFile(File path) {
		if (path == null) {
			throw new IllegalArgumentException("Null path");
		}
		return selectFile(path.getAbsolutePath());
	}
}
