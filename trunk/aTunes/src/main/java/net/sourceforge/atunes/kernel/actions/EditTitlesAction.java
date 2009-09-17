/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.utils.I18nUtils;

public class EditTitlesAction extends Action {

    private static final long serialVersionUID = -2883223880879440970L;

    EditTitlesAction() {
        super(I18nUtils.getString("EDIT_TITLES"), ImageLoader.getImage(ImageLoader.EDIT_ALBUM));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("EDIT_TITLES"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath path = NavigationHandler.getInstance().getCurrentView().getTree().getSelectionPath();
        Album a = (Album) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        ControllerProxy.getInstance().getEditTitlesDialogController().editFiles(a);
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
