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
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * this action show a dialog to add album of artists to the current playlist
 * If more than one artist is selected, a dialog is show for each artist
 * 
 * @author encestre
 * 
 */
public class AddAlbumWithSelectedArtistsAction extends AbstractActionOverSelectedObjects<AudioObject> {

    private static final long serialVersionUID = 242525309967706255L;

    AddAlbumWithSelectedArtistsAction() {
        super(I18nUtils.getString("ADD_ALBUM_ARTIST_TO_PLAYLIST"), AudioObject.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("ADD_ALBUM_ARTIST_TOOLTIP"));
        setEnabled(false);
    }

    @Override
    protected void performAction(List<AudioObject> objects) {
    	
    	// Access artist structure
        Map<String, Artist> structure = RepositoryHandler.getInstance().getArtistStructure();

        // Get selected artists from play list
        List<Artist> selectedArtists = new ArrayList<Artist>();
        for (AudioObject ao : objects) {
            String artist = ao.getArtist();
            if (structure.containsKey(artist)) {
                Artist art = structure.get(artist);
                if (!selectedArtists.contains(art)) {
                    selectedArtists.add(art);
                }
            }
        }

        // For every artist
        for (Artist artist : selectedArtists) {
            GuiHandler.getInstance().showAddArtistDragDialog(artist);
        }
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<AudioObject> selection) {
        return !selection.isEmpty();
    }
}
