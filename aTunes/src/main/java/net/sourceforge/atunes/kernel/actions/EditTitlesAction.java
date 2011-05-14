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

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.tags.TagHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.utils.I18nUtils;

public class EditTitlesAction extends AbstractAction {

    private static final long serialVersionUID = -2883223880879440970L;

    EditTitlesAction() {
        super(I18nUtils.getString("EDIT_TITLES"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("EDIT_TITLES"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath path = NavigationHandler.getInstance().getCurrentView().getTree().getSelectionPath();
        Album a = (Album) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        TagHandler.getInstance().editFiles(a);
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (selection.isEmpty()) {
            return false;
        }

        for (DefaultMutableTreeNode node : selection) {
            if (!(node.getUserObject() instanceof Album)) {
                return false;
            }
        }

        return true;
    }

}
