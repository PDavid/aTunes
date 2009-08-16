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

package net.sourceforge.atunes.kernel.modules.search;

import net.sourceforge.atunes.model.AudioObject;

/**
 * This class represents a search result.
 */
public class SearchResult {

    /** Search result object. */
    private AudioObject audioObject;

    /** Lucene score. */
    private float score;

    /**
     * Constructor.
     * 
     * @param audioFile
     *            the audio file
     * @param score
     *            the score
     */
    public SearchResult(AudioObject audioFile, float score) {
        this.audioObject = audioFile;
        this.score = score;
    }

    /**
     * Returns audio object.
     * 
     * @return the audio object
     */
    public AudioObject getAudioObject() {
        return audioObject;
    }

    /**
     * Returns score.
     * 
     * @return the score
     */
    public float getScore() {
        return score;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return audioObject.getUrl();
    }

}
