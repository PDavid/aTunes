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
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Adds a song to banned tracks in Last.fm profile
 * 
 * @author fleax
 * 
 */
public class AddBannedSongInLastFMAction extends CustomAbstractAction {

    private final class BanSongSwingWorker extends SwingWorker<Void, Void> {
		private final IAudioObject song;

		private BanSongSwingWorker(IAudioObject song) {
			this.song = song;
		}

		@Override
		protected Void doInBackground() throws Exception {
			getBean(IWebServicesHandler.class).addBannedSong(song);
		    return null;
		}

		@Override
		protected void done() {
		    try {
		        get();
		    } catch (InterruptedException e) {
		    	Logger.error(e);
		    } catch (ExecutionException e) {
		    	Logger.error(e);
		    } finally {
		        setEnabled(true);
		    }
		}
	}

	private static final long serialVersionUID = -2687851398606488392L;

    AddBannedSongInLastFMAction() {
        super(I18nUtils.getString("ADD_BANNED_SONG_IN_LASTFM"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("ADD_BANNED_SONG_IN_LASTFM"));
    }
    
    @Override
    protected void initialize() {
        setEnabled(getState().isLastFmEnabled());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        banSong(getBean(IContextHandler.class).getCurrentAudioObject());
    }

    /**
     * Calls last.fm service to ban a song
     * 
     * @param song
     */
    public void banSong(final IAudioObject song) {
        setEnabled(false);
        new BanSongSwingWorker(song).execute();
    }

}
