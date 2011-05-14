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

/**
 * The playing stats of an audio file.
 */
public class AudioFileStats implements Serializable {

    private static final long serialVersionUID = -2392613471327847012L;

    /** The last played. */
    private Date lastPlayed;

    /** The times played. */
    private int timesPlayed;

    /**
     * Instantiates a new audio file stats object.
     */
    public AudioFileStats() {
        lastPlayed = new Date();
        timesPlayed = 1;
    }

    /**
     * Gets the last played.
     * 
     * @return the last played
     */
    public Date getLastPlayed() {
        return new Date(lastPlayed.getTime());
    }

    /**
     * Gets the times played.
     * 
     * @return the times played
     */
    public int getTimesPlayed() {
        return timesPlayed;
    }

    /**
     * Increase times played.
     */
    public void increaseTimesPlayed() {
        this.timesPlayed++;
    }

    /**
     * Reset.
     */
    public void reset() {
        lastPlayed = null;
        timesPlayed = 0;
    }

    /**
     * Sets the last played.
     * 
     * @param lastPlayed
     *            the new last played
     */
    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = new Date(lastPlayed.getTime());
    }
}
