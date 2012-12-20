/*
 * aTunes 3.1.0
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

import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IStateContext;
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

    private IContextHandler contextHandler;

    private IWebServicesHandler webServicesHandler;

    private IBackgroundWorkerFactory backgroundWorkerFactory;

    private IStateContext stateContext;

    /**
     * @param stateContext
     */
    public void setStateContext(final IStateContext stateContext) {
	this.stateContext = stateContext;
    }

    /**
     * @param contextHandler
     */
    public void setContextHandler(final IContextHandler contextHandler) {
	this.contextHandler = contextHandler;
    }

    /**
     * @param webServicesHandler
     */
    public void setWebServicesHandler(
	    final IWebServicesHandler webServicesHandler) {
	this.webServicesHandler = webServicesHandler;
    }

    /**
     * @param backgroundWorkerFactory
     */
    public void setBackgroundWorkerFactory(
	    final IBackgroundWorkerFactory backgroundWorkerFactory) {
	this.backgroundWorkerFactory = backgroundWorkerFactory;
    }

    /**
     * 
     */
    public AddLovedSongInLastFMAction() {
	super(I18nUtils.getString("ADD_LOVED_SONG_IN_LASTFM"));
    }

    @Override
    protected void initialize() {
	super.initialize();
	setEnabled(stateContext.isLastFmEnabled());
    }

    @Override
    protected void executeAction() {
	loveSong(contextHandler.getCurrentAudioObject());
    }

    /**
     * Calls last.fm service to add a song as favorite
     * 
     * @param song
     */
    public void loveSong(final IAudioObject song) {
	setEnabled(false);
	IBackgroundWorker<Void> backgroundWorker = backgroundWorkerFactory
		.getWorker();
	backgroundWorker.setBackgroundActions(new Callable<Void>() {
	    @Override
	    public Void call() {
		webServicesHandler.addLovedSong(song);
		return null;
	    }
	});
	backgroundWorker
		.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<Void>() {
		    @Override
		    public void call(final Void result) {
			setEnabled(true);
		    }
		});
	backgroundWorker.execute();
    }

}
