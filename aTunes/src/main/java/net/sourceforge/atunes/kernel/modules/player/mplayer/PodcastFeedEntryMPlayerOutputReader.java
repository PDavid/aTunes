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

/**
 * Output reader for podcasts
 * 
 * @author alex
 * 
 */
public class PodcastFeedEntryMPlayerOutputReader extends
		AbstractMPlayerOutputReader {

	/** The podcast feed entry. */
	private IPodcastFeedEntry podcastFeedEntry;

	private boolean started;

	/**
	 * @param podcastFeedEntry
	 */
	public void setPodcastFeedEntry(final IPodcastFeedEntry podcastFeedEntry) {
		this.podcastFeedEntry = podcastFeedEntry;
	}

	@Override
	protected void init() {
	}

	@Override
	protected void read(final String line) {
		super.read(line);

		if (!isReadStopped()) {
			readAndApplyLength(this.podcastFeedEntry, line, false);

			// When starting playback, update status bar
			if (!this.started && line.startsWith("Starting playback")) {
				getEngine().notifyRadioOrPodcastFeedEntry();
				this.started = true;
			}
		}
	}

}
