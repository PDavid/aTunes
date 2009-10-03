/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action creates a new play list with all songs of the artist that is
 * currently selected in play list
 * 
 * If more than one artist is selected, creates one play list for each one
 * 
 * @author fleax
 * 
 */
public class CreatePlayListWithSelectedArtistsAction extends ActionOverSelectedObjects<AudioObject> {

    private static final long serialVersionUID = 242525309967706255L;

    CreatePlayListWithSelectedArtistsAction() {
        super(I18nUtils.getString("SET_ARTIST_AS_PLAYLIST"), ImageLoader.getImage(ImageLoader.ARTIST), AudioObject.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("ARTIST_BUTTON_TOOLTIP"));
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
            // Sort audio objects of artist with current sort options
            List<AudioObject> audioObjects = RepositoryHandler.getInstance().sort(artist.getAudioObjects());

            // Create a new play list with artist as name and audio objects selected
            PlayListHandler.getInstance().newPlayList(artist.getName(), audioObjects);
        }
    }
}
