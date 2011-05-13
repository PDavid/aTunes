/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.model.AudioObject;

public class FavoriteColumn extends AbstractColumn {

    /**
     * 
     */
    private static final long serialVersionUID = -4652512586792166062L;

    public FavoriteColumn() {
        super("FAVORITES", Property.class);
        setResizable(false);
        setWidth(20);
        setVisible(true);
    }

    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        return 0;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        // Return image
        if (audioObject instanceof Radio) {
            return null;
        }
        if (audioObject instanceof PodcastFeedEntry) {
            return null;
        }
        return FavoritesHandler.getInstance().getFavoriteSongsInfo().containsValue(audioObject) ? Property.FAVORITE : null;
    }

    @Override
    public String getHeaderText() {
        return "";
    }

}
