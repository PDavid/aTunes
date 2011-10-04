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

package net.sourceforge.atunes.kernel.actions;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens OS file browser with folder of selected elements
 * 
 * @author fleax
 * 
 */
public class OpenFolderAction extends AbstractActionOverSelectedObjects<AudioFile> {

    private static final long serialVersionUID = 1682289345922375850L;

    OpenFolderAction() {
        super(I18nUtils.getString("OPEN_FOLDER"), AudioFile.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("OPEN_FOLDER"));
    }

    @Override
    protected void performAction(List<AudioFile> objects) {
        HashSet<File> foldersToOpen = new HashSet<File>();

        // Get folders ...
        for (AudioFile ao : objects) {
            if (!foldersToOpen.contains(ao.getFile().getParentFile())) {
                foldersToOpen.add(ao.getFile().getParentFile());
            }
        }

        // ... then open
        for (File folder : foldersToOpen) {
            DesktopUtils.openFile(folder, getBean(IOSManager.class));
        }
    }
}
