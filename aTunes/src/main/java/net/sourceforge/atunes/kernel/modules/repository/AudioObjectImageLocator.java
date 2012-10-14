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

package net.sourceforge.atunes.kernel.modules.repository;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;

/**
 * Locates audio objects for local audio objects
 * @author alex
 *
 */
public class AudioObjectImageLocator implements IAudioObjectImageLocator {

	private IOSManager osManager;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public ImageIcon getImage(final IAudioObject audioObject, final ImageSize imageSize) {
		if (audioObject instanceof ILocalAudioObject) {
			ILocalAudioObject localAudioObject = (ILocalAudioObject) audioObject;

			ImageIcon result = AudioFilePictureUtils.getInsidePicture(localAudioObject, imageSize.getSize(), imageSize.getSize());
			if (result == null) {
				result = AudioFilePictureUtils.getExternalPicture(localAudioObject, imageSize.getSize(), imageSize.getSize(), osManager, unknownObjectChecker);
			}

			return result;
		}
		return null;
	}

	@Override
	public ImageIcon getImage(final IAlbum album, final ImageSize imageSize) {
		if (album == null || album.getAudioObjects().isEmpty()) {
			return null;
		}
		return getImage(album.getAudioObjects().get(0), imageSize);
	}
}
