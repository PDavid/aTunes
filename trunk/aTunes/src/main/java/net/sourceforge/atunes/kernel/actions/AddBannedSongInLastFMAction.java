/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds a song to banned tracks in Last.fm profile
 * 
 * @author fleax
 * 
 */
public class AddBannedSongInLastFMAction extends Action {

    private static final long serialVersionUID = -2687851398606488392L;

    AddBannedSongInLastFMAction() {
        super(I18nUtils.getString("ADD_BANNED_SONG_IN_LASTFM"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("ADD_BANNED_SONG_IN_LASTFM"));
        setEnabled(ApplicationState.getInstance().isLastFmEnabled());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        banSong(ContextHandler.getInstance().getCurrentAudioObject());
    }

    /**
     * Calls last.fm service to ban a song
     * 
     * @param song
     */
    public void banSong(final AudioObject song) {
        setEnabled(false);
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                LastFmService.getInstance().addBannedSong(song);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException e) {
                	new Logger().error(LogCategories.CONTEXT, e);
                } catch (ExecutionException e) {
                	new Logger().error(LogCategories.CONTEXT, e);
                } finally {
                    setEnabled(true);
                }
            }
        }.execute();
    }

}
