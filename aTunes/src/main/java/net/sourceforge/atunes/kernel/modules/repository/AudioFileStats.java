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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.Serializable;
import java.util.Date;

/**
 * The Class SongStats.
 */
public class AudioFileStats implements Serializable {

    private static final long serialVersionUID = -2392613471327847012L;

    /** The previous played. */
    private Date previousPlayed;

    /** The last played. */
    private Date lastPlayed;

    /** The times played. */
    private int timesPlayed;

    /**
     * Instantiates a new audio file stats object.
     */
    public AudioFileStats() {
        previousPlayed = null;
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
     * Gets the previous played.
     * 
     * @return the previous played
     */
    public Date getPreviousPlayed() {
        return previousPlayed != null ? new Date(previousPlayed.getTime()) : null;
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
        previousPlayed = null;
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
        this.previousPlayed = this.lastPlayed;
        this.lastPlayed = new Date(lastPlayed.getTime());
    }
}
