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

import java.awt.Paint;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.ArtistFavoriteImageIcon;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.model.ColumnSort;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILookAndFeelManager;

public class ArtistColumn extends AbstractColumn {

    
    private static final long serialVersionUID = 8144686293055648148L;

    public ArtistColumn() {
        super("ARTIST", TextAndIcon.class);
        setVisible(true);
        setUsedForFilter(true);
        setColumnSort(ColumnSort.ASCENDING); // Column sets are ordered by default by this column
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
        // Return artist
        return new TextAndIcon(audioObject.getArtist(), 
        		!FavoritesHandler.getInstance().getFavoriteArtistsInfo().containsKey(audioObject.getArtist()) ? null : new IColorMutableImageIcon() {
					
					@Override
					public ImageIcon getIcon(Paint paint) {
						return ArtistFavoriteImageIcon.getIcon(paint, Context.getBean(ILookAndFeelManager.class).getCurrentLookAndFeel());
					}
				}, SwingConstants.LEFT);
    }

    @Override
    public String getValueForFilter(IAudioObject audioObject) {
        return audioObject.getArtist();
    }

}
