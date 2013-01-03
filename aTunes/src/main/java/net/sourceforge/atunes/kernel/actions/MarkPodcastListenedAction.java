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

import java.util.List;

import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Marks a podcast as listened
 * 
 * @author alex
 * 
 */
public class MarkPodcastListenedAction extends
	AbstractActionOverSelectedTreeObjects<IPodcastFeed> {

    private static final long serialVersionUID = 2594418895817769179L;

    private INavigationHandler navigationHandler;

    private INavigationView podcastNavigationView;

    /**
     * @param podcastNavigationView
     */
    public void setPodcastNavigationView(
	    final INavigationView podcastNavigationView) {
	this.podcastNavigationView = podcastNavigationView;
    }

    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(final INavigationHandler navigationHandler) {
	this.navigationHandler = navigationHandler;
    }

    /**
     * Default constructor
     */
    public MarkPodcastListenedAction() {
	super(I18nUtils.getString("MARK_PODCAST_AS_LISTENED"));
    }

    @Override
    protected void executeAction(final List<IPodcastFeed> objects) {
	objects.get(0).markEntriesAsListened();
	navigationHandler.refreshView(podcastNavigationView);
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	if (rootSelected) {
	    return false;
	}

	if (selection.size() != 1) {
	    return false;
	}

	boolean nonListenedEntries = false;
	for (ITreeNode node : selection) {
	    List<IPodcastFeedEntry> podcastFeedEntries = ((IPodcastFeed) node
		    .getUserObject()).getPodcastFeedEntries();
	    for (IPodcastFeedEntry entry : podcastFeedEntries) {
		if (!entry.isListened()) {
		    nonListenedEntries = true;
		}
	    }
	}

	return nonListenedEntries;
    }
}
