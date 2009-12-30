/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

public class StatisticsAlbum implements Serializable {

    private static final long serialVersionUID = -5304107353617114945L;

    private String artist;

    private String album;

    protected StatisticsAlbum(String artist, String album) {
        super();
        this.artist = artist;
        this.album = album;
    }

    /**
     * @return the artist
     */
    protected String getArtist() {
        return artist;
    }

    /**
     * @param artist
     *            the artist to set
     */
    protected void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * @return the album
     */
    protected String getAlbum() {
        return album;
    }

    /**
     * @param album
     *            the album to set
     */
    protected void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((album == null) ? 0 : album.hashCode());
        result = prime * result + ((artist == null) ? 0 : artist.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StatisticsAlbum other = (StatisticsAlbum) obj;
        if (album == null) {
            if (other.album != null)
                return false;
        } else if (!album.equals(other.album))
            return false;
        if (artist == null) {
            if (other.artist != null)
                return false;
        } else if (!artist.equals(other.artist))
            return false;
        return true;
    }

}
