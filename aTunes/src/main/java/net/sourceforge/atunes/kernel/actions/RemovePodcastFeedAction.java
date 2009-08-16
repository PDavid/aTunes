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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.utils.LanguageTool;

public class RemovePodcastFeedAction extends ActionOverSelectedObjects<PodcastFeedEntry> {

    private static final long serialVersionUID = -7470658878101801512L;

    RemovePodcastFeedAction() {
        super(LanguageTool.getString("REMOVE_PODCAST_FEED"), ImageLoader.DELETE_FILE, PodcastFeedEntry.class);
        putValue(SHORT_DESCRIPTION, LanguageTool.getString("REMOVE_PODCAST_FEED"));
    }

    @Override
    protected void performAction(List<PodcastFeedEntry> objects) {
        Set<PodcastFeed> podcastsToRemove = new HashSet<PodcastFeed>();
        for (PodcastFeedEntry pfe : objects) {
            podcastsToRemove.add(pfe.getPodcastFeed());
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
