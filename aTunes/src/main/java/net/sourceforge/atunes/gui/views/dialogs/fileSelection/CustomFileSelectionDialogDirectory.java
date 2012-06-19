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

package net.sourceforge.atunes.gui.views.dialogs.fileSelection;

import java.io.File;

/**
 * The Class Directory.
 */
class CustomFileSelectionDialogDirectory {

    /** The file. */
    private File file;

    /**
     * Instantiates a new directory.
     * 
     * @param file
     *            the file
     */
    CustomFileSelectionDialogDirectory(File file) {
        this.file = file;
    }
    
    /**
     * @return underlying file
     */
    File getFile() {
		return file;
	}

    @Override
    public String toString() {
        return CustomFileSelectionDialog.fsView.getSystemDisplayName(file);
    }
}