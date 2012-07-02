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

import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IStateRipper;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Performs cd ripping in background
 * @author alex
 *
 */
public final class GetCdInfoAndStartRippingSwingWorker extends	SwingWorker<CDInfo, Void> {

	private RipperHandler ripperHandler;
	private RipCdDialog dialog;
	private IWebServicesHandler webServicesHandler;
	private IStateRipper stateRipper;

	/**
	 * @param stateRipper
	 */
	public void setStateRipper(IStateRipper stateRipper) {
		this.stateRipper = stateRipper;
	}
	
	/**
	 * @param ripperHandler
	 */
	public void setRipperHandler(RipperHandler ripperHandler) {
		this.ripperHandler = ripperHandler;
	}
	
	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
	
	/**
	 * @param dialog
	 */
	public void setDialog(RipCdDialog dialog) {
		this.dialog = dialog;
	}
	
	@Override
	protected CDInfo doInBackground() {
	    if (!this.ripperHandler.testTools()) {
	        return null;
	    }
	    CdRipper ripper = Context.getBean(CdRipper.class);
	    ripper.setNoCdListener(new NoCdListener() {
	        @Override
	        public void noCd() {
	            Logger.error("No cd inserted");
	            GetCdInfoAndStartRippingSwingWorker.this.ripperHandler.setInterrupted(true);
	            SwingUtilities.invokeLater(new Runnable() {
	                @Override
	                public void run() {
	                	GetCdInfoAndStartRippingSwingWorker.this.ripperHandler.getIndeterminateProgressDialog().hideDialog();
	                    Context.getBean(IDialogFactory.class).newDialog(IErrorDialog.class).showErrorDialog(I18nUtils.getString("NO_CD_INSERTED"));
	                }
	            });
	        }
	    });
	    this.ripperHandler.setRipper(ripper);
	    CDInfo cdInfo = ripper.getCDInfo();
	    
	    tryToGetGenre(cdInfo);
	    
	    return cdInfo;
	}

	@Override
	protected void done() {
		this.ripperHandler.getIndeterminateProgressDialog().hideDialog();
	    CDInfo cdInfo;
	    try {
	        cdInfo = get();
	        if (cdInfo != null) {
	        	this.ripperHandler.getRipCdDialogController().showCdInfo(cdInfo, this.ripperHandler.getRepositoryHandler().getPathForNewAudioFilesRipped(), this.ripperHandler.getRepositoryHandler().getRepositoryPath());
	            if (!this.ripperHandler.getRipCdDialogController().isCancelled()) {
	            	String folder = this.ripperHandler.getRipCdDialogController().getFolder();
	                this.ripperHandler.importSongs(folder, getMetadata(), dialog.getFormat().getSelectedItem().toString(), dialog.getQuality(), dialog.getUseCdErrorCorrection().isSelected());
	            }
	            // Even if canceling, save these settings
	            stateRipper.setUseCdErrorCorrection(dialog.getUseCdErrorCorrection().isSelected());
	            stateRipper.setEncoder(dialog.getFormat().getSelectedItem().toString());
	            stateRipper.setEncoderQuality(dialog.getQuality());
	        }
	        // TODO: Add error message id cdInfo returns null
	    } catch (InterruptedException e) {
	    	this.ripperHandler.getRipCdDialogController().getComponentControlled().setVisible(false);
	        Logger.error(e);
	    } catch (ExecutionException e) {
	    	this.ripperHandler.getRipCdDialogController().getComponentControlled().setVisible(false);
	        Logger.error(e);
	    }
	}

	/**
	 * @return
	 */
	private CDMetadata getMetadata() {
		CDMetadata metadata = new CDMetadata();
		metadata.setAlbumArtist(this.ripperHandler.getRipCdDialogController().getArtist());
		metadata.setAlbum(this.ripperHandler.getRipCdDialogController().getAlbum());
		metadata.setYear(this.ripperHandler.getRipCdDialogController().getYear());
		metadata.setGenre(this.ripperHandler.getRipCdDialogController().getGenre());
		metadata.setDisc(this.ripperHandler.getRipCdDialogController().getDiscNumber());
		metadata.setTracks(dialog.getTracksSelected());
		metadata.setTrackNames(dialog.getTrackNames());
		metadata.setArtistNames(dialog.getArtistNames());
		metadata.setComposerNames(dialog.getComposerNames());
		return metadata;
	}
	
	private void tryToGetGenre(CDInfo cdInfo) {
		if (cdInfo != null && cdInfo.getArtist() != null) {
			Logger.debug("Retrieving artist top tag");
			String tag = webServicesHandler.getArtistTopTag(cdInfo.getArtist());
			if (tag != null) {
				Logger.debug("Tag retrieved for artist ", cdInfo.getArtist(), ": ", tag);
				cdInfo.setGenre(tag);
			}
		}
	}
}