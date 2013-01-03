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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import net.sourceforge.atunes.model.IPodcastFeedEntry;

class PodcastFeedEntryMPlayerOutputReader extends AbstractMPlayerOutputReader {

    /** The podcast feed entry. */
    private IPodcastFeedEntry podcastFeedEntry;

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
    PodcastFeedEntryMPlayerOutputReader(MPlayerEngine engine, Process process, IPodcastFeedEntry podcastFeedEntry) {
        super(engine, process);
        this.podcastFeedEntry = podcastFeedEntry;
    }
    
    @Override
    protected void init() {
    }

    @Override
    protected void read(String line) {
        super.read(line);

        readAndApplyLength(podcastFeedEntry, line, false);

        // When starting playback, update status bar
        if (!started && line.startsWith("Starting playback")) {
        	getEngine().notifyRadioOrPodcastFeedEntry();
        	started = true;
        }
    }

}
