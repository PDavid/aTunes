/*
 * aTunes 2.2.0-SNAPSHOT
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

/**
 * Adds a song to loved tracks in Last.fm profile
 * 
 * @author fleax
 * 
 */
public class AddLovedSongInLastFMAction extends CustomAbstractAction {

    private static final long serialVersionUID = -2687851398606488392L;

    AddLovedSongInLastFMAction() {
        super(I18nUtils.getString("ADD_LOVED_SONG_IN_LASTFM"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("ADD_LOVED_SONG_IN_LASTFM"));
    }

    @Override
    protected void initialize() {
        setEnabled(getState().isLastFmEnabled());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        loveSong(getBean(IContextHandler.class).getCurrentAudioObject());
    }

    /**
     * Calls last.fm service to add a song as favorite
     * 
     * @param song
     */
    public void loveSong(final IAudioObject song) {
        setEnabled(false);
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                getBean(IWebServicesHandler.class).addLovedSong(song);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                } finally {
                    setEnabled(true);
                }
            }

        }.execute();
    }

}
