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
package net.sourceforge.atunes.kernel.modules.player.mplayer;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;

class PodcastFeedEntryMPlayerOutputReader extends MPlayerOutputReader {

    /** The podcast feed entry. */
    private PodcastFeedEntry podcastFeedEntry;

    private boolean started;

    /**
     * Instantiates a new podcast feed entry mplayer output reader.
     * 
     * @param engine
     *            the engine
     * @param process
     *            the process
     * @param podcastFeedEntry
     *            the podcast feed entry
     */
    PodcastFeedEntryMPlayerOutputReader(MPlayerEngine engine, Process process, PodcastFeedEntry podcastFeedEntry) {
        super(engine, process);
        this.podcastFeedEntry = podcastFeedEntry;
    }

    @Override
    protected void read(String line) {
        super.read(line);

        readAndApplyLength(podcastFeedEntry, line, false);

        // When starting playback, update status bar
        if (line.startsWith("Starting playback")) {
            GuiHandler.getInstance().updateStatusBar(podcastFeedEntry);
            if (!started) {
                getEngine().notifyRadioOrPodcastFeedEntry();
                started = true;
            }
        }
    }

}
