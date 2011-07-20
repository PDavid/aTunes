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
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.utils.I18nUtils;

public class MarkPodcastListenedAction extends CustomAbstractAction {

    private static final long serialVersionUID = 2594418895817769179L;

    MarkPodcastListenedAction() {
        super(I18nUtils.getString("MARK_PODCAST_AS_LISTENED"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("MARK_PODCAST_AS_LISTENED"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath path = NavigationHandler.getInstance().getView(PodcastNavigationView.class).getTree().getSelectionPath();
        PodcastFeed podcastFeedToMarkAsListened = (PodcastFeed) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        podcastFeedToMarkAsListened.markEntriesAsListened();
        NavigationHandler.getInstance().refreshView(PodcastNavigationView.class);
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (rootSelected) {
            return false;
        }

        boolean nonListenedEntries = false;
        for (DefaultMutableTreeNode node : selection) {
            List<PodcastFeedEntry> podcastFeedEntries = ((PodcastFeed) node.getUserObject()).getPodcastFeedEntries();
            for (PodcastFeedEntry entry : podcastFeedEntries) {
                if (!entry.isListened()) {
                    nonListenedEntries = true;
                }
            }
        }

        return nonListenedEntries;
    }

}
