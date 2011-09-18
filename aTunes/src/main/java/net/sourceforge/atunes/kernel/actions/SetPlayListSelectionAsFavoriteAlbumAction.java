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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action adds selected rows in play list to favorite albums
 * 
 * @author alex
 * 
 */
public class SetPlayListSelectionAsFavoriteAlbumAction extends CustomAbstractAction {

    private static final long serialVersionUID = 4274931214366676521L;

    SetPlayListSelectionAsFavoriteAlbumAction() {
        super(I18nUtils.getString("SET_FAVORITE_ALBUM"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FavoritesHandler.getInstance().toggleFavoriteAlbums(AudioFile.getAudioFiles(PlayListHandler.getInstance().getSelectedAudioObjects()));
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        if (selection.isEmpty()) {
            return false;
        }

        for (IAudioObject ao : selection) {
            if (ao instanceof IRadio || ao instanceof IPodcastFeedEntry) {
                return false;
            }
        }
        return true;
    }

}
