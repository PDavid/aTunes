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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class EditTitlesAction extends CustomAbstractAction {

    private static final long serialVersionUID = -2883223880879440970L;

    private INavigationHandler navigationHandler;
    
    private ITagHandler tagHandler;
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @param tagHandler
     */
    public void setTagHandler(ITagHandler tagHandler) {
		this.tagHandler = tagHandler;
	}
    
    public EditTitlesAction() {
        super(I18nUtils.getString("EDIT_TITLES"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("EDIT_TITLES"));
    }

    @Override
    protected void executeAction() {
        TreePath path = navigationHandler.getCurrentView().getTree().getSelectionPath();
        IAlbum a = (IAlbum) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        tagHandler.editFiles(a);
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (selection.isEmpty()) {
            return false;
        }

        for (DefaultMutableTreeNode node : selection) {
            if (!(node.getUserObject() instanceof IAlbum)) {
                return false;
            }
        }

        return true;
    }
}
