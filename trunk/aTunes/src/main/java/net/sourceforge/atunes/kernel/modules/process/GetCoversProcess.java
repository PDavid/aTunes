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

package net.sourceforge.atunes.kernel.modules.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.sanselan.ImageWriteException;

/**
 * The Class GetCoversProcess.
 */
public class GetCoversProcess extends AbstractProcess<Void> {

	/** The artist. */
	private IArtist artist;

	private IWebServicesHandler webServicesHandler;

	private ILocalAudioObjectImageHandler localAudioObjectImageHandler;

	/**
	 * @param localAudioObjectImageHandler
	 */
	public void setLocalAudioObjectImageHandler(
			final ILocalAudioObjectImageHandler localAudioObjectImageHandler) {
		this.localAudioObjectImageHandler = localAudioObjectImageHandler;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param artist
	 */
	public void setArtist(final IArtist artist) {
		this.artist = artist;
	}

	@Override
	protected long getProcessSize() {
		return this.artist.getAlbums().size();
	}

	@Override
	protected boolean runProcess() {
		long coversRetrieved = 0;
		List<IAlbum> albums = new ArrayList<IAlbum>(this.artist.getAlbums()
				.values());
		for (int i = 0; i < albums.size() && !isCanceled(); i++) {
			IAlbum album = albums.get(i);
			if (!this.localAudioObjectImageHandler.hasCoverDownloaded(album)) {
				ImageIcon albumImage = this.webServicesHandler.getAlbumImage(
						this.artist.getName(), album.getName());
				if (albumImage != null) {
					try {
						this.localAudioObjectImageHandler.writeCover(album,
								albumImage);
					} catch (IOException e1) {
						Logger.error(StringUtils.getString(
								"Error writing image for artist: ",
								this.artist.getName(), " album: ",
								album.getName(), " Error: ", e1.getMessage()));
					} catch (ImageWriteException e) {
						Logger.error(StringUtils.getString(
								"Error writing image for artist: ",
								this.artist.getName(), " album: ",
								album.getName(), " Error: ", e.getMessage()));
					}
				}
			}
			coversRetrieved++;
			setCurrentProgress(coversRetrieved);
		}

		return true;
	}

	@Override
	public String getProgressDialogTitle() {
		return I18nUtils.getString("RETRIEVING_COVERS");
	}

	@Override
	protected void runCancel() {
		// Nothing to do
	}

	@Override
	protected Void getProcessResult() {
		return null;
	}
}
