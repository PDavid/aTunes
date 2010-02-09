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
package net.sourceforge.atunes.kernel.modules.columns;

import net.sourceforge.atunes.model.AudioObject;

public class GenreColumn extends Column {

    private static final long serialVersionUID = 1420893111015572964L;

    public GenreColumn() {
        super("GENRE", String.class);
        setVisible(true);
        setUsedForFilter(true);
    }

    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        if (ao1.getGenre().equals(ao2.getGenre())) {
            if (ao1.getArtist().equals(ao2.getArtist())) {
                if (ao1.getAlbum().equals(ao2.getAlbum())) {
                    return Integer.valueOf(ao1.getTrackNumber()).compareTo(ao2.getTrackNumber());
                }
                return ao1.getAlbum().compareTo(ao2.getAlbum());
            }
            return ao1.getArtist().compareTo(ao2.getArtist());
        }
        return ao1.getGenre().compareTo(ao2.getGenre());
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        // Return genre
        return audioObject.getGenre();
    }

    @Override
    public String getValueForFilter(AudioObject audioObject) {
        return audioObject.getGenre();
    }
}
