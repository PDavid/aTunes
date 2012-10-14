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

package net.sourceforge.atunes.kernel.modules.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.sanselan.ImageWriteException;

/**
 * The Class GetCoversProcess.
 */
public class GetCoversProcess extends AbstractProcess<Void> {

	/** The artist. */
	private IArtist artist;

	private IOSManager osManager;

	private IWebServicesHandler webServicesHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param artist
	 */
	public void setArtist(final IArtist artist) {
		this.artist = artist;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	protected long getProcessSize() {
		return artist.getAlbums().size();
	}

	@Override
	protected boolean runProcess() {
		long coversRetrieved = 0;
		List<IAlbum> albums = new ArrayList<IAlbum>(artist.getAlbums().values());
		for (int i = 0; i < albums.size() && !isCanceled(); i++) {
			IAlbum album = albums.get(i);
			if (!hasCoverDownloaded(album)) {
				ImageIcon albumImage = webServicesHandler.getAlbumImage(artist.getName(), album.getName());
				if (albumImage != null) {
					try {
						ImageUtils.writeImageToFile(albumImage.getImage(), AudioFilePictureUtils.getFileNameForCover(album.getAudioObjects().get(0), osManager, unknownObjectChecker));
					} catch (IOException e1) {
						Logger.error(StringUtils.getString("Error writing image for artist: ", artist.getName(), " album: ", album.getName(), " Error: ", e1.getMessage()));
					} catch (ImageWriteException e) {
						Logger.error(StringUtils.getString("Error writing image for artist: ", artist.getName(), " album: ", album.getName(), " Error: ", e.getMessage()));
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

	/**
	 * Returns true if aTunes has saved cover image.
	 * 
	 * @param album
	 * 
	 * @return true, if checks for cover downloaded
	 */
	private boolean hasCoverDownloaded(final IAlbum album) {
		return new File(AudioFilePictureUtils.getFileNameForCover((album.getAudioObjects().get(0)), osManager, unknownObjectChecker)).exists();
	}

	@Override
	protected Void getProcessResult() {
		return null;
	}
}
