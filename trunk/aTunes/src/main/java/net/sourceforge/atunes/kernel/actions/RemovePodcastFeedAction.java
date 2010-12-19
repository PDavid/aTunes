/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class RemovePodcastFeedAction extends AbstractAction {

    private static final long serialVersionUID = -7470658878101801512L;

    RemovePodcastFeedAction() {
        super(I18nUtils.getString("REMOVE_PODCAST_FEED"), Images.getImage(Images.DELETE_FILE));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("REMOVE_PODCAST_FEED"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath[] paths = NavigationHandler.getInstance().getView(PodcastNavigationView.class).getTree().getSelectionPaths();
        Set<PodcastFeed> podcastsToRemove = new HashSet<PodcastFeed>();
        for (TreePath path : paths) {
        	PodcastFeed podcastFeed = (PodcastFeed) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            podcastsToRemove.add(podcastFeed);
        }        	
        for (PodcastFeed pf : podcastsToRemove) {
            PodcastFeedHandler.getInstance().removePodcastFeed(pf);
        }
    }
    
    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

}
