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

import net.sourceforge.atunes.gui.model.TextAndIcon;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.model.ColumnSort;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFavoritesHandler;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ArtistColumn extends AbstractColumn implements ApplicationContextAware {

    private static final long serialVersionUID = 8144686293055648148L;

    private CachedIconFactory artistFavoriteIcon;

    private ApplicationContext context;
    
    private IFavoritesHandler favoritesHandler;
    
    public ArtistColumn() {
        super("ARTIST", TextAndIcon.class);
        setVisible(true);
        setUsedForFilter(true);
        setColumnSort(ColumnSort.ASCENDING); // Column sets are ordered by default by this column
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	this.context = applicationContext;
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
    	if (ao1.getArtist().equals(ao2.getArtist())) {
        	if (ao1.getAlbum().equals(ao2.getAlbum())) {
        		if (ao1.getDiscNumber() == ao2.getDiscNumber()) {
        			return Integer.valueOf(ao1.getTrackNumber()).compareTo(ao2.getTrackNumber());
        		}
        		return Integer.valueOf(ao1.getDiscNumber()).compareTo(ao2.getDiscNumber());
        	}
        	return ao1.getAlbum().compareTo(ao2.getAlbum());
    	}
        return ao1.getArtist().compareTo(ao2.getArtist());
    }

    @Override
    public Object getValueFor(IAudioObject audioObject) {
    	if (getFavoritesHandler().getFavoriteArtistsInfo().containsKey(audioObject.getArtist())) {
            return new TextAndIcon(audioObject.getArtist(), artistFavoriteIcon.getColorMutableIcon(), SwingConstants.LEFT);
    	} else {
    		return new TextAndIcon(audioObject.getArtist(), null, SwingConstants.LEFT);
    	}
    }

    @Override
    public String getValueForFilter(IAudioObject audioObject) {
        return audioObject.getArtist();
    }
    
    /**
     * @return favorites handler
     */
    private IFavoritesHandler getFavoritesHandler() {
    	if (favoritesHandler == null) {
    		favoritesHandler = context.getBean(IFavoritesHandler.class);
    	}
    	return favoritesHandler;
    }
    
    /**
     * @param artistFavoriteIcon
     */
    public void setArtistFavoriteIcon(CachedIconFactory artistFavoriteIcon) {
		this.artistFavoriteIcon = artistFavoriteIcon;
	}
}
