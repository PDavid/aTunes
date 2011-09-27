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

package net.sourceforge.atunes.kernel.modules.columns;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;

public class FavoriteColumn extends AbstractColumn {

    /**
     * 
     */
    private static final long serialVersionUID = -4652512586792166062L;

    private IFavoritesHandler favoritesHandler;
    
    public FavoriteColumn() {
        super("FAVORITES", Property.class);
        setResizable(false);
        setWidth(20);
        setVisible(true);
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
        return 0;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    @Override
    public Object getValueFor(IAudioObject audioObject) {
        // Return image
        if (audioObject instanceof IRadio) {
            return null;
        }
        if (audioObject instanceof IPodcastFeedEntry) {
            return null;
        }
        return getFavoritesHandler().getFavoriteSongsInfo().containsValue(audioObject) ? Property.FAVORITE : null;
    }

    @Override
    public String getHeaderText() {
        return "";
    }
    
    private IFavoritesHandler getFavoritesHandler() {
    	if (favoritesHandler == null) {
    		favoritesHandler = Context.getBean(IFavoritesHandler.class);
    	}
    	return favoritesHandler;
    }


}
