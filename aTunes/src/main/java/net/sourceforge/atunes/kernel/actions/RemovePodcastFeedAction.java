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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class RemovePodcastFeedAction extends CustomAbstractAction {

    private static final long serialVersionUID = -7470658878101801512L;

    private INavigationHandler navigationHandler;
    
    private IPodcastFeedHandler podcastFeedHandler;
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @param podcastFeedHandler
     */
    public void setPodcastFeedHandler(IPodcastFeedHandler podcastFeedHandler) {
		this.podcastFeedHandler = podcastFeedHandler;
	}
    
    public RemovePodcastFeedAction() {
        super(I18nUtils.getString("REMOVE_PODCAST_FEED"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("REMOVE_PODCAST_FEED"));
    }

    @Override
    protected void executeAction() {
        TreePath[] paths = navigationHandler.getView(PodcastNavigationView.class).getTree().getSelectionPaths();
        Set<PodcastFeed> podcastsToRemove = new HashSet<PodcastFeed>();
        for (TreePath path : paths) {
        	PodcastFeed podcastFeed = (PodcastFeed) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            podcastsToRemove.add(podcastFeed);
        }        	
        for (IPodcastFeed pf : podcastsToRemove) {
        	podcastFeedHandler.removePodcastFeed(pf);
        }
    }
    
    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

}
