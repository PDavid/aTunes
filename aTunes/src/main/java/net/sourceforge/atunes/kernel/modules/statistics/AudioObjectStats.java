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

package net.sourceforge.atunes.kernel.modules.statistics;

import java.io.Serializable;
import java.util.Date;

import net.sourceforge.atunes.model.IAudioObjectStatistics;

/**
 * The playing stats of an audio object.
 */
public class AudioObjectStats implements Serializable, IAudioObjectStatistics {

    private static final long serialVersionUID = -2392613471327847012L;

    /** The last played. */
    private Date lastPlayed;

    /** The times played. */
    private int timesPlayed;

    /**
     * Instantiates a new audio file stats object.
     */
    public AudioObjectStats() {
        lastPlayed = new Date();
        timesPlayed = 1;
    }

    @Override
	public Date getLastPlayed() {
        return new Date(lastPlayed.getTime());
    }

    @Override
	public int getTimesPlayed() {
        return timesPlayed;
    }

    @Override
	public void increaseTimesPlayed() {
        this.timesPlayed++;
    }

    @Override
	public void reset() {
        lastPlayed = null;
        timesPlayed = 0;
    }

    @Override
	public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = new Date(lastPlayed.getTime());
    }
}
