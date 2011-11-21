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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.utils.Logger;

final class DeleteDownloadedPodcastFeedEntryWorker extends SwingWorker<Boolean, Void> {
    private final File f;
    private final IPodcastFeedEntry podcastFeedEntry;
    private ITable navigationTable;

    DeleteDownloadedPodcastFeedEntryWorker(File f, IPodcastFeedEntry podcastFeedEntry, ITable navigationTable) {
        this.f = f;
        this.podcastFeedEntry = podcastFeedEntry;
        this.navigationTable = navigationTable;
    }

    @Override
    protected Boolean doInBackground() {
        return f.delete();
    }

    @Override
    protected void done() {
        try {
            if (get()) {
                podcastFeedEntry.setDownloaded(false);
                navigationTable.repaint();
            }
        } catch (InterruptedException e) {
            Logger.error(e);
        } catch (ExecutionException e) {
            Logger.error(e);
        }
    }
}