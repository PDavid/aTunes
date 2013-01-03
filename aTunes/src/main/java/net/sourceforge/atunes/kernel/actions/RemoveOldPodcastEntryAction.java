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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Removes podcast feed entries
 * 
 * @author alex
 * 
 */
public class RemoveOldPodcastEntryAction extends
	AbstractActionOverSelectedObjects<IPodcastFeedEntry> {

    private static final long serialVersionUID = -1499729879534990802L;

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
    public RemoveOldPodcastEntryAction() {
	super(I18nUtils.getString("REMOVE_OLD_PODCAST_ENTRY"));
    }

    @Override
    protected void executeAction(final List<IPodcastFeedEntry> objects) {
	for (IPodcastFeedEntry pfe : objects) {
	    pfe.getPodcastFeed().removeEntry(pfe);
	}
	navigationHandler.refreshView(podcastNavigationView);
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(
	    final List<IAudioObject> selection) {
	for (IAudioObject ao : selection) {
	    if (!(ao instanceof IPodcastFeedEntry)
		    || !((IPodcastFeedEntry) ao).isOld()) {
		return false;
	    }
	}
	return true;
    }

}
