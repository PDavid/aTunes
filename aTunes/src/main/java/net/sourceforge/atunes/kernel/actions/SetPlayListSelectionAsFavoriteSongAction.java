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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * This action adds selected rows in play list to favorite songs
 * 
 * @author fleax
 * 
 */
public class SetPlayListSelectionAsFavoriteSongAction extends Action {

    private static final long serialVersionUID = 1185876110005394694L;

    SetPlayListSelectionAsFavoriteSongAction() {
        super(LanguageTool.getString("SET_FAVORITE_SONG"), ImageLoader.getImage(ImageLoader.FAVORITE));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FavoritesHandler.getInstance().addFavoriteSongs(AudioFile.getAudioFiles(PlayListHandler.getInstance().getSelectedAudioObjects()));
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<AudioObject> selection) {
        if (selection.isEmpty()) {
            return false;
        }

        for (AudioObject ao : selection) {
            if (ao instanceof Radio || ao instanceof PodcastFeedEntry) {
                return false;
            }
        }
        return true;
    }

}
