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

package net.sourceforge.atunes.kernel.modules.context.album;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.Logger;

import org.apache.sanselan.ImageWriteException;

/**
 * Data Source for basic album object information Retrieves basic information
 * and optionally image too
 * 
 * @author alex
 * 
 */
public class AlbumInfoDataSource implements IContextInformationSource {

	private IStateContext stateContext;

	private IWebServicesHandler webServicesHandler;

	private IAlbumInfo albumInfo;

	private ImageIcon image;

	private IAudioObject audioObject;

	private IUnknownObjectChecker unknownObjectChecker;

	private IRepositoryHandler repositoryHandler;

	private ILocalAudioObjectImageHandler localAudioObjectImageHandler;

	/**
	 * @param localAudioObjectImageHandler
	 */
	public void setLocalAudioObjectImageHandler(
			final ILocalAudioObjectImageHandler localAudioObjectImageHandler) {
		this.localAudioObjectImageHandler = localAudioObjectImageHandler;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	public void getData(final IAudioObject audioObject) {
		this.audioObject = audioObject;
		this.albumInfo = getAlbumInfoData(audioObject);
		this.repositoryHandler.checkAvailability(
				audioObject.getArtist(this.unknownObjectChecker),
				this.albumInfo.getTracks());
		this.image = getImageData(this.albumInfo, audioObject);
	}

	/**
	 * @return
	 */
	public IAudioObject getAudioObject() {
		return this.audioObject;
	}

	/**
	 * @return
	 */
	public IAlbumInfo getAlbumInfo() {
		return this.albumInfo;
	}

	/**
	 * @return
	 */
	public ImageIcon getImage() {
		return this.image;
	}

	/**
	 * Returns album information
	 * 
	 * @param audioObject
	 * @return
	 */
	private IAlbumInfo getAlbumInfoData(final IAudioObject audioObject) {
		// Get album info
		IAlbumInfo album = this.webServicesHandler.getAlbum(
				audioObject.getAlbumArtistOrArtist(this.unknownObjectChecker),
				audioObject.getAlbum(this.unknownObjectChecker));

		// If album was not found try to get an album from the same artist that
		// match
		if (album == null) {
			album = tryToFindAnotherAlbumFromSameArtist(audioObject);
		}

		return album;
	}

	/**
	 * @param audioObject
	 * @return
	 */
	private IAlbumInfo tryToFindAnotherAlbumFromSameArtist(
			final IAudioObject audioObject) {

		// Wait a second to prevent IP banning
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		List<IAlbumInfo> albums = null;
		if (!this.unknownObjectChecker.isUnknownArtist(audioObject
				.getAlbumArtistOrArtist(this.unknownObjectChecker))) {
			// Get album list
			albums = getAlbumList(audioObject
					.getAlbumArtistOrArtist(this.unknownObjectChecker));
		}

		if (albums != null) {
			// Try to find an album which fits
			IAlbumInfo matchingAlbum = analyseAlbums(audioObject, albums);
			if (matchingAlbum != null) {
				// Get full information for album
				matchingAlbum = this.webServicesHandler.getAlbum(
						matchingAlbum.getArtist(), matchingAlbum.getTitle());
				if (matchingAlbum != null) {
					return matchingAlbum;
				}
			}
		}
		return null;
	}

	/**
	 * @param audioObject
	 * @param albums
	 * @return
	 */
	private IAlbumInfo analyseAlbums(final IAudioObject audioObject,
			final List<IAlbumInfo> albums) {
		for (IAlbumInfo a : albums) {
			IAlbumInfo auxAlbum = getMacthingAlbum(audioObject, a);
			if (auxAlbum != null) {
				return auxAlbum;
			}
		}
		return null;
	}

	/**
	 * @param audioObject
	 * @param auxAlbum
	 * @param a
	 * @return
	 */
	private IAlbumInfo getMacthingAlbum(final IAudioObject audioObject,
			final IAlbumInfo a) {
		StringTokenizer st = new StringTokenizer(a.getTitle(), " ");
		boolean matches = true;
		int tokensAnalyzed = 0;
		while (st.hasMoreTokens() && matches) {
			String t = st.nextToken();
			if (forbiddenToken(t)) { // Ignore album if contains forbidden chars
				matches = false;
				break;
			}
			if (!validToken(t)) { // Ignore tokens without alphanumerics
				if (tokensAnalyzed == 0 && !st.hasMoreTokens()) {
					matches = false;
				} else {
					continue;
				}
			}
			if (!audioObject.getAlbum(this.unknownObjectChecker).toLowerCase()
					.contains(t.toLowerCase())) {
				matches = false;
			}
			tokensAnalyzed++;
		}
		if (matches) {
			return a;
		}
		return null;
	}

	/**
	 * @param artist
	 * @return
	 */
	private List<IAlbumInfo> getAlbumList(final String artist) {
		IAlbumListInfo albumList = this.webServicesHandler.getAlbumList(artist);
		if (albumList != null) {
			return albumList.getAlbums();
		}
		return null;
	}

	/**
	 * Returns image from lastfm or from custom image
	 * 
	 * @param albumInfo
	 * @param audioObject
	 * @return
	 */
	private ImageIcon getImageData(final IAlbumInfo albumInfo,
			final IAudioObject audioObject) {
		ImageIcon imageIcon = null;
		if (albumInfo != null) {
			imageIcon = this.webServicesHandler.getAlbumImage(albumInfo);
			// This data source should only be used with audio files but anyway
			// check if audioObject is an LocalAudioObject before save picture
			if (audioObject instanceof ILocalAudioObject) {
				savePicture(imageIcon, (ILocalAudioObject) audioObject);
			}
		} else {
			imageIcon = this.localAudioObjectImageHandler.getImage(audioObject,
					ImageSize.SIZE_MAX);
		}

		return imageIcon;
	}

	/**
	 * Saves an image related to an audio file from a web service
	 * 
	 * @param img
	 * @param file
	 */
	private void savePicture(final ImageIcon img, final ILocalAudioObject file) {
		if (img != null && this.stateContext.isSaveContextPicture()) {
			// Save picture
			try {
				this.localAudioObjectImageHandler.writeCover(file, img);
			} catch (IOException e) {
				Logger.error(e);
			} catch (ImageWriteException e) {
				Logger.error(e);
			}
		}
	}

	/**
	 * Valid token.
	 * 
	 * @param t
	 *            the t
	 * 
	 * @return true, if successful
	 */
	private boolean validToken(final String t) {
		return t.matches("[A-Za-z]+");
		// t.contains("(") || t.contains(")")
	}

	/**
	 * Forbidden token.
	 * 
	 * @param t
	 *            the t
	 * 
	 * @return true, if successful
	 */
	private boolean forbiddenToken(final String t) {
		return t.contains("/");
	}

	/**
	 * @param webServicesHandler
	 */
	public final void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	@Override
	public void cancel() {
	}
}
