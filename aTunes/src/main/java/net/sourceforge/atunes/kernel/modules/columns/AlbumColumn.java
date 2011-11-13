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

import javax.swing.SwingConstants;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IFavoritesHandler;

public class AlbumColumn extends AbstractColumn {

    private static final long serialVersionUID = -6162621108007788707L;

    private IFavoritesHandler favoritesHandler;

    private IColorMutableImageIcon albumFavoriteIcon;
    
    public AlbumColumn() {
        super("ALBUM", TextAndIcon.class);
        setVisible(true);
        setUsedForFilter(true);
        this.favoritesHandler = Context.getBean(IFavoritesHandler.class);
        this.albumFavoriteIcon = (IColorMutableImageIcon) Context.getBean("albumFavoriteIcon");
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
        if (ao1.getAlbum().equals(ao2.getAlbum())) {
            if (ao1.getDiscNumber() == ao2.getDiscNumber()) {
                return Integer.valueOf(ao1.getTrackNumber()).compareTo(ao2.getTrackNumber());
            }
            return Integer.valueOf(ao1.getDiscNumber()).compareTo(ao2.getDiscNumber());
        }
        return ao1.getAlbum().compareTo(ao2.getAlbum());
    }

    @Override
    public Object getValueFor(IAudioObject audioObject) {
    	if (favoritesHandler.getFavoriteAlbumsInfo().containsKey(audioObject.getAlbum())) {
            return new TextAndIcon(audioObject.getAlbum(), albumFavoriteIcon, SwingConstants.LEFT);
    	} else {
    		return new TextAndIcon(audioObject.getAlbum(), null, SwingConstants.LEFT);
    	}
    }

    @Override
    public String getValueForFilter(IAudioObject audioObject) {
        return audioObject.getAlbum();
    }
}
