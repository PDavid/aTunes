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

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds a song to banned tracks in Last.fm profile
 * 
 * @author fleax
 * 
 */
public class AddBannedSongInLastFMAction extends CustomAbstractAction {

	private static final long serialVersionUID = -2687851398606488392L;

	private IBackgroundWorker backgroundWorker;
	
	private IWebServicesHandler webServicesHandler;
	
	private IContextHandler contextHandler;
	
	/**
	 * @param backgroundWorker
	 */
	public void setBackgroundWorker(IBackgroundWorker backgroundWorker) {
		this.backgroundWorker = backgroundWorker;
	}
	
	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
	
	/**
	 * @param contextHandler
	 */
	public void setContextHandler(IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}
	
    public AddBannedSongInLastFMAction() {
        super(I18nUtils.getString("ADD_BANNED_SONG_IN_LASTFM"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("ADD_BANNED_SONG_IN_LASTFM"));
    }
    
    @Override
    protected void initialize() {
        setEnabled(getState().isLastFmEnabled());
    }
    
    @Override
    protected void executeAction() {
        setEnabled(false);
        backgroundWorker.setBackgroundActions(new Runnable() {
        	@Override
        	public void run() {
    			webServicesHandler.addBannedSong(contextHandler.getCurrentAudioObject());
        	}
        });
        backgroundWorker.setGraphicalActionsWhenDone(new Runnable() {
        	@Override
        	public void run() {
		        setEnabled(true);
        	}
        });
        backgroundWorker.execute();
    }
}
