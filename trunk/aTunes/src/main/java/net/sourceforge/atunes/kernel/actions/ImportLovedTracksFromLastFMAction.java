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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmLovedTrack;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class ImportLovedTracksFromLastFMAction extends CustomAbstractAction {

	private IIndeterminateProgressDialog dialog;
	
    private class ImportLovedTracksWorker extends SwingWorker<List<ILocalAudioObject>, Void> {
        @Override
        protected List<ILocalAudioObject> doInBackground() throws Exception {
            // Get loved tracks
            List<LastFmLovedTrack> lovedTracks = getBean(IWebServicesHandler.class).getLovedTracks();
            if (!lovedTracks.isEmpty()) {
                List<ILocalAudioObject> favoriteAudioFiles = new ArrayList<ILocalAudioObject>();
                for (LastFmLovedTrack lovedTrack : lovedTracks) {
                    Artist artist = RepositoryHandler.getInstance().getArtist(lovedTrack.getArtist());
                    if (artist != null) {
                        for (ILocalAudioObject audioObject : artist.getAudioObjects()) {
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
            dialog.hideDialog();
            List<ILocalAudioObject> lovedTracks = null;
            try {
                // Get loved tracks
                lovedTracks = get();
                // Set favorites
                FavoritesHandler.getInstance().toggleFavoriteSongs(lovedTracks);
            } catch (Exception e) {
            }
            getBean(IMessageDialog.class).showMessage(StringUtils.getString(I18nUtils.getString("LOVED_TRACKS_IMPORTED"), ": ", lovedTracks == null ? "0" : lovedTracks.size()), getBean(IFrame.class));

        }
    }

    private static final long serialVersionUID = 5620935204300321285L;

    ImportLovedTracksFromLastFMAction() {
        super(I18nUtils.getString("IMPORT_LOVED_TRACKS_FROM_LASTFM"));
    }
    
    @Override
    protected void initialize() {
        setEnabled(getState().isLastFmEnabled());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SwingWorker<List<ILocalAudioObject>, Void> worker = new ImportLovedTracksWorker();
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
        		dialog = getBean(IIndeterminateProgressDialogFactory.class).newDialog(getBean(IFrame.class));
        		dialog.setTitle(I18nUtils.getString("GETTING_LOVED_TRACKS_FROM_LASTFM"));
        		dialog.showDialog();
        	}
        });
        worker.execute();
    }
}
