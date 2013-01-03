/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Removes a podcast feed
 * 
 * @author alex
 * 
 */
public class RemovePodcastFeedAction extends CustomAbstractAction {

    private static final long serialVersionUID = -7470658878101801512L;

    private IPodcastFeedHandler podcastFeedHandler;

    private INavigationView podcastNavigationView;

    /**
     * @param podcastNavigationView
     */
    public void setPodcastNavigationView(
	    final INavigationView podcastNavigationView) {
	this.podcastNavigationView = podcastNavigationView;
    }

    /**
     * @param podcastFeedHandler
     */
    public void setPodcastFeedHandler(
	    final IPodcastFeedHandler podcastFeedHandler) {
	this.podcastFeedHandler = podcastFeedHandler;
    }

    /**
     * Default constructor
     */
    public RemovePodcastFeedAction() {
	super(I18nUtils.getString("REMOVE_PODCAST_FEED"));
    }

    @Override
    protected void executeAction() {
	List<ITreeNode> nodes = podcastNavigationView.getTree()
		.getSelectedNodes();
	Set<PodcastFeed> podcastsToRemove = new HashSet<PodcastFeed>();
	for (ITreeNode node : nodes) {
	    podcastsToRemove.add((PodcastFeed) node.getUserObject());
	}
	for (IPodcastFeed pf : podcastsToRemove) {
	    podcastFeedHandler.removePodcastFeed(pf);
	}
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	return !rootSelected && !selection.isEmpty();
    }

}
