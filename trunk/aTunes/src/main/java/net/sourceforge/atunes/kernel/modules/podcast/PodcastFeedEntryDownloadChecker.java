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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.utils.Logger;

/**
 * Checks if podcasts are downloaded.
 */
public class PodcastFeedEntryDownloadChecker implements Runnable {

	private IFrame frame;
	
	private IPodcastFeedHandler podcastFeedHandler;
	
    private static final class SetDownloadedRunnable implements Runnable {
    	
        private final Map<IPodcastFeedEntry, Boolean> downloaded;
        private IFrame frame;

        private SetDownloadedRunnable(Map<IPodcastFeedEntry, Boolean> downloaded, IFrame frame) {
            this.downloaded = downloaded;
            this.frame = frame;
        }

        @Override
        public void run() {
            for (Entry<IPodcastFeedEntry, Boolean> entry : downloaded.entrySet()) {
                entry.getKey().setDownloaded(entry.getValue());
            }
            frame.getNavigationTablePanel().getNavigationTable().repaint();
        }
    }

    private final class GetPodcastFeedEntriesFilesRunnable implements Runnable {
        private final Map<IPodcastFeedEntry, File> files;

        private GetPodcastFeedEntriesFilesRunnable(Map<IPodcastFeedEntry, File> files) {
            this.files = files;
        }

        @Override
        public void run() {
            for (IPodcastFeedEntry podcastFeedEntry : podcastFeedHandler.getPodcastFeedEntries()) {
                File f = new File(podcastFeedHandler.getDownloadPath(podcastFeedEntry));
                files.put(podcastFeedEntry, f);
            }
        }
    }

    public PodcastFeedEntryDownloadChecker(IFrame frame, IPodcastFeedHandler podcastFeedHandler) {
    	this.frame = frame;
    	this.podcastFeedHandler = podcastFeedHandler;
	}
    
    @Override
    public void run() {
        final Map<IPodcastFeedEntry, File> files = new HashMap<IPodcastFeedEntry, File>();
        try {
            SwingUtilities.invokeAndWait(new GetPodcastFeedEntriesFilesRunnable(files));
        } catch (InterruptedException e) {
            return;
        } catch (InvocationTargetException e) {
            Logger.error(e);
        }
        final Map<IPodcastFeedEntry, Boolean> downloaded = new HashMap<IPodcastFeedEntry, Boolean>();
        for (Entry<IPodcastFeedEntry, File> entry : files.entrySet()) {
            downloaded.put(entry.getKey(), entry.getValue().exists() && !podcastFeedHandler.isDownloading(entry.getKey()));
        }
        SwingUtilities.invokeLater(new SetDownloadedRunnable(downloaded, frame));
    }
}
