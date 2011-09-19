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

import net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class SearchArtistAtAction extends CustomAbstractAction {

    private static final long serialVersionUID = -8934175706272236046L;

    SearchArtistAtAction() {
        super(StringUtils.getString(I18nUtils.getString("SEARCH_ARTIST_AT"), "..."));
        putValue(SHORT_DESCRIPTION, StringUtils.getString(I18nUtils.getString("SEARCH_ARTIST_AT"), "..."));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath path = getBean(INavigationHandler.class).getCurrentView().getTree().getSelectionPath();
        if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof Artist) {
            Artist a = (Artist) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            ISearchDialog dialog = getBean(ISearchDialog.class);
            ISearch search = getBean(INavigationHandler.class).openSearchDialog(dialog, true);
            if (dialog.isSetAsDefault() && search != null) {
                getState().setDefaultSearch(search.toString());
            }
            if (search != null) {
                DesktopUtils.openSearch(search, a.getName());
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (selection.isEmpty()) {
            return false;
        }

        for (DefaultMutableTreeNode node : selection) {
            if (!(node.getUserObject() instanceof Artist) || ((Artist) node.getUserObject()).isUnknownArtist()) {
                return false;
            }
        }

        return true;
    }
}
