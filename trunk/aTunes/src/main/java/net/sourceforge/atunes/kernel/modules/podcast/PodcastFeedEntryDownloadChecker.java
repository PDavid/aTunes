/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

/**
 * Checks if podcasts are downloaded.
 */
public class PodcastFeedEntryDownloadChecker implements Runnable {

    private static class SetDownloadedRunnable implements Runnable {
		private final Map<PodcastFeedEntry, Boolean> downloaded;

		private SetDownloadedRunnable(Map<PodcastFeedEntry, Boolean> downloaded) {
			this.downloaded = downloaded;
		}

		@Override
		public void run() {
		    for (Entry<PodcastFeedEntry, Boolean> entry : downloaded.entrySet()) {
		        entry.getKey().setDownloaded(entry.getValue());
		    }
		    GuiHandler.getInstance().getNavigationTablePanel().getNavigationTable().repaint();
		}
	}

	private static class GetPodcastFeedEntriesFilesRunnable implements Runnable {
		private final Map<PodcastFeedEntry, File> files;

		private GetPodcastFeedEntriesFilesRunnable(
				Map<PodcastFeedEntry, File> files) {
			this.files = files;
		}

		@Override
		public void run() {
		    for (PodcastFeedEntry podcastFeedEntry : PodcastFeedHandler.getInstance().getPodcastFeedEntries()) {
		        File f = new File(PodcastFeedHandler.getInstance().getDownloadPath(podcastFeedEntry));
		        files.put(podcastFeedEntry, f);
		    }
		}
	}

	private Logger logger = new Logger();

    @Override
    public void run() {
        final Map<PodcastFeedEntry, File> files = new HashMap<PodcastFeedEntry, File>();
        try {
            SwingUtilities.invokeAndWait(new GetPodcastFeedEntriesFilesRunnable(files));
        } catch (InterruptedException e) {
            return;
        } catch (InvocationTargetException e) {
            logger.error(LogCategories.PODCAST, e);
        }
        final Map<PodcastFeedEntry, Boolean> downloaded = new HashMap<PodcastFeedEntry, Boolean>();
        for (Entry<PodcastFeedEntry, File> entry : files.entrySet()) {
            downloaded.put(entry.getKey(), entry.getValue().exists() && !PodcastFeedHandler.getInstance().isDownloading(entry.getKey()));
        }
        SwingUtilities.invokeLater(new SetDownloadedRunnable(downloaded));
    }
}
