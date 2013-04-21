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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.Color;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.ImageSize;

/**
 * Gets covers for full screen mode
 * 
 * @author alex
 * 
 */
public class FullScreenCoverImageRetriever {

	private IWebServicesHandler webServicesHandler;

	private IIconFactory radioBigIcon;

	private IIconFactory rssBigIcon;

	private IUnknownObjectChecker unknownObjectChecker;

	private ILocalAudioObjectImageHandler localAudioObjectImageHandler;

	/**
	 * @param localAudioObjectImageHandler
	 */
	public void setLocalAudioObjectImageHandler(
			final ILocalAudioObjectImageHandler localAudioObjectImageHandler) {
		this.localAudioObjectImageHandler = localAudioObjectImageHandler;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param radioBigIcon
	 */
	public void setRadioBigIcon(final IIconFactory radioBigIcon) {
		this.radioBigIcon = radioBigIcon;
	}

	/**
	 * @param rssBigIcon
	 */
	public void setRssBigIcon(final IIconFactory rssBigIcon) {
		this.rssBigIcon = rssBigIcon;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * Returns picture for audio object
	 * 
	 * @param audioObject
	 * @param osManager
	 * @return
	 */
	ImageIcon getPicture(final IAudioObject audioObject) {
		ImageIcon image = null;
		if (audioObject instanceof IRadio) {
			image = this.radioBigIcon.getIcon(Color.WHITE);
		} else if (audioObject instanceof IPodcastFeedEntry) {
			image = this.rssBigIcon.getIcon(Color.WHITE);
		} else if (audioObject instanceof ILocalAudioObject) {
			image = getPictureForLocalAudioObject(audioObject);
		}

		return image;
	}

	/**
	 * @param audioObject
	 * @return
	 */
	private ImageIcon getPictureForLocalAudioObject(
			final IAudioObject audioObject) {
		ImageIcon image = this.webServicesHandler.getAlbumImage(
				audioObject.getArtist(this.unknownObjectChecker),
				audioObject.getAlbum(this.unknownObjectChecker));
		if (image == null) {
			image = this.localAudioObjectImageHandler.getImage(audioObject,
					ImageSize.SIZE_MAX);
		}
		if (image == null) {
			image = Images.getImage(Images.APP_LOGO_300);
		}
		return image;
	}
}
