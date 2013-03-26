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

package net.sourceforge.atunes.kernel.modules.context.similar;

import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * Similar artists data source
 * 
 * @author alex
 * 
 */
public class SimilarArtistsDataSource implements IContextInformationSource {

	/**
	 * Input parameter
	 */
	public static final String INPUT_AUDIO_OBJECT = "AUDIO_OBJECT";

	/**
	 * Output parameter
	 */
	public static final String OUTPUT_ARTISTS = "ARTISTS";

	private IWebServicesHandler webServicesHandler;

	private IRepositoryHandler repositoryHandler;

	private ISimilarArtistsInfo similarArtistsInfo;

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
		this.similarArtistsInfo = getSimilarArtists(audioObject);
	}

	/**
	 * @return
	 */
	public ISimilarArtistsInfo getSimilarArtistsInfo() {
		return similarArtistsInfo;
	}

	/**
	 * Returns similar artists
	 * 
	 * @param audioObject
	 */
	private ISimilarArtistsInfo getSimilarArtists(final IAudioObject audioObject) {
		if (!unknownObjectChecker.isUnknownArtist(audioObject
				.getArtist(unknownObjectChecker))) {
			ISimilarArtistsInfo artists = webServicesHandler
					.getSimilarArtists(audioObject
							.getArtist(unknownObjectChecker));
			if (artists != null) {
				Set<String> artistNamesSet = new HashSet<String>();
				for (IArtist a : repositoryHandler.getArtists()) {
					artistNamesSet.add(a.getName().toUpperCase());
				}
				for (int i = 0; i < artists.getArtists().size(); i++) {
					IArtistInfo a = artists.getArtists().get(i);
					ImageIcon img = webServicesHandler.getArtistThumbImage(a);
					a.setImage(ImageUtils.resize(img,
							Constants.THUMB_IMAGE_WIDTH,
							Constants.THUMB_IMAGE_HEIGHT));
					a.setAvailable(artistNamesSet.contains(a.getName()
							.toUpperCase()));
				}
			}
			return artists;
		}
		return null;
	}

	/**
	 * @param webServicesHandler
	 */
	public final void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	public void cancel() {
	}

}
