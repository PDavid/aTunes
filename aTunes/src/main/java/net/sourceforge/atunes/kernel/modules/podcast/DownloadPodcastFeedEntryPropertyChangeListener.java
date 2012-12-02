/*
 * aTunes 3.0.0
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IProgressDialog;

final class DownloadPodcastFeedEntryPropertyChangeListener implements PropertyChangeListener {
	
	private final PodcastFeedHandler podcastFeedHandler;
	private final IPodcastFeedEntry podcastFeedEntry;
	private final IProgressDialog d;
	private final PodcastFeedEntryDownloader downloadPodcastFeedEntry;
	private final List<PodcastFeedEntryDownloader> runningDownloads;

	DownloadPodcastFeedEntryPropertyChangeListener(
			PodcastFeedHandler podcastFeedHandler,
			IPodcastFeedEntry podcastFeedEntry, IProgressDialog d,
			PodcastFeedEntryDownloader downloadPodcastFeedEntry,
			List<PodcastFeedEntryDownloader> runningDownloads) {
		this.podcastFeedHandler = podcastFeedHandler;
		this.podcastFeedEntry = podcastFeedEntry;
		this.d = d;
		this.downloadPodcastFeedEntry = downloadPodcastFeedEntry;
		this.runningDownloads = runningDownloads;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	    if (evt.getPropertyName().equals("progress")) {
	        d.setProgressBarValue((Integer) evt.getNewValue());
	        if ((Integer) evt.getNewValue() == 100) {
	            d.hideDialog();
	            synchronized (runningDownloads) {
	                runningDownloads.remove(downloadPodcastFeedEntry);
	            }
	        }
	    } else if (evt.getPropertyName().equals("byteProgress")) {
	        d.setCurrentProgress((Long) evt.getNewValue());
	    } else if (evt.getPropertyName().equals("totalBytes")) {
	        d.setTotalProgress((Long) evt.getNewValue());
	    } else if (evt.getPropertyName().equals("failed")) {
	    	podcastFeedHandler.cancelDownloading(podcastFeedEntry, d, downloadPodcastFeedEntry);
	    }
	}
}