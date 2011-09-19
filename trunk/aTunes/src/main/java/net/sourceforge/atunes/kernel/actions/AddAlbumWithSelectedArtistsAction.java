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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * this action show a dialog to add album of artists to the current playlist
 * If more than one artist is selected, a dialog is show for each artist
 * 
 * @author encestre
 * 
 */
public class AddAlbumWithSelectedArtistsAction extends AbstractActionOverSelectedObjects<IAudioObject> {

    private static final long serialVersionUID = 242525309967706255L;

    AddAlbumWithSelectedArtistsAction() {
        super(I18nUtils.getString("ADD_ALBUM_ARTIST_TO_PLAYLIST"), IAudioObject.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("ADD_ALBUM_ARTIST_TOOLTIP"));
    }
    
    @Override
    protected void initialize() {
        setEnabled(false);
    }

    @Override
    protected void performAction(List<IAudioObject> objects) {
    	
        // Get selected artists from play list
        List<Artist> selectedArtists = new ArrayList<Artist>();
        for (IAudioObject ao : objects) {
            String artistName = ao.getArtist();
            Artist a = RepositoryHandler.getInstance().getArtist(artistName);
            if (a != null) {
                if (!selectedArtists.contains(a)) {
                    selectedArtists.add(a);
                }
            }
        }

        // For every artist
        for (Artist artist : selectedArtists) {
        	Context.getBean(IPlayListHandler.class).showAddArtistDragDialog(artist);
        }
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        return !selection.isEmpty();
    }
}
