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

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * Data Source for basic album object information Retrieves basic information
 * and optionally image too
 * 
 * @author alex
 * 
 */
public class ArtistAlbumListImagesDataSource implements
		IContextInformationSource {

	private IWebServicesHandler webServicesHandler;

	private IAudioObject audioObject;

	private IAlbumListInfo albumList;

	private Map<IAlbumInfo, ImageIcon> covers;

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
		this.albumList = getAlbumListData(audioObject);
		this.covers = new HashMap<IAlbumInfo, ImageIcon>();
		if (albumList != null && !albumList.getAlbums().isEmpty()) {
			for (IAlbumInfo album : albumList.getAlbums()) {
				ImageIcon albumImage = getAlbumImageData(album);
				covers.put(album, ImageUtils.resize(albumImage,
						Constants.THUMB_IMAGE_WIDTH,
						Constants.THUMB_IMAGE_HEIGHT));
			}
		}
	}

	/**
	 * @return
	 */
	public IAlbumListInfo getAlbumList() {
		return albumList;
	}

	/**
	 * @return
	 */
	public Map<IAlbumInfo, ImageIcon> getCovers() {
		return covers;
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
	 * Returns image for album
	 * 
	 * @param album
	 * @return
	 */
	private ImageIcon getAlbumImageData(final IAlbumInfo album) {
		return webServicesHandler.getAlbumThumbImage(album);
	}

	/**
	 * @return
	 */
	public IAudioObject getAudioObject() {
		return audioObject;
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
