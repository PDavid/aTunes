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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.NoCdListener;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

final class GetCdInfoAndStartRippingSwingWorker extends	SwingWorker<CDInfo, Void> {

	private IOSManager osManager;
	private IState state;
	private RipperHandler ripperHandler;
	private IFrame frame;
	private RipCdDialog dialog;

	GetCdInfoAndStartRippingSwingWorker(IOSManager osManager, IState state, RipperHandler ripperHandler, IFrame frame, RipCdDialog dialog) {
		this.osManager = osManager;
		this.state = state;
		this.ripperHandler = ripperHandler;
		this.frame = frame;
		this.dialog = dialog;
	}

	@Override
	protected CDInfo doInBackground() throws Exception {
	    if (!this.ripperHandler.testTools()) {
	        return null;
	    }
	    this.ripperHandler.ripper = new CdRipper(osManager);
	    this.ripperHandler.ripper.setNoCdListener(new NoCdListener() {
	        @Override
	        public void noCd() {
	            Logger.error("No cd inserted");
	            GetCdInfoAndStartRippingSwingWorker.this.ripperHandler.interrupted = true;
	            SwingUtilities.invokeLater(new Runnable() {
	                @Override
	                public void run() {
	                	GetCdInfoAndStartRippingSwingWorker.this.ripperHandler.indeterminateProgressDialog.hideDialog();
	                    Context.getBean(IErrorDialogFactory.class).getDialog().showErrorDialog(frame, I18nUtils.getString("NO_CD_INSERTED"));
	                }
	            });
	        }
	    });
	    return this.ripperHandler.ripper.getCDInfo();
	}

	@Override
	protected void done() {
		this.ripperHandler.indeterminateProgressDialog.hideDialog();
	    CDInfo cdInfo;
	    try {
	        cdInfo = get();
	        if (cdInfo != null) {
	        	this.ripperHandler.getRipCdDialogController().showCdInfo(cdInfo, this.ripperHandler.repositoryHandler.getPathForNewAudioFilesRipped(), this.ripperHandler.repositoryHandler.getRepositoryPath());
	            if (!this.ripperHandler.getRipCdDialogController().isCancelled()) {
	                String artist = this.ripperHandler.getRipCdDialogController().getArtist();
	                String album = this.ripperHandler.getRipCdDialogController().getAlbum();
	                int year = this.ripperHandler.getRipCdDialogController().getYear();
	                String genre = this.ripperHandler.getRipCdDialogController().getGenre();
	                String folder = this.ripperHandler.getRipCdDialogController().getFolder();
	                List<Integer> tracks = dialog.getTracksSelected();
	                List<String> trckNames = dialog.getTrackNames();
	                List<String> artistNames = dialog.getArtistNames();
	                List<String> composerNames = dialog.getComposerNames();
	                this.ripperHandler.importSongs(folder, artist, album, year, genre, tracks, trckNames, artistNames, composerNames, dialog.getFormat().getSelectedItem().toString(), dialog
	                        .getQuality(), dialog.getUseCdErrorCorrection().isSelected());
	            }
	            // Even if canceling, save these settings
	            state.setUseCdErrorCorrection(dialog.getUseCdErrorCorrection().isSelected());
	            state.setEncoder(dialog.getFormat().getSelectedItem().toString());
	            state.setEncoderQuality(dialog.getQuality());
	            state.setCdRipperFileNamePattern(dialog.getFileNamePattern());
	        }
	    } catch (InterruptedException e) {
	    	this.ripperHandler.getRipCdDialogController().getComponentControlled().setVisible(false);
	        Logger.error(e);
	    } catch (ExecutionException e) {
	    	this.ripperHandler.getRipCdDialogController().getComponentControlled().setVisible(false);
	        Logger.error(e);
	    }
	}
}