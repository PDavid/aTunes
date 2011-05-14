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

package net.sourceforge.atunes.kernel.modules.webservices.lyrics;

import java.beans.ConstructorProperties;
import java.io.Serializable;

import net.sourceforge.atunes.utils.StringUtils;

public class Lyrics implements Serializable {

    private static final long serialVersionUID = 8228456817740963954L;

    private String lyrics;
    private String url;

    @ConstructorProperties( { "lyrics", "url" })
    public Lyrics(String lyrics, String url) {
        setLyrics(lyrics);
        this.url = url;
    }

    public void setLyrics(String lyrics) {
        if (lyrics == null) {
            throw new IllegalArgumentException();
        }
        // Many lyrics contain html escape sequences so unescape
        lyrics = StringUtils.unescapeHTML(lyrics, 0);
        this.lyrics = lyrics != null ? lyrics : "";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lyrics == null) ? 0 : lyrics.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Lyrics other = (Lyrics) obj;
        if (lyrics == null) {
            if (other.lyrics != null) {
                return false;
            }
        } else if (!lyrics.equals(other.lyrics)) {
            return false;
        }
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        return true;
    }

}
