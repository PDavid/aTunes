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

package net.sourceforge.atunes.kernel.modules.context.artist;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * Data Source for basic album object information Retrieves basic information
 * and optionally image too
 * 
 * @author alex
 * 
 */
public class ArtistInfoDataSource implements IContextInformationSource {

	private IWebServicesHandler webServicesHandler;

	private IAudioObject audioObject;

	private String wikiText;

	private String wikiUrl;

	private String artistName;

	private String artistUrl;

	private ImageIcon artistImage;

	private IAlbumListInfo albumList;

	private IUnknownObjectChecker unknownObjectChecker;

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
		this.wikiText = getWikiTextData(audioObject);
		this.wikiUrl = getWikiUrlData(audioObject);
		this.artistImage = getArtistImageData(audioObject);
		this.albumList = getAlbumListData(audioObject);
		if (albumList != null && !albumList.getAlbums().isEmpty()) {
			this.artistName = albumList.getArtist();
			this.artistUrl = albumList.getAlbums().get(0).getArtistUrl();
		} else {
			this.artistName = audioObject.getArtist(unknownObjectChecker);
			this.artistUrl = wikiUrl;
		}
	}

	/**
	 * @return
	 */
	public IAlbumListInfo getAlbumList() {
		return albumList;
	}

	/**
	 * Return album list for artist
	 * 
	 * @param audioObject
	 * @return
	 */
	private IAlbumListInfo getAlbumListData(final IAudioObject audioObject) {
		if (!unknownObjectChecker.isUnknownArtist(audioObject
				.getArtist(unknownObjectChecker))) {
			return webServicesHandler.getAlbumList(audioObject
					.getArtist(unknownObjectChecker));
		}
		return null;
	}

	/**
	 * @return
	 */
	public IAudioObject getAudioObject() {
		return audioObject;
	}

	/**
	 * @return
	 */
	public String getWikiText() {
		return wikiText;
	}

	/**
	 * @return
	 */
	public String getWikiUrl() {
		return wikiUrl;
	}

	/**
	 * @return
	 */
	public ImageIcon getArtistImage() {
		return artistImage;
	}

	/**
	 * @return
	 */
	public String getArtistName() {
		return artistName;
	}

	/**
	 * @return
	 */
	public String getArtistUrl() {
		return artistUrl;
	}

	/**
	 * Return wiki text for artist
	 * 
	 * @param audioObject
	 * @return
	 */
	private String getWikiTextData(final IAudioObject audioObject) {
		if (!audioObject.getArtist(unknownObjectChecker).equals(
				I18nUtils.getString("UNKNOWN_ARTIST"))) {
			return webServicesHandler.getBioText(audioObject
					.getArtist(unknownObjectChecker));
		}
		return null;
	}

	/**
	 * Return wiki url for artist
	 * 
	 * @param audioObject
	 * @return
	 */
	private String getWikiUrlData(final IAudioObject audioObject) {
		if (!audioObject.getArtist(unknownObjectChecker).equals(
				I18nUtils.getString("UNKNOWN_ARTIST"))) {
			return webServicesHandler.getBioURL(audioObject
					.getArtist(unknownObjectChecker));
		}
		return null;
	}

	/**
	 * Returns image for artist
	 * 
	 * @param audioObject
	 * @return
	 */
	private ImageIcon getArtistImageData(final IAudioObject audioObject) {
		ImageIcon image = webServicesHandler.getArtistImage(audioObject
				.getArtist(unknownObjectChecker));
		if (image != null) {
			image = ImageUtils.resize(image, Constants.ARTIST_IMAGE_SIZE,
					Constants.ARTIST_IMAGE_SIZE);
		}
		return image;
	}

	/**
	 * @param webServicesHandler
	 */
	public final void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	@Override
	public void cancel() {
	}

}
