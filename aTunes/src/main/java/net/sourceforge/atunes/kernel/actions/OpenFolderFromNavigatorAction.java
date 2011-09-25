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

package net.sourceforge.atunes.kernel.actions;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;

/**
 * Opens OS file browser with folder of selected elements
 * 
 * @author fleax
 * 
 */
public class OpenFolderFromNavigatorAction extends OpenFolderAction {

    private static final long serialVersionUID = 8251208528513562627L;

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        List<IAudioObject> filesSelectedInNavigator = getBean(INavigationHandler.class).getFilesSelectedInNavigator();
        return sameParentFile(AudioFile.getAudioFiles(filesSelectedInNavigator));
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return sameParentFile(AudioFile.getAudioFiles(selection));
    }
    
    /**
     * Checks if a collection of files have the same parent file.
     * 
     * @param c
     *            collection of files
     * @return if a collection of files have the same parent file
     */
    private boolean sameParentFile(Collection<? extends ILocalAudioObject> c) {
        Set<File> set = new HashSet<File>();
        for (ILocalAudioObject af : c) {
            set.add(af.getFile().getParentFile());
        }
        return set.size() == 1;
    }
}
