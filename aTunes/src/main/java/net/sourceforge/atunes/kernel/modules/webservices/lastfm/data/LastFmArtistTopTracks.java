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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.ITrackInfo;
import de.umass.lastfm.Track;

/**
 * Top tracks retrieved from last.fm
 * 
 * @author alex
 * 
 */
public class LastFmArtistTopTracks implements IArtistTopTracks {

    private static final long serialVersionUID = 3643217441833583787L;

    private String artist;

    private List<ITrackInfo> tracks;

    /**
     * Returns an object containing information about artist top tracks
     * 
     * @param artist
     * @param topTracks
     * @return
     */
    public static IArtistTopTracks getTopTracks(final String artist,
	    final Collection<Track> topTracks) {
	LastFmArtistTopTracks result = new LastFmArtistTopTracks();
	result.artist = artist;

	if (topTracks != null) {
	    List<ITrackInfo> ts = new ArrayList<ITrackInfo>();
	    for (Track t : topTracks) {
		ts.add(LastFmTrack.getTrack(t));
	    }
	    result.tracks = ts;
	}

	return result;
    }

    @Override
    public String getArtist() {
	return artist;
    }

    @Override
    public List<ITrackInfo> getTracks() {
	return tracks;
    }

}
