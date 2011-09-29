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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Refresh folder of selected elements
 * 
 * @author fleax
 * 
 */
public class RefreshFolderFromNavigatorAction extends AbstractActionOverSelectedTreeObjects<Folder> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6840836346786226858L;

	RefreshFolderFromNavigatorAction() {
        super(I18nUtils.getString("REFRESH_FOLDER"), Folder.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("REFRESH_FOLDER"));
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (selection.isEmpty()) {
            return false;
        }

        if (getState().getViewMode() != ViewMode.FOLDER) {
            return false;
        }

        for (DefaultMutableTreeNode node : selection) {
            if (!(node.getUserObject() instanceof Folder)) {
                return false;
            }
        }

        return true;
    }
    
    @Override
    protected void performAction(List<Folder> folders) {
       	getBean(IRepositoryHandler.class).refreshFolders(folders);        	        
    }

}
