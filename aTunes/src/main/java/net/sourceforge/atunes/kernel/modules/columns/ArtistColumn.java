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
package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.playList.Column;
import net.sourceforge.atunes.kernel.modules.favorites.FavoritesHandler;
import net.sourceforge.atunes.model.AudioObject;

public class ArtistColumn extends Column {

    /**
     * 
     */
    private static final long serialVersionUID = 8144686293055648148L;

    public ArtistColumn() {
        super("ARTIST", JLabel.class);
        setVisible(true);
    }
    
    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        return ao1.getArtist().compareTo(ao2.getArtist());
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        // Return artist
        return new JLabel(audioObject.getArtist(), !FavoritesHandler.getInstance().getFavoriteArtistsInfo().containsKey(audioObject.getArtist()) ? null
                : ImageLoader.ARTIST_FAVORITE, SwingConstants.LEFT);
    }

}
