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

package net.sourceforge.atunes.kernel.modules.statistics;

import java.io.Serializable;

import net.sourceforge.atunes.model.IAudioObjectStatistics;

import org.joda.time.DateTime;

/**
 * The playing statistics of an audio object.
 */
public class AudioObjectStats implements Serializable, IAudioObjectStatistics {

	private static final long serialVersionUID = -2392613471327847012L;

	/** The last played. */
	DateTime lastPlayed;

	/** The times played. */
	int timesPlayed;

	@Override
	public DateTime getLastPlayed() {
		return lastPlayed;
	}

	@Override
	public int getTimesPlayed() {
		return timesPlayed;
	}

	@Override
	public void increaseStatistics() {
		this.timesPlayed++;
		this.lastPlayed = new DateTime();
	}

	@Override
	public void resetStatistics() {
		lastPlayed = null;
		timesPlayed = 0;
	}
}
