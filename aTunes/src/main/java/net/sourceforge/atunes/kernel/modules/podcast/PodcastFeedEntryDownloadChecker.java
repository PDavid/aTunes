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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.ITable;

/**
 * Checks if podcasts are downloaded.
 */
public class PodcastFeedEntryDownloadChecker implements Runnable {

	private ITable navigationTable;
	
	private IPodcastFeedHandler podcastFeedHandler;
	
    /**
     * @param navigationTable
     * @param podcastFeedHandler
     */
    public PodcastFeedEntryDownloadChecker(ITable navigationTable, IPodcastFeedHandler podcastFeedHandler) {
    	this.navigationTable = navigationTable;
    	this.podcastFeedHandler = podcastFeedHandler;
	}
    
    @Override
    public void run() {
    	if (!podcastFeedHandler.getPodcastFeeds().isEmpty()) {
    		final Map<IPodcastFeedEntry, File> files = new HashMap<IPodcastFeedEntry, File>();
            for (IPodcastFeedEntry podcastFeedEntry : podcastFeedHandler.getPodcastFeedEntries()) {
                files.put(podcastFeedEntry, new File(podcastFeedHandler.getDownloadPath(podcastFeedEntry)));
            }
    		final Map<IPodcastFeedEntry, Boolean> downloaded = new HashMap<IPodcastFeedEntry, Boolean>();
    		for (Entry<IPodcastFeedEntry, File> entry : files.entrySet()) {
    			downloaded.put(entry.getKey(), entry.getValue().exists() && !podcastFeedHandler.isDownloading(entry.getKey()));
    		}
    		SwingUtilities.invokeLater(new SetDownloadedRunnable(downloaded, navigationTable));
    	}
    }
}
