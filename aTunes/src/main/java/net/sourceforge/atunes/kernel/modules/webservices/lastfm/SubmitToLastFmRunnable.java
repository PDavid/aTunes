/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Runnable to scrobble
 * 
 * @author alex
 * 
 */
public final class SubmitToLastFmRunnable implements Runnable {

    private long secondsPlayed;
    private IAudioObject audioFile;
    private LastFmUserServices lastFmUserServices;
    private IStateContext stateContext;
    private IDialogFactory dialogFactory;

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(final IDialogFactory dialogFactory) {
	this.dialogFactory = dialogFactory;
    }

    /**
     * @param secondsPlayed
     */
    public void setSecondsPlayed(final long secondsPlayed) {
	this.secondsPlayed = secondsPlayed;
    }

    /**
     * @param audioFile
     */
    public void setAudioFile(final IAudioObject audioFile) {
	this.audioFile = audioFile;
    }

    /**
     * @param lastFmUserServices
     */
    public void setLastFmUserServices(
	    final LastFmUserServices lastFmUserServices) {
	this.lastFmUserServices = lastFmUserServices;
    }

    /**
     * @param stateContext
     */
    public void setStateContext(final IStateContext stateContext) {
	this.stateContext = stateContext;
    }

    @Override
    public void run() {
	try {
	    lastFmUserServices.submit(audioFile, secondsPlayed);
	} catch (ScrobblerException e) {
	    if (e.getStatus() == 2) {
		Logger.error("Authentication failure on Last.fm service");
		SwingUtilities.invokeLater(new Runnable() {

		    @Override
		    public void run() {
			dialogFactory
				.newDialog(IErrorDialog.class)
				.showErrorDialog(
					I18nUtils
						.getString("LASTFM_USER_ERROR"));
			// Disable service by deleting password
			stateContext.setLastFmEnabled(false);
		    }
		});
	    } else {
		Logger.error(e.getMessage());
	    }
	}
    }
}