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

package net.sourceforge.atunes.kernel.modules.columns;

import net.sourceforge.atunes.model.IAudioObject;

public class GenreColumn extends AbstractColumn<String> {

    private static final long serialVersionUID = 1420893111015572964L;

    public GenreColumn() {
        super("GENRE");
        setVisible(true);
        setUsedForFilter(true);
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
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
    public String getValueFor(IAudioObject audioObject) {
        // Return genre
        return audioObject.getGenre();
    }

    @Override
    public String getValueForFilter(IAudioObject audioObject) {
        return audioObject.getGenre();
    }
}
