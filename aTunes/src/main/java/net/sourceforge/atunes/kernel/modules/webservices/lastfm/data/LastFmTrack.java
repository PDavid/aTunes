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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm.data;

import net.sourceforge.atunes.model.ITrackInfo;
import de.umass.lastfm.Track;

public class LastFmTrack implements ITrackInfo {

    private static final long serialVersionUID = -2692319576271311514L;

    private String title;
    private String url;
    private String artist;
    private String album;
    

    /**
     * Gets the track.
     * 
     * @return the track
     */
    protected static LastFmTrack getTrack(Track t) {
        LastFmTrack track = new LastFmTrack();

        track.title = t.getName();
        track.url = t.getUrl();
        track.artist = t.getArtist();
        track.album = t.getAlbum();

        return track;
    }

    /**
     * Gets the title.
     * 
     * @return the title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    @Override
    public String getUrl() {
        return url;
    }

    /**
     * Sets the title.
     * 
     * @param title
     *            the title to set
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the url to set
     */
    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getArtist() {
		return artist;
	}
    
    @Override
    public String getAlbum() {
		return album;
	}
    
    @Override
    public void setAlbum(String album) {
		this.album = album;
	}

    @Override
    public void setArtist(String artist) {
		this.artist = artist;
	}
}
