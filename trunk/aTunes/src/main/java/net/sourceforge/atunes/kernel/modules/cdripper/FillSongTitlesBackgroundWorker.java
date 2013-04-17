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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * Retrieves song titles
 * 
 * @author alex
 * 
 */
public final class FillSongTitlesBackgroundWorker extends
		BackgroundWorker<IAlbumInfo, Void> {

	private RipperHandler ripperHandler;
	private String artist;
	private String album;
	private IWebServicesHandler webServicesHandler;

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
	 * @param artist
	 * @param album
	 */
	void retrieveTitles(String artist, String album) {
		this.artist = artist;
		this.album = album;
		execute();
	}

	@Override
	protected void before() {
		this.ripperHandler.getRipCdDialogController().getComponentControlled()
				.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.ripperHandler.getRipCdDialogController().getComponentControlled()
				.getTitlesButton().setEnabled(false);
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected IAlbumInfo doInBackground() {
		return webServicesHandler.getAlbum(artist, album);
	}

	@Override
	protected void done(final IAlbumInfo result) {
		if (result != null) {
			this.ripperHandler.setAlbumInfo(result);
			List<String> trackNames = new ArrayList<String>();
			for (ITrackInfo trackInfo : result.getTracks()) {
				trackNames.add(trackInfo.getTitle());
			}
			this.ripperHandler.getRipCdDialogController()
					.getComponentControlled().updateTrackNames(trackNames);
		}
		this.ripperHandler.getRipCdDialogController().getComponentControlled()
				.setCursor(Cursor.getDefaultCursor());
		this.ripperHandler.getRipCdDialogController().getComponentControlled()
				.getTitlesButton().setEnabled(true);
	}
}