/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.player.vlcplayer;

import java.io.File;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;

class PodcastFeedEntryVlcPlayerOutputReader extends VlcPlayerOutputReader {

    private PodcastFeedEntry podcastFeedEntry;

    /**
     * Instantiates a new podcast feed entry m player output reader.
     * 
     * @param engine
     *            the engine
     * @param process
     *            the process
     * @param podcastFeedEntry
     *            the podcast feed entry
     */
    PodcastFeedEntryVlcPlayerOutputReader(VlcPlayerEngine engine, PodcastFeedEntry podcastFeedEntry) {
        super(engine);
        this.podcastFeedEntry = podcastFeedEntry;
    }

    @Override
    protected void init() {
        super.init();
        engine.setCurrentLength(podcastFeedEntry.getDuration() * 1000);

        //it's reasonable to enable the slide bar if playing
        //feed entry is a valid audio file, even if it was not downloaded  
        if (AudioFile.isValidAudioFile(new File(podcastFeedEntry.getUrl()))) {
            ControllerProxy.getInstance().getPlayerControlsController().setSlidable(true);
        }
    }

}
