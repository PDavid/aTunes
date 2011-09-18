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

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.utils.I18nUtils;

public class RenamePodcastFeedAction extends CustomAbstractAction {

    private static final long serialVersionUID = 8334487960720117561L;

    RenamePodcastFeedAction() {
        super(I18nUtils.getString("RENAME_PODCAST_FEED"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RENAME_PODCAST_FEED"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath path = NavigationHandler.getInstance().getView(PodcastNavigationView.class).getTree().getSelectionPath();
        IPodcastFeed podcastFeed = (IPodcastFeed) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        String result = GuiHandler.getInstance().showInputDialog(I18nUtils.getString("RENAME_PODCAST_FEED"), podcastFeed.getName());
        if (result != null) {
            podcastFeed.setName(result);
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

}
