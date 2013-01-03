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
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Downloads a podcast entry
 * 
 * @author alex
 * 
 */
public class DownloadPodcastEntryAction extends
	AbstractActionOverSelectedObjects<IPodcastFeedEntry> {

    private static final long serialVersionUID = 1081237259786604605L;

    private IPodcastFeedHandler podcastFeedHandler;

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
    public DownloadPodcastEntryAction() {
	super(I18nUtils.getString("DOWNLOAD_PODCAST_ENTRY"));
	setEnabled(false);
    }

    @Override
    protected void executeAction(final List<IPodcastFeedEntry> objects) {
	for (IPodcastFeedEntry pfe : objects) {
	    podcastFeedHandler.downloadPodcastFeedEntry(pfe);
	}
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(
	    final List<IAudioObject> selection) {
	for (IAudioObject ao : selection) {
	    if (!(ao instanceof IPodcastFeedEntry)
		    || ((IPodcastFeedEntry) ao).isDownloaded()
		    || podcastFeedHandler.isDownloading((IPodcastFeedEntry) ao)) {
		return false;
	    }
	}
	return true;
    }

}
