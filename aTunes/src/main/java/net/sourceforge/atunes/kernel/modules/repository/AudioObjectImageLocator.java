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

package net.sourceforge.atunes.kernel.modules.repository;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.ImageSize;

/**
 * Locates audio objects for local audio objects
 * 
 * @author alex
 * 
 */
public class AudioObjectImageLocator implements IAudioObjectImageLocator {

	private ILocalAudioObjectImageHandler localAudioObjectImageHandler;

	/**
	 * @param localAudioObjectImageHandler
	 */
	public void setLocalAudioObjectImageHandler(
			ILocalAudioObjectImageHandler localAudioObjectImageHandler) {
		this.localAudioObjectImageHandler = localAudioObjectImageHandler;
	}

	@Override
	public ImageIcon getImage(final IAudioObject audioObject,
			final ImageSize imageSize) {
		if (audioObject instanceof ILocalAudioObject) {
			ILocalAudioObject localAudioObject = (ILocalAudioObject) audioObject;

			ImageIcon result = localAudioObjectImageHandler.getInsidePicture(
					localAudioObject, imageSize.getSize(), imageSize.getSize());
			if (result == null) {
				result = localAudioObjectImageHandler.getExternalPicture(
						localAudioObject, imageSize.getSize(),
						imageSize.getSize());
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
