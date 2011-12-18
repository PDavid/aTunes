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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

final class SubmitToLastFmRunnable implements Runnable {

	private final long secondsPlayed;
	private final IAudioObject audioFile;
	private final LastFmService lastFmService;
	private final IFrame frame;
	private final IState state;

	/**
	 * @param secondsPlayed
	 * @param audioFile
	 * @param lastFmService
	 * @param frame
	 * @param state
	 */
	SubmitToLastFmRunnable(long secondsPlayed, IAudioObject audioFile, LastFmService lastFmService, IFrame frame, IState state) {
		this.secondsPlayed = secondsPlayed;
		this.audioFile = audioFile;
		this.lastFmService = lastFmService;
		this.frame = frame;
		this.state = state;
	}

	@Override
	public void run() {
	    try {
	        lastFmService.submit(audioFile, secondsPlayed);
	    } catch (ScrobblerException e) {
	        if (e.getStatus() == 2) {
	            Logger.error("Authentication failure on Last.fm service");
	            SwingUtilities.invokeLater(new Runnable() {

	                @Override
	                public void run() {
	                	Context.getBean(IErrorDialogFactory.class).getDialog().showErrorDialog(frame, I18nUtils.getString("LASTFM_USER_ERROR"));
	                    // Disable service by deleting password
	                    state.setLastFmEnabled(false);
	                }
	            });
	        } else {
	            Logger.error(e.getMessage());
	        }
	    }
	}
}