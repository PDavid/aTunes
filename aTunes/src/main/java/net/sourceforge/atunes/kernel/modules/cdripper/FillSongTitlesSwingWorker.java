/*
 * aTunes 3.0.0
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

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.Logger;

final class FillSongTitlesSwingWorker extends SwingWorker<IAlbumInfo, Void> {
	/**
	 * 
	 */
	private final RipperHandler ripperHandler;
	private final String artist;
	private final String album;
	private IWebServicesHandler webServicesHandler;

	FillSongTitlesSwingWorker(RipperHandler ripperHandler, String artist, String album, IWebServicesHandler webServicesHandler) {
		this.ripperHandler = ripperHandler;
		this.artist = artist;
		this.album = album;
		this.webServicesHandler = webServicesHandler;
	}

	@Override
	protected IAlbumInfo doInBackground() {
	    return webServicesHandler.getAlbum(artist, album);
	}

	@Override
	protected void done() {
	    try {
	        if (get() != null) {
	            this.ripperHandler.setAlbumInfo(get());
	            List<String> trackNames = new ArrayList<String>();
	            for (ITrackInfo trackInfo : get().getTracks()) {
	                trackNames.add(trackInfo.getTitle());
	            }
	            this.ripperHandler.getRipCdDialogController().getComponentControlled().updateTrackNames(trackNames);
	        }
	    } catch (InterruptedException e) {
	        Logger.error(e);
	    } catch (ExecutionException e) {
	        Logger.error(e);
	    } finally {
	    	this.ripperHandler.getRipCdDialogController().getComponentControlled().setCursor(Cursor.getDefaultCursor());
	    	this.ripperHandler.getRipCdDialogController().getComponentControlled().getTitlesButton().setEnabled(true);
	    }
	}
}