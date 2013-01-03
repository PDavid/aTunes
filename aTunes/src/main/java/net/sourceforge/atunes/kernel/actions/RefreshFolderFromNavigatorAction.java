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

import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Refresh folder of selected elements
 * 
 * @author fleax
 * 
 */
public class RefreshFolderFromNavigatorAction extends
	AbstractActionOverSelectedTreeObjects<IFolder> {

    private static final long serialVersionUID = -6840836346786226858L;

    private IRepositoryHandler repositoryHandler;

    private IStateNavigation stateNavigation;

    /**
     * @param stateNavigation
     */
    public void setStateNavigation(final IStateNavigation stateNavigation) {
	this.stateNavigation = stateNavigation;
    }

    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
	this.repositoryHandler = repositoryHandler;
    }

    /**
     * Default constructor
     */
    public RefreshFolderFromNavigatorAction() {
	super(I18nUtils.getString("REFRESH_FOLDER"));
    }

    @Override
    protected void executeAction(final List<IFolder> folders) {
	repositoryHandler.refreshFolders(folders);
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	if (selection.isEmpty()) {
	    return false;
	}

	if (stateNavigation.getViewMode() != ViewMode.FOLDER) {
	    return false;
	}

	for (ITreeNode node : selection) {
	    if (!(node.getUserObject() instanceof IFolder)) {
		return false;
	    }
	}

	return true;
    }
}
