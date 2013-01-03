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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens OS file browser with folder of selected elements
 * 
 * @author fleax
 * 
 */
public class OpenFolderFromNavigatorTreeAction extends
	AbstractActionOverSelectedTreeObjects<IFolder> {

    private static final long serialVersionUID = 8251208528513562627L;

    private IDesktop desktop;

    private IOSManager osManager;

    /**
     * Default constructor
     */
    public OpenFolderFromNavigatorTreeAction() {
	super(I18nUtils.getString("OPEN_FOLDER"));
    }

    /**
     * @param osManager
     */
    public void setOsManager(final IOSManager osManager) {
	this.osManager = osManager;
    }

    /**
     * @param desktop
     */
    public void setDesktop(final IDesktop desktop) {
	this.desktop = desktop;
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	return allNodesAreFolders(selection);
    }

    private boolean allNodesAreFolders(final List<ITreeNode> nodes) {
	for (ITreeNode node : nodes) {
	    if (!(node.getUserObject() instanceof IFolder)) {
		return false;
	    }
	}
	return true;
    }

    @Override
    protected void executeAction(final List<IFolder> folders) {
	for (IFolder folder : folders) {
	    desktop.openFile(folder.getFolderPath(osManager));
	}
    }
}
