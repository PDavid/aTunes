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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.modules.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmLovedTrack;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class ImportLovedTracksFromLastFM extends Action {

    private static final long serialVersionUID = 5620935204300321285L;

    ImportLovedTracksFromLastFM() {
        super(LanguageTool.getString("IMPORT_LOVED_TRACKS_FROM_LASTFM"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingWorker<List<AudioFile>, Void> worker = new SwingWorker<List<AudioFile>, Void>() {
            @Override
            protected List<AudioFile> doInBackground() throws Exception {
                // Get loved tracks
                List<LastFmLovedTrack> lovedTracks = LastFmService.getInstance().getLovedTracks();
                if (!lovedTracks.isEmpty()) {
                    List<AudioFile> favoriteAudioFiles = new ArrayList<AudioFile>();
                    for (LastFmLovedTrack lovedTrack : lovedTracks) {
                        Artist artist = RepositoryHandler.getInstance().getArtistStructure().get(lovedTrack.getArtist());
                        if (artist != null) {
                            for (AudioFile audioObject : artist.getAudioFiles()) {
                                if (audioObject.getTitleOrFileName().equalsIgnoreCase(lovedTrack.getTitle())) {
                                    favoriteAudioFiles.add(audioObject);
                                }
                            }
                        }
                    }
                    return favoriteAudioFiles;
                }
                return Collections.emptyList();
            }

            @Override
            protected void done() {
                VisualHandler.getInstance().hideIndeterminateProgressDialog();
                List<AudioFile> lovedTracks = null;
                try {
                    // Get loved tracks
                    lovedTracks = get();
                    // Set favorites
                    FavoritesHandler.getInstance().addFavoriteSongs(lovedTracks);
                } catch (Exception e) {
                }
                VisualHandler.getInstance().showMessage(
                        StringUtils.getString(LanguageTool.getString("LOVED_TRACKS_IMPORTED"), ": ", lovedTracks == null ? "0" : lovedTracks.size()));

            }
        };
        VisualHandler.getInstance().showIndeterminateProgressDialog(LanguageTool.getString("GETTING_LOVED_TRACKS_FROM_LASTFM"));
        worker.execute();
    }
}
