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

import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;

/**
 * Locates audio objects for local audio objects
 * @author alex
 *
 */
public class AudioObjectImageLocator implements IAudioObjectImageLocator {
	
	private IOSManager osManager;
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	@Override
	public ImageIcon getImage(IAudioObject audioObject, ImageSize imageSize) {
		if (audioObject instanceof ILocalAudioObject) {
			ILocalAudioObject localAudioObject = (ILocalAudioObject) audioObject;
			ImageIcon result = null;

			result = ImageCache.getImageCache().retrieveImage(localAudioObject, imageSize);

			if (result == null) {
				result = AudioFilePictureUtils.getInsidePicture(localAudioObject, imageSize.getSize(), imageSize.getSize());
				if (result == null) {
					result = AudioFilePictureUtils.getExternalPicture(localAudioObject, imageSize.getSize(), imageSize.getSize(), osManager);
				}
			} else {
				return result;
			}

			if (result != null) {
				ImageCache.getImageCache().storeImage(localAudioObject, imageSize, result);
			}

			return result;
		}
		return null;
	}
	
	@Override
	public ImageIcon getImage(Album album, ImageSize imageSize) {
		if (album == null || album.getAudioObjects().isEmpty()) {
			return null;
		}
		return getImage(album.getAudioObjects().get(0), imageSize);
    }
    

	
	

}
